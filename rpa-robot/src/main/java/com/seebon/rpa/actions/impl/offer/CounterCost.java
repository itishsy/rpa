package com.seebon.rpa.actions.impl.offer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.CostType;
import com.seebon.rpa.context.enums.CounterOfferNumLine;
import com.seebon.rpa.context.enums.RegisterBusinessType;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.service.RobotCommonService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author LY
 * @date 2023/12/11 9:54
 */
@Slf4j
@ActionUtils
@RobotAction(name = "回盘-费用明细", targetType = NoneTarget.class, comment = "回盘-费用明细")
public class CounterCost extends AbstractAction {

    @ActionArgs(value = "申报类型", dict = RegisterBusinessType.class, required = true)
    private RegisterBusinessType costBusinessType;

    @ActionArgs(value = "回盘类型", dict = CostType.class, required = true)
    @Conditions({"CostTypeExcel:costBusinessType,headerLine,costXpath,personTotalAmountStr,companyTotalAmountStr",
            "CostTypeTable: costBusinessType,costXpath,headerXpath,nextPageXpath,personTotalAmountStr,companyTotalAmountStr"})
    private CostType costType;


    @ActionArgs(value = "表头行数", dict = CounterOfferNumLine.class, required = true)
    private CounterOfferNumLine headerLine;

    //===================Web==========================

    @ActionArgs(value = "费用所属期", style = DynamicFieldType.text, required = true)
    private String costXpath;
    @ActionArgs(value = "表头行", style = DynamicFieldType.text, required = true)
    private String headerXpath;
    @ActionArgs(value = "下一页", style = DynamicFieldType.text, required = true)
    private String nextPageXpath;

    @ActionArgs(value = "个人合计金额", style = DynamicFieldType.text)
    private String personTotalAmountStr;

    @ActionArgs(value = "单位合计金额", style = DynamicFieldType.text)
    private String companyTotalAmountStr;

    @Autowired
    private RobotCommonService robotCommonService;

    private int iframeIndex = 0;
    private Integer nextPageTime;

    @Override
    public void run() {
        List<Map<String, Object>> details;

        WebDriver webDriver = ctx.getWebDriver();
        String costParseXpath = switchToFrame(costXpath);
        WebElement costElement = webDriver.findElement(By.xpath(costParseXpath));
        if (costElement == null) {
            throw new RobotInterruptException("费用所属期解析失败");
        }
        String periodOfExpense = (String) ((JavascriptExecutor) webDriver).executeScript("arguments[0].innerText", costElement);
        if (periodOfExpense == null || periodOfExpense.isEmpty()) {
            periodOfExpense = parseDate(costElement.getText());
        }
        String fileId = "";
        if (costType == CostType.CostTypeExcel) {
            String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
            File[] ls = FileUtil.ls(downloadPath);
            Optional<File> checkFile = Stream.of(ls).filter(item -> Arrays.asList("xls", "xlsx").contains(item.getName().split("\\.")[1])).max(Comparator.comparing(File::lastModified));
            if (!checkFile.isPresent()) {
                throw new RobotInterruptException("未找到的可核验的文件格式");
            }
            details = readCostExcel(checkFile.get());
            fileId = sendCostFileToSass(checkFile.get());
        } else {
            details = readCostTable();
            File file = genExcel(details);
            fileId = sendCostFileToSass(file);
        }
        String totalAmount = ctx.getVariable("totalAmount");
        if (totalAmount == null || totalAmount.isEmpty()) {
            BigDecimal totalAmountDecimal = new BigDecimal(0);
            for (Map<String, Object> detail : details) {
                if (detail.containsKey("缴纳合计金额")) {
                    totalAmountDecimal = totalAmountDecimal.add(new BigDecimal(detail.get("缴纳合计金额").toString()));
                }
            }
            totalAmount = totalAmountDecimal.toString();
        }
        sendCostToSass(details, fileId, totalAmount, periodOfExpense);
    }

    private File genExcel(List<Map<String, Object>> details) {
        String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
        String uuid = IdUtil.fastSimpleUUID();
        File file = new File(downloadPath + "\\" + uuid + ".xlsx");
        ExcelWriter writer = ExcelUtil.getWriter(file);
        writer.write(details, true);
        writer.close();
        return file;
    }

