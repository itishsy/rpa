package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-16 17:14
 */
@Data
public class TaskCountVo{

    @ApiModelProperty("今日任务")
    private Long todayTaskCount;

    @ApiModelProperty("今日任务-执行中")
    private Long todayTaskExecuteCount;

    @ApiModelProperty("今日任务-执行次数")
    private Long todayTaskExecuteNumber;

    @ApiModelProperty("今日执行异常")
    private Long todayExecuteError;

    @ApiModelProperty("预警次数")
    private Long warnCount;
}
