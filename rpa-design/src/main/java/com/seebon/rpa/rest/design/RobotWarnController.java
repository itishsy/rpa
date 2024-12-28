package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgDTO;
import com.seebon.rpa.entity.robot.dto.robotWarn.RobotWarnMsgForwardDTO;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import com.seebon.rpa.entity.robot.vo.robotWarn.RobotWarnConfigBaseVO;
import com.seebon.rpa.service.RobotWarnService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-02 10:28:25
 */
@Slf4j
@Api(value = "机器人预警", tags = "机器人预警")
@RequestMapping("/warn")
@RestController
public class RobotWarnController {

    @Autowired
    private RobotWarnService robotWarnService;

    @ApiOperation("机器人预警-新增or编辑预警配置信息")
    @PostMapping("/addOrUpdate")
    @MyLog(moduleName = "机器人预警", pageName = "机器人预警-新增or编辑预警配置信息", operation = "新增or编辑预警配置信息")
    public int addOrUpdate(@ApiParam(name = "vo", value = "预警配置信息，id为空时视为新增，id有值时视为编辑", required = true) @RequestBody RobotWarnConfigBaseVO vo) {
        return robotWarnService.addOrUpdate(vo);
    }

    @ApiOperation("机器人预警-调整预警状态")
    @PostMapping("/updateStatus/{id}/{status}")
    @MyLog(moduleName = "机器人预警", pageName = "机器人预警-调整预警状态", operation = "调整预警状态")
    public void start(@ApiParam(name = "id", value = "预警配置信息id", required = true) @PathVariable("id") Integer id,
                      @ApiParam(name = "status", value = "预警状态，1：启动、重启，0：停止", required = true) @PathVariable("status") Integer status) {
        robotWarnService.updateStatus(id, status);
    }

    @ApiOperation(value = "机器人预警-分页查询预警配置信息")
    @PostMapping(value = "/page")
    @ResponseBody
    public IgGridDefaultPage<RobotWarnConfigBaseVO> page(@RequestBody @ApiParam("分页查询对象") IgRequestObject reqObj) {
        Map<String, Object> params = RequestParamsUtil.toMap(reqObj);
        return robotWarnService.getByPage(params);
    }

    @ApiOperation(value = "机器人预警-发送预警信息")
    @PostMapping(value = "/sendExecErrorMsg")
    @MyLog(moduleName = "机器人预警", pageName = "机器人预警-发送预警信息", operation = "发送预警信息")
    public void sendExecErrorMsg(@RequestBody RobotWarnMsgDTO msgDTO) {
        robotWarnService.sendExecErrorMsg(msgDTO);
    }

    @ApiOperation(value = "机器人预警-分页查询预警配置信息")
    @PostMapping(value = "/msgPage")
    @ResponseBody
    public IgGridDefaultPage<RobotWarnMsgList> msgPage(@RequestBody @ApiParam("分页查询对象") IgRequestObject reqObj) {
        Map<String, Object> params = RequestParamsUtil.toMap(reqObj);
        return robotWarnService.getMsgPage(params);
    }

    @ApiOperation(value = "机器人预警-转发预警信息")
    @PostMapping(value = "/forwardMsg")
    public void forwardMsg(@RequestBody @ApiParam("转发信息") RobotWarnMsgForwardDTO dto) {
        robotWarnService.forwardMsg(dto);
    }
}
