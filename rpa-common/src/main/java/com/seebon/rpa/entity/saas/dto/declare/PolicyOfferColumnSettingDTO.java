package com.seebon.rpa.entity.saas.dto.declare;

import com.seebon.rpa.entity.saas.vo.declare.PolicyDeclareColumnRuleVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-09-27 18:36:16
 */
@ApiModel("报盘字段信息DTO")
@Data
public class PolicyOfferColumnSettingDTO {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("字段别名")
    private String columnName;

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("字段类型（1：文本，2：下拉，3：数值，4：年月日，5：年月）")
    private Integer fieldType;

    @ApiModelProperty("默认值")
    private String defaultValue;

    /*@ApiModelProperty("格式编码")
    private String formatCode;*/

    /*@ApiModelProperty("格式要求说明")
    private String formatExplain;*/

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("是否必填。1：不是；2：是")
    private Integer required;

    @ApiModelProperty("字段校验规则信息")
    private List<PolicyDeclareColumnRuleVO> rules;

    @ApiModelProperty("可选范围，字段类型为下拉时")
    private List<ColumnOptionsDTO> options;

    @ApiModelProperty("显示顺序")
    private Integer sort;

    @ApiModelProperty("非首次收集字段，1：是，0：否")
    private Integer notFirstTime;

    @ApiModelProperty("其它属于，字典值 10036，多个用逗号拼接")
    private String otherBelongTo;
}
