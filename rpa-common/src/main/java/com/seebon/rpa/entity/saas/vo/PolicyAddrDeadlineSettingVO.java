package com.seebon.rpa.entity.saas.vo;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-20 16:22:42
 */
@Data
@ApiModel("参保地申报期信息")
public class PolicyAddrDeadlineSettingVO extends Identity {

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;


    @ApiModelProperty("开始月类型 0：当月，1：下月")
    private Integer monthBegin;

    @ApiModelProperty("开始日")
    private Integer dayBegin;

    @ApiModelProperty("开始小时")
    private Integer hourBegin;

    @ApiModelProperty("截止月类型0：当月，1：下月,2：上月")
   private Integer monthEnd;

    @ApiModelProperty("截止日")
    private Integer dayEnd;

    @ApiModelProperty("截止小时")
    private Integer hourEnd;

    @ApiModelProperty("(0 遇节假日提前 1 遇节假日延后)")
    private Integer isWeekend;

    @ApiModelProperty("延后天数")
    private Integer isWeekendDay;

}
