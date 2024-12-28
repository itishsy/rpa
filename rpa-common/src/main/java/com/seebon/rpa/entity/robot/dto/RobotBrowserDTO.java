package com.seebon.rpa.entity.robot.dto;

import lombok.Data;

/**
 * @author LY
 * @date 2023/11/22 11:35
 */
@Data
public class RobotBrowserDTO {

    private String driverName;

    private String driverPath;

    private String binaryPath;

    private String plugsPath;

    private String url;
}
