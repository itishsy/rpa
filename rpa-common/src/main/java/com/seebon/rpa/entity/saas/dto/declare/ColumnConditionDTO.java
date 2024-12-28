package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 17:07:10
 */
@ApiModel("报盘字段条件信息")
@Data
public class ColumnConditionDTO {

    @ApiModelProperty("关系 1：与，2：或")
    private Integer relation;

    @ApiModelProperty("是否显示 1：是，0：否")
    private Integer isShow;

    @ApiModelProperty("满足条件的默认值")
    private String conditionValue;

    @ApiModelProperty("是否必填 2：是，1：否")
    @Column
    private Integer required;

    @ApiModelProperty("结论")
    @Column
    private String conclusion;

    @ApiModelProperty("字段条件明细信息")
    private List<ColumnConditionItemDTO> conditionItems;

}
