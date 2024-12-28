package com.seebon.rpa.entity.robot.vo;

import lombok.Data;

/**
 * @author LY
 * @date 2023/11/6 15:17
 */

@Data
public class RobotEmpAccountVO{

    private String empAccount;

    private String idCard;


    public RobotEmpAccountVO() {
    }

    public RobotEmpAccountVO(String empAccount, String idCard) {
        this.empAccount = empAccount;
        this.idCard = idCard;
    }
}
