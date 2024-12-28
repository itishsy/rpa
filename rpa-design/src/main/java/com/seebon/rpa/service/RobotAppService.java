package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.Robot;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.RobotAppExplain;
import com.seebon.rpa.entity.robot.RobotClientUsb;
import com.seebon.rpa.entity.robot.dto.DeclareAccountBaseDTO;
import com.seebon.rpa.entity.robot.dto.RobotAppDTO;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.dto.history.RobotAppHistory;
import com.seebon.rpa.entity.robot.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface RobotAppService {

    /**
     * 主列表查询流程设计
     *
     * @param map
     * @return
     */
    IgGridDefaultPage<RobotAppVO> listPage(Map<String, Object> map);

    /**
     * 新增流程设计
     *
     * @param addrName
     * @param businessType
     * @return
     */
    int add(String addrName, String businessType);

    /**
     * 发版。更新版本号，并存储到历史表
     *
     * @param robotAppVO
     * @return
     */
    int release(RobotAppVO1 robotAppVO);

    /**
     * 根据code查询版本历史
     *
     * @param appCode
     * @return
     */
    List<RobotAppHistoryVO> findHistory(String appCode);

    /**
     * 使用此版本
     *
     * @param appCode
     * @return
     */
    int activate(String appCode, String version);


    /**
     * 根据版本号查找最新版本
     *
     * @param appCode
     * @return
     */
    RobotAppDTO findLatest(String appCode);


    /**
     * 所有机器人应用地区
     *
     * @return
     */
    List<RobotApp> allApp();

    List<RobotApp> list();

    /**
     * 查询机器人类型
     *
     * @return
     */
    List<Robot> listRobot();

    /**
     * 添加机器人应用
     *
     * @return
     */
    Integer addRobotApp(RobotAppAddVO robotAppAddVO);

    /**
     * 应用管理-新增-配置逻辑
     *
     * @return
     */
    Integer addAppExplain(RobotAppExplain appExplain);

    /**
     * 应用管理-获取配置逻辑
     *
     * @return
     */
    RobotAppExplain getAppExplain(String appCode);

    String queryAppName(String appCode);

    String queryAppCode(String appName);

    /**
     * 根据当前用户的客户id，返回所有机器人。
     *
     * @return
     */
    List<RobotClientVO> getAppByClientId();

    /**
     * 根据当前用户的客户id，返回所有应用
     *
     * @return
     */
    List<RobotClientAppVo> listAppByCustId();

    /**
     * 根据当前用户的客户id，是否有未激活应用
     *
     * @return
     */
    Boolean hasActivateAppByCustId();

    /**
     * 获取机器人业务类型
     *
     * @param appCode
     * @return
     */
    RobotAppVO getRobotDeclareType(String appCode);

    String getDictName(String appCode);

    Integer saveDeclareAccount(DeclareAccountBaseDTO dto);

    List<RobotClientUsb> listUsb(String machineCode);

    void callbackUsb(String machineCode);

    void clearUsb(String machineCode);

    int saveUsb(RobotClientUsb clientUsb);

    /**
     * 文件上传，提供给后台使用
     *
     * @param params
     */
    void fileUploadForServer(Map<String, Object> params);

    List<String> getMachineCode(Integer clientId, Integer addrId, String businessType, String accountNumber);

    /**
     * 同步流程应用信息
     *
     * @param robotAppInfos
     * @return
     */
    int syncInRobotAppInfos(RobotAppHistory robotAppInfos);

    int syncOut(String appCode, String comment);

    IgGridDefaultPage<RobotAppDesignVO> getAppData(Map<String,Object> map);

    RobotCountVO getAppCount();

    RobotCountVO getStatusCount();

    void offlineStatus (String appCode,String comment,Integer appStatus);

    void updateAppStatus(String appCode,Integer appStatus,String remark);

    List<RobotFlowStatusHistoryVO> selectAppHistoryStatus(String appCode);

    void downExcel(Map<String,Object> map,HttpServletResponse response);

    void updateApp(RobotAppAddVO robotAppAddVO);
}
