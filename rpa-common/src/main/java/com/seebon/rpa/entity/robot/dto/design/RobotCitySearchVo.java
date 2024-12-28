package com.seebon.rpa.entity.robot.dto.design;

import lombok.Data;

/**
 * @author LY
 * @date 2023/7/24 15:45
 */
@Data
public class RobotCitySearchVo {

    private Long appId;

    private Integer page;

    private Integer size;

    private String addrName;

    private Integer onlineStatus;

    private Integer appType;


}
