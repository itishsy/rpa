package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel("机器人应用流程")
@Table(name = "robot_flow")
public class RobotFlow extends Identity {

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @Column
    @ApiModelProperty("关联的那条数据的flowCode")
    private String relationFlowCode;

    @Column
    @ApiModelProperty(value = "类型（1主流程，2子流程）")
    private Integer flowType;

    @Column
    @ApiModelProperty(value = "执行顺序")
    private Integer execOrder;

    @Column
    @ApiModelProperty(value = "流程状态 0:配置 1:调试通过 2:等待数据 3:验证有效 4:修复问题")
    private Integer status;

    @Column
    @ApiModelProperty(value = "说明")
    private String comment;

    @Column
    @ApiModelProperty(value = "add自定义新增  ,copyAdd复制新增, relateAdd关联新增")
    private String addType;

    @Column
    @ApiModelProperty(value = "开放类型  0-开放  1-私有")
    private Integer openType;

    @Column
    @ApiModelProperty(value = "是否模板(0：否,1：是)")
    private Integer templateType;

    @Column
    @ApiModelProperty(value = "标签编码")
    private String tagCode;

    @Column
    @ApiModelProperty(value = "服务项目1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细 9：政策通知 10：基数申报")
    private Integer serviceItem;

    @Column
    @ApiModelProperty(value = "申报系统 字典 10007,10008")
    private String declareSystem;

    @Column
    @ApiModelProperty(value = "运行载体 字典 10019")
    private String runSupport;
}
