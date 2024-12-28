package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lsh
 * @dateTime 2022-04-26
 */
@Data
public class InsureBySocial {

    @ApiModelProperty("业务类型名称 1:社保/ 2:公积金")
    private Integer bussinssTypeName;

    @ApiModelProperty("申报类型名称  1:增员/2:减员/3:调基/4:变更/5:补缴")
    private Integer changeTypeName;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("身份证号码")
    private String idCard;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("方案id")
    private Integer productId;

    @ApiModelProperty("方案名称")
    private String productName;

    @NotNull(message = "参保城市不能为空")
    @ApiModelProperty("参保城市名称")
    private String addrName;


    @ApiModelProperty("缴纳起始月份")
    private String insuredDate;

    @ApiModelProperty("缴纳停止月份")
    private String endDate;

    @NotNull(message = "缴纳基数不能为空")
    @ApiModelProperty("缴纳基数")
    private String empTbBase;

    @ApiModelProperty("单位社保号id")
    private Integer compAccountId;

    @ApiModelProperty("单位社保号")
    private String compAccount;

    @ApiModelProperty("社保补缴参保请求参数")
    private InsureByFillUp insureByFillUp;

    @ApiModelProperty("社保参保动态字段对象")
    private List<DynamicFieldDTO> fields;

    @ApiModelProperty("险种对象")
    private List<InsuranceTypeDTO> codings;
}
