package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.dto.AppVersionDTO;
import com.seebon.rpa.entity.robot.dto.GetRobotStatusRespDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientAppVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface RobotClientAppDao extends Mapper<RobotClientApp> {
    /**
     * 根据客户ID查询客户应用
     *
     * @param map
     * @return
     */
    List<RobotClientAppVo> selectAppListByClientId(Map<String,Object> map);

    /**
     * 根据appCode、version查询客户应用
     *
     * @param appCode
     * @param version
     * @return
     */
    RobotClientApp selectClientByAppCode(@Param("appCode") String appCode, @Param("version") String version);

    Integer updateStatus(@Param("status") Integer status, @Param("id") Integer id);

    Integer insertClientApp(RobotClientApp robotClientApp);

    List<RobotClientApp> selectTaskData(@Param("clientId") Integer clientId, @Param("appCode") String appCode);

    List<AppVersionDTO> selectAppVersion(@Param("clientId") Integer clientId, @Param("machineCode") String machineCode);

    List<RobotClientAppVo> selectAppByClientId(@Param("clientId") Integer clientId, @Param("type") String type);

    String selectTaskCodeByClientId(@Param("clientId") Integer clientId, @Param("accountNumber") String accountNumber, @Param("appArgs") String appArgs);

    List<GetRobotStatusRespDTO> selectByClientIds(@Param("taskCodes") List<String> taskCodes, @Param("clientIds") List<Integer> clientIds);

    int selectCountByParams(Map<String,Object> map);

    List<Integer> getCustIdsByParams(Map<String,Object> params);

    List<RobotClientApp> getByClientId(@Param("clientId") Integer clientId, @Param("runSupport") String runSupport);

    List<String> getDeclareSystem(String appCode);
}
