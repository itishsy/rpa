package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotClientComponent;
import com.seebon.rpa.entity.robot.dto.RobotStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientComponentVO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientFlowVO;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import com.seebon.rpa.entity.robot.dto.design.RobotVersionVO;
import com.seebon.rpa.entity.robot.vo.RobotClientLogVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;

import java.util.List;
import java.util.Map;

public interface RobotClientService {
    /**
     * 登记机器人客户端。
     * 1) robotCode存在则更新，不存在则创建
     * 2) 如果ip和port与原来不一致，创建一条记录，原来的记录作废
     * 3) status 1 正常 0 离线 2 作废
     *
     * @param client
     * @return
     */
    int add(RobotClient client);

    /**
     * 登记机器人客户端。
     * 1) robotCode存在则更新，不存在则创建
     * 2) 如果ip和port与原来不一致，创建一条记录，原来的记录作废
     * 3) status 1 正常 0 离线 2 作废
     *
     * @return
     */
    int check(String machineCode);

    /**
     * 客户端心跳检测，记录机器人的存活状态。
     * 1) 更新心跳时间
     * 2) robotCode不存在，需要记录业务异常，返回0，客户端需要重新登记
     *
     * @param robotCode
     * @param status
     */
    String heartbeat(String robotCode, int status);

    /**
     * 机器人组件登记
     * 1) robotCode不存在，执行新增. 如客户端机器人安装时
     * 2) robotCode存在，更新版本号. 如客户端机器人升级时
     *
     * @param clientComponent
     * @return
     */
    int registerComponent(RobotClientComponent clientComponent);

    /**
     * 客户端组件版本检查
     *
     * @param component 组件
     * @param version   版本号
     * @return
     */
    RobotVersionVO checkComponent(String component, String version);

    /**
     * 客户端组件版本检查
     *
     * @param component 组件
     * @return 版本号
     */
    RobotComponentVO checkComponentVersion(String component);

    /**
     * 客户端监控列表
     *
     * @param map
     * @return
     */
    IgGridDefaultPage<RobotClient> monitorListPage(Map<String, Object> map);

    /**
     * 客户端组件查看
     *
     * @param appCode
     * @return
     */
    List<RobotClientComponentVO> componentList(String appCode);

    /**
     * RPA流程版本查看
     *
     * @param appCode
     * @return
     */
    List<RobotClientFlowVO> appList(String appCode);

    /**
     * 客户设备维护-列表
     *
     * @param map
     * @return
     */
    IgGridDefaultPage<RobotClientVO> listRobotClient(Map<String, Object> map);

    /**
     * 客户设备维护-列表
     *
     * @return
     */
    List<RobotClientVO> getByClientId(Integer clientId);

    /**
     * 客户设备维护-生成机器码
     *
     * @return
     */
    String generateCode();

    /**
     * 客户设备维护-日志列表
     *
     * @param map
     * @return
     */
    List<RobotClientLogVO> listRobotClientLog(Map<String, Object> map);

    /**
     * 客户设备维护-日志添加
     *
     * @return
     */
    Integer saveRobotClientLog(RobotClientLogVO logVO);

    RobotStatisticsDTO robotStatistics();

    void updateById(RobotClientVO clientVO);
}
