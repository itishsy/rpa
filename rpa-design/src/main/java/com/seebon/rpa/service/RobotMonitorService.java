package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;

import java.util.Map;

/**
 * @author LY
 * @date 2023/4/3 13:39
 */
public interface RobotMonitorService {

    IgGridDefaultPage<RobotCityListVo> listCityPage(Map<String, Object> searchVo);

    CityStatisticsVo cityStatistics();

    Integer takeUpRobotRunStatus(RobotCityEditVo editVo);

    Integer takeDownRobotRunStatus(RobotCityEditVo editVo);

    Integer editRobotRunStatus(RobotCityEditVo editVo);

    Map<String, Integer> declareStatistics(Long id);

    IgGridDefaultPage<RobotClientVO> boxPage(Map<String, Object> map);

    IgGridDefaultPage<RobotClientAppVo> customerPage(Map<String, Object> map);

    IgGridDefaultPage<RobotCityPrincipalListVo> principalPage(Map<String, Object> map);

    RobotCityListVo detail(Long appId);

    Map getClientAndMaCode(Integer id,Integer number);


}
