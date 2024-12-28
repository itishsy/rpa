package com.seebon.rpa.web.rest;

import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.service.RobotArgsDefineService;
import com.seebon.rpa.service.RobotRunService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ProcessUtil;
import com.seebon.rpa.utils.usb.hub10.CharReference;
import com.seebon.rpa.utils.usb.hub10.USBCore;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/robot/start/")
public class RobotRunController {
    @Autowired
    private SyncInService syncInService;
    @Autowired
    private RobotArgsDefineService argsDefineService;
    @Autowired
    private RobotRunService robotRunService;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private DriverTarget driverTarget;

    @ApiOperation(value = "同步")
    @RequestMapping("/sync")
    public Integer syncApp() {
        try {
            return syncInService.syncAppData();
        } catch (Exception e) {
            log.error("【Exception】", e);
        }
        return -1;
    }

    @ResponseBody
    @ApiOperation(value = "启动任务的流程。本地调试调用")
    @GetMapping("/taskFlow/{taskCode}/{flowCode}")
    public List<RobotExecutionDetail> taskFlow(@PathVariable("taskCode") String taskCode, @PathVariable("flowCode") String flowCode) {
        return robotRunService.taskFlow(taskCode, flowCode);
    }

    @ResponseBody
    @ApiOperation(value = "启动任务的流程。本地调试调用")
    @GetMapping("/startFlow/{taskCode}/{flowCode}")
    public List<RobotExecutionDetail> startFlow(@PathVariable("taskCode") String taskCode, @PathVariable("flowCode") String flowCode) {
        return robotRunService.startFlow(taskCode, flowCode);
    }

    @ApiOperation(value = "启动任务的流程。本地调试调用")
    @GetMapping("/startFlowStep/{taskCode}/{flowCode}/{stepCode}")
    public List<RobotExecutionDetail> startFlowStep(@PathVariable("taskCode") String taskCode, @PathVariable("flowCode") String flowCode, @PathVariable("stepCode") String stepCode) {
        return robotRunService.startFlowStep(taskCode, flowCode, stepCode);
    }

    @ApiOperation(value = "查看运行状态：0-未运行 1-运行中，2-需重启")
    @RequestMapping("/status")
    public int runningStatus() {
        boolean isRun = ctx.isRun();
        if (isRun) {
            return 1;
        }
        if (RobotConstant.RESTART_ROBOT) {
            return 2;
        }
        return 0;
    }

    @ApiOperation(value = "查看运行步骤锁状态")
    @RequestMapping("/stepLock")
    public int stepLock() {
        return 0;
    }

    @ApiOperation(value = "绑定")
    @RequestMapping("/usb/bing/{port}")
    public void usbBing(@PathVariable("port") String port) {
        argsDefineService.bing(port);
    }

    @ApiOperation(value = "usb绑定的数据")
    @RequestMapping("/usb/data")
    public String usbData() {
        return argsDefineService.usbData();
    }

    @ApiOperation(value = "初始化USB")
    @RequestMapping("/usb/init")
    public void usbInit() {
        argsDefineService.initUsbPort();
    }

    @ApiOperation(value = "打开usb端口")
    @RequestMapping("/usb/open/{port}")
    public String usbOpen(@PathVariable("port") String port) {
        return argsDefineService.usbOpen(port);
    }

    @ApiOperation(value = "初始化进程名称")
    @RequestMapping("/process/init")
    public void initProcess() {
        ProcessUtil.init();
    }

    @ApiOperation(value = "比对进程名称")
    @RequestMapping("/process/compare")
    public Map<String, Object> compareProcess() {
        return ProcessUtil.compare();
    }

    @ApiOperation(value = "打开usb端口")
    @RequestMapping("/test/{pos}")
    public void testUsbHub(@PathVariable("pos") int pos) {
        CharReference devicelist = new CharReference();
        int len = 20;
        len = USBCore.INSTANTCE.GetDeviceList(devicelist, len);
        System.out.println("检测到电脑有" + len + "个可用设备");
        for (int i = 0; i < len; i++) {
            byte b = devicelist.getByte(i);
            int port = b;
            System.out.println("尝试端口:" + port);
            int device = USBCore.INSTANTCE.OpenDevice(port);
            if (device <= 0) {
                System.out.println("打开端口" + port + "失败");
            } else {
                System.out.println("打开端口" + port + "成功");

                int usbcount = USBCore.INSTANTCE.GetDeviceUSBCount(device);

                System.out.println("拥有" + usbcount + "个USB口!");

                CharReference deviceid = new CharReference();
                USBCore.INSTANTCE.GetDeviceId(device, deviceid, 12);

                byte[] bdeviceid = deviceid.getBytes(12);
                String str = Convert.ToHexStrFromByte(bdeviceid);

                System.out.println("设备ID为" + str);

                CharReference status = new CharReference();
                USBCore.INSTANTCE.GetDeviceStatus(device, status, 64);

                System.out.println("USB口" + pos + "现在" + (status.getByte(pos) > 0 ? "开" : "关"));

                status.setValue((byte) (status.getByte(pos) == 0 ? 1 : 0));

                System.out.println("开始设置USB口" + pos + "为" + (status.getByte(pos) > 0 ? "开" : "关"));

                int ret = USBCore.INSTANTCE.WriteDevice(device, status, 64);

                if (ret > 0) {
                    System.out.println("设置USB口" + pos + "为" + (status.getByte(pos) > 0 ? "开" : "关") + "成功!");
                } else {
                    System.out.println("设置USB口" + pos + "为" + (status.getByte(pos) > 0 ? "开" : "关") + "失败!");
                }
                USBCore.INSTANTCE.CloseDevice(device);
            }
        }
    }

    /**
     * @param status 0 关闭工具打开的浏览器  1 强制关闭chrome浏览器
     * @return
     */
    @ApiOperation(value = "关闭录制工具打开的浏览器")
    @PostMapping("/closeBrowser/{status}")
    public int closeBrowser(@PathVariable("status") Integer status) {
        if (ctx.getVariable("openToolBrowser") == null && status == 0) {
            return 1;
        }
        WebDriver driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
        if (null != driver) {
            driver.quit();
            ctx.remove(RobotConstant.DEFAULT_DRIVER_KEY);
        }
        ctx.remove(RobotConstant.OPEN_TOOL_BROWSER);
        return 1;
    }

    @ApiOperation(value = "录制工具打开长链接浏览器")
    @RequestMapping("/tool/openBrowser")
    public void openBrowser(@RequestBody Map<String, Object> optionMap) {
        Map<String, Object> options = (Map<String, Object>) optionMap.get("options");
        String driverPath = (String) optionMap.get("driverPath");
        driverTarget.setDriverPath(driverPath);
        if (optionMap.get("plugsPath") != null) {
            driverTarget.setPlugsPath((String) optionMap.get("plugsPath"));
        }
        if (optionMap.get("binaryPath") != null) {
            driverTarget.setBinaryPath((String) optionMap.get("binaryPath"));
        }
        driverTarget.setOptionMap(options);
        WebDriver webDriver = driverTarget.fetchTarget();
        if (ctx.getVariable(RobotConstant.OPEN_TOOL_BROWSER) == null) {
            throw new BusinessException("请先关闭浏览器");
        }
        String url = (String) optionMap.get("url");
        if (url != null) {
            webDriver.get(url);
        }
    }
}
