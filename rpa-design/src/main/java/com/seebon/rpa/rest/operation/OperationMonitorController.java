package com.seebon.rpa.rest.operation;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.dto.design.HandleMessageDTO;
import com.seebon.rpa.entity.robot.dto.design.HandleMessageVo;
import com.seebon.rpa.entity.robot.dto.design.OperationMonitorVo;
import com.seebon.rpa.entity.robot.dto.design.RobotOperationExcelVo;
import com.seebon.rpa.entity.robot.vo.design.RobotOperationMonitorDetailVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.vo.system.customer.CustomerBaseVO;
import com.seebon.rpa.service.OperationMonitorService;
import com.seebon.rpa.utils.BeanCopyUtils;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023/4/12 17:20
 */
@Api(value = "运维监控", tags = "运维监控")
@RestController
@RequestMapping("/operation")
public class OperationMonitorController{

    @Autowired
    private OperationMonitorService operationMonitorService;

    @ApiOperation(value = "运维监控-分页查询列表")
    @PostMapping("/page")
    public IgGridDefaultPage<OperationMonitorVo> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return operationMonitorService.listPage(map);
    }


    @ApiOperation(value = "预警明细-分页查询列表")
    @PostMapping("/operationDetail")
    public IgGridDefaultPage<RobotOperationMonitorDetailVO> operationDetail(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return operationMonitorService.queryDetailPage(map);
    }

    @ApiOperation(value = "运维监控-列表分页查询（新）")
    @PostMapping("/operationPage")
    public IgGridDefaultPage<RobotOperationMonitorDetailVO> operationPage(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return operationMonitorService.operationPage(map);
    }

    @ApiOperation(value = "运维监控-下载异常")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        operationMonitorService.export(response, map);
    }

    @ApiOperation("查询运营预警客户")
    @PostMapping(value = "/operationListCustomer")
    public List<CustomerBaseVO> operationListCustomer() {
        List<CustomerBase> customerBases = operationMonitorService.operationListCustomer();
        List<CustomerBaseVO> customerBaseVOS = BeanCopyUtils.copyListProperties(customerBases, CustomerBaseVO::new);
        customerBaseVOS.forEach(s -> s.setCustomerName(s.getName()));
        return customerBaseVOS;
    }

    @ApiOperation(value = "运维监控-办结")
    @PostMapping("/finish")
    @MyLog(moduleName = "运维监控", pageName = "预警信息-办结信息处理", operation = "办结")
    public int finish(@ApiParam(name = "dto", value = "参数信息", required = true) @RequestBody HandleMessageDTO dto) {
        return operationMonitorService.finish(dto);
    }

    @ApiOperation(value = "运维监控-调整预警项目")
    @PostMapping("/updateWarnType/{warnType}")
    public int updateWarnType(@ApiParam(name = "warnType", value = "调整后的预警项目", required = true)@PathVariable("warnType") String warnType,
                              @ApiParam(name = "detailVO", value = "被调整的预警记录", required = true)@RequestBody RobotOperationMonitorDetailVO detailVO) {
        return operationMonitorService.updateWarnType(warnType, detailVO);
    }

    @ApiOperation(value = "预警信息-办结信息处理")
    @PostMapping("/updateHandleMessage")
    @MyLog(moduleName = "运维监控", pageName = "预警信息-办结信息处理", operation = "办结信息处理")
    public int updateHandleMessage(@RequestBody HandleMessageVo handleMessageVo) {
        return operationMonitorService.updateExceptionMessage(handleMessageVo);
    }

    @ApiOperation(value = "预警信息-监控运维数量统计")
    @PostMapping("/operationCount")
    public Map<String, Integer> operationCount() {
        return operationMonitorService.operationCount();
    }

    @ApiOperation("导出运维监控异常信息")
    @PostMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response,@RequestBody RobotOperationExcelVo robotOperationExcelVo){
        operationMonitorService.downloadExcel(response,robotOperationExcelVo);

    }
}
