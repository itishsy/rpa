package com.seebon.rpa.rest.design;

import cn.hutool.core.date.DateUtil;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.EmployeeChangeTrackDTO;
import com.seebon.rpa.entity.robot.dto.GenerateQueueDTO;
import com.seebon.rpa.entity.robot.dto.RobotLoginAuthDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskQueueGenerate;
import com.seebon.rpa.entity.robot.vo.GenerateQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotLoginAuthVO;
import com.seebon.rpa.entity.robot.vo.RobotTrackVO;
import com.seebon.rpa.schedule.RobotLoginAuthNoticeTask;
import com.seebon.rpa.service.RobotLoginAuthService;
import com.seebon.rpa.service.impl.component.RobotTaskQueueComponent;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "集中登录验证", tags = "集中登录验证")
@RestController
@RequestMapping("/loginAuth")
public class RobotLoginAuthController {

    @Autowired
    private RobotLoginAuthService loginAuthService;
    @Autowired
    private RobotTaskQueueComponent taskQueueComponent;
    @Autowired
    private RobotLoginAuthNoticeTask loginAuthNoticeTask;

    @ApiOperation(value = "列表-查询")
    @PostMapping("/pageList")
    @ApiImplicitParams({
            @ApiImplicitParam(name="addrName",value="参保城市",dataType = "string",paramType = "body"),
            @ApiImplicitParam(name="declareAccount",value="申报账户"),
            @ApiImplicitParam(name="processStatusList",value="处理状态  0待登录 1已登录",allowMultiple = true,dataType = "string",paramType = "body")
    })
    public IgGridDefaultPage<RobotLoginAuthVO> pageList(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return loginAuthService.pageList(map);
    }

    @ApiOperation(value = "查询当前用户未登录数量")
    @PostMapping("/curUserNoLoginCount")
    public Integer curUserNoLoginCount(@RequestBody Map<String, Object> map) {
        return loginAuthService.curUserNoLoginCount(map);
    }

    @ApiOperation(value = "启动登录")
    @PostMapping("/startLogin")
    public Boolean startLogin(Integer id) {
        return loginAuthService.startLogin(id);
    }

    @ApiOperation(value = "修改登录状态")
    @PostMapping("/updateLoginStatus")
    public Boolean updateLoginStatus(@RequestBody ChangeLoginStatusDTO changeLoginStatusDTO) {
        return loginAuthService.updateLoginStatus(changeLoginStatusDTO);
    }

    @ApiOperation(value = "校验H5执行方式并执行")
    @PostMapping("/checkLoginTypeAndToDo")
    public Boolean checkLoginTypeAndToDo(@RequestBody RobotLoginAuthDTO robotLoginAuthDTO) {
        loginAuthService.addLoginAuthOrAddQueue(robotLoginAuthDTO);
        return Boolean.TRUE;
    }

    @ApiOperation(value = "生成任务队列")
    @PostMapping("/generateQueueOrLoginAuth")
    public GenerateQueueVO generateQueueOrLoginAuth(@RequestBody List<GenerateQueueDTO> generateQueueDTOList){
        return loginAuthService.generateQueueOrLoginAuth(generateQueueDTOList);
    }

    @ApiOperation(value = "触发自动生成任务")
    @PostMapping("/triggerGenerateQueue")
    public Boolean triggerGenerateQueue(
            @RequestParam(required = false) List<String> taskCodeList,
            @RequestParam(required = false) List<String> appCodeList
    ) {
        taskQueueComponent.generateQueue(DateUtil.date(),taskCodeList,appCodeList);
        return Boolean.TRUE;
    }
    @ApiOperation(value = "触发自动生成任务（全部）")
    @GetMapping("/triggerGenerateQueueAll")
    public Boolean triggerGenerateQueueAll() {
        taskQueueComponent.generateQueue(DateUtil.date());
        return Boolean.TRUE;
    }
    @ApiOperation(value = "触发自动发送登录提醒任务")
    @PostMapping("/triggerLoginAuthNotice")
    public Boolean triggerLoginAuthNotice() {
        loginAuthNoticeTask.notifyLoginRobot();
        return Boolean.TRUE;
    }


    @ApiOperation(value = "查询执行轨迹状态")
    @PostMapping("/queryEmployeeChangeTrackStatus")
    public RobotTrackVO queryEmployeeChangeTrackStatus(@RequestBody EmployeeChangeTrackDTO employeeChangeTrackDTO){
        return loginAuthService.queryEmployeeChangeTrackStatus(employeeChangeTrackDTO);
    }

    @ApiOperation(value = "查询执行轨迹信息")
    @PostMapping("/queryEmployeeChangeTrackInfo")
    public RobotTrackVO queryEmployeeChangeTrackInfo(@RequestBody EmployeeChangeTrackDTO employeeChangeTrackDTO){
        return loginAuthService.queryEmployeeChangeTrackInfo(employeeChangeTrackDTO);
    }

    @ApiOperation(value = "查找队列生成记录")
    @PostMapping("/findTaskQueueGenerate")
    public List<RobotTaskQueueGenerate> findTaskQueueGenerate(@RequestBody String queryStr, @RequestParam(required = false) Integer limit, @RequestParam(required = false) String orderBy, @RequestParam(required = false) String orderDirection){
        return loginAuthService.findTaskQueueGenerate(queryStr, limit, orderBy, orderDirection);
    }

}
