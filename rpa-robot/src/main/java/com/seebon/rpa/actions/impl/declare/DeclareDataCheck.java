package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.DeclareDataCheckType;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareOfferExportParamsDTO;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.enums.DeclareTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
@RobotAction(name = "申报数据检查", targetType = NoneTarget.class, order = 10, comment = "查询后台是否存在满足条件的申报数据，返回结果条数")
public class DeclareDataCheck extends AbstractAction {

    @Autowired
    private RobotCommonService robotCommonService;


    @ActionArgs(value = "检查项", required = true, comment = "检查后台数据项", dict = DeclareDataCheckType.class)
    private String checkType;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;


    @Override
    public void run() {
        DeclareDataCheckType dataExeType = DeclareDataCheckType.valueOf(checkType);
        int result = 0;
        String res;

        switch (dataExeType) {
            case unTagDeclareData:
                DeclareOfferExportParamsDTO dto = new DeclareOfferExportParamsDTO();
                dto.setAddrName(ctx.get("addrName"));
                dto.setBusinessType(Convert.getBusinessType(ctx.get("businessType")));
                dto.setTplTypeCode(ctx.get("tplTypeCode"));
                dto.setDeclareType(ctx.get("declareType"));
                dto.setAccountNumber(ctx.getAccountNumber());
                res = robotCommonService.declareTagCheck(dto);
                JSONObject resObj = JSON.parseObject(res);
                String data = resObj.getString("data");
                if(data != null && data.equalsIgnoreCase("true")){
                    result = 1;
                }
                break;
            case unDoneDeclareData:
                Map<String, Object> reqMap = Maps.newHashMap();
                reqMap.put("addrName", ctx.get("addrName"));
                reqMap.put("accountNumber", ctx.getAccountNumber());
                reqMap.put("tplTypeCode", ctx.get("tplTypeCode"));
                reqMap.put("businessType", Convert.getBusinessType(ctx.get("businessType")));
                reqMap.put("tbDate", DateUtil.date().toString("yyyy-MM"));
                reqMap.put("validateFlag", 1);
                String tplTypeCode = ctx.get("tplTypeCode");
                if ("10007007".equals(tplTypeCode)) {
                    reqMap.put("operationType", "1016");
                }
                res = robotCommonService.checkOfferExportUrl(reqMap);
                JSONObject resObj2 = JSON.parseObject(res);
                JSONObject data2 = resObj2.getJSONObject("data");
                String declareName = DeclareTypeEnum.getNameByCode(ctx.get("declareType"));
                result = data2.getInteger(declareName);
                break;
            default:
                break;
        }
        ctx.setVariable(dataKey, result);
    }

}
