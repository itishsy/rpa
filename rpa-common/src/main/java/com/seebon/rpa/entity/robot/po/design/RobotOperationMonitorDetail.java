package com.seebon.rpa.entity.robot.po.design;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author s
 * @describe
 * @date 2023/4/12 17:58
 */

@Data
@ApiModel("监控运维详情表")
@Table(name = "robot_operation_monitor_detail")
public class RobotOperationMonitorDetail{
    @Id
    @ApiModelProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @ApiModelProperty("客户id")
    private Integer clientId;

    @Column
    @ApiModelProperty("客户名称")
    @ExcelProperty(value = "客户名称",index = 8)
    @ColumnWidth(20)
    private String clientName;

    @Column
    @ApiModelProperty("设备编号")
    @ExcelProperty(value = "设备编号",index = 7)
    @ColumnWidth(20)
    private String machineCode;

    @Column
    @ApiModelProperty("参保地")
    @ExcelProperty(value = "参保城市",index = 4)
    @ColumnWidth(15)
    private String addrName;

    @Column
    @ApiModelProperty("业务名")
    @ExcelProperty(value = "业务",index = 5)
    @ColumnWidth(15)
    private String serviceName;

    @Column
    @ApiModelProperty("申报账户")
    @ExcelProperty(value = "申报账户",index = 3)
    @ColumnWidth(25)
    private String accountNumber;

    @Column
    @ApiModelProperty("处理状态")
    private Integer handleStatus;

    @Column
    @ApiModelProperty("原因类型")
    private String handleType;

    @Column
    @ApiModelProperty("补充说明")
    @ExcelProperty(value = "补充说明",index = 14)
    @ColumnWidth(20)
    private String handleRemark;

    @Column
    @ApiModelProperty("关联链接")
    @ExcelProperty(value = "关联链接",index = 15)
    @ColumnWidth(20)
    private String handleLink;

    @Column
    @ApiModelProperty("办理人")
    private String handlePerson;

    @Column
    @ApiModelProperty("办理时间")
    private String handleDate;

    @Column
    @ApiModelProperty("预警项目类型")
    @ExcelProperty(value = "预警项目",index = 0)
    @ColumnWidth(15)
    private String warnType;

    @Column
    @ApiModelProperty("上报时间")
    @ExcelProperty(value = "上报时间",index = 9)
    @ColumnWidth(15)
    private String reportTime;

    @Column
    @ApiModelProperty("消息主题")
    private String messageTopic;

    @Column
    @ApiModelProperty("消息内容")
    private String messageContent;

    @Column
    @ApiModelProperty(value="创建时间")
    private Date createTime;


    @Column
    @ApiModelProperty("应用编码")
    private String appCode;


    @Column
    @ApiModelProperty(value="执行id")
    private String executionId;

    @Column
    @ApiModelProperty(value="错误原因")
    @ExcelProperty(value = "异常原因",index = 1)
    @ColumnWidth(30)
    private String errorResult;

    @Column
    @ApiModelProperty(value = "应用名字")
    private String appName;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "任务名字")
    private String taskName;

    @Column
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty(value = "流程名字")
    @ExcelProperty(value = "流程名称",index = 6)
    @ColumnWidth(30)
    private String flowName;



}
