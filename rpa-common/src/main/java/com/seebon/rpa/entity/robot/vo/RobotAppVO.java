package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotApp;
import lombok.Data;

/**
 * @Author hao
 * @Date 2022/8/25 10:43
 * @Version 1.0
 **/
@Data
public class RobotAppVO extends RobotApp {
    /**
     * 状态(1 已发布，0 未发布 , 2 历史版本)
     */
    private String state;

    /**
     * 机器人类型
     */
    private String robotName;

    private String businessType;

    private Integer clientId;
}
