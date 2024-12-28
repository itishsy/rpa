package com.seebon.rpa.actions.impl.offer;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.*;
import com.seebon.rpa.context.runtime.FinishRuntimeException;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.entity.saas.vo.DeclareTempColInfoDTO;
import com.seebon.rpa.entity.saas.vo.DeclareTempInfoDTO;
import com.seebon.rpa.entity.saas.vo.OfferPropertyVO;
import com.seebon.rpa.service.RobotCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LY
 * @date 2023/10/26 16:17
 */

@Slf4j
@ActionUtils
@RobotAction(name = "拉取报盘文件", targetType = NoneTarget.class, comment = "拉取报盘文件")
public class PullOffer extends AbstractAction {

    @ActionArgs(value = "业务类型", dict = BusinessType.class, required = true)
    @Conditions({"allInsurance:operateType",
            "social: operateType",
            "medicare: operateType",
            "singleWorkInjury: operateType",
            "workInjury: operateType",
            "filingSystem: filingSystemLabel",
            "accfund: accfundOperateType"})
    private BusinessType businessType;

    @ActionArgs(value = "社保操作类型", dict = OfferOperateType.class, required = true)
    @Conditions({"supplementing:businessType,supplementing",
            "subtract: businessType,socialReductionType",
            "increase: businessType,socialAddType",
            "baseAdjust: businessType,changeBaseLabel",
            "doCheck: businessType"})
    private OfferOperateType operateType;

    @ActionArgs(value = "公积金操作类型", dict = OfferOperateType.class, required = true)
    @Conditions({"supplementing:businessType,supplementing",
            "subtract: businessType,accfundReductionType",
            "increase: businessType,accfundAddType",
            "baseAdjust: businessType,changeBaseLabel",
            "doCheck: businessType"})
    private OfferOperateType accfundOperateType;

    @ActionArgs(value = "标签", dict = OfferLabelType.class, required = true)
    private OfferLabelType offerLabelType;

    @ActionArgs(value = "备案标签", dict = OfferLabelIncreaseFilingType.class, required = true)
    private OfferLabelIncreaseFilingType filingSystemLabel;

    @ActionArgs(value = "补缴标签", dict = OfferLabelSupplementing.class, required = true)
    private OfferLabelSupplementing supplementing;

    @ActionArgs(value = "调基标签", dict = OfferLabelChangeBase.class, required = true)
    private OfferLabelChangeBase changeBaseLabel;

    @ActionArgs(value = "社保减员标签", dict = OfferLabelSocialReductionType.class, required = true)
    private OfferLabelSocialReductionType socialReductionType;

    @ActionArgs(value = "社保增员标签", dict = OfferLabelSocialAddType.class, required = true)
    private OfferLabelSocialAddType socialAddType;

    @ActionArgs(value = "公积金减员标签", dict = OfferLabelAccfundReductionType.class, required = true)
    private OfferLabelAccfundReductionType accfundReductionType;

    @ActionArgs(value = "公积金增员标签", dict = OfferLabelAccfundAddType.class, required = true)
    private OfferLabelAccfundAddType accfundAddType;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RobotCommonService robotCommonService;

