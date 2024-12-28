package com.seebon.rpa.entity.robot.dto.design;

import lombok.Data;

/**
 * @author LY
 * @date 2023/7/25 11:02
 */
@Data
public class RobotCityEditVo {

    private Long appId;

    private Integer onlineStatus;

    private String websiteLinks;

    private Integer lastExecDay;

//    private String versionId;

    private String remark;
}