    private List<Map<String, Object>> readCostExcel(File file) {
        ExcelReader reader = ExcelUtil.getReader(file, 0);
        List<Map<String, Object>> details = new ArrayList<>();
        List<List<Object>> readList = reader.read(headerLine.getName() - 1);
        if (readList.isEmpty()) {
            return details;
        }
        List<Object> headerList = readList.get(0);
        List<Integer> sumPersonFieldIndex = getSumFieldKey(personTotalAmountStr);
        List<Integer> sumCompanyFieldIndex = getSumFieldKey(companyTotalAmountStr);
        for (int i = 1; i < readList.size(); i++) {
            List<Object> rowList = readList.get(i);
            LinkedHashMap<String, Object> rowMap = new LinkedHashMap<>();
            BigDecimal sumPersonAmount = new BigDecimal(0);
            BigDecimal sumCompanyAmount = new BigDecimal(0);
            for (int j = 0; j < rowList.size(); j++) {
                rowMap.put(String.valueOf(headerList.get(j)), rowList.get(j));
                int index = j + 1;
                if (sumPersonFieldIndex.contains(index) && NumberUtil.isNumber(rowList.get(j).toString())) {
                    sumPersonAmount = sumPersonAmount.add(new BigDecimal(rowList.get(j).toString()));
                }
                if (sumCompanyFieldIndex.contains(index) && NumberUtil.isNumber(rowList.get(j).toString())) {
                    sumCompanyAmount = sumCompanyAmount.add(new BigDecimal(rowList.get(j).toString()));
                }
            }
            rowMap.put("个人合计金额", sumPersonAmount);
            rowMap.put("单位合计金额", sumCompanyAmount);
            rowMap.put("缴纳合计金额", sumPersonAmount.add(sumCompanyAmount));
            details.add(rowMap);
        }
        return details;
    }

