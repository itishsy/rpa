package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskNotice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/18 18:31
 * @Version 1.0
 **/
@Data
public class RobotTaskVO extends RobotTask {

    /**
     * 单位名称
     */
    private String declareAccount;

    private Integer valueType;

    /**
     * 待申报人数
     */
    private Integer awaitDeclareNumber;

    private Date lastExecTime;

    /**
     * 单位社保号/公积金号
     */
    private String accountNumber;

    /**
     * 业务类型 社保/公积金
     */
    private String businessType;
    /**
     * 机器标识码
     */
    private String machineCode;

    @ApiModelProperty("上次已完成申报人数")
    private Integer lastCompletedCount;

    private String statusName;

    private String usbPort;

    private RobotTaskNotice robotTaskNotice;

    private String appArgs;

    private String orgName;

    private String appName;

    private Integer fixMachine;

    private String executionCode;

    /**
     * 最近两个月是否有在册数据
     */
    private Boolean isAccountRegisterRecently;

    private List<RobotTaskArgs> taskArgsList;
}
