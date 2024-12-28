package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppSchedule;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.dto.RobotTaskServiceItemDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;


public interface RobotFlowDao extends Mapper<RobotFlow>, MySqlMapper<RobotFlow> {
    /**
     * 修改执行顺序
     *
     * @param appCode
     * @param execOrder
     * @return
     */
    int updateExecOrder(@Param("appCode") String appCode, @Param("execOrder") Integer execOrder);

    List<RobotFlowVO> showRobotFlow();

    /**
     * 发布成功 appcode下的配置状态更新
     *
     * @param appCode
     * @return
     */
    int updateFlowStatus(@Param("appCode") String appCode);

    /**
     * 查询机器人流程列表
     *
     * @param flowVO
     * @return
     */
    List<RobotFlowVO> flowList(RobotFlowVO flowVO);

    /**
     * 统计流程状态数量
     *
     * @param flowVO
     * @return
     */
    List<RobotFlowVO> totalStatus(RobotFlowVO flowVO);

    /**
     * 查询机器人流程
     *
     * @param flowCode
     * @return
     */
    RobotFlowVO getByFlowCode(@Param("flowCode") String flowCode);
    /**
     * 查询机器人流程
     *
     * @param flowCode
     * @return
     */
    RobotFlowVO findByFlowCode(@Param("flowCode") String flowCode);

    /**
     * 模板列表
     *
     * @param templateType
     * @return
     */
    List<RobotFlowVO> listTemplate(@Param("templateType") Integer templateType, @Param("flowType") String flowType);

    int updateRelationFlow(@Param("flowCode") String flowCode);

    /**
     * 流程计划列表
     *
     * @param appCode
     * @return
     */
    List<RobotFlowVO> selectFlowSchedule(@Param("appCode") String appCode);

    int updateSchedule(RobotAppSchedule robotAppSchedule);

    int selectScheduleCountByFlowCode(@Param("flowCodes") List<String> flowCodes);

    int selectTaskScheduleCountByFlowCode(@Param("taskCode") String taskCode, @Param("flowCodes") List<String> flowCodes);

    List<RobotFlowVO> selectScheduleFlow(@Param("appCode") String appCode);

    List<RobotFlow> selectMainFlowListByAppCode(@Param("appCode") String appCode);

    /**
     * 任务计划列表
     */
    List<RobotFlowVO> selectTaskSchedule(@Param("taskCode") String taskCode, @Param("appCode") String appCode);

    RobotTaskServiceItemDTO getLoginFlow(@Param("appCode") String appCode);

    List<RobotFlow> getServiceItemList(String appCode);

    /**
     * 根据taskCode查询flow
     *
     * @param taskCode 任务编码
     * @return flowDTO集合
     */
    List<com.seebon.rpa.entity.robot.vo.RobotFlowVO> findAppFlows(@Param("taskCode") String taskCode, List<String> flowCodes);

    /**
     * 根据taskCode查询flow
     *
     * @param taskCode 任务编码
     * @return flowDTO集合
     */
    List<com.seebon.rpa.entity.robot.vo.RobotFlowVO> findTaskFlows(@Param("taskCode") String taskCode, List<String> flowCodes);

    /**
     * 根据taskCode查询flow
     *
     * @param params 任务编码
     * @return flowDTO集合
     */
    List<com.seebon.rpa.entity.robot.vo.RobotFlowVO> findFlows(Map<String, Object> params);
}
