package com.seebon.rpa.actions.impl.offer;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.CounterOfferCharLine;
import com.seebon.rpa.context.enums.CounterOfferNumLine;
import com.seebon.rpa.context.enums.OfferRegisterDetailsType;
import com.seebon.rpa.context.enums.RegisterBusinessType;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/12/8  18:14
 */
@Slf4j
@ActionUtils
@RobotAction(name = "回盘-在册明细", targetType = NoneTarget.class, comment = "回盘-在册明细")
public class OfferRegisterDetails extends AbstractAction {

    @ActionArgs(value = "申报类型", dict = RegisterBusinessType.class, required = true)
    private RegisterBusinessType registerBusinessType;

    @ActionArgs(value = "回盘类型", dict = OfferRegisterDetailsType.class, required = true)
    @Conditions({"OfferRegisterDetailsTable:registerBusinessType,nameXpath,idCardXpath,personNoXpath,paymentBaseXpath,nextPageXpath",
            "OfferRegisterDetailsExcel:registerBusinessType,headerRowNum,nameLine,idLine,personNoLine,paymentBaseLine"})
    private OfferRegisterDetailsType offerRegisterDetailsType;

    // ============================excel文件===========================//

    @ActionArgs(value = "数据开始行数", dict = CounterOfferNumLine.class, required = true)
    private CounterOfferNumLine headerRowNum;

    @ActionArgs(value = "姓名", dict = CounterOfferCharLine.class, required = true)
    private CounterOfferCharLine nameLine;

    @ActionArgs(value = "身份证标识列", dict = CounterOfferCharLine.class, required = true)
    private CounterOfferCharLine idLine;

    @ActionArgs(value = "个人编号标识列", dict = CounterOfferCharLine.class)
    private CounterOfferCharLine personNoLine;

    @ActionArgs(value = "缴纳基数标识列", dict = CounterOfferCharLine.class)
    private CounterOfferCharLine paymentBaseLine;

    // ===========================表格table============================//
    @ActionArgs(value = "姓名Xpath", style = DynamicFieldType.text, required = true)
    private String nameXpath;

    @ActionArgs(value = "身份证Xpath", style = DynamicFieldType.text, required = true)
    private String idCardXpath;

    @ActionArgs(value = "个人编号Xpath", style = DynamicFieldType.text)
    private String personNoXpath;

    @ActionArgs(value = "缴纳基数Xpath", style = DynamicFieldType.text)
    private String paymentBaseXpath;

    @ActionArgs(value = "下一页Xpath", style = DynamicFieldType.text)
    private String nextPageXpath;

    private Integer nextPageTime;

    private int iframeIndex = 0;

    @Autowired
    private RobotCommonService robotCommonService;

    @Override
    public void run() {
        if (offerRegisterDetailsType == OfferRegisterDetailsType.OfferRegisterDetailsExcel) {
            excelRun();
        } else if (offerRegisterDetailsType == OfferRegisterDetailsType.OfferRegisterDetailsTable) {
            tableRun();
        }
    }

