package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.robot.RobotAction;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;
import com.seebon.rpa.service.RobotActionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(value = "操作指令", tags = "RPA应用设计")
@RestController
@RequestMapping("/action")
public class RobotActionController {

    @Autowired
    private RobotActionService robotActionService;

    @ApiOperation(value = "操作指令-查询所有")
    @PostMapping("/all")
    public List<RobotAction> findAll() {
        return robotActionService.findAll();
    }

    @ApiOperation(value = "操作指令-行为动态字段")
    @PostMapping("/args")
    public List<RobotAppArgsVO> findArgs(@RequestParam(value = "dataCode") String dataCode) {
        return robotActionService.findActionArgs(dataCode);
    }

    @ApiOperation(value = "操作指令-目标动态字段")
    @PostMapping("/target/args")
    public List<RobotAppArgsVO> findTargetArgs(@RequestParam(value = "dataCode") String dataCode) {
        return robotActionService.findTargetArgs(dataCode);
    }
}
