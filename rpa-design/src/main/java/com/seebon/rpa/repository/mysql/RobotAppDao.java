package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.dto.GetTaskFlowDTO;
import com.seebon.rpa.entity.robot.dto.GetTaskFlowRespDTO;
import com.seebon.rpa.entity.robot.dto.RobotAppInfoDTO;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.vo.RobotAppAddVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskNumVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface RobotAppDao extends Mapper<RobotApp> {


    List<com.seebon.rpa.entity.robot.vo.RobotAppVO> selectRobotApp(Map<String,Object> map);
    /**
     * 分页查询主列表信息
     *
     * @param map
     * @return
     */
    List<RobotAppVO1> findByIgQuery(Map<String, Object> map);

    String selectCustomer(Integer id);

    /**
     * 修改状态为未发布
     */
    int updateStatus(@Param("appCode") String appCode);
    /**
     * 修改应用状态
     */
    int updateAppStatus(@Param("appCode") String appCode, @Param("appStatus") Integer appStatus, @Param("remark") String remark);

    /**
     * 根据flow查找当前app
     */
    RobotApp selectAppByFlowCode(@Param("flowCode") String flowCode);

    /**
     * 添加机器人应用
     */
    Integer addRobotApp(RobotApp robotApp);

    List<String> selectAppName(String appCode);

    List<String> selectByAppCode(String appCode);

    com.seebon.rpa.entity.robot.vo.RobotAppVO selectOneRobotAppVO(@Param("appCode") String appCode);

    RobotApp selectAppByTaskCode(@Param("taskCode") String taskCode);

    List<String> getMachineCode(@Param("clientId") Integer clientId, @Param("addrId") Integer addrId,
                                @Param("businessType") String businessType, @Param("accountNumber")String accountNumber);

    List<RobotCityListVo> listCityPage(Map<String, Object> searchVo);

    Integer listCityCount(RobotCitySearchVo searchVo);

    Integer cityCount();

    CityStatisticsVo cityStatistics();

    List<RobotClientVO> boxPage(Map<String, Object> params);

    List<RobotClientAppVo> customerPage(Map<String, Object> params);

    List<RobotCityPrincipalListVo> principalPage(Map<String, Object> params);

    RobotCityListVo getCityDetailByAppId(Long appId);

    GetTaskFlowRespDTO getTaskFlow(GetTaskFlowDTO flowDTO);

    RobotTaskRunRespVO getTaskRun(Map<String, Object> params);

    List<RobotTaskNumVO> getRobot(Map<String, Object> params);

    String queryAppCode(@Param("appName") String appName);

    List<RobotAppInfoDTO> getRobotAppList(@Param("clientId") Integer clientId);

    List<RobotAppDesignVO> getAppList(Map<String,Object>map);

    RobotCountVO getAppCount();

    void updateOfflineStatus(Integer appStatus,String comment,String appCode);

    void updateApp(RobotAppAddVO robotAppAddVO);

    List<RobotAppDesignVO> getWaitOnline();

    List<RobotAppDesignVO> getOnlineOrOffline();

    void updateOnlineMess(RobotApp robotApp);

    List<RobotApp> selectListByParams(Map<String,Object> map);

    List<RobotAppDesignVO> getWaitOnlineByParams(Map<String,Object> params);

    List<RobotApp> selectAllByTask(@Param("clientId") Integer clientId);

    List<RobotTaskNumVO> getRobotTwo(Map<String,Object> params);

}
