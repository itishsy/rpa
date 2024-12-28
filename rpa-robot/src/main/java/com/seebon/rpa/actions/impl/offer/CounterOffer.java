package com.seebon.rpa.actions.impl.offer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONArray;
import com.seebon.rpa.BusinessException;
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
import com.seebon.rpa.context.enums.CounterOfferType;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.entity.robot.vo.OfferCheckVO;
import com.seebon.rpa.entity.robot.vo.RobotEmpAccountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author LY
 * @date 2023/11/7 14:22
 */

@Slf4j
@ActionUtils
@RobotAction(name = "回盘网页数据", targetType = NoneTarget.class, comment = "回盘聚合")
public class CounterOffer extends AbstractAction {

    @ActionArgs(value = "回盘类型", dict = CounterOfferType.class, required = true)
    @Conditions({"CounterOfferTable:idCardXpath,statusXpath,nextPageXpath",
            "CounterOfferExcel:headerRowNum,idLine,personNoLine,statusLine",
            "CounterOfferSingle:singleXpath"})
    private CounterOfferType counterOfferType;

    // ============================excel文件===========================//

    @ActionArgs(value = "数据开始行数", dict = CounterOfferNumLine.class, required = true)
    private CounterOfferNumLine headerRowNum;

    @ActionArgs(value = "身份证标识列", dict = CounterOfferCharLine.class)
    private CounterOfferCharLine idLine;

    @ActionArgs(value = "个人编号标识列", dict = CounterOfferCharLine.class)
    private CounterOfferCharLine personNoLine;

    @ActionArgs(value = "状态列", dict = CounterOfferCharLine.class, required = true)
    private CounterOfferCharLine statusLine;

    // ============================excel文件======= end ====================//

    // ===========================表格table============================//
    @ActionArgs(value = "身份证Xpath", style = DynamicFieldType.text, required = true)
    private String idCardXpath;

    @ActionArgs(value = "状态Xpath", style = DynamicFieldType.text, required = true)
    private String statusXpath;

    @ActionArgs(value = "下一页Xpath", style = DynamicFieldType.text)
    private String nextPageXpath;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Integer nextPageTime;

    private int iframeIndex = 0;
    // ===========================表格table======= end =====================//


    // ===========================单条=====================================//
    @ActionArgs(value = "Xpath", style = DynamicFieldType.text, comment = "单条回盘xpath", required = true)
    private String singleXpath;

    @Override
    public void run() {
        if (counterOfferType == CounterOfferType.CounterOfferExcel) {
            excelRun();
        } else if (counterOfferType == CounterOfferType.CounterOfferSingle) {
            singleRun();
        } else if (counterOfferType == CounterOfferType.CounterOfferTable) {
            tableRun();
        } else {
            nonResultRun();
        }
    }

    public void nonResultRun() {
        if (ctx.getVariable("offerIdCardList") == null) {
            log.error("offerIdCardList为空");
            return;
        }
        log.info("offerIdCardList: {}", Optional.ofNullable(ctx.getVariableToList("offerIdCardList")));
        List<String> list = ctx.getVariableToList("offerIdCardList");
        if (CollectionUtils.isNotEmpty(list)) {
            for (String sfz : list) {
                if (!ifSfz(sfz)) {
                    //TODO
                    //不是身份证，需要通过个人编号拿到身份证再插入
                    Map<String, Object> personNumber = ctx.getVariable("personNoIdCardMap");
                    if (personNumber != null && personNumber.get(sfz) != null) {
                        sfz = (String) personNumber.get(sfz);
                        ;
                    }
                }
                updateExecStatusBySfz(sfz);
            }
        }
    }

    private void updateExecStatusBySfz(String sfz) {
        String sql = String.format("update robot_execution_data set robotExecStatus = 2,failReason = '待核验' " +
                        " where idCard = '%s' and " +
                        " accountNumber  = '%s' and " +
                        " businessType  = '%s' and  " +
                        " declareType =  '%s' and robotExecStatus = 0  and sync = 0",
                sfz, ctx.getAccountNumber(), ctx.getVariable("businessIntType").toString(), ctx.getVariable("operateType").toString(), ctx.getVariable("businessTplCode").toString());
        jdbcTemplate.update(sql);
    }

