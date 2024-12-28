package com.seebon.rpa.entity.agent.vo.declare.employee;


import com.seebon.rpa.entity.agent.dto.declare.DynamicFieldDTO;
import com.seebon.rpa.entity.agent.dto.declare.employee.EmployeeFilestoreDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 个人投保-公积金详情
 */
@Data
public class InsuredAccfundVO {
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

    @ApiModelProperty("参保比例")
    private String proportion;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("原因")
    private String comment;

    @ApiModelProperty("回盘动态字段对象")
    private List<DynamicFieldDTO> dynamics;

    @ApiModelProperty("人员附件信息")
    private List<EmployeeFilestoreDTO> empFiles;
}
