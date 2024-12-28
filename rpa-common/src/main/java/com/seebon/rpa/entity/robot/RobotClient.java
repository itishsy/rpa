package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人客户端
 */
@Data
@ApiModel(value = "机器人客户端")
@Table(name = "robot_client")
public class RobotClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "设备名称")
    private String machineName;

    @Column
    @ApiModelProperty(value = "设备厂商")
    private String machineFactory;

    @Column
    @ApiModelProperty(value = "设备费用")
    private String machineAmount;

    @Column
    @ApiModelProperty(value = "机器登录凭证")
    private String token;

    @Column
    @ApiModelProperty(value = "内网IP地址")
    private String ip;

    @Column
    @ApiModelProperty(value = "内网端口号")
    private Integer port;

    @Column
    @ApiModelProperty(value = "工作目录")
    private String workPath;


    @Column
    @ApiModelProperty(value = "设备状态 " +
            "0 New 初始化状态、用户未登录;" +
            "1 Runnable 机器人已启动，准备就绪 ;" +
            "2 Running 机器人正在执行任务中;" +
            "3 Closed 关闭;" +
            "4 RobotError 机器人程序内部异常;" +
            "5 ClientError 客户端内部异常;" +
            "6 Upgrading 机器人正在升级;" +
            "7 UpgradeFailed 机器人升级失败;" +
            "8 Offline 离线状态，超过10分钟没有心跳"+
            "9 unused 闲置 ")
    private Integer status;

    @Column
    @ApiModelProperty(value = "操作命令")
    private String cmd;

    @Column
    @ApiModelProperty(value = "提醒开关")
    private Integer msg;

    @Column
    @ApiModelProperty(value = "维护负责人")
    private String userName;

    @Column
    @ApiModelProperty(value = "设备类型：1-数据机器，2-费用机器")
    private Integer type;

    @Column
    @ApiModelProperty(value = "维持的最大数量")
    private Integer maxKeepNum;

    @Column
    @ApiModelProperty(value = "同步标识  0-未同步  1-已同步")
    private Integer sync;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "登录时间")
    private Date loginTime;

    @Column
    @ApiModelProperty(value = "心跳时间")
    private Date heartbeatTime;

    @Column
    @ApiModelProperty(value = "最新维护日期")
    private Date lastMaintenanceDate;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
