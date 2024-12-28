package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.DeclareMonth;
import com.seebon.rpa.context.enums.OfferLabelType;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.context.enums.WebsiteVerify;
import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.entity.saas.vo.DeclareTempColInfoDTO;
import com.seebon.rpa.entity.saas.vo.DeclareTempInfoDTO;
import com.seebon.rpa.entity.saas.vo.OfferPropertyVO;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.enums.DeclareTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ActionUtils
@RobotAction(name = "生成核验数据", targetType = NoneTarget.class, comment = "生成核验数据")
public class GenerateVerify extends AbstractAction {

    @Setter
    @ActionArgs(value = "申报年月", dict = DeclareMonth.class, required = true)
    @Conditions({
            "lastMonth:limit,businessNode,websiteVerify,intervalTime,businessNode",
            "currentMonth:limit,businessNode,websiteVerify,intervalTime,businessNode",
            "nextMonth:limit,businessNode,websiteVerify,intervalTime,businessNode",
            "excelColumn:limit,excelColumn,businessNode,websiteVerify,intervalTime,businessNode",
            "customMonth:limit,customMonth,businessNode,websiteVerify,intervalTime,businessNode"
    })
    private String declareMonth;

    @Setter
    @ActionArgs(value = "指定Excel列", dict = WebExcelNumLine.class, required = true)
    private WebExcelNumLine excelColumn;

    @Setter
    @ActionArgs(value = "自定义", style = DynamicFieldType.text, required = true)
    private String customMonth;

    @Setter
    @ActionArgs(value = "需要网站审核", dict = WebsiteVerify.class)
    private String websiteVerify;

    @Setter
    @ActionArgs(value = "间隔时间", style = DynamicFieldType.text, comment = "间隔时间，单位小时")
    private String intervalTime;

    @Setter
    @ActionArgs(value = "业务节点", dict = OfferLabelType.class, comment = "可以为空")
    private OfferLabelType businessNode;

    @Autowired
    private RobotCommonService robotCommonService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DeclareVerify declareVerify;

    @Override
    public void run() {
        List<RobotExecutionData> list = declareVerify.getCheckList();
        try {
            log.info("h2 checkList =" + JSON.toJSONString(list));
            List<String> idCardList = list.stream().map(vo -> vo.getIdCard()).distinct().collect(Collectors.toList());
            if (StringUtils.isNotBlank(websiteVerify) && WebsiteVerify.TRUE.equals(WebsiteVerify.valueOf(websiteVerify))) {
                DeclareTempInfoDTO declareTempInfo = this.getDeclareTempInfo();
                String checkPath = this.downloadOffer(declareTempInfo);
                List<OfferPropertyVO> offerPropertyList = this.readExcel(declareTempInfo, idCardList, checkPath);
                this.saveDataToH2(offerPropertyList);
                List<RobotExecutionData> verifyList = declareVerify.getCheckList();
                if (CollectionUtils.isNotEmpty(verifyList)) {
                    for (RobotExecutionData verify : verifyList) {
                        if (idCardList.contains(verify.getIdCard())) {
                            continue;
                        }
                        list.add(verify);
                    }
                }
            }
            ctx.setVariable(RobotConstant.CHECK_LIST, new ObjectMapper().convertValue(list, new TypeReference<List<Map<String, Object>>>() {
            }));
            log.info("verifyList =" + JSON.toJSONString(list));
        } catch (Exception e) {
            log.error("核验生成异常" + e.getMessage(), e);
        }
    }

