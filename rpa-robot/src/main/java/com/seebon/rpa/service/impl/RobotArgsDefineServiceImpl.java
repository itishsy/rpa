package com.seebon.rpa.service.impl;

import cn.hutool.core.io.IoUtil;
import com.google.common.collect.Lists;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.entity.robot.RobotAppEnv;
import com.seebon.rpa.entity.robot.RobotClientUsb;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotAppEnvMapper;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import com.seebon.rpa.repository.mapper.RobotFlowStepMapper;
import com.seebon.rpa.repository.mapper.RobotTaskMapper;
import com.seebon.rpa.service.RobotArgsDefineService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.FileStorage;
import com.seebon.rpa.utils.python.PythonUtil;
import com.seebon.rpa.utils.usb.api.UsbJavaAPI;
import com.seebon.rpa.utils.usb.constants.USBStatus;
import com.seebon.rpa.utils.usb.core.UsbCommandCore;
import com.seebon.rpa.utils.usb.hub.UsbUtil;
import com.seebon.rpa.utils.usb.listener.CommandExec4DeviceListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author hao
 * @Date 2022/11/3 11:20
 * @Version 1.0
 **/
@Slf4j
@Service
public class RobotArgsDefineServiceImpl implements RobotArgsDefineService {
    @Autowired
    private RobotTaskMapper robotTaskMapper;
    @Autowired
    private RobotAppEnvMapper appEnvMapper;
    @Autowired
    private RobotFlowMapper flowMapper;
    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RpaDesignService rpaDesignService;

    @Value("${rpa.use-hub:0}")
    private String useHub;

    @Override
    public Boolean haveTask() {
        return CollectionUtils.isNotEmpty(robotTaskMapper.selectAll());
    }

    @Override
    public void bing(String port) {
        log.info("开始绑定：port=" + port);
        List<String> devices = UsbJavaAPI.getUsbJavaAPI().listUSBDevice();
        log.info("新增：" + devices.size());
        for (String device : devices) {
            log.info("new bing device=" + device);
            RobotClientUsb usb = new RobotClientUsb();
            usb.setMachineCode(FileStorage.loadMachineCodeFromDisk());
            usb.setSort(port);
            usb.setOrginal(device.split("\\\\")[2]);
            usb.setCreateTime(new Date());
            usb.setUpdateTime(new Date());
            rpaDesignService.saveUsb(usb);

            //缓存文件
            FileStorage.diskSavePorts(port + "|" + usb.getOrginal());
        }

        log.info("结束绑定：port=" + port);
    }

    @Override
    public String usbData() {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = FileStorage.loadPortFromDisk();
        if (map == null || map.isEmpty()) {
            return "";
        }
        for (String key : map.keySet()) {
            sb.append(key + "|" + map.get(key) + ";");
        }
        return sb.toString();
    }

    @Override
    public void initUsbPort() {
        //删除已初始化的文件
        File file = FileStorage.getFile(FileStorage.STORAGE_FILE_INIT_NAME);
        if (file.exists()) {
            file.delete();
        }
        File port = FileStorage.getFile(FileStorage.STORAGE_FILE_PORT_NAME);
        if (port.exists()) {
            port.delete();
        }
        UsbCommandCore.listCommand(new CommandExec4DeviceListener());
        rpaDesignService.clearUsb(FileStorage.loadMachineCodeFromDisk());
    }

