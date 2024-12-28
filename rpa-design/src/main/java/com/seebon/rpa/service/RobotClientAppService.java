package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.*;
import com.seebon.rpa.entity.robot.dto.design.ClientExecutionVO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientAppVo;
import com.seebon.rpa.entity.robot.dto.design.RobotClientExecutionVO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionMo;
import com.seebon.rpa.entity.robot.vo.design.RobotExecutionDetailMoVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskVO1;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.vo.CustomerInsuredRegisterExVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RobotClientAppService {

    /**
     * 查询客户的应用
     *
     * @param map
     * @return
     */
    IgGridDefaultPage<RobotClientAppVo> appList(Map<String, Object> map);

    /**
     * 查询客户的所有应用及最新版本号
     *
     * @return
     */
    List<AppVersionDTO> findClientApps(String machineCode);

    /**
     * 查找应用的明细
     *
     * @param appCode
     * @param version
     * @return
     */
    List<RobotClientApp> findAppDetail(String appCode, String version);

    /**
     * 保存客户任务
     * 1) taskCode存在则更新，不存在则新建
     * 2) 存储4表。robot_client_task、robot_client_task_args、robot_client_task_flow、robot_client_flow_step
     * 3) 根据flowCode复制一份robot_flow_step到robot_client_flow_step
     *
     * @param
     * @return
     */
    int saveTask(RobotTask clientTask);

    /**
     * 添加执行明细
     * 保存2表。robot_client_execution、robot_client_execution_detail
     *
     * @param executionVO
     * @return
     */
    int addExecution(RobotClientExecutionVO executionVO);

    /**
     * 查看执行操作
     *
     * @param appCode
     * @return
     */
    List<ClientExecutionVO> appExecutions(String appCode);

    /**
     * 查看执行明细
     *
     * @param executionCode
     * @return
     */
    List<RobotExecutionDetailMoVO> appExecutionsDetail(String executionCode, String flowCode);

    Integer disableOrEnable(Integer status, Integer id);

    Integer removeApp(Integer id);

    Integer addApp(RobotClientApp clientApp);

    RobotClientApp checkVersion(String appCode, String version);

    List<RobotTask> listTask();

    /**
     * 执行计划列表
     *
     * @param appCode
     * @return
     */
    RobotClientAppVo queryClientTask(String appCode, Integer clientId, String machineCode);

    /**
     * 查询客户
     */
    List<CustomerBase> customerList();

    /**
     * 编辑页面
     *
     * @param id
     * @return
     */
    RobotTask editEcho(Integer id);

    int editTask(RobotTask robotTask);

    /**
     * 应用概况
     *
     * @param appCode
     * @return
     */
    RobotClientAppVo appBasic(String appCode, Integer clientId);

    /**
     * 添加客户应用
     *
     * @param robotClientAppVO
     * @return
     */
    int addClientApp(RobotClientAppVo robotClientAppVO);

    int editRobotClientApp(RobotClientAppVo robotClientAppVO);

    /**
     * 切换应用盒子
     */
    int changeRobotClientApp(RobotClientAppVo robotClientAppVO);

    /**
     * 获取盒子下任务
     */
    List<RobotTaskVO1> getClientAppTask(RobotClientAppVo robotClientAppVO);

    List<RobotExecutionMo> executions(ExecutionQueryDTO dto);

    void executionExport(HttpServletResponse response, ExecutionQueryDTO dto) throws Exception;

    /**
     * 客户应用列表导出
     *
     * @param response
     */
    void exportClientApp(HttpServletResponse response, Map<String, Object> map) throws IOException;

    /**
     * 获取客户的ip和端口
     *
     * @return
     */
    RobotClient getRobotClient();

    List<RobotTaskVO1> listAllTask(String appCode);

    Integer startTask(String taskCode);

    /**
     * 平台方可启用/停用指定申报账户的机器人
     * http://192.168.0.87:8080/browse/RPA-878
     *
     * @param argsValue
     * @param status
     * @return
     */
    Integer stopOrEnableTask(String argsValue, Integer status);

    List<RobotClient> getMachineByClientId(Integer clientId);

    void downloadFile(String fileName,String filePath,HttpServletResponse response);

    RobotExecutionMo getExecutionMessage(String executionCode);

    RobotClientDTO getRobotClientInfo(Integer clientId, String machineCode, String appCode);

    List<RobotTaskInfoDTO> getRobotTasks(Map<String,Object> params);

    List<RobotTaskConfigDTO> getRobotTaskConfigInfo(String taskCode);

    int updateTaskStatus(String appCode, String taskCode, Integer status, String comment);

    List<RobotTaskServiceItemDTO> showServiceItemFlow(String appCode, String taskCode, String accountNumber);

    int updateTaskFlowStatus(String appCode, String taskCode, Integer status, List<String> flowCodes, String comment);

    void runTask(String appCode, String taskCode, String machineCode, List<String> flowCodes, String serviceItemName, Boolean checkSchedule);

    List<RobotTaskInfoDTO> getRobotPlant(Map<String,Object> params);

    int editRobotTaskSchedule(RobotTaskScheduleDTO dto);

    List<RobotAppInfoDTO> getRobotAppList(Integer clientId);

    List<RobotTaskInfoDTO> getRobotTaskList(Integer clientId, String appCode);

    Integer checkRegister(CustomerInsuredRegisterExVO registerVO);

    List<RobotAccountStatusHistory> getAccountStatusHistory(String taskCode);

    List<RobotFlowStep> listFlowStep(RobotFlowStep flowStep);

    com.seebon.rpa.entity.robot.vo.RobotFlowVO getByFlowCode(String flowCode);

    String stopAccount(RobotTaskNotice taskNotice);

    RobotClient getRobotClientByMachineCode(String machineCode);

    RobotClientApp getRobotClientApp(String appCode, String machineCode);

    Integer selectExecutionCountByParams(Map<String,Object> params);

    int robotUpdateTaskStatus(RobotTaskStatusChangeDTO robotTaskStatusChangeDTO);

    void loginSuccess(ChangeLoginStatusDTO statusDTO);
}
