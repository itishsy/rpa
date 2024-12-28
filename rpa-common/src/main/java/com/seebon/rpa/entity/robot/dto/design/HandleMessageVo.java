package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe 办结信息处理vo
 * @date 2023/4/13 11:20
 */
@ApiModel("办结信息处理vo")
@Data
public class HandleMessageVo{
    @Column
    @ApiModelProperty("办结id集合")
    private List<Integer> ids;

    @Column
    @ApiModelProperty("原因归类")
    private String handleType;

    @Column
    @ApiModelProperty("补充说明")
    private String handleRemark;

    @Column
    @ApiModelProperty("关联链接")
    private String handleLink;

    @Column
    @ApiModelProperty("客户id")
    private Integer clientId;

    @Column
    @ApiModelProperty("客户名称")
    private String clientName;

    @Column
    @ApiModelProperty("设备编号")
    private String machineCode;


    @Column
    @ApiModelProperty("处理状态")
    private Integer handleStatus;

    @Column
    @ApiModelProperty("处理状态")
    private String reportStartTime;

    @Column
    @ApiModelProperty("处理状态")
    private String warnTypeCode;

}