    @Override
    public void run() {
        // 第一步下载报盘
        String addrName = ctx.get("addrName");
        String accountNumber = "";
        Integer operateTypeNum = null;
        String operateTypeName = "";
        String path = "";
        String changeTypeCode = "";
        ctx.setVariable("businessIntType", businessType.getType());
        ctx.setVariable("businessTplCode", businessType.getTplCode());
        boolean isCheckOperate = false;
        if (businessType.getType() == 1) {
            if (operateType == OfferOperateType.doCheck) {
                isCheckOperate = true;
            } else {
                if (operateType.getType() == 1) {
                    changeTypeCode = socialAddType.getCode();
                    operateTypeName = socialAddType.getName();
                } else if (operateType.getType() == 2) {
                    changeTypeCode = socialReductionType.getCode();
                    operateTypeName = socialReductionType.getName();
                } else if (operateType.getType() == 3) {
                    changeTypeCode = changeBaseLabel.getCode();
                    operateTypeName = changeBaseLabel.getName();
                } else {
                    changeTypeCode = supplementing.getCode();
                    operateTypeName = supplementing.getName();
                }
            }
            ctx.setVariable("operateType", String.valueOf(operateType.getType()));
            operateTypeNum = operateType.getType();
            accountNumber = ctx.getVariable("socialNumber").toString();
        } else {
            if (accfundOperateType == OfferOperateType.doCheck) {
                isCheckOperate = true;
            } else {
                if (accfundOperateType.getType() == 1) {
                    changeTypeCode = accfundAddType.getCode();
                    operateTypeName = accfundAddType.getName();
                } else if (accfundOperateType.getType() == 2) {
                    changeTypeCode = accfundReductionType.getCode();
                    operateTypeName = accfundReductionType.getName();
                } else if (accfundOperateType.getType() == 3) {
                    changeTypeCode = changeBaseLabel.getCode();
                    operateTypeName = changeBaseLabel.getName();
                } else {
                    changeTypeCode = supplementing.getCode();
                    operateTypeName = supplementing.getName();
                }
            }
            ctx.setVariable("operateType", String.valueOf(accfundOperateType.getType()));
            operateTypeNum = accfundOperateType.getType();
            accountNumber = ctx.getVariable("accfundNumber").toString();
        }
        if (isCheckOperate) {
            Map<Integer, List<Map>> checkMap = getCheckMap();
            this.saveCheckList(addrName, accountNumber, operateTypeNum, changeTypeCode, checkMap);
            this.fillCheckList(addrName, accountNumber);
            return;
        }
        try {
            path = this.downloadOffer(addrName, accountNumber, operateTypeNum, changeTypeCode, operateTypeName);
            ctx.setVariable("offerFilePath", path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("报盘文件路径：{}", ctx.getVariable("offerFilePath").toString());
        // 获取报盘信息字段
        DeclareTempInfoDTO declareTempInfo = this.getDeclareTempInfo(addrName, accountNumber, operateTypeNum, changeTypeCode);
        // 保存报盘映射信息
        ctx.setVariable("declareTempInfo", declareTempInfo);
        //  读取报盘Excel （身份证，投保年月）
        List<OfferPropertyVO> offerPropertyList = this.readExcelInfo(new File(path), declareTempInfo, changeTypeCode);
        // 写入 H2
        this.saveDataToH2(offerPropertyList, accountNumber, addrName, String.valueOf(operateTypeNum), changeTypeCode);
    }

    private void fillCheckList(String addrName, String accountNumber) {
        String sql = String.format("SELECT `id`,`name`,`idCard`,`declareMonth`,`declareType` FROM `robot_execution_data` " +
                        "WHERE robotExecStatus = 3" +
                        "and addrName='%s' " +
                        "and businessType='%s' " +
                        "and accountNumber='%s'" +
                        "and tplTypeCode='%s'", addrName, businessType.getType()
                , accountNumber, businessType.getTplCode());

        RowMapper<Map<String, Object>> rowMapper = (resultSet, i) -> {
            Map<String, Object> map = new HashMap<>();
            try {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int j = 1; j <= columnCount; j++) {
                    String columnName = metaData.getColumnLabel(j);
                    Object columnValue = resultSet.getObject(columnName);
                    map.put(columnName, columnValue);
                }
            } catch (SQLException e) {
                log.error("【Exception】", e);
            }
            return map;
        };
        List<Map<String, Object>> list = jdbcTemplate.query(sql, new Object[]{}, rowMapper);
        if (list.isEmpty()) {
            log.info("没有待核验数据");
            throw new FinishRuntimeException("暂无核验数据");
        }
        ctx.setVariable("checkList", list);
        log.info("checkList: {}", list);

    }

    private void saveDataToH2(List<OfferPropertyVO> dataList, String accountNumber, String addrName, String operateType, String changeTypeCode) {
        String batchCode = UUID.randomUUID().toString().replaceAll("-", "");
        String machineCode = ctx.get("machineCode");
        Integer clientId = ctx.get("clientId");
        for (OfferPropertyVO vo : dataList) {
            String executionCode = UUID.randomUUID().toString().replaceAll("-", "");
            String sql = String.format("INSERT INTO `robot_execution_data` " +
                            "(`execution_code`,`machine_code`,`uuid`,`batch_code`,`addrName`," +
                            "`businessType`,`declareType`,`accountNumber`," +
                            "`name`,`idCard`,`declareMonth`,`tplTypeCode`," +
                            "`robotExecStatus`,`sync`,`createTime`,flagType,`client_id`,`operationType`)\n" +
                            "VALUES(" +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s',  0, 0,  NOW(),1,'%s','%s');"
                    , executionCode, machineCode, executionCode, batchCode, addrName, businessType.getType(), operateType, accountNumber,
                    vo.getName(), vo.getIdCard(), vo.getInsureDate(), businessType.getTplCode(), clientId, changeTypeCode);
            jdbcTemplate.update(sql);
        }
    }

    private List<OfferPropertyVO> readExcelInfo(File file, DeclareTempInfoDTO declareTempInfo, String changeTypeCode) {
        ExcelReader reader = ExcelUtil.getReader(file, declareTempInfo.getSheetIndex());
        Optional<Integer> personNoColumnIndex = declareTempInfo.getCols().stream().filter(item -> Arrays.asList("20000008", "20000009", "20000010").contains(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getColIndex);
        Optional<Integer> insureDate = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "20000002".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getColIndex);
        Optional<Integer> idCardColumnIndex = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "10000003".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getColIndex);
        Optional<Integer> nameIndex = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "10000001".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getColIndex);
        if (!(idCardColumnIndex.isPresent() && nameIndex.isPresent())) {
            throw new RobotInterruptException("无法解析到报盘文件中的身份证、姓名列");
        }

