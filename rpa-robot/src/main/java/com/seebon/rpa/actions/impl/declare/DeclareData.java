package com.seebon.rpa.actions.impl.declare;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.DeclareDataExeType;
import com.seebon.rpa.context.enums.DeclareDataField;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "申报数据操作", targetType = NoneTarget.class, order = 10, comment = "操作申报数据表")
public class DeclareData extends AbstractAction {

    @Conditions({
            "updateFailedByIdCard:failReason,idCard",
            "selectIdCardByCond:condField,condValue,dataKey",
            "selectByCond:condField,condValue,dataKey",
            "updateByCond:updateField,updateValue,condField,condValue",
            "declareFailed:dataList,failReason",
            "declareing:dataList,failReason"
    })
    @ActionArgs(value = "执行类型", dict = DeclareDataExeType.class)
    private String exeType;

    @ActionArgs(value = "数据列表", comment = "数据列表", required = true, style = DynamicFieldType.text)
    private List<Map<String, Object>> dataList;

    @ActionArgs(value = "失败原因", required = true)
    private String failReason;

    @ActionArgs(value = "身份证号", required = true)
    private String idCard;

    @ActionArgs(value = "更新字段", dict = DeclareDataField.class)
    private String updateField;

    @ActionArgs(value = "更新值")
    private String updateValue;

    @ActionArgs(value = "条件字段", dict = DeclareDataField.class)
    private String condField;

    @ActionArgs(value = "条件值")
    private String condValue;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run() {
        DeclareDataExeType dataExeType = DeclareDataExeType.valueOf(exeType);
        String batchCode = ctx.getVariable(RobotConstant.INST_ID);
        Integer declareType = ctx.getVariable("declareType");
        String tplTypeCode = ctx.getVariable("tplTypeCode");
        String accountNumber = ctx.getAccountNumber();

        switch (dataExeType) {
            case updateFailedByIdCard:
                String sql = "update robot_execution_data set robotExecStatus =1,declareStatus =2, failReason=? WHERE batch_code=? and declareType=? and tplTypeCode=? and accountNumber= ? and idCard = ?";
                jdbcTemplate.update(sql, failReason, batchCode, declareType, tplTypeCode, accountNumber, idCard);
                break;
            case selectByCond:
                ctx.setVariable(dataKey, queryList());
                break;
            case selectIdCardByCond:
                List<String> idCards = new ArrayList<>();
                List<Map<String, Object>> list = queryList();
                for (Map<String, Object> map : list) {
                    idCards.add(map.get("idCard").toString());
                }
                ctx.setVariable(dataKey, idCards);
                break;
            case updateByCond:
                String updateByCondSql = String.format("update robot_execution_data set robotExecStatus =1,declareStatus =2, %s=? WHERE batch_code=? and declareType=? and tplTypeCode=? and accountNumber= ? and %s = ?",
                        updateField, condField);
                jdbcTemplate.update(updateByCondSql, updateValue, batchCode, declareType, tplTypeCode, accountNumber, condValue);
                break;
            case declareFailed:
                for (Map<String, Object> map : dataList) {
                    Object idCard = map.get(RobotConstant.ID_CARD_KEY_NAME);
                    if (idCard == null) {
                        continue;
                    }
                    String updateSql = "update robot_execution_data set robotExecStatus =2,declareStatus =5,failType=1,failReason=? WHERE batch_code=? and declareType=? and tplTypeCode=? and accountNumber= ? and idCard = ?";
                    jdbcTemplate.update(updateSql, failReason, batchCode, declareType, tplTypeCode, accountNumber, idCard.toString());
                }
                break;
            case declareing:
                for (Map<String, Object> map : dataList) {
                    Object idCard = map.get(RobotConstant.ID_CARD_KEY_NAME);
                    if (idCard == null) {
                        continue;
                    }
                    String updateSql = "update robot_execution_data set robotExecStatus =1,declareStatus =2,failType=1,failReason=? WHERE batch_code=? and declareType=? and tplTypeCode=? and accountNumber= ? and idCard = ?";
                    jdbcTemplate.update(updateSql, failReason, batchCode, declareType, tplTypeCode, accountNumber, idCard.toString());
                }
                break;
            default:
                break;
        }
    }

    private List<Map<String, Object>> queryList() {
        String batchCode = ctx.getVariable(RobotConstant.INST_ID);
        String businessType = ctx.getVariable("businessType");
        Integer declareType = ctx.getVariable("declareType");
        String tplTypeCode = ctx.getVariable("tplTypeCode");
        String accountNumber = "1001001".equals(businessType) ? ctx.getVariable("socialNumber") : ctx.getVariable("accfundNumber");
        String selectByCondSql = String.format("select * from robot_execution_data WHERE batch_code='%s' and declareType=%s and tplTypeCode='%s' and accountNumber='%s' ",
                batchCode, declareType, tplTypeCode, accountNumber);
        if ("failReason".equals(condField)) {
            selectByCondSql = selectByCondSql.concat(" and failReason like '%" + condValue + "%';");
        } else {
            selectByCondSql = selectByCondSql.concat(String.format(" and %s = '%s';", condField, condValue));
        }
        return jdbcTemplate.query(selectByCondSql, new Object[]{}, Convert.newRowMapper());
    }
}
