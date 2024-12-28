package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("PolicyAddrDeadlineSettingDTO")
@Data
public class PolicyAddrDeadlineSettingDTO {


    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @ApiModelProperty("申报类型名称")
    private String declareTypeName;

    @ApiModelProperty("开始月类型 0：当月，1：下月")
    private Integer monthBegin;

    @ApiModelProperty("开始日")
    private Integer dayBegin;

    @ApiModelProperty("开始小时")
    private Integer hourBegin;

    @ApiModelProperty("截止月类型0：当月，1：下月")
    private Integer monthEnd;

    @ApiModelProperty("截止日")
    private Integer dayEnd;

    @ApiModelProperty("截止小时")
    private Integer hourEnd;

    @ApiModelProperty("是否允许同月增减， 1：是 0：否")
    private Integer allowAgainInsure;

}
