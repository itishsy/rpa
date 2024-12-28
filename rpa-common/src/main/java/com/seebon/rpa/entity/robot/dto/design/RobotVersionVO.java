package com.seebon.rpa.entity.robot.dto.design;

import lombok.Data;

@Data
public class RobotVersionVO {
    /**
     * 新的版本号
     */
    private String newVersion;

    /**
     * 新版下载链接
     */
    private String downloaderUrl;

}
