package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.Robot;
import com.seebon.rpa.entity.robot.RobotApp;
import com.seebon.rpa.entity.robot.RobotAppExplain;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.dto.history.RobotAppHistory;
import com.seebon.rpa.entity.robot.vo.RobotAppAddVO;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStatusHistoryVO;
import com.seebon.rpa.service.RobotAppService;
import com.seebon.rpa.service.RobotArgsDefineService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "应用管理", tags = "RPA应用设计")
@RestController
@RequestMapping("/app")
public class RobotAppController {
    @Autowired
    private RobotAppService robotAppService;
    @Autowired
    private RobotArgsDefineService argsDefineService;

    @ApiOperation(value = "应用管理-主列表")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotAppVO> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotAppService.listPage(map);
    }

    @ApiOperation("应用管理-新增")
    @PostMapping(value = "/add")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-新增应用", operation = "新增应用")
    public Integer addRobotApp(@RequestBody RobotAppAddVO robotAppAddVO) {
        return robotAppService.addRobotApp(robotAppAddVO);
    }

    @ApiOperation("应用管理-新增应用配置逻辑")
    @PostMapping(value = "/addAppExplain")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-新增应用配置逻辑", operation = "新增应用配置逻辑")
    public Integer addAppExplain(@RequestBody RobotAppExplain appExplain) {
        return robotAppService.addAppExplain(appExplain);
    }

    @ApiOperation("应用管理-获取应用配置逻辑")
    @PostMapping(value = "/getExplain")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-获取应用配置逻辑", operation = "获取应用配置逻辑")
    public RobotAppExplain getAppExplain(String appCode) {
        return robotAppService.getAppExplain(appCode);
    }

    @ApiOperation("应用管理-加载动态字段")
    @PostMapping(value = "/dynamic/field")
    public List<RobotArgsDefine> findArgsDefine(String robotType) {
        return argsDefineService.findArgs("robot", robotType);
    }

    @ApiOperation(value = "应用管理-发版")
    @PostMapping("/release")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-发版", operation = "发版")
    public int release(@RequestBody RobotAppVO1 robotAppVO) {
        return robotAppService.release(robotAppVO);
    }

    @ApiOperation(value = "应用管理-同步接受")
    @PostMapping("/syncIn")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-接受同步", operation = "接受同步")
    public int syncIn(@ApiParam(name = "robotAppInfos",value = "同步的应用信息") @RequestBody RobotAppHistory robotAppInfos) {
        return robotAppService.syncInRobotAppInfos(robotAppInfos);
    }

    @ApiOperation(value = "应用管理-同步对外")
    @PostMapping("/syncOut")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-对外同步", operation = "对外同步")
    public int syncOut(@RequestParam(value = "appCode") String appCode, @RequestParam(value = "comment") String comment) {
        return robotAppService.syncOut(appCode, comment);
    }

    @ApiOperation(value = "应用管理-查看历史版本")
    @PostMapping("/history")
    public List<RobotAppHistoryVO> history(@RequestParam(value = "appCode") String appCode) {
        return robotAppService.findHistory(appCode);
    }

    @ApiOperation(value = "应用管理-使用此版本")
    @PostMapping("/activate")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-使用此版本", operation = "使用此版本")
    public int activate(@RequestParam(value = "appCode") String appCode, @RequestParam(value = "version") String version) {
        return robotAppService.activate(appCode, version);
    }

    @ApiOperation(value = "应用管理-所有应用")
    @PostMapping("/all")
    public List<RobotApp> allApp() {
        return robotAppService.allApp();
    }

    @ApiOperation(value = "应用管理-所有应用")
    @PostMapping("/list")
    public List<RobotApp> list() {
        return robotAppService.list();
    }

    @ApiOperation("应用管理-机器人类型")
    @PostMapping(value = "/type")
    public List<Robot> listRobotSort() {
        return robotAppService.listRobot();
    }


    @ApiOperation("应用管理-查询应用名称")
    @PostMapping(value = "/queryAppName")
    public String queryAppName(String appCode) {
        return robotAppService.queryAppName(appCode);
    }

    @ApiOperation("根据当前用户的客户id，返回所有机器人")
    @PostMapping("/listAppByClientId")
    public List<RobotClientVO> listAppByClientId() {
        return robotAppService.getAppByClientId();
    }

    @ApiOperation("根据当前用户的客户id，返回所有应用")
    @PostMapping("/listAppByCustId")
    public List<RobotClientAppVo> listAppByCustId() {
        return robotAppService.listAppByCustId();
    }

    @ApiOperation("根据当前用户的客户id，是否有未激活应用")
    @PostMapping("/hasActivateAppByCustId")
    public Boolean hasActivateAppByCustId() {
        return robotAppService.hasActivateAppByCustId();
    }

    @ApiOperation("应用管理-查询新")
    @PostMapping("/getAppData")
    public IgGridDefaultPage<RobotAppDesignVO> getAppData(@RequestBody IgRequestObject reqObj)  {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotAppService.getAppData(map);
    }

    @ApiOperation("应用管理-下线应用")
    @PostMapping("/offlineStatus")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-下线应用", operation = "下线应用")
    public void offlineStatus(String appCode,String comment,Integer appStatus)  {
        robotAppService.offlineStatus(appCode,comment,appStatus);
    }

    @ApiOperation("应用管理-调整应用状态")
    @PostMapping("/updateAppStatus")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-调整应用状态", operation = "调整应用状态")
    public void updateAppStatus(String appCode,Integer appStatus,String remark)  {
        robotAppService.updateAppStatus(appCode,appStatus,remark);
    }

    @ApiOperation("应用管理-查看应用历史记录")
    @PostMapping("/selectAppHistoryStatus")
    public List<RobotFlowStatusHistoryVO> selectAppHistoryStatus(String appCode)  {
        return robotAppService.selectAppHistoryStatus(appCode);
    }

    @ApiOperation("应用管理-获取应用统计")
    @PostMapping("/getAppCount")
    public RobotCountVO getAppCount() {
        return robotAppService.getAppCount();
    }

    @ApiOperation("应用管理-excel导出")
    @PostMapping("/downExcel")
    public void downExcel(@RequestBody IgRequestObject reqObj,HttpServletResponse response) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
         robotAppService.downExcel(map, response);
    }

    @ApiOperation("应用管理-编辑")
    @PostMapping(value = "/updateApp")
    @MyLog(moduleName = "应用管理", pageName = "应用管理-编辑应用", operation = "编辑应用")
    public void updateApp(@RequestBody RobotAppAddVO robotAppAddVO) {
        robotAppService.updateApp(robotAppAddVO);
    }
}
