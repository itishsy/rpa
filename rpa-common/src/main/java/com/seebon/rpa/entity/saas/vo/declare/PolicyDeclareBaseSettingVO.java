package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareBaseSetting;
import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareBaseTpl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 11:05:32
 */
@ApiModel(value = "报盘设置信息VO")
@Data
public class PolicyDeclareBaseSettingVO extends PolicyDeclareBaseSetting {

    @ApiModelProperty("引用模板的参保地名称")
    private String quoteAddrName;

    @ApiModelProperty("引用模板的参保地Id")
    private Integer quoteAddrId;

    @ApiModelProperty("业务类型名称 社保/公积金")
    private String bussinssTypeName;

    @ApiModelProperty("申报类型名称  增员/减员/调基/变更/补缴")
    private String changeTypeName;

    @ApiModelProperty("关联文件信息")
    private List<PolicyDeclareBaseTpl> tpls;

    @ApiModelProperty("报盘字段信息")
    private List<PolicyDeclareColumnSettingVO> columnSettings;

    @ApiModelProperty("'2:必填  1：不必填'")
    private Integer required;


}
