package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotApp;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/7 17:37
 * @Version 1.0
 **/
@Data
public class RobotClientAppVO extends RobotApp {
    private List<RobotTaskVO> robotTaskVOS;
    /**
     * 单位社保号/公积金信息
     */
    private String accountNumber;
    /**
     * 机器标识码
     */
    private String machineCode;

    private boolean flag=true;

}