    private void tableRun() {
        List<Map<String, Object>> details = new ArrayList<>();
        WebDriver webDriver = ctx.getWebDriver();
        String nameParseXpath = switchToFrame(nameXpath);
        String idCardParseXpath = switchToFrame(idCardXpath);
        String personNoParseXpath = switchToFrame(personNoXpath);
        String paymentBaseParseXpath = null;
        if (paymentBaseXpath != null) {
            paymentBaseParseXpath = switchToFrame(paymentBaseXpath);
        }
        String nextPageParseXpath = switchToFrame(nextPageXpath);
        if (webDriver.findElement(By.xpath(idCardParseXpath)) == null || webDriver.findElement(By.xpath(nameParseXpath)) == null) {
            throw new RobotInterruptException("解析身份证或姓名xpath失败");
        }
        List<String> idCardList = new ArrayList<>();
        do {
            if (StringUtils.isNotEmpty(idCardParseXpath)) {
                idCardList = getNextTableXpathStr(idCardParseXpath, webDriver);
            } else {
                idCardList = getNextTableXpathStr(personNoParseXpath, webDriver);
            }
            List<String> nameList = getNextTableXpathStr(nameParseXpath, webDriver);
            List<String> paymentBaseList = new ArrayList<>();
            if (StringUtils.isNotEmpty(paymentBaseParseXpath)) {
                paymentBaseList = getNextTableXpathStr(paymentBaseParseXpath, webDriver);
            }
            log.info("CounterOfferTable：idCardList【{}】", idCardList);
            log.info("CounterOfferTable：nameList【{}】", nameList);
            log.info("CounterOfferTable：paymentBaseList【{}】", paymentBaseList);
            if (idCardList.isEmpty()) {
                break;
            }
            for (int i = 0; i < idCardList.size(); i++) {
                Map<String, Object> resultMap = new LinkedHashMap<>();
                if (!resultMap.containsKey(idCardList.get(i))) {
                    resultMap.put("empName", nameList.get(i));
                    resultMap.put("idCard", idCardList.get(i));
                    resultMap.put("empAccount", paymentBaseList.get(i));
                }
                details.add(resultMap);
            }
        } while (doNextPageXpath(nextPageParseXpath, webDriver));
        ctx.setVariable("uploadList", details);
        uploadFee();
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

    private List<String> getNextTableXpathStr(String xpath, WebDriver webDriver) {
        List<String> contentList = new ArrayList<>();
        int tr = xpath.lastIndexOf("tr[");
        int trEI = xpath.indexOf("]", tr);

        String rowNum = xpath.substring(tr + 3, trEI);
        for (int j = Integer.parseInt(rowNum); ; j++) {
            String baseStr = xpath.substring(0, tr + 3);
            String endStr = xpath.substring(trEI);
            String nextXpath = baseStr + j + endStr;
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(3));
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(nextXpath)));
//                WebElement element = webDriver.findElement(By.xpath(nextXpath));
                if (element == null || !element.isDisplayed()) {
                    break;
                }
                contentList.add(element.getText());
            } catch (Exception e) {
                break;
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

    private void excelRun() {
        if (personNoLine == null && idLine == null) {
            throw new RobotInterruptException("身份证和个人编号最少选一个");
        }
        String filePath = downloadNewFile();
        Integer fileId = uploadFile(filePath);
        ctx.setVariable("fileId", fileId);
        //根据文件id读取excel
        List<Map<String, Object>> readExcel = readExcel(filePath);
        if (readExcel == null) {
            throw new RuntimeException("没有回盘明细数据");
        }
        ctx.setVariable("uploadList", readExcel);
        uploadFee();
    }

    @SneakyThrows
    private List<Map<String, Object>> readExcel(String filePath) {
        File file = null;
        Workbook workbook = null;
        try {
            file = new File(filePath);
            workbook = WorkbookFactory.create(file);
        } catch (Exception e) {
            log.error("【Exception】", e);
            if (e.getMessage() != null && file != null && e.getMessage().contains("unsupported file type")) {
                System.gc();
                FileUtil.rename(file, "error_file_" + System.currentTimeMillis(), true);
            }
            return null;
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            log.info("没有在册明细数据");
            return null;
        }
        Row header = sheet.getRow(headerRowNum.getName() - 1);
        Iterator<Cell> cellIterator = header.cellIterator();
        Map<String, Integer> fieldMap = new HashMap<>();
        while (cellIterator.hasNext()) {
            Cell next = cellIterator.next();
            //表头
            if ((!(next.getColumnIndex() == nameLine.getIndex()
                    || next.getColumnIndex() == idLine.getIndex() ||
                    next.getColumnIndex() == personNoLine.getIndex()
                    || next.getColumnIndex() == paymentBaseLine.getIndex())) && StringUtils.isNotEmpty(next.getStringCellValue())) {
                fieldMap.put("otherInfo." + next.getStringCellValue(), next.getColumnIndex());
            }
        }
        int rowSize = sheet.getLastRowNum() + 1;
        fieldMap.put("empName", nameLine.getIndex());
        if (idLine != null) {
            fieldMap.put("idCard", idLine.getIndex());
        } else {
            fieldMap.put("idCard", personNoLine.getIndex());
        }
        fieldMap.put("empAccount", paymentBaseLine.getIndex());
        for (int i = headerRowNum.getName(); i < rowSize; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            variable("row", row);
            variable("rowIndex", i);
            Map<String, Object> result = Maps.newHashMap();
            for (String key : fieldMap.keySet()) {
                Integer value = Integer.valueOf(fieldMap.get(key).toString());
                Object cellVal = getCellVal(row, value);
                if (cellVal != null) {
                    result.put(key, cellVal);
                }
            }
            if (!result.isEmpty()) {
                resultList.add(result);
            }
        }
        return resultList;
    }

    @SneakyThrows
    private void uploadFee() {
        Integer fileId = ctx.getVariable("fileId");
        Map<String, Object> params = new HashMap<>();
        params.put("addrName", ctx.getVariable("addrName"));
        params.put("businessType", registerBusinessType.getType());
        params.put("accountNumber", ctx.getAccountNumber());
        params.put("tplTypeCode", registerBusinessType.getTplCode());
        List<Map<String, String>> files = new ArrayList<>();
        Map<String, String> fileIdMap = new HashMap<>();
        if (fileId != null) {
            fileIdMap.put("fileId", fileId.toString());
        } else {
            fileIdMap.put("fileId", null);
        }
        files.add(fileIdMap);
        params.put("files", fileIdMap);
        params.put("dataMonth", new SimpleDateFormat("yyyy-MM").format(new Date()));
        params.put("details", ctx.getVariable("uploadList"));

        String resp = robotCommonService.saveRegister(params);
        log.info("resp=" + resp);
    }

    private Integer uploadFile(String filePath) {
        String fileId = robotCommonService.fileUpload(new File(filePath));
        return Integer.parseInt(fileId);
    }

    private Object getCellVal(Row row, Integer value) {
        Map<String, String> replaceMap = null;
        Object val = null;
        if (row.getCell(value) == null) {
            return val;
        }
        Cell cell = row.getCell(value);
        switch (cell.getCellType().name()) {
            case "STRING":
                val = cell.getRichStringCellValue().getString();
                if (val != null && replaceMap != null && !replaceMap.isEmpty() && replaceMap.containsKey(String.valueOf(value))) {
                    String replaceInfo = replaceMap.get(String.valueOf(value));
                    String[] split = replaceInfo.split(",");
                    for (String str : split) {
                        String[] replaceArr = str.split(":");
                        val = ((String) val).replace(replaceArr[0], replaceArr.length < 2 ? "" : replaceArr[1]);
                    }
                }
                break;
            case "NUMERIC":
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date dateCellValue = cell.getDateCellValue();
                    val = new SimpleDateFormat("yyyy/MM/dd").format(dateCellValue);
                } else {
                    String s2 = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                    val = new BigDecimal(s2).stripTrailingZeros().toPlainString();
                }
                break;
            case "BOOLEAN":
                val = cell.getBooleanCellValue();
                break;
            case "BLACK":
                val = "";
                break;
            default:
                val = cell.toString();
                if (StringUtils.isNotBlank((String) val) && !replaceMap.isEmpty() && replaceMap.containsKey(value)) {
                    String replaceInfo = replaceMap.get(String.valueOf(value));
                    String[] split = replaceInfo.split(",");
                    for (String str : split) {
                        String[] replaceArr = str.split(":");
                        val = ((String) val).replace(replaceArr[0], replaceArr.length < 2 ? "" : replaceArr[1]);
                    }
                }
                break;
        }

        return val;
    }

    private String getResult(Object response) throws Exception {
        if (response instanceof CloseableHttpResponse) {
            HttpEntity entity = ((CloseableHttpResponse) response).getEntity();
            return EntityUtils.toString(entity, "utf-8");
        }
        if (response instanceof String) {
            return response.toString();
        }
        return null;
    }

    private String downloadNewFile() {
        String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
        if (StringUtils.isBlank(downloadPath)) {
            throw new RuntimeException("浏览器不支持自动下载,请先配置文件路径.");
        }
        //缓存文件列表
        List<String> fileNames = ctx.get(RobotConstant.CACHE_DOWNLOAD_FILE_NAME);
//        List<String> fileNames = new ArrayList<>();
//        fileNames.add("ded67179ec884c04b7a1a2a157ba521d_减员封存.xlsx");
        //新的文件路径
        String newFilePath = Convert.getNewPath(downloadPath, fileNames);
        if (newFilePath.contains(".crdownload")) {//下载中的文件名
            Convert.sleep(5);//休眠5秒
            newFilePath = Convert.getNewPath(downloadPath, fileNames);
        }
        //用完后，清空文件名称
        ctx.setVariable(RobotConstant.CACHE_DOWNLOAD_FILE_NAME, Lists.newArrayList());
        return newFilePath;
    }

}