    @Override
    public String usbOpen(String port) {
        if (StringUtils.isBlank(port)) {
            throw new RobotConfigException("usb端口为空.");
        }
        log.info("检测是否插入usb扩展设备.isHub=" + useHub);
        if (StringUtils.isNotBlank(useHub) && "1".equals(useHub)) {
            if (this.existJar()) {
                boolean isOpen = this.openHub(Integer.parseInt(port));
                if (!isOpen) {
                    throw new RuntimeException("[uKey异常] usb扩展设备，端口：" + port + " 打开失败，请检查。");
                }
            } else {
                boolean isOpen = UsbUtil.openOnly(Integer.parseInt(port));
                if (!isOpen) {
                    throw new RuntimeException("usb扩展设备，端口：" + port + " 打开失败，请检查。");
                }
            }
            return "usb扩展设备，端口：" + port + " 打开成功";
        }

        Map<String, String> usbMap = FileStorage.loadPortFromDisk();
        if (MapUtils.isEmpty(usbMap)) {
            throw new RuntimeException("usb未绑定端口");
        }

        String orginal = usbMap.get(port);
        if (StringUtils.isBlank(orginal)) {
            throw new RobotConfigException("usb端口未绑定，请检查.");
        }
        log.info("USB端口：" + port + ",USB码：" + orginal);

        //关闭所有证书
        this.closeAll();

        //1、rescan所有设备
        UsbJavaAPI.getUsbJavaAPI().rescanUSBDevice();
        //休眠1秒
        Convert.sleep(1);

        //2、list最新的所有设备
        List<String> devices = UsbJavaAPI.getUsbJavaAPI().listUSBDevice();
        //当前设备
        String currentDevice = Convert.getDevice(devices, orginal);
        //禁用并删除所有端口
        for (String device : devices) {
            if (currentDevice.equals(device)) {
                log.info("当前设备：" + device + " 跳过，不禁用且不删除.");
                continue;
            }
            if (!Convert.isBing(usbMap, device)) {
                log.info("当前设备：" + device + "，不是绑定设备码，跳过.");
                continue;
            }
            UsbJavaAPI.getUsbJavaAPI().disableUSBDevice(device);
            Convert.sleep(2);

            UsbJavaAPI.getUsbJavaAPI().removeUSBDevice(device);
            Convert.sleep(1);
        }

        //激活当前设备
        UsbJavaAPI.getUsbJavaAPI().activeUSBDevice(currentDevice);

        //休眠1秒
        Convert.sleep(1);

        //获取当前端口状态
        USBStatus usbStatus = UsbJavaAPI.getUsbJavaAPI().statusUSBDevice(currentDevice);
        return "USB端口：" + port + ",USB编码=" + currentDevice + ",USB状态=" + usbStatus.getDescription();
    }

    private void closeAll() {
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Close.py");
        List<String> exePaths = this.getExePath();
        if (CollectionUtils.isEmpty(exePaths)) {
            return;
        }
        commands.add(this.getExeName(exePaths));
        PythonUtil.runCommand("win应用关闭", null, 30, commands);
    }

    private List<String> getExePath() {
        List<RobotAppEnv> appEnvs = appEnvMapper.selectAll();
        if (CollectionUtils.isEmpty(appEnvs)) {
            return null;
        }
        return appEnvs.stream().map(a -> a.getPath()).distinct().collect(Collectors.toList());
    }

    private String getExeName(List<String> paths) {
        return paths.stream().map(path -> StringUtils.substringAfterLast(path, "\\")).collect(Collectors.joining(","));
    }