    public List<OfferPropertyVO> readExcel(DeclareTempInfoDTO declareInfo, List<String> idCardList, String checkPath) throws Exception {
        DeclareTempColInfoDTO nameTemp = Convert.getByFieldCode(declareInfo, "10000001"); // 姓名
        DeclareTempColInfoDTO idCardTemp = Convert.getByFieldCode(declareInfo, "10000003"); // 证件号码
        DeclareTempColInfoDTO socialNumberTemp = Convert.getByFieldCode(declareInfo, "20000008"); // 个人社保号
        DeclareTempColInfoDTO accfundNumberTemp = Convert.getByFieldCode(declareInfo, "20000009"); // 个人公积金号
        DeclareTempColInfoDTO computerNumberTemp = Convert.getByFieldCode(declareInfo, "20000010"); // 个人电脑编号
        DeclareTempColInfoDTO bjKsTemp = Convert.getByFieldCode(declareInfo, "20000003"); // 补缴开始年月
        DeclareTempColInfoDTO bjJsTemp = Convert.getByFieldCode(declareInfo, "20000004"); // 补缴结束年月

        Workbook workbook = WorkbookFactory.create(new File(checkPath));
        Sheet sheet = workbook.getSheetAt(declareInfo.getSheetIndex());
        ExcelHelper excelHelper = ExcelHelper.readExcel(sheet, declareInfo.getHeardIndex() + 1);
        String[][] rows = excelHelper.getDatas();
        List<OfferPropertyVO> list = Lists.newArrayList();
        String[] excelHeaderRow = excelHelper.getHeaders();
        for (int i = 0; i < excelHeaderRow.length; i++) {
            if (excelHeaderRow[i].contains(".")) {
                excelHeaderRow[i] = excelHeaderRow[i].substring(excelHeaderRow[i].lastIndexOf(".") + 1);
            }
        }
        String[] headerRow = Convert.getHeaderRow(declareInfo, excelHeaderRow);
        for (String[] row : rows) {
            String name = row[nameTemp.getColIndex()];
            String idCard = row[idCardTemp.getColIndex()];
            if (StringUtils.isBlank(idCard) || idCardList.contains(idCard) || Validator.hasChinese(idCard)) {
                continue;
            }
            LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
            Convert.fetchMap(headerRow, row, rowMap);
            rowMap.put(RobotConstant.ID_CARD_KEY_NAME, idCard);
            System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
            ctx.setVariable("row", rowMap);
            list.add(new OfferPropertyVO(idCard, this.getDeclareMonth(row, bjKsTemp, bjJsTemp), name));
        }
        ctx.setVariable(RobotConstant.SHEET_INDEX, declareInfo.getSheetIndex());
        ctx.setVariable(RobotConstant.HEARD_INDEX, declareInfo.getHeardIndex());
        ctx.setVariable(RobotConstant.ID_CARD_COL_INDEX, idCardTemp.getColIndex());
        ctx.setVariable(RobotConstant.ID_CARD_COL_NAME, headerRow[idCardTemp.getColIndex()]);
        if (socialNumberTemp != null) {
            ctx.setVariable(RobotConstant.SOCIAL_NUMBER_COL_INDEX, socialNumberTemp.getColIndex());
            String socialNumber = headerRow[socialNumberTemp.getColIndex()];
            if (StringUtils.isNotBlank(socialNumber)) {
                ctx.setVariable(RobotConstant.SOCIAL_NUMBER_COL_NAME, socialNumber);
            }
        }
        if (accfundNumberTemp != null) {
            ctx.setVariable(RobotConstant.ACCFUND_NUMBER_COL_INDEX, accfundNumberTemp.getColIndex());
            String accfundNumber = headerRow[accfundNumberTemp.getColIndex()];
            if (StringUtils.isNotBlank(accfundNumber)) {
                ctx.setVariable(RobotConstant.ACCFUND_NUMBER_COL_NAME, accfundNumber);
            }
        }
        if (computerNumberTemp != null) {
            ctx.setVariable(RobotConstant.COMPUTER_NUMBER_COL_INDEX, computerNumberTemp.getColIndex());
            String computerNumber = headerRow[computerNumberTemp.getColIndex()];
            if (StringUtils.isNotBlank(computerNumber)) {
                ctx.setVariable(RobotConstant.COMPUTER_NUMBER_COL_NAME, computerNumber);
            }
        }
        ctx.remove("row");
        excelHelper.close();
        excelHelper.clear();
        workbook.close();
        return list;
    }

    private void saveDataToH2(List<OfferPropertyVO> dataList) {
        String executionCode = ctx.get(RobotConstant.EXECUTION_CODE_KEY);
        String batchCode = ctx.get(RobotConstant.INST_ID);
        String machineCode = ctx.get("machineCode");
        Integer clientId = ctx.get("clientId");
        String accountNumber = ctx.getAccountNumber();
        String addrName = ctx.get("addrName");
        Integer declareType = ctx.get("declareType");
        String tplTypeCode = ctx.get("tplTypeCode");
        Integer businessType = ctx.get("businessTypeInt");
        for (OfferPropertyVO vo : dataList) {
            String uuid = UUIDGenerator.uuidStringWithoutLine();
            if (businessNode != null) {
                String sql = String.format("INSERT INTO `robot_execution_data` (`uuid`,`client_id`,`machine_code`,`execution_code`,`batch_code`,`addrName`,`businessType`," +
                                "`declareType`,`accountNumber`,`name`,`idCard`,`declareMonth`,`tplTypeCode`,`operationType`,`declareStatus`,`robotExecStatus`,sync,`createTime`)\n" +
                                "VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',2,1,0,NOW());"
                        , uuid, clientId, machineCode, executionCode, batchCode, addrName, businessType, declareType, accountNumber,
                        vo.getName(), vo.getIdCard(), vo.getInsureDate(), tplTypeCode, businessNode.getCode());
                jdbcTemplate.update(sql);
            } else {
                String sql = String.format("INSERT INTO `robot_execution_data` (`uuid`,`client_id`,`machine_code`,`execution_code`,`batch_code`,`addrName`,`businessType`," +
                                "`declareType`,`accountNumber`,`name`,`idCard`,`declareMonth`,`tplTypeCode`,`operationType`,`declareStatus`,`robotExecStatus`,sync,`createTime`)\n" +
                                "VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',2,1,0,NOW());"
                        , uuid, clientId, machineCode, executionCode, batchCode, addrName, businessType, declareType, accountNumber,
                        vo.getName(), vo.getIdCard(), vo.getInsureDate(), tplTypeCode, OfferLabelType.check.getCode());
                jdbcTemplate.update(sql);
            }
        }
    }

