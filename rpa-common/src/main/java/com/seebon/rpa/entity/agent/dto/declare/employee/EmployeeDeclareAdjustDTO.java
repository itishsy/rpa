package com.seebon.rpa.entity.agent.dto.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("增减员调整状态DTO")
@Data
public class EmployeeDeclareAdjustDTO implements Serializable {

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型 1：申报，2：公积金")
    private Integer businessType;

    @ApiModelProperty("系统类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统")
    private String tplTypeCode;

    @ApiModelProperty("调整后状态，0:作废，1：待申报，2：申报中，4：申报成功，5：申报失败，6待提交")
    private Integer declareStatus;

    @ApiModelProperty("调整状态的增减员记录uuid")
    private List<String> changeUuids;

    @ApiModelProperty("原因备注")
    private String failReason;

    @ApiModelProperty("调整理由")
    private String adjustReason;

}