        List<OfferPropertyVO> list = new ArrayList<>();
        List<List<Object>> readList = reader.read(declareTempInfo.getHeardIndex() + 1);
        List<Object> idCardList = readList.stream().map(row -> row.get(idCardColumnIndex.get())).collect(Collectors.toList());
        List<Object> insureDateList = Collections.emptyList();
        if ("1014".equals(changeTypeCode)) {
            Optional<Integer> startDateIndex = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "20000003".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getColIndex);
            Optional<Integer> endDateIndex = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "20000004".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getColIndex);
            if (startDateIndex.isPresent()) {
                insureDateList = endDateIndex.<List<Object>>map(integer -> readList.stream().map(item -> {
                    if (item.get(integer) != null) {
                        if (item.get(startDateIndex.get()).toString().equals(item.get(integer).toString())) {
                            return parseInsureDate(item.get(startDateIndex.get()).toString());
                        }
                        return parseInsureDate(item.get(startDateIndex.get()).toString()) + "," + parseInsureDate(item.get(integer).toString());
                    } else {
                        return item.get(startDateIndex.get()).toString();
                    }
                }).collect(Collectors.toList())).orElse(Collections.emptyList());
            }
        } else {
            insureDateList = insureDate.map(integer -> readList.stream().map(item -> item.get(integer)).collect(Collectors.toList())).orElse(Collections.emptyList());
        }
        List<Object> nameList = readList.stream().map(row -> row.get(nameIndex.get())).collect(Collectors.toList());
        List<Object> personNoList = personNoColumnIndex.map(integer -> readList.stream().map(item -> item.get(integer)).collect(Collectors.toList())).orElse(Collections.emptyList());
        if (idCardList.isEmpty() || nameList.isEmpty()) {
            log.info("报盘文件数据为空");
            throw new FinishRuntimeException("报盘文件数据为空");
        }
        if (idCardList.contains("") || nameList.contains("")) {
            throw new BusinessException("报盘文件身份证、姓名列存在空值");
        }
        for (int i = 0; i < idCardList.size(); i++) {
            String year = "";
            if (insureDateList.isEmpty()) {
                year = DateUtil.date().toString("yyyy-MM");
            } else {
                if (insureDateList.get(i).toString().contains(",")) {
                    year = insureDateList.get(i).toString();
                } else {
                    year = parseInsureDate(insureDateList.get(i).toString());
                }
            }
            list.add(new OfferPropertyVO(String.valueOf(idCardList.get(i)), year, String.valueOf(nameList.get(i))));
        }

        if (!personNoList.isEmpty()) {
            HashMap<String, Object> personNoIdCardMap = new HashMap<>();
            for (int i = 0; i < personNoList.size(); i++) {
                personNoIdCardMap.put(String.valueOf(personNoList.get(i)), String.valueOf(idCardList.get(i)));
            }
            ctx.setVariable("personNoIdCardMap", personNoIdCardMap);
        }
        List<String> offerIdCardList = list.stream().map(OfferPropertyVO::getIdCard).collect(Collectors.toList());
        log.info("setOfferIdCardList: {}", offerIdCardList);
        ctx.setVariable("offerIdCardList", offerIdCardList);
        return list;
    }

    //    String date1 = "202310";
    //    String date2 = "2023-10-01";
    //    String date3 = "2023-10";
    //    String date4 = "20231001";
    //    String date5 = "2023/10/01";
    //    String date6 = "2023/10";
    public String parseInsureDate(String date) {
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

    private Map<Integer, List<Map>> getCheckMap() {
        Map<String, Object> body = Maps.newHashMap();
        body.put("addrName", ctx.getVariable("addrName"));
        body.put("businessType", businessType.getType());
        body.put("accountNumber", ctx.getAccountNumber());
        body.put("tplTypeCode", businessType.getTplCode());
        body.put("tbDate", DateUtil.date().toString("yyyy-MM"));
        body.put("declareTypes", "1,2");
        body.put("declareStatus", "1,2");
        body.put("operationType", "1015");
        Map<Integer, List<Map>> checkMap = Maps.newHashMap();
        try {
            String respStr = robotCommonService.getDeclareDataUrl(body);
            log.info(respStr);
            JSONObject jsonObject = JSONObject.parseObject(respStr);
            if (jsonObject.getInteger("code") != 200) {
                throw new BusinessException(jsonObject.getString("message"));
            }
            JSONObject data = (JSONObject) jsonObject.get("data");
            if (data.get("1") != null) {
                checkMap.put(1, JSONObject.parseArray(data.getString("1"), Map.class));
            }
            if (data.get("2") != null) {
                checkMap.put(2, JSONObject.parseArray(data.getString("2"), Map.class));
            }
        } catch (Exception e) {
            log.error("上传在册明细失败: {}", e.getMessage());
            throw new RobotInterruptException(e.getMessage());
        }
        if (checkMap.isEmpty()) {
            log.info("没有待核验数据");
            throw new FinishRuntimeException("暂无核验数据");
        }
        return checkMap;
    }

    private void saveCheckList(String addrName, String accountNumber, Integer changeType, String operationTypeCode, Map<Integer, List<Map>> checkMap) {
        List<Map> addMap = checkMap.get(1);
        if (addMap != null && !addMap.isEmpty()) {
            DeclareTempInfoDTO declareTempInfo = getDeclareTempInfo(addrName, accountNumber, 1, "1001");
            Optional<String> insureDateStr = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "20000002".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getFieldName);
            Optional<String> idCardStr = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "10000003".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getFieldName);
            Optional<String> nameStr = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "10000001".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getFieldName);
            List<OfferPropertyVO> addList = new ArrayList<>();
            if (!idCardStr.isPresent() || !nameStr.isPresent()) {
                throw new RobotInterruptException("解析模板字段错误，姓名与身份证号必须存在");
            }
            for (Map map : addMap) {
                addList.add(new OfferPropertyVO(map.get(idCardStr.get()).toString(), insureDateStr.isPresent() ? parseInsureDate(map.get(insureDateStr.get()).toString()) : DateUtil.date().toString("yyyy-MM"), map.get(nameStr.get()).toString()));
            }
            if (!addList.isEmpty()) {
                saveCheckToH2(addList, addrName, "1");
            }
        }
        List<Map> reduceMap = checkMap.get(2);
        if (reduceMap != null && !reduceMap.isEmpty()) {
            DeclareTempInfoDTO declareTempInfo = getDeclareTempInfo(addrName, accountNumber, 2, "1008");
            Optional<String> insureDateStr = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "20000002".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getFieldName);
            Optional<String> idCardStr = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "10000003".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getFieldName);
            Optional<String> nameStr = declareTempInfo.getCols().stream().filter(item -> item.getFieldCode() != null && "10000001".equals(item.getFieldCode())).findFirst().map(DeclareTempColInfoDTO::getFieldName);
            List<OfferPropertyVO> addList = new ArrayList<>();
            if (!idCardStr.isPresent() || !nameStr.isPresent()) {
                throw new RobotInterruptException("解析模板字段错误，姓名与身份证号必须存在");
            }
            for (Map map : reduceMap) {
                addList.add(new OfferPropertyVO(map.get(idCardStr.get()).toString(), insureDateStr.isPresent() ? parseInsureDate(map.get(idCardStr.get()).toString()) : DateUtil.date().toString("yyyy-MM"), map.get(nameStr.get()).toString()));
            }
            if (!addList.isEmpty()) {
                saveCheckToH2(addList, addrName, "2");
            }
        }
    }

    private void saveCheckToH2(List<OfferPropertyVO> dataList, String addrName, String declareType) {
        String batchCode = UUID.randomUUID().toString().replaceAll("-", "");
        String machineCode = ctx.get("machineCode");
        Integer clientId = ctx.get("clientId");
        for (OfferPropertyVO vo : dataList) {
            String executionCode = UUID.randomUUID().toString().replaceAll("-", "");
            String sql = String.format("INSERT INTO `robot_execution_data` " +
                            "(`execution_code`,`machine_code`,`uuid`,`batch_code`,`addrName`," +
                            "`businessType`,`declareType`,`accountNumber`," +
                            "`name`,`idCard`,`declareMonth`,`tplTypeCode`," +
                            "`robotExecStatus`,`sync`,`createTime`,flagType,`client_id`,`operationType`)\n" +
                            "VALUES(" +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s', " +
                            " '%s',  3, 0,  NOW(),1,'%s','1015');"
                    , executionCode, machineCode, executionCode, batchCode, addrName, businessType.getType(), declareType, ctx.getAccountNumber(),
                    vo.getName(), vo.getIdCard(), vo.getInsureDate(), businessType.getTplCode(), clientId);
            jdbcTemplate.update(sql);
        }
    }

    private String downloadOffer(String addrName, String accountNumber, Integer declareType, String changeTypeCode, String changeTypeName) throws IOException {
        Map<String, Object> body = Maps.newHashMap();
        body.put("addrName", addrName);
        body.put("businessType", businessType.getType());
        body.put("declareType", declareType);
        body.put("accountNumber", accountNumber);
        body.put("declareStatus", "1,2");
        body.put("operationType", changeTypeCode);
        body.put("tplTypeCode", businessType.getTplCode());
        body.put("tbDate", DateUtil.date().toString("yyyy-MM"));
        if (Arrays.asList(1, 2).contains(declareType)) {
            body.put("limit", 50);
        }
        body.put(RobotConstant.EXECUTION_CODE_KEY, ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY));

        CloseableHttpResponse response = robotCommonService.offerDownloadUrl(body);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        String suffix = "xls";
        if (response.getFirstHeader("Filename") != null) {
            suffix = response.getFirstHeader("Filename").getValue().split("\\.")[1];
        }
        String instId = UUID.randomUUID().toString().replace("-", "");
        String fileName = instId + "_" + changeTypeName + "." + suffix;
        // 重置 instId
        String filePath = ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH).toString() + '\\' + fileName;
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            inputStream.close();
            fos.flush();
        }
        return filePath;
    }

    private DeclareTempInfoDTO getDeclareTempInfo(String addrName, String accountNumber, Integer changeType, String operationTypeCode) {
        Map<String, Object> body = Maps.newHashMap();
        body.put("addrName", addrName);
        body.put("businessType", businessType.getType());
        body.put("declareType", changeType);
        body.put("accountNumber", accountNumber);
        body.put("declareStatus", "1,2");
        body.put("operationType", operationTypeCode);
        body.put("tplTypeCode", businessType.getTplCode());
        try {
            String respBody = robotCommonService.declareTempInfoUrl(body);
            JSONObject json = JSON.parseObject(respBody);
            if (json.getInteger("code") != 200) {
                throw new RobotInterruptException("获取报盘模板信息错误," + json.getString("message"));
            }
            return JSON.parseObject(json.getString("data"), DeclareTempInfoDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RobotInterruptException("获取报盘模板信息错误：" + e.getMessage());
        }
    }
}
