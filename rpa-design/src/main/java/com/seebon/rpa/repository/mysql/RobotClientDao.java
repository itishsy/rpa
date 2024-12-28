package com.seebon.rpa.repository.mysql;


import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.dto.RobotClientDTO;
import com.seebon.rpa.entity.robot.dto.RobotStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.OperationRequestVo;
import com.seebon.rpa.entity.robot.dto.design.RobotClientComponentVO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface RobotClientDao extends Mapper<RobotClient> {

    /**
     * 查询机器人客户端列表
     */
    List<RobotClientVO> selectRobotClients(Map<String,Object> map);

    List<RobotClientApp> selectRobotClientAppByClientId();
    /**
     * 根据machineCode查询机器人
     * @param machineCode
     * @return
     */
    RobotClient selectClient(@Param("machineCode") String machineCode);

    /**
     * 修改机器人数据
     *
     * @param robotClient
     * @return
     */
    int updateByRobotCode(RobotClient robotClient);

    /**
     * 根据machineCode修改机器人状态
     * @param machineCode
     * @return
     */
    int updateStatus(@Param("machineCode") String machineCode,@Param("status") Integer status);

    /**
     * 分页查询主列表信息
     *
     * @param map
     * @return
     */
    List<RobotClient> findByIgQuery(Map<String, Object> map);

    /**
     * 查询总记录数
     *
     * @param map
     * @return
     */
    int findByRecords(Map<String, Object> map);

    /**
     * component列表
     *
     * @param appCode
     * @return
     */
    List<RobotClientComponentVO> componentList(@Param("appCode") String appCode);

    /**
     * RPA流程列表
     *
     * @param appCode
     * @return
     */
    List<RobotClientFlowVO> appList(@Param("appCode") String appCode);

    List<RobotClientVO> selectClientByClientId(Integer clientId);

    List<Map<String,Object>> getHeartbeatErrorList(@Param("rangeTime") Integer rangeTime);

    List<Map<String,Object>> getUpgradeErrorList();

    RobotClientVO selectClientLog(@Param("machineCode") String machineCode);

    List<Integer> selectMoreThanTenMinutes(@Param("time") Long time);

    void updateStatusByIds(@Param("status") Integer status, @Param("ids") List<Integer> ids);

    void stopTaskByIds(@Param("run") Integer run, @Param("ids") List<Integer> ids);

    List<OperationRequestVo> getClientDetail(@Param("ids") List<Integer> ids);

    List<Map<String,Object>> getBoxErrorList();

    void updateBoxCountTime(@Param("machineCode") String machineCode,@Param("countTime") Long countTime);

    void updateAppCountTime(@Param("appCode") String appCode,@Param("countTime") Long countTime);

    RobotClientDTO getRobotClientInfo(@Param("clientId")Integer clientId, @Param("machineCode")String machineCode, @Param("appCode") String appCode);

    RobotStatisticsDTO selectStatistics();

    List<Integer> selectExistsCustIds(@Param("custIds")List<Integer> custIds);

    Integer countFreeRobotClient(@Param("clientId")Integer clientId,@Param("machineCodeList")List<String> machineCodeList);

}
