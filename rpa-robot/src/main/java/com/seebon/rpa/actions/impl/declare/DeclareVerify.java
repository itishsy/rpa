package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.FileUtil;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.DeclareVerifyType;
import com.seebon.rpa.context.enums.FileMethod;
import com.seebon.rpa.context.enums.OfferLabelType;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "申报核验", order = 50, targetType = NoneTarget.class, comment = "读取网站反馈失败原因，回写到H2相应状态。并将报盘文件中的异常数据剔除")
public class DeclareVerify extends AbstractAction {

    @ActionArgs(value = "获取方式", dict = DeclareVerifyType.class, required = true)
    @Conditions({
            "excel:idCardColIndex,nameColIndex,successMonthColIndex,condition,successFlag,header",
            "json:jsonList,idCardFieldName,nameFieldName,successMonthFieldName,condition,successFlag,failReasonFieldName,businessNode",
            "customPath:customPath,idCardColIndex,nameColIndex,successMonthColIndex,condition,successFlag,header",
    })
    private String verifyType;

    @ActionArgs(value = "表头行号", comment = "缺省，第1行为表头")
    private Integer header;

    @ActionArgs(value = "json列表", comment = "json列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> jsonList;

    @ActionArgs(value = "json身份证属性名", comment = "json身份证属性名", required = true, style = DynamicFieldType.text)
    private String idCardFieldName;

    @ActionArgs(value = "json姓名属性名", comment = "json姓名属性名", style = DynamicFieldType.text)
    private String nameFieldName;

    @ActionArgs(value = "json成功年月属性名", comment = "json成功年月属性名，兼容补缴数据核验", style = DynamicFieldType.text)
    private String successMonthFieldName;

    @ActionArgs(value = "json失败原因属性名", comment = "json失败原因属性名", style = DynamicFieldType.text)
    private String failReasonFieldName;

    @ActionArgs(value = "文件路径", comment = "文件路径", style = DynamicFieldType.text)
    private String customPath;

    @ActionArgs(value = "身份证号所在列", dict = WebExcelNumLine.class, required = true, comment = "配置excel文件身份证所在列")
    private WebExcelNumLine idCardColIndex;

    @ActionArgs(value = "姓名所在列", dict = WebExcelNumLine.class, comment = "配置excel文件姓名所在列，并自动上传在册数据")
    private WebExcelNumLine nameColIndex;

    @ActionArgs(value = "成功年月所在列", dict = WebExcelNumLine.class, comment = "配置excel文件成功年月所在列，兼容补缴数据核验")
    private WebExcelNumLine successMonthColIndex;

    @ActionArgs(value = "在册标识", comment = "EL表达式。缺省，默认逻辑为在excel中表示在册，不在表示不在册 ", style = DynamicFieldType.text)
    private String condition;

    @ActionArgs(value = "成功标识", comment = "EL表达式。数据存在则成功，不存在则失败,与在册标识只能用一个", style = DynamicFieldType.text)
    private String successFlag;

    @ActionArgs(value = "下一节点", dict = OfferLabelType.class, comment = "可以为空")
    private OfferLabelType businessNode;

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RobotCommonService robotCommonService;

    @Override
    public void run() {
        List<RobotExecutionData> checkList = this.getCheckList();
        if (CollectionUtils.isEmpty(checkList)) {
            log.info("未找到待核验数据");
            return;
        }
        String nameColName = null;
        Map<String, Map<String, Object>> successMap = Maps.newHashMap();
        Map<String, Map<String, Object>> idCardMap = Maps.newHashMap();
        Map<String, Object> successMonthMap = Maps.newHashMap();
        Map<String, Object> failReasonMap = Maps.newHashMap();
        List<Map<String, Object>> dataList = Lists.newArrayList();
        switch (DeclareVerifyType.valueOf(verifyType)) {
            case excel:
                String downloadFileKey = "declareVerifyDownloadFilePath";
                fileUtil.setActionType(FileMethod.downloadNewFile.toString());
                fileUtil.setVarKey(downloadFileKey);
                fileUtil.run();

                String filePath = ctx.get(downloadFileKey);
                this.excel(dataList, idCardMap, successMonthMap, successMap, filePath, nameColName);
                ctx.remove(downloadFileKey);
                //保存在册
                this.saveRegister(dataList, filePath);
                break;
            case customPath:
                this.excel(dataList, idCardMap, successMonthMap, successMap, customPath, nameColName);
                //保存在册
                this.saveRegister(dataList, customPath);
                break;
            case json:
                this.json(idCardMap, successMonthMap, successMap, failReasonMap);
                break;
            default:
                break;
        }
        //核验数据
        if (StringUtils.isNotBlank(successFlag)) {
            this.checkResult(checkList, successMap, successMonthMap, failReasonMap, nameColName);
        } else {
            this.verifyData(checkList, idCardMap, successMonthMap, nameColName);
        }
        this.declareResult();
        log.info("核验完成");
    }

    private void excel(List<Map<String, Object>> dataList, Map<String, Map<String, Object>> idCardMap, Map<String, Object> successMonthMap, Map<String, Map<String, Object>> successMap, String filePath, String nameColName) {
        //csv自动转换xlsx
        filePath = fileUtil.csvToExcel(filePath);
        Workbook workbook = null;
        header = header == null ? 1 : header;
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            ExcelHelper excelHelper = ExcelHelper.readExcel(sheet, header);
            String[] headerRow = null;
            String[][] rows = excelHelper.getDatas();
            for (String[] row : rows) {
                if (null == headerRow) {
                    headerRow = excelHelper.getHeaders();
                    for (int i = 0; i < row.length; i++) {
                        if (headerRow[i].contains(".")) {
                            headerRow[i] = headerRow[i].substring(headerRow[i].lastIndexOf(".") + 1);
                        }
                    }
                    if (StringUtils.isBlank(nameColName) && nameColIndex != null) {
                        nameColName = headerRow[nameColIndex.getIndex()];
                    }
                }
                String idCard = row[idCardColIndex.getIndex()];
                if (StringUtils.isBlank(idCard) || Validator.hasChinese(idCard)) {
                    continue;
                }
                String name = null;
                if (nameColIndex != null) {
                    name = row[nameColIndex.getIndex()];
                }
                String successMonth = null;
                if (successMonthColIndex != null) {
                    successMonth = row[successMonthColIndex.getIndex()];
                }
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                ctx.setVariable("row", rowMap);
                if (StringUtils.isNotBlank(successFlag) && ELParser.parse(successFlag, ctx.getVariables(), Boolean.class)) {
                    successMap.put(idCard, rowMap);
                    if (StringUtils.isNotBlank(successMonth)) {
                        successMonthMap.put(idCard, Convert.parseDate(successMonth));
                    }
                } else {
                    if (StringUtils.isBlank(condition) || ELParser.parse(condition, ctx.getVariables(), Boolean.class)) {
                        idCardMap.put(idCard, rowMap);
                        if (StringUtils.isNotBlank(successMonth)) {
                            successMonthMap.put(idCard, Convert.parseDate(successMonth));
                        }
                        if (nameColIndex != null) {
                            Map<String, Object> dataMap = Maps.newHashMap();
                            for (String key : rowMap.keySet()) {
                                Object value = rowMap.get(key);
                                if (value != null) {
                                    if (idCard.equals(value.toString())) {
                                        dataMap.put("idCard", value);
                                    } else if (name.equals(value.toString())) {
                                        dataMap.put("empName", value);
                                    } else {
                                        dataMap.put("otherInfo." + key, value);
                                    }
                                } else {
                                    dataMap.put("otherInfo." + key, "");
                                }
                            }
                            dataList.add(dataMap);
                        }
                    }
                }
            }
            excelHelper.close();
            excelHelper.clear();
        } catch (Exception e) {
            log.error("Excel核验异常" + e.getMessage(), e);
            throw new RuntimeException("Excel核验异常" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    private void json(Map<String, Map<String, Object>> idCardMap, Map<String, Object> successMonthMap, Map<String, Map<String, Object>> successMap, Map<String, Object> failReasonMap) {
        for (Map<String, Object> map : jsonList) {
            ctx.setVariable("row", map);
            String idCard = String.valueOf(map.get(idCardFieldName));
            if (StringUtils.isBlank(idCard) || Validator.hasChinese(idCard)) {
                continue;
            }
            Object successMonth = map.get(successMonthFieldName);
            Object failReason = map.get(failReasonFieldName);
            if (StringUtils.isNotBlank(successFlag)) {
                if (ELParser.parse(successFlag, ctx.getVariables(), Boolean.class)) {
                    successMap.put(idCard, map);
                    if (successMonth != null && StringUtils.isNotBlank(successMonth.toString())) {
                        successMonthMap.put(idCard, Convert.parseDate(successMonth.toString()));
                    }
                }
                if (failReason != null && StringUtils.isNotBlank(failReason.toString())) {
                    failReasonMap.put(idCard, failReason.toString());
                }
            } else {
                if (StringUtils.isBlank(condition) || ELParser.parse(condition, ctx.getVariables(), Boolean.class)) {
                    idCardMap.put(idCard, map);
                    if (successMonth != null && StringUtils.isNotBlank(successMonth.toString())) {
                        successMonthMap.put(idCard, Convert.parseDate(successMonth.toString()));
                    }
                }
            }
        }
    }

    private void verifyData(List<RobotExecutionData> checkList, Map<String, Map<String, Object>> idCardMap, Map<String, Object> successMonthMap, String nameColName) {
        log.info("verifyData: idCardMap = " + JSON.toJSONString(idCardMap));
        log.info("verifyData: successMonthMap = " + JSON.toJSONString(successMonthMap));
        for (RobotExecutionData data : checkList) {
            Map<String, Object> valueMap = idCardMap.get(data.getIdCard());
            if (data.getDeclareType() == 1) {//1：增员
                Object successMonth = successMonthMap.get(data.getIdCard());
                int declareMonth = Integer.parseInt(data.getDeclareMonth().replace("-", ""));
                int nowMonth = Integer.parseInt(DateUtil.date().toString("yyyyMM"));
                int lastMonth = Integer.parseInt(DateUtil.offsetMonth(new Date(), -1).toString("yyyyMM"));
                if (declareMonth < nowMonth) {//是补缴年月
                    if (MapUtils.isNotEmpty(valueMap)) { //补缴成功
                        if (this.nameEquals(valueMap, nameColName, data)) {
                            this.updateFail(data.getId(), StringUtils.defaultIfBlank(data.getFailReason(), "") + " 姓名、证件号码匹配不上，请核实信息.");
                        } else {
                            if (successMonth == null || successMonth.toString().equals(data.getDeclareMonth())) {
                                this.updateSuccess(data.getId());
                            } else {
                                if (declareMonth < lastMonth) {//补缴多个月
                                    String failReason = "成功年月:" + successMonth + "|失败原因:校验失败 补缴超出月数请前台递交资料申报.";
                                    this.updateSuccess(data.getId(), failReason);
                                } else if (declareMonth == lastMonth) {//补缴一个月
                                    String failReason = "成功年月:" + successMonth + "|失败原因:" + data.getFailReason();
                                    this.updateSuccess(data.getId(), failReason);
                                }
                            }
                        }
                    } else {
                        this.updateFail(data.getId());
                    }
                } else {
                    if (MapUtils.isNotEmpty(valueMap)) {
                        if (this.nameEquals(valueMap, nameColName, data)) {
                            this.updateFail(data.getId(), StringUtils.defaultIfBlank(data.getFailReason(), "") + " 姓名、证件号码匹配不上，请核实信息.");
                        } else {
                            this.updateSuccess(data.getId());
                        }
                    } else {
                        this.updateFail(data.getId());
                    }
                }
            } else if (data.getDeclareType() == 2) {//2：减员
                if (MapUtils.isNotEmpty(valueMap)) {
                    if (this.nameEquals(valueMap, nameColName, data)) {
                        this.updateFail(data.getId(), StringUtils.defaultIfBlank(data.getFailReason(), "") + " 姓名、证件号码匹配不上，请核实信息.");
                    } else {
                        this.updateFail(data.getId());
                    }
                } else {
                    this.updateSuccess(data.getId());
                }
            }
        }
    }

    private void checkResult(List<RobotExecutionData> checkList, Map<String, Map<String, Object>> successMap, Map<String, Object> successMonthMap, Map<String, Object> failReasonMap, String nameColName) {
        for (RobotExecutionData data : checkList) {
            Object failReasonObj = failReasonMap.get(data.getIdCard());
            Map<String, Object> valueMap = successMap.get(data.getIdCard());
            if (data.getDeclareType() == 1) {//1：增员
                Object successMonth = successMonthMap.get(data.getIdCard());
                int declareMonth = Integer.parseInt(data.getDeclareMonth().replace("-", ""));
                int nowMonth = Integer.parseInt(DateUtil.date().toString("yyyyMM"));
                int lastMonth = Integer.parseInt(DateUtil.offsetMonth(new Date(), -1).toString("yyyyMM"));
                if (declareMonth < nowMonth) {//是补缴年月
                    if (MapUtils.isNotEmpty(valueMap) && successMonth != null) { //补缴成功
                        if (this.nameEquals(valueMap, nameColName, data)) {
                            this.updateFail(data.getId(), StringUtils.defaultIfBlank(data.getFailReason(), "") + " 姓名、证件号码匹配不上，请核实信息.");
                        } else {
                            if (successMonth.toString().equals(data.getDeclareMonth())) {
                                this.updateSuccess(data.getId());
                            } else {
                                if (declareMonth < lastMonth) {//补缴多个月
                                    String failReason = "成功年月:" + successMonth + "|失败原因:校验失败 补缴超出月数请前台递交资料申报.";
                                    this.updateSuccess(data.getId(), failReason);
                                } else if (declareMonth == lastMonth) {//补缴一个月
                                    String failReason = "成功年月:" + successMonth + "|失败原因:" + data.getFailReason();
                                    this.updateSuccess(data.getId(), failReason);
                                }
                            }
                        }
                    } else {
                        if (failReasonObj != null && StringUtils.isNotBlank(failReasonObj.toString())) {
                            this.updateFail(data.getId(), failReasonObj.toString());
                        } else {
                            this.updateFail(data.getId());
                        }
                    }
                } else {
                    if (MapUtils.isNotEmpty(valueMap)) {
                        if (this.nameEquals(valueMap, nameColName, data)) {
                            this.updateFail(data.getId(), StringUtils.defaultIfBlank(data.getFailReason(), "") + " 姓名、证件号码匹配不上，请核实信息.");
                        } else {
                            if (businessNode != null) {
                                this.updateDeclareing(data.getId(), "增员");
                            } else {
                                this.updateSuccess(data.getId());
                            }
                        }
                    } else {
                        if (businessNode != null) {
                            if (failReasonObj != null && StringUtils.isNotBlank(failReasonObj.toString())) {
                                this.updateFail(data.getId(), failReasonObj.toString());
                            } else {
                                if (StringUtils.isNotBlank(data.getFailReason())) {
                                    this.updateFail(data.getId());
                                } else {
                                    this.updateDeclareingFailReason(data.getId(), "网站审核中");
                                }
                            }
                        } else {
                            if (failReasonObj != null && StringUtils.isNotBlank(failReasonObj.toString())) {
                                this.updateFail(data.getId(), failReasonObj.toString());
                            } else {
                                this.updateFail(data.getId());
                            }
                        }
                    }
                }
            } else if (data.getDeclareType() == 2) {//2：减员
                if (MapUtils.isNotEmpty(valueMap)) {
                    if (this.nameEquals(valueMap, nameColName, data)) {
                        this.updateFail(data.getId(), StringUtils.defaultIfBlank(data.getFailReason(), "") + " 姓名、证件号码匹配不上，请核实信息.");
                    } else {
                        if (businessNode != null) {
                            this.updateDeclareing(data.getId(), "减员");
                        } else {
                            this.updateSuccess(data.getId());
                        }
                    }
                } else {
                    if (businessNode != null) {
                        if (failReasonObj != null && StringUtils.isNotBlank(failReasonObj.toString())) {
                            this.updateFail(data.getId(), failReasonObj.toString());
                        } else {
                            if (StringUtils.isNotBlank(data.getFailReason())) {
                                this.updateFail(data.getId());
                            } else {
                                this.updateDeclareingFailReason(data.getId(), "网站审核中");
                            }
                        }
                    } else {
                        if (failReasonObj != null && StringUtils.isNotBlank(failReasonObj.toString())) {
                            this.updateFail(data.getId(), failReasonObj.toString());
                        } else {
                            this.updateFail(data.getId());
                        }
                    }
                }
            }
        }
    }

    private void declareResult() {
        List<RobotExecutionData> checkList = this.getCheckList();
        if (CollectionUtils.isEmpty(checkList)) {
            return;
        }
        for (RobotExecutionData data : checkList) {
            this.updateDeclareing(data.getId());
        }
    }

    private void updateSuccess(Integer id) {
        String sql = "update robot_execution_data set robotExecStatus=2,declareStatus=4,failType=1,failReason=null where robotExecStatus=1 and id=?";
        log.info("updateSuccess sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, id);
    }

    private void updateSuccess(Integer id, String failReason) {
        String sql = "update robot_execution_data set robotExecStatus=2,declareStatus=4,failType=1,failReason=? where robotExecStatus=1 and id=?";
        log.info("updateSuccess sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, failReason, id);
    }

    private void updateFail(Integer id) {
        String sql = "update robot_execution_data set robotExecStatus=2,declareStatus=5,failType=1 where robotExecStatus=1 and failReason is not null and id=?";
        log.info("updateFail sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, id);
    }

    private void updateFail(Integer id, String failReason) {
        String sql = "update robot_execution_data set robotExecStatus=2,declareStatus=5,failType=1,failReason=? where robotExecStatus=1 and id=?";
        log.info("updateFail sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, failReason, id);
    }

    private void updateDeclareing(Integer id) {
        String sql = "update robot_execution_data set robotExecStatus=2,declareStatus=2 where robotExecStatus=1 and id=?";
        log.info("updateDeclareing sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, id);
    }

    private void updateDeclareing(Integer id, String declareName) {
        String tplTypeName = TplTypeCodeEnum.getNameByCode(ctx.get("tplTypeCode"));
        String sql = "update robot_execution_data set robotExecStatus =2,declareStatus =2,failType=1,failReason=?,operationType=?,nodeComment=? WHERE id = ?";
        log.info("updateDeclareing sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, tplTypeName + declareName + "已申报", businessNode.getCode(), businessNode.getName(), id);
    }

    private void updateDeclareingFailReason(Integer id, String failReason) {
        String sql = "update robot_execution_data set robotExecStatus =2,declareStatus =2,failType=1,failReason=? WHERE id = ?";
        log.info("updateDeclareingFailReason sql：" + sql + ",id:" + id);
        jdbcTemplate.update(sql, failReason, id);
    }

    private void saveRegister(List<Map<String, Object>> dataList, String filePath) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        //上传文件
        String fileId = robotCommonService.fileUpload(new File(filePath));
        //在册参数
        Map<String, Object> registerMap = Maps.newHashMap();
        registerMap.put("addrName", ctx.get("addrName"));
        registerMap.put("businessType", Convert.getBusinessType(ctx.get("businessType")));
        registerMap.put("accountNumber", ctx.getAccountNumber());
        registerMap.put("tplTypeCode", ctx.get("tplTypeCode"));
        registerMap.put("dataMonth", DateUtil.date().toString("yyyy-MM"));
        registerMap.put("registerNumber", dataList.size());
        registerMap.put("details", dataList);
        Map<String, Object> fileMap = Maps.newHashMap();
        fileMap.put("fileId", fileId);
        registerMap.put("files", Lists.newArrayList(fileMap));

        robotCommonService.saveRegister(registerMap);
        log.info("保存在册完成,在册人数：" + dataList.size());
    }

    public List<RobotExecutionData> getCheckList() {
        String addrName = ctx.get("addrName");
        Integer businessType = Convert.getBusinessType(ctx.get("businessType"));
        Integer declareType = ctx.get("declareType");
        String accountNumber = ctx.getAccountNumber();
        String tplTypeCode = ctx.get("tplTypeCode");

        String sql = "select id,name,idCard,declareMonth,failReason,declareType,operationType,extend,createTime,dataType from robot_execution_data WHERE sync = 0 and robotExecStatus = 1 and declareStatus in (1,2) "
                + "and addrName=? and businessType=? and declareType=? and accountNumber=? and tplTypeCode=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RobotExecutionData.class), addrName, businessType, declareType, accountNumber, tplTypeCode);
    }

    private boolean nameEquals(Map<String, Object> valueMap, String nameColName, RobotExecutionData data) {
        if (DeclareVerifyType.json.equals(DeclareVerifyType.valueOf(verifyType))) {
            if (StringUtils.isNotBlank(nameFieldName) && valueMap.get(nameFieldName) != null && !valueMap.get(nameFieldName).toString().equals(data.getName())) {
                log.info("身份证：" + data.getIdCard() + "，申报姓名：" + data.getName() + ",网站姓名：" + valueMap.get(nameFieldName) + ",身份证与姓名不一致。");
                return true;
            }
            return false;
        }
        if (DeclareVerifyType.excel.equals(DeclareVerifyType.valueOf(verifyType)) || DeclareVerifyType.customPath.equals(DeclareVerifyType.valueOf(verifyType))) {
            if (nameColIndex != null && nameColName != null && valueMap.get(nameColName) != null && !valueMap.get(nameColName).toString().equals(data.getName())) {
                log.info("身份证：" + data.getIdCard() + "，申报姓名：" + data.getName() + ",网站姓名：" + valueMap.get(nameColName) + ",身份证与姓名不一致。");
                return true;
            }
            return false;
        }
        return false;
    }
}
