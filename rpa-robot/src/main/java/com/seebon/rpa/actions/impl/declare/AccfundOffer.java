package com.seebon.rpa.actions.impl.declare;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.AccfundSheetTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.OfferLabelType;
import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.enums.DeclareTypeEnum;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ActionUtils
@RobotAction(name = "写入报盘文件", targetType = AccfundSheetTarget.class, comment = "写入报盘文件")
public class AccfundOffer extends AbstractAction {

    @ActionArgs(value = "业务节点", dict = OfferLabelType.class, required = true)
    private OfferLabelType businessNode;

    @ActionArgs(value = "表头索引", comment = "从0开始")
    private Integer header;

    @ActionArgs(value = "表头列", comment = "如个人账号,姓名,转出单位账号")
    private String headerCols;

    @Autowired
    private RpaDesignService rpaDesignService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run() {
        try {
            List<RobotExecutionData> list = this.getExecutionData();
            if (CollectionUtils.isEmpty(list)) {
                this.setNodeList(Lists.newArrayList());
                return;
            }
            // 获取规则
            List<RobotOfferRuleVO> ruleList = this.getRobotOfferRule();
            Map<String, String> idCardMap = list.stream().filter(vo -> StringUtils.isNotBlank(vo.getFailReason())).collect(Collectors.toMap(k -> k.getIdCard(), v -> v.getFailReason()));
            List<String> nextIdCardList = Lists.newArrayList();
            for (String idCard : idCardMap.keySet()) {
                RobotOfferRuleItem ruleItem = Convert.getRuleItem(ruleList, idCardMap.get(idCard));
                if (ruleItem == null) {
                    continue;
                }
                nextIdCardList.add(idCard);
            }
            if (CollectionUtils.isEmpty(nextIdCardList)) {
                this.setNodeList(Lists.newArrayList());
                return;
            }
            ctx.setVariable("businessNode", businessNode);

            //业务节点文件路径
            this.getNodePath();

            //业务节点list
            List<Map<String, Object>> nodeList = Lists.newArrayList();
            // 获取报盘信息
            List<Map<String, Object>> declareList = ctx.get(RobotConstant.DECLARE_LIST);
            for (Map<String, Object> map : declareList) {
                Object value = map.get(RobotConstant.ID_CARD_KEY_NAME);
                if (value == null) {
                    continue;
                }
                if (nextIdCardList.contains(value.toString())) {
                    nodeList.add(map);
                }
            }
            this.setNodeList(nodeList);
            // 生成报盘文件
            this.generateExcel(nodeList);
        } catch (Exception e) {
            log.error("公积金报盘文件生成异常" + e.getMessage(), e);
            throw new RuntimeException("公积金报盘文件生成异常." + e.getMessage() + "，请检查报盘文件是否正确");
        }
    }

    private void generateExcel(List<Map<String, Object>> mapList) {
        Sheet sheet = getTarget();
        AccfundSheetTarget sheetTarget = (AccfundSheetTarget) this.getVariable("AccfundSheetTarget");
        sheetTarget.setWrite(true);
        List<String> keys = Lists.newArrayList();
        header = (header == null) ? 0 : header;
        Row headerRow = sheet.getRow(header);
        if (StringUtils.isNotBlank(headerCols)) {
            keys = Lists.newArrayList(headerCols.split(","));
        } else {
            keys = Lists.newArrayList();
            short lastCellNum = headerRow.getLastCellNum();
            for (int i = 0; i < lastCellNum; i++) {
                keys.add(getCellVal(headerRow.getCell(i)));
            }
        }

        int rowSize = mapList.size();
        for (int i = 0; i < rowSize; i++) {
            Map<String, Object> map = mapList.get(i);
            map.put("序号", (i + 1) + "");
            Row row = sheet.getRow(i + header + 1);
            if (row == null) {
                row = sheet.createRow(i + header + 1);
            }
            for (int j = 0; j < keys.size(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                if (sheetTarget.getStyle() != null) {
                    cell.setCellStyle(sheetTarget.getStyle());
                }
                if (map.containsKey(keys.get(j))) {
                    Object o = map.get(keys.get(j));
                    if (o != null) {
                        cell.setCellValue(o.toString());
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
        }
    }

    private String getCellVal(Cell cell) {
        String val = "";
        if (cell == null) {
            return val;
        }
        switch (cell.getCellType().name()) {
            case "STRING":
                val = cell.getRichStringCellValue().getString();
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
            case "BLACK":
                val = "";
                break;
            default:
                val = cell.toString();
                break;
        }
        return val;
    }

    private String getNodePath() {
        String filePath = ctx.get(RobotConstant.FILE_PATH);
        String instId = ctx.getVariable(RobotConstant.INST_ID);
        String dataPath = ctx.get(RobotConstant.DATA_PATH);
        Integer declareType = ctx.get("declareType");
        String accountNumber = ctx.getAccountNumber();
        String tplTypeCode = ctx.get("tplTypeCode");
        String fileSuffix = StringUtils.substringAfterLast(filePath, ".");
        String declareName = DeclareTypeEnum.getNameByCode(declareType);
        String tplTypeName = TplTypeCodeEnum.getNameByCode(tplTypeCode);
        String nodePath = Convert.appendPath(dataPath, accountNumber + "_" + tplTypeName + "_" + declareName + "_" + businessNode.getName() + "_" + instId + "." + fileSuffix);
        if (OfferLabelType.changeInto.equals(businessNode)) {
            ctx.setVariable(RobotConstant.ZR_PATH, nodePath);
        }
        if (OfferLabelType.accountUnsealed.equals(businessNode)) {
            ctx.setVariable(RobotConstant.QF_PATH, nodePath);
        }
        return nodePath;
    }

    private List<RobotOfferRuleVO> getRobotOfferRule() {
        RobotOfferRuleVO ruleVO = new RobotOfferRuleVO();
        ruleVO.setAddrName(ctx.get("addrName"));
        ruleVO.setBusinessType(Convert.getBusinessType(ctx.get("businessType")));
        ruleVO.setDeclareType(ctx.get("declareType"));
        ruleVO.setDeclareWebsite(ctx.get("tplTypeCode"));
        ruleVO.setNextNode(businessNode.getCode());
        ruleVO.setRuleType("checkNextNode");
        return rpaDesignService.listOfferRule(ruleVO);
    }

    public void setNodeList(List<Map<String, Object>> nodeList) {
        if (OfferLabelType.changeInto.equals(businessNode)) {
            ctx.setVariable(RobotConstant.ZR_LIST, nodeList);
        }
        if (OfferLabelType.accountUnsealed.equals(businessNode)) {
            ctx.setVariable(RobotConstant.QF_LIST, nodeList);
        }
    }

    private List<RobotExecutionData> getExecutionData() {
        String addrName = ctx.get("addrName");
        String accountNumber = ctx.getAccountNumber();
        String tplTypeCode = ctx.get("tplTypeCode");
        Integer businessType = Convert.getBusinessType(ctx.get("businessType"));
        Integer declareType = ctx.get("declareType");
        String batchCode = ctx.get(RobotConstant.INST_ID);
        String sql = "SELECT id,addrName,businessType,declareType,accountNumber,idCard,declareMonth,tplTypeCode,declareStatus,robotExecStatus,failReason,failType FROM robot_execution_data  " +
                "WHERE sync = 0 and robotExecStatus = 1 and declareStatus in (1,2) and addrName =? and accountNumber =? and tplTypeCode=? and businessType=? and declareType=? and batch_code=? and failReason is not null";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RobotExecutionData.class), addrName, accountNumber, tplTypeCode, businessType, declareType, batchCode);
    }
}
