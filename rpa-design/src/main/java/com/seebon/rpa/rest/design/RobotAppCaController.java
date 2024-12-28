package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.service.RobotAppCaService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Api(value = "应用证书", tags = "应用证书")
@RestController
@RequestMapping("/appCa")
public class RobotAppCaController {
    @Autowired
    private RobotAppCaService appCaService;

    @ApiOperation(value = "应用证书-查询")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotAppCaVO> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return appCaService.list(map);
    }

    @ApiOperation(value = "应用证书-保存")
    @PostMapping("/save")
    public Integer save(@RequestBody RobotAppCa appCa) {
        return appCaService.save(appCa);
    }

    @ApiOperation(value = "应用证书-获取")
    @GetMapping("/getById")
    public RobotAppCaVO getById(@RequestParam("id") Integer id) {
        return appCaService.getById(id);
    }

    @ApiOperation(value = "应用证书-重新同步")
    @PostMapping("/againSync")
    public void againSync(@RequestParam(value = "id", required = false) Integer id) {
        appCaService.againSync(id);
    }

    @ApiOperation(value = "应用证书-启用状态")
    @PostMapping("/disabled")
    public void disabled(RobotAppCa appCa) {
        appCaService.disabled(appCa);
    }
}