    public void singleRun() {

        WebDriver webDriver = ctx.getWebDriver();
        String singleChangeXpath = switchToFrame(singleXpath);
        WebElement element = webDriver.findElement(By.xpath(singleChangeXpath));
        if (element == null) {
            return;
        }
        String innerText = element.getAttribute("innerText");
        if (StringUtils.isNotEmpty(innerText)) {
            List<String> list = ctx.getVariableToList("offerIdCardList");
            //获取单个的索引坐标
            int index = ctx.getVariable("offerIteratorIndex");
            String sfzStr = list.get(index);
            //判断该字符串是否是身份证
            if (StrUtil.isNotBlank(sfzStr)) {
                //不是身份证，需要通过个人编号拿到身份证再插入
                if (!ifSfz(sfzStr)) {
                    Map<String, Object> personNumber = ctx.getVariable("personNoIdCardMap");
                    sfzStr = (String) personNumber.get(sfzStr);
                    ;
                }
                updateOfferStatusByCheckSingle(sfzStr, innerText);
            } else {
                throw new RuntimeException("上下文中不存在身份证信息");
            }
        }
    }

    private Boolean ifSfz(String input) {
        String regex = "\\d{17}[\\d|x|X]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        boolean isMatched = matcher.matches();
        if (isMatched) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private void updateOfferStatusByCheckSingle(String idCard, String failReason) {
        String sql = String.format("update robot_execution_data set robotExecStatus = 2 ," +
                        " failReason = '%s' " +
                        " where idCard = '%s'",
                failReason, idCard);
        jdbcTemplate.update(sql);
    }

    public void tableRun() {
        WebDriver webDriver = ctx.getWebDriver();
        String idCardParseXpath = switchToFrame(idCardXpath);
        String statusParseXpath = switchToFrame(statusXpath);
        String nextPageParseXpath = switchToFrame(nextPageXpath);

        if (webDriver.findElement(By.xpath(idCardParseXpath)) == null || webDriver.findElement(By.xpath(statusParseXpath)) == null) {
            throw new RobotInterruptException("解析身份证或状态xpath失败");
        }
        Map<String, String> resultMap = new LinkedHashMap<>();
        do {
            List<String> idCardList = getNextTableXpathStr(idCardParseXpath, webDriver);
            List<String> statusList = getNextTableXpathStr(statusParseXpath, webDriver);
            log.info("CounterOfferTable：idCardList【{}】", idCardList);
            log.info("CounterOfferTable：statusList【{}】", statusList);
            if (idCardList.isEmpty()) {
                break;
            }
            boolean isRepeat = false;
            for (int i = 0; i < idCardList.size(); i++) {
                if (resultMap.containsKey(idCardList.get(i))) {
                    if (resultMap.get(idCardList.get(i)).equals(statusList.get(i))) {
                        isRepeat = true;
                    }
                }
            }
            if (isRepeat) {
                break;
            }
            if (!IdcardUtil.isValidCard(String.valueOf(idCardList.get(0)))) {
                Object variable = ctx.getVariable("personNoIdCardMap");
                if (variable != null) {
                    List<String> copyIdCardList = new ArrayList<>();
                    HashMap<String, Object> personNoIdCardMap = (HashMap<String, Object>) variable;
                    idCardList.forEach(personNo -> {
                        if (personNoIdCardMap.containsKey(String.valueOf(personNo))) {
                            copyIdCardList.add(String.valueOf(personNoIdCardMap.get(personNo)));
                        }
                    });
                    idCardList = copyIdCardList;
                }
            }
            for (int i = 0; i < idCardList.size(); i++) {
                if (!resultMap.containsKey(idCardList.get(i))) {
                    resultMap.put(idCardList.get(i), statusList.get(i));
                }
            }
        } while (doNextPageXpath(nextPageParseXpath, webDriver));

        String addrName = ctx.getVariable("addrName").toString();
        int businessType = Integer.parseInt(ctx.getVariable("businessIntType").toString());
        String accountNumber = "";
        if (businessType == 1) {
            accountNumber = ctx.getVariable("socialNumber").toString();
        } else {
            accountNumber = ctx.getVariable("accfundNumber").toString();
        }
        Object o = ctx.get("operateType");
        if (o == null) {
            throw new BusinessException("获取回盘操作类型失败 operateType");
        }
        for (Map.Entry<String, String> stringStringEntry : resultMap.entrySet()) {
            updateOfferStatusByTable(stringStringEntry.getKey(), stringStringEntry.getValue(), addrName, accountNumber, String.valueOf(businessType));
        }
    }

    private void updateOfferStatusByTable(String idCard, String failReason, String addrName, String accountNumber, String businessType) {
        String sql = String.format("update robot_execution_data  set robotExecStatus =2 " +
                ", failReason = '%s' " +
                "where sync = 0  and robotExecStatus = 0 and  " +
                " addrName = '%s' and " +
                " accountNumber  = '%s' and " +
                " businessType  = '%s' and  " +
                " idCard = '%s'", failReason, addrName, accountNumber, businessType, idCard);

        jdbcTemplate.update(sql);
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

    private void updateOfferStatus(String idCard,
                                   String failReason,
                                   String addrName,
                                   String accountNumber,
                                   String businessType,
                                   String declareType) {

        String sql = String.format("update robot_execution_data  set robotExecStatus =2 " +
                ", failReason = '%s' " +
                "where sync = 0  and  " +
                " addrName = '%s' and " +
                " accountNumber  = '%s' and " +
                " businessType  = '%s' and  " +
                " declareType =  '%s'  and " +
                " idCard = '%s'", failReason, addrName, accountNumber, businessType, declareType, idCard);

        jdbcTemplate.update(sql);
    }

    public void excelRun() {
        if (personNoLine == null && idLine == null) {
            throw new RobotInterruptException("身份证和个人编号最少选一个");
        }

        String addrName = ctx.getVariable("addrName").toString();
        int businessType = Integer.parseInt(ctx.getVariable("businessIntType").toString());
        String accountNumber = "";
        if (businessType == 1) {
            accountNumber = ctx.getVariable("socialNumber").toString();
        } else {
            accountNumber = ctx.getVariable("accfundNumber").toString();
        }
        Object checkListObj = ctx.getVariableToList("checkList");
        String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
        File[] ls = FileUtil.ls(downloadPath);
        Optional<File> checkFile = Stream.of(ls).filter(item -> Arrays.asList("xls", "xlsx", "csv").contains(item.getName().split("\\.")[1])).max(Comparator.comparing(File::lastModified));
        if (!checkFile.isPresent()) {
            throw new BusinessException("未找到的可核验的文件格式");
        }

        String operateType = ctx.get("operateType");
        if (operateType == null) {
            throw new BusinessException("获取回盘操作类型失败 operateType");
        }
        List<OfferCheckVO> offerCheckList;
        if (checkFile.get().getName().contains("csv")) {
            offerCheckList = readCheckFileCSV(checkFile.get());
        } else {
            offerCheckList = readCheckFileExcel(checkFile.get());
        }
        if (checkListObj != null) {
            // 回盘核验
            List<HashMap<String, Object>> checkList = (List<HashMap<String, Object>>) checkListObj;
            if (checkList.isEmpty()) {
                return;
            }
            List<RobotEmpAccountVO> empAccountList = new ArrayList<>();
            for (HashMap<String, Object> check : checkList) {
                Optional<OfferCheckVO> checkResultItem = offerCheckList.stream().filter(item -> item.getIdCard() != null && item.getIdCard().equals(String.valueOf(check.get("idCard")))).findFirst();
                // 增员
                if (Integer.parseInt(check.get("declareType").toString()) == 1) {
                    if (checkResultItem.isPresent() && checkResultItem.get().getPersonNo() != null) {
                        if (empAccountList.stream().noneMatch(item -> item.getIdCard().equals(checkResultItem.get().getIdCard()))) {
                            empAccountList.add(new RobotEmpAccountVO(checkResultItem.get().getPersonNo(), checkResultItem.get().getIdCard()));
                        }
                    }
                    updateOfferStatusByCheck(String.valueOf(check.get("id")), checkResultItem.isPresent() ? "增员成功" : "人员不在册");
                } else if (Integer.parseInt(check.get("declareType").toString()) == 2) {
                    updateOfferStatusByCheck(String.valueOf(check.get("id")), !checkResultItem.isPresent() ? "减员成功" : "减员失败");
                }
            }
            if (!empAccountList.isEmpty()) {
                saveEmpAccount(empAccountList);
            }
            return;
        }
        // 非核验回盘
        for (OfferCheckVO vo : offerCheckList) {
            updateOfferStatus(vo.getIdCard(), vo.getStatus(), addrName, accountNumber, String.valueOf(businessType), operateType);
        }
        System.out.println("回盘");
    }

    private List<OfferCheckVO> readCheckFileExcel(File checkFile) {
        ExcelReader reader = ExcelUtil.getReader(checkFile);
        List<OfferCheckVO> list = new ArrayList<>();
        List<Object> statusList = reader.readColumn(statusLine.getIndex(), headerRowNum.getName() - 1);
        List<Object> idCardList = idLine == null ? Collections.emptyList() : reader.readColumn(idLine.getIndex(), headerRowNum.getName() - 1);
        List<Object> personNoList = personNoLine == null ? Collections.emptyList() : reader.readColumn(personNoLine.getIndex(), headerRowNum.getName() - 1);
        if ((idCardList == null || idCardList.isEmpty()) && (personNoList == null || personNoList.isEmpty())) {
            throw new RobotInterruptException("核验数据文件为空");
        }
        if (idCardList != null && !idCardList.isEmpty()) {
            boolean flag = !personNoList.isEmpty();
            for (int i = 0; i < idCardList.size(); i++) {
                list.add(new OfferCheckVO(String.valueOf(idCardList.get(i)), flag ? String.valueOf(personNoList.get(i)) : null, String.valueOf(statusList.get(i))));
            }
        } else {
            for (int i = 0; i < personNoList.size(); i++) {
                list.add(new OfferCheckVO(null, String.valueOf(personNoList.get(i)), String.valueOf(statusList.get(i))));
            }
        }
        return list;
    }

    private List<OfferCheckVO> readCheckFileCSV(File checkFile) {
        CsvData read = CsvUtil.getReader().read(checkFile, StandardCharsets.UTF_8);
        List<CsvRow> rows = read.getRows();
        List<OfferCheckVO> list = new ArrayList<>();
        for (int i = headerRowNum.getName() - 1; i < rows.size(); i++) {
            CsvRow row = rows.get(i);
            if (personNoLine != null) {
                list.add(new OfferCheckVO(idLine == null ? null : row.get(idLine.getIndex()).replaceAll("\"", "").replaceAll("=", ""), row.get(personNoLine.getIndex()).replaceAll("\"", "").replaceAll("=", ""), row.get(statusLine.getIndex())));
            } else {
                list.add(new OfferCheckVO(row.get(idLine.getIndex()).replaceAll("\"", "").replaceAll("=", ""), null, row.get(statusLine.getIndex())));
            }
        }
        if (list.isEmpty()) {
            throw new RobotInterruptException("核验数据文件为空");
        }
        return list;
    }

    private void saveEmpAccount(List<RobotEmpAccountVO> accountList) {
        int businessType = Integer.parseInt(ctx.getVariable("businessIntType").toString());
        String addrId = ctx.getVariable("addrId");
        String sql = String.format("insert into robot_emp_account (businessType,addrId,accountList,sync) values(" +
                        " '%s'," +
                        " '%s'," +
                        " '%s'," +
                        " 0)",
                businessType, addrId, JSONArray.toJSONString(accountList));
        jdbcTemplate.update(sql);
    }

    private void updateOfferStatusByCheck(String id, String failReason) {
        String sql = String.format("update robot_execution_data set robotExecStatus = 2 ," +
                        " failReason = '%s' " +
                        " where id = %d",
                failReason, Integer.valueOf(id));
        jdbcTemplate.update(sql);
    }
}
