package com.seebon.rpa.rest.monitor;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.service.RobotMonitorService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LY
 */
@Slf4j
@Api(value = "监控台", tags = "监控台")
@RestController
@RequestMapping("/monitor/city")
public class RobotMonitorController {


    @Autowired
    private RobotMonitorService robotMonitorService;

    @Autowired
    private RpaSaasService rpaSaasService;


    @ApiOperation(value = "监控台-城市应用列表")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotCityListVo> cityPage(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotMonitorService.listCityPage(map);
    }

    @ApiOperation(value = "监控台-城市应用详情")
    @GetMapping("/detail")
    public RobotCityListVo detail(@RequestParam("id") Long appId) {
        return robotMonitorService.detail(appId);
    }

    @ApiOperation(value = "监控台-城市统计数据")
    @GetMapping("/statistics")
    public CityStatisticsVo cityStatistics() {
        return robotMonitorService.cityStatistics();
    }

    @ApiOperation(value = "监控台-城市应用上架")
    @PutMapping("/up")
    public Integer takeUpRobotRunStatus(@RequestBody RobotCityEditVo editVo) {
        return robotMonitorService.takeUpRobotRunStatus(editVo);
    }


    @ApiOperation(value = "监控台-城市应用下架")
    @PutMapping("/down")
    @MyLog(moduleName = "监控台", pageName = "监控台-城市应用下架", operation = "城市应用下架")
    public Integer takeDownRobotRunStatus(@RequestBody RobotCityEditVo editVo) {
        return robotMonitorService.takeDownRobotRunStatus(editVo);
    }


    @ApiOperation(value = "监控台-城市应用编辑")
    @PutMapping("/edit")
    @MyLog(moduleName = "监控台", pageName = "监控台-城市应用编辑", operation = "城市应用编辑")
    public Integer editRobotRunStatus(@RequestBody RobotCityEditVo editVo) {
        return robotMonitorService.editRobotRunStatus(editVo);
    }


    @ApiOperation(value = "监控台-申报数据统计")
    @GetMapping("/declareStatistics")
    public Map<String, Integer> declareStatistics(@RequestParam("id") Long id) {
        return robotMonitorService.declareStatistics(id);
    }

    @ApiOperation(value = "监控台-详情盒子列表")
    @PostMapping("/boxPage")
    public IgGridDefaultPage<RobotClientVO> boxPage(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotMonitorService.boxPage(map);
    }

    @ApiOperation(value = "监控台-详情客户列表")
    @PostMapping("/customerPage")
    public IgGridDefaultPage<RobotClientAppVo> customerPage(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotMonitorService.customerPage(map);
    }

//    @ApiOperation(value = "监控台-详情主体列表")
    @PostMapping("/principalPage")
    public IgGridDefaultPage<RobotCityPrincipalListVo> principalPage(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotMonitorService.principalPage(map);
    }

    @ApiOperation(value = "监控台-获取客户及设备列表")
    @PostMapping("/getClientAndMaCode")
    public Map getClientAndMaCode(@RequestParam ("id")Integer id,@RequestParam ("number")Integer number) {
        return robotMonitorService.getClientAndMaCode(id,number);
    }

    @ApiOperation(value = "监控台-获取组织列表")
    @PostMapping("/getOrganizeList")
    public Map getOrganizeList(@RequestParam ("addressName")String addressName) {
        List<DeclareAccountBaseVo> organizeList=rpaSaasService.getOrganizeList(addressName);
        List<String> organizes = organizeList.stream().map(DeclareAccountBaseVo::getOrgName).collect(Collectors.toList());
        Map map=new HashMap();
        map.put("organizeList",organizes);
        return map;

    }


}