    private List<Integer> getSumFieldKey(String sumFieldStr) {
        List<Integer> fieldKeyList = new ArrayList<>();
        sumFieldStr = sumFieldStr.replaceAll("，", ",");
        if (sumFieldStr.contains(",")) {
            fieldKeyList = Arrays.stream(sumFieldStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        } else {
            fieldKeyList.add(Integer.parseInt(sumFieldStr));
        }
        return fieldKeyList;
    }

    private List<Map<String, Object>> readCostTable() {
        List<Map<String, Object>> details = new ArrayList<>();
        WebDriver webDriver = ctx.getWebDriver();
        String headerParseXpath = switchToFrame(headerXpath);

        List<String> tableHeaderList = getTableHeader(headerParseXpath, webDriver);
        log.info("表头：【{}】", tableHeaderList);
        List<List<String>> lastList = null;
        do {
            List<List<String>> dataList = getNextTableXpathStr(headerParseXpath, tableHeaderList.size(), webDriver);
            log.info("CounterRegister：dataList【{}】", dataList);
            if (dataList.isEmpty()) {
                break;
            }
            if (lastList != null) {
                if (CollUtil.containsAll(dataList, dataList)) {
                    break;
                }
            }
            lastList = dataList;
            List<Integer> sumPersonFieldIndex = getSumFieldKey(personTotalAmountStr);
            List<Integer> sumCompanyFieldIndex = getSumFieldKey(companyTotalAmountStr);

            for (List<String> rowList : dataList) {
                Map<String, Object> row = new LinkedHashMap<>();
                BigDecimal sumPersonAmount = new BigDecimal(0);
                BigDecimal sumCompanyAmount = new BigDecimal(0);
                for (int j = 0; j < rowList.size(); j++) {
                    row.put(tableHeaderList.get(j), rowList.get(j));
                    int index = j + 1;
                    if (sumPersonFieldIndex.contains(index) && NumberUtil.isNumber(rowList.get(j).toString())) {
                        sumPersonAmount = sumPersonAmount.add(new BigDecimal(rowList.get(j).toString()));
                    }
                    if (sumCompanyFieldIndex.contains(index) && NumberUtil.isNumber(rowList.get(j).toString())) {
                        sumCompanyAmount = sumCompanyAmount.add(new BigDecimal(rowList.get(j).toString()));
                    }
                }
                row.put("个人合计金额", sumPersonAmount.toString());
                row.put("单位合计金额", sumCompanyAmount.toString());
                row.put("缴纳合计金额", sumPersonAmount.add(sumCompanyAmount).toString());
                details.add(row);
            }
        } while (doNextPageXpath(nextPageXpath, webDriver));

        return details;
    }

    private String sendCostFileToSass(File file) {
        return robotCommonService.fileUpload(file);
    }

    public String parseDate(String date) {
        if (date.contains("-")) {
            if (date.split("-").length == 3) {
                String[] split = date.split("-");
                return split[0] + "-" + split[1];
            } else {
                return date;
            }
        } else if (date.contains("/")) {
            String[] split = date.split("/");
            return split[0] + "-" + split[1];
        } else {
            if (date.length() == 8) {
                DateTime parse = DateUtil.parse(date, "yyyyMMdd");
                return parse.toString("yyyy-MM");
            } else {
                return date.substring(0, 4) + "-" + date.substring(4);
            }
        }
    }

    private void sendCostToSass(List<Map<String, Object>> details, String fileId, String totalAmount, String periodOfExpense) {
        HashMap<String, Object> body = Maps.newHashMap();
        body.put("addrName", ctx.getVariable("addrName"));
        body.put("addressName", ctx.getVariable("addrId"));
        body.put("orgName", ctx.getVariable("companyName"));
        body.put("accountNumber", ctx.getAccountNumber());
        body.put("periodOfExpense", periodOfExpense);
        body.put("prefix", costBusinessType.getTplCode());
        body.put("totalAmount", totalAmount);
        body.put("businessType", costBusinessType.getType());
        body.put("mapList", details);
        body.put("fileId", fileId);
        try {
            String respStr = robotCommonService.savePaidInPersonDataUrl(body);
            log.info(respStr);
        } catch (Exception e) {
            log.error("上传费用明细失败: {}", e.getMessage());
        }
    }

    private boolean doNextPageXpath(String pageXpath, WebDriver webDriver) {
        if (pageXpath.isEmpty()) {
            return false;
        }
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(pageXpath)));
            if (!element.isEnabled() || !element.isDisplayed()) {
                return false;
            }
            element.click();
            if (nextPageTime == null) {
                Thread.sleep(3 * 1000);
            } else {
                Thread.sleep(nextPageTime * 1000);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<String> getTableHeader(String headerXpath, WebDriver webDriver) {
        List<String> headerList = new ArrayList<>();
        int th = headerXpath.lastIndexOf("th[");
        int thEI = headerXpath.indexOf("]", th);
        String rowNum = headerXpath.substring(th + 3, thEI);
        for (int j = Integer.parseInt(rowNum); ; j++) {
            String baseStr = headerXpath.substring(0, th + 3);
            String endStr = headerXpath.substring(thEI);
            String nextXpath = baseStr + j + endStr;
            try {
                WebElement element = webDriver.findElement(By.xpath(nextXpath));
                if (element == null || !element.isDisplayed()) {
                    break;
                }
                headerList.add(element.getText());
            } catch (Exception e) {
                break;
            }

        }
        return headerList;
    }

    private List<List<String>> getNextTableXpathStr(String xpath, Integer lineSize, WebDriver webDriver) {
        List<List<String>> contentList = new ArrayList<>();
        xpath = xpath.replace("th", "td");
        int tr = xpath.lastIndexOf("tr[");
        int trEI = xpath.indexOf("]", tr);
        Integer rowNum = Integer.parseInt(xpath.substring(tr + 3, trEI));
        boolean finish = false;
        for (int i = rowNum + 1; ; i++) {
            if (finish) {
                break;
            }
            String baseStr = xpath.substring(0, tr + 3);
            String endStr = xpath.substring(trEI);
            String nextRowXpath = baseStr + i + endStr;
            int td = xpath.lastIndexOf("td[");
            int tdEI = xpath.indexOf("]", td);
            List<String> rowList = new ArrayList<>();
            for (int k = rowNum; k < lineSize + rowNum; k++) {
                String tdBaseStr = nextRowXpath.substring(0, td + 3);
                String tdEndStr = nextRowXpath.substring(tdEI);
                String nextXpath = tdBaseStr + k + tdEndStr;
                try {
                    WebElement element = webDriver.findElement(By.xpath(nextXpath));
                    if (element == null || !element.isDisplayed()) {
                        break;
                    }
                    rowList.add(element.getText());
                } catch (Exception e) {
                    finish = true;
                    break;
                }
            }
            if (!rowList.isEmpty()) {
                contentList.add(rowList);
            }
        }
        return contentList;
    }

    private String switchToFrame(String attrValue) {
        if (attrValue.startsWith("[iframe")) {
            for (int i = 0; i < 100; i++) {
                String iframeName = "[iframe" + i + "]";
                if (attrValue.startsWith(iframeName)) {
                    WebDriver driver = ctx.getWebDriver();
                    driver.switchTo().frame(i);
                    iframeIndex++;
                    attrValue = attrValue.substring(iframeName.length());
                    if (attrValue.startsWith("[iframe")) {
                        return switchToFrame(attrValue);
                    }
                    break;
                }
            }
        }
        return attrValue;
    }
}
