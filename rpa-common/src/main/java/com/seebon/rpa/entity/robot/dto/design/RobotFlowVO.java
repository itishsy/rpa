package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotAppSchedule;
import com.seebon.rpa.entity.robot.RobotTaskSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@ApiModel("机器人应用流程VO")
public class RobotFlowVO {

    private Integer id;

    @ApiModelProperty(value = "应用code")
    private String appCode;

    @ApiModelProperty(value = "应用code")
    private String appName;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "执行顺序")
    private Integer execOrder;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @ApiModelProperty(value = "类型（1主流程，2子流程）")
    private String flowType;

    @ApiModelProperty(value = "新增方式")
    private String addType;

    @ApiModelProperty(value = "编辑人姓名")
    private String customerName;

    @ApiModelProperty(value = "appName+flowName")
    private String appNameAndFlowName;

    @ApiModelProperty(value = "是否模板(0：否,1：是)")
    private Integer templateType;

    @ApiModelProperty(value = "模板流程编码")
    private String templateFlowCode;

    @ApiModelProperty(value = "模板流程编码")
    private String templateFlowName;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;

    @ApiModelProperty(value = "服务项目1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细 9：政策通知  10：基数申报 ")
    private Integer serviceItem;

    @ApiModelProperty(value = "服务项目名称")
    private String serviceItemName;

    @ApiModelProperty(value = "申报系统  字典 10019")
    private String declareSystem;

    @ApiModelProperty(value = "申报系统名称")
    private String declareSystemName;

    @ApiModelProperty(value = "运行载体，1：客户端，2：谷歌浏览器，3：IE浏览器，4：火狐浏览器，5:360浏览器")
    private String runSupport;

    @ApiModelProperty(value = "运行载体名称")
    private String runSupportName;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty("关联的那条数据的flowCode")
    private String relationFlowCode;

    @ApiModelProperty("说明")
    private String comment;

    @ApiModelProperty(value = "步骤数")
    private Integer stepNum;

    @ApiModelProperty(value = "流程状态 0:配置 1:调试通过 2:等待数据 3:验证有效 4:修复问题")
    private Integer status;

    @ApiModelProperty(value = "流程状态数")
    private Integer statusNum;

    @ApiModelProperty(value = "数据类型：1：应用状态，2：流程状态")
    private Integer historyType;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "服务项目1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细")
    private List<Integer> serviceItemList;

    @ApiModelProperty(value = "类型（1主流程，2子流程）")
    private List<String> flowTypeList;

    @ApiModelProperty(value = "申报系统  字典 10019")
    private List<String> declareSystemList;

    private RobotAppSchedule robotAppSchedule;

    private RobotTaskSchedule robotTaskSchedule;
}

