package com.seebon.rpa.entity.robot.dto.design;

import lombok.Data;

/**
 * @author LY
 * @date 2023/7/24 17:59
 */
@Data
public class CityStatisticsVo {

    private Integer onlineRobotCityCount;

    private Integer offlineRobotCityCount;

    private Integer surveyCount;

    private Integer configurationCount;

    private Integer onlineCount;

    private Integer maintenanceCount;



}