    public boolean openHub(Integer usbPort) {
        try {
            // 指定要执行的 JAR 文件路径
            String jarFilePath = RobotContext.workPath + "\\rpa-robot-hub-0.0.1.jar";

            // 构建执行命令，包括 JAR 文件路径和参数
            String[] command = {RobotContext.workPath + "\\bin\\java", "-jar", jarFilePath, usbPort.toString()};
            // 执行命令
            Process pid = Runtime.getRuntime().exec(command);
            String result = this.getExecResult(pid);
            // 等待命令执行完毕
            int exitCode = pid.waitFor();
            log.info("exitCode=" + exitCode + ",result=" + result);
            if (result.contains("打开端口:" + usbPort + " 成功")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getExecResult(Process pid) {
        log.info("shell exec start");
        try {
            //脚本执行正常时的输出信息
            String inputResult = IoUtil.read(pid.getInputStream(), Charset.forName("GBK"));
            //脚本执行异常时的输出信息
            String errorResult = IoUtil.read(pid.getErrorStream(), Charset.forName("GBK"));
            return inputResult + " " + errorResult;
        } catch (Exception e) {
            log.error("shell exec error." + e.getMessage(), e);
        }
        log.info("shell exec end");
        return null;
    }

    private boolean existJar() {
        String jarFilePath = RobotContext.workPath + "\\rpa-robot-hub-0.0.1.jar";
        return new File(jarFilePath).exists();
    }

    @Override
    @Transactional
    public String copyFlowStep(String flowCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow flow = flowMapper.selectOneByExample(example);

        example = new Example(RobotFlowStep.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        List<RobotFlowStep> flowSteps = flowStepMapper.selectByExample(example);
        String subFlowName = null;
        for (RobotFlowStep step : flowSteps) {
            if ("subFlow".equals(step.getActionCode())) {
                subFlowName = step.getStepName();
            }
        }
        if (StringUtils.isBlank(subFlowName)) {
            return "主流程不存在子流程或者已合并完成.";
        }
        example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", flow.getAppCode()).andEqualTo("flowName", subFlowName);
        RobotFlow subFlow = flowMapper.selectOneByExample(example);

        example = new Example(RobotFlowStep.class);
        example.createCriteria().andEqualTo("flowCode", subFlow.getFlowCode());
        List<RobotFlowStep> subFlowSteps = flowStepMapper.selectByExample(example);
        Integer num = 1;
        for (RobotFlowStep step : subFlowSteps) {
            RobotFlowStep add = new RobotFlowStep();
            BeanUtils.copyProperties(step, add);
            add.setId(null);
            add.setFlowCode(flowCode);
            add.setNumber(flowSteps.size() + (++num));

            flowStepMapper.insertSelective(add);
        }
        return "复制完成";
    }

    @Override
    @Transactional
    public String copyFlow(String sourceFlowCode, String targetFlowCode) {
        //删除目标流程步骤
        Example example = new Example(RobotFlowStep.class);
        example.createCriteria().andEqualTo("flowCode", targetFlowCode);
        flowStepMapper.deleteByExample(example);

        //把源流程步骤复制到目标流程
        example = new Example(RobotFlowStep.class);
        example.createCriteria().andEqualTo("flowCode", sourceFlowCode);
        List<RobotFlowStep> sourceFlowSteps = flowStepMapper.selectByExample(example);
        for (RobotFlowStep step : sourceFlowSteps) {
            RobotFlowStep add = new RobotFlowStep();
            BeanUtils.copyProperties(step, add);
            add.setId(null);
            add.setFlowCode(targetFlowCode);

            flowStepMapper.insertSelective(add);
        }
        return "复制完成";
    }

    @Override
    @Transactional
    public String copyApp(String sourceAppCode, String targetAppCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", targetAppCode);
        List<RobotFlow> targetFlows = flowMapper.selectByExample(example);
        Map<String, RobotFlow> targetFlowMap = targetFlows.stream().collect(Collectors.toMap(k -> k.getFlowName(), v -> v));

        //把源流程步骤复制到目标流程
        example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("appCode", sourceAppCode);
        List<RobotFlow> sourceFlows = flowMapper.selectByExample(example);
        Map<String, RobotFlow> sourceFlowMap = sourceFlows.stream().collect(Collectors.toMap(k -> k.getFlowName(), v -> v));
        for (RobotFlow targetFlow : targetFlows) {
            if (sourceFlowMap.containsKey(targetFlow.getFlowName())) {
                this.copyFlow(sourceFlowMap.get(targetFlow.getFlowName()).getFlowCode(), targetFlow.getFlowCode());
            }
        }
        for (RobotFlow sourceFlow : sourceFlows) {
            if (targetFlowMap.containsKey(sourceFlow.getFlowName())) {
                continue;
            }
            String newFlowCode = UUIDGenerator.uuidStringWithoutLine();
            RobotFlow add = new RobotFlow();
            BeanUtils.copyProperties(sourceFlow, add);
            add.setId(null);
            add.setAppCode(targetAppCode);
            add.setFlowCode(newFlowCode);
            flowMapper.insertSelective(add);

            this.copyFlow(sourceFlow.getFlowCode(), newFlowCode);
        }
        return "复制完成";
    }
}
