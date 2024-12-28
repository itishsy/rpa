package com.seebon.rpa.entity.robot.vo.design;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.RobotTask;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author hao
 * @Date 2022/9/29 18:38
 * @Version 1.0
 **/
@Data
public class RobotTaskVO1 extends RobotTask {

    /**
     * 成功次数
     */
    private Integer successNumber;

    /**
     * 失败次数
     */
    private Integer failureNumber;

    /**
     * 操作数据
     */
    private Integer dataNumber;

    /**
     * 执行结果（1--成功；2--失败）
     */
    private Integer executionResult;

    /**
     * 执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date executionTime;

    /**
     * 执行记录表execution_code
     */
    private String executionCode;

    /**
     * 单位社保号、公积金号
     */
    private String accountNumber;
    /**
     * 单位名称
     */
    private String companyName;
}
