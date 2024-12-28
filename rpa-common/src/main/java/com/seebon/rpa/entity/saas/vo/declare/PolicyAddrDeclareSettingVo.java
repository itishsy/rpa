package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyAddrCostSetting;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddrDeadlineSetting;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddrDeclareSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe 申报政策VO
 * @date 2023/5/29 11:14
 */
@Data
@ApiModel("申报政策VO")
public class PolicyAddrDeclareSettingVo{

    @ApiModelProperty("网站申报期集合")
    List<PolicyAddrDeadlineSetting> policyAddrDeadlineSettingList;

    @ApiModelProperty("网站政策实体")
    PolicyAddrDeclareSetting policyAddrDeclareSetting;

    @ApiModelProperty("缴费设置实体")
    List<PolicyAddrCostSetting> policyAddrCostSettingList;

    @ApiModelProperty("地区id")
    private Integer addrId;

    @ApiModelProperty("地区名字")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

}
