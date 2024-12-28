package com.seebon.rpa.entity.agent.vo.declare.employee;


import com.seebon.rpa.entity.agent.dto.declare.DynamicFieldDTO;
import com.seebon.rpa.entity.agent.dto.declare.employee.EmployeeFilestoreDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 个人投保-社保详情
 */
@Data
public class InsuredSocialVO {
    @ApiModelProperty("缴纳起始月")
    private String insuredDate;

    @ApiModelProperty("申报状态")
    private String status;

    @ApiModelProperty("申报类型")
    private String changeType;

    @ApiModelProperty("申报城市ID")
    private Integer addrId;

    @ApiModelProperty("申报城市名字")
    private String addrName;

    @ApiModelProperty("缴纳基数")
    private String empTbBase;

    @ApiModelProperty("参保险种")
    private String types;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("原因")
    private String comment;

    @ApiModelProperty("创建时间")
    private String createTime;


    @ApiModelProperty("回盘动态字段对象")
    private List<DynamicFieldDTO> dynamics;

    @ApiModelProperty("人员附件信息")
    private List<EmployeeFilestoreDTO> empFiles;
}