    private String downloadOffer(DeclareTempInfoDTO declareTempInfo) throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("addrName", ctx.get("addrName"));
        params.put("businessType", ctx.get("businessTypeInt"));
        params.put("declareType", ctx.get("declareType"));
        params.put("declareStatus", "1,2");
        params.put("tplTypeCode", ctx.get("tplTypeCode"));
        params.put("accountNumber", ctx.getAccountNumber());
        params.put("robotExecCode", ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY));
        params.put("tbDate", DateUtil.date().toString("yyyy-MM"));
        if (businessNode == null) {
            params.put("operationType", OfferLabelType.check.getCode());
        } else {
            params.put("operationType", businessNode.getCode());
        }
        String filePath = ctx.get(RobotConstant.FILE_PATH);
        String checkPath = StringUtils.substringBeforeLast(filePath, ".") + "_check_" + UUIDGenerator.uuidStringWithoutLine() + declareTempInfo.getFileSuffix();
        log.info("核验报盘文件地址：checkPath=" + checkPath);
        CloseableHttpResponse response = robotCommonService.offerDownloadUrl(params);
        Convert.respToFile(response, checkPath);
        return checkPath;
    }

    public DeclareTempInfoDTO getDeclareTempInfo() {
        Map<String, Object> body = Maps.newHashMap();
        body.put("addrName", ctx.get("addrName"));
        body.put("businessType", ctx.get("businessTypeInt"));
        body.put("declareType", ctx.get("declareType"));
        body.put("declareStatus", "1,2");
        body.put("tplTypeCode", ctx.get("tplTypeCode"));
        body.put("accountNumber", ctx.getAccountNumber());
        if (businessNode == null) {
            body.put("operationType", OfferLabelType.check.getCode());
        } else {
            if (!OfferLabelType.appRenewal.getCode().equals(businessNode.getCode())) {
                body.put("operationType", businessNode.getCode());
            }
        }
        String respBody = robotCommonService.declareTempInfoUrl(body);
        JSONObject json = JSON.parseObject(respBody);
        return JSON.parseObject(json.getString("data"), DeclareTempInfoDTO.class);
    }

    private String getDeclareMonth(String[] row, DeclareTempColInfoDTO bjKsTemp, DeclareTempColInfoDTO bjJsTemp) {
        if (DeclareMonth.lastMonth.equals(DeclareMonth.valueOf(declareMonth))) {
            return DateUtil.format(DateUtil.offsetMonth(new Date(), -1), "yyyy-MM");
        }
        if (DeclareMonth.currentMonth.equals(DeclareMonth.valueOf(declareMonth))) {
            return DateUtil.format(new Date(), "yyyy-MM");
        }
        if (DeclareMonth.nextMonth.equals(DeclareMonth.valueOf(declareMonth))) {
            return DateUtil.format(DateUtil.offsetMonth(new Date(), 1), "yyyy-MM");
        }
        if (DeclareMonth.excelColumn.equals(DeclareMonth.valueOf(declareMonth))) {
            Integer declareType = ctx.get("declareType");
            if (DeclareTypeEnum.BJ.getCode().equals(declareType) && bjKsTemp != null && bjJsTemp != null) { // 补缴
                String bjKsStr = Convert.parseDate(row[bjKsTemp.getColIndex()]);
                String bjJsStr = Convert.parseDate(row[bjJsTemp.getColIndex()]);
                return String.format("%s,%s", bjKsStr, bjJsStr);
            } else {
                return Convert.parseDate(row[excelColumn.getIndex()]);
            }
        }
        if (DeclareMonth.customMonth.equals(DeclareMonth.valueOf(declareMonth))) {
            return ELParser.parse(customMonth, ctx.getVariables(), String.class);
        }
        return "";
    }
}
