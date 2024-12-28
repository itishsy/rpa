package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.io.IoUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.UsbType;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.usb.api.UsbJavaAPI;
import com.seebon.rpa.utils.usb.constants.USBStatus;
import com.seebon.rpa.utils.usb.hub.UsbUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 使用前提：解压好devcon包，且设置好devcon包的环境变量，否则使用API会抛出环境异常，启动程序前请不要插入key.
 * 1、UsbDeviceEventNoticeListener 属于设备状态的反馈类，请注意4个状态，详见：USBStatus.
 * 2、使用API前请绑定好listener
 * 3、调用bindingUsbDevice 前插入key，请严格保证顺序，此外由于多地key都属于同一供应商设备号一致，请不要改变端口
 * 4、使用key时请必须调用activeUSBDevice，默认key属于disable的状态
 */
@Slf4j
@RobotAction(name = "USB控制", comment = "开启USB端口")
public class UsbCtrl extends AbstractAction {
    @Autowired(required = false)
    private HikariDataSource dataSource;

    @Setter
    @ActionArgs(value = "操作类型", required = true, dict = UsbType.class)
    private String actionType;

    @Value("${rpa.use-hub:0}")
    private String useHub;

    @Override
    public void run() {
        if (dataSource != null && dataSource.getJdbcUrl().contains("jdbc:mysql")) {
            log.info("已配置本地开发模式,取消USB操作.");
            ctx.setVariable("USBStatus", USBStatus.ENABLED.getKey());//默认为可用状态
            return;
        }
        String usbPort = ctx.getVariable("usbPort");
        if (StringUtils.isBlank(usbPort)) {
            throw new RobotConfigException("[uKey异常] usb端口未配置，请检查.");
        }
        log.info("检测是否插入usb扩展设备.useHub=" + useHub);
        if (StringUtils.isNotBlank(useHub) && "1".equals(useHub)) {
            if (this.existJar()) {
                boolean isOpen = this.openHub(Integer.parseInt(usbPort));
                if (isOpen) {
                    ctx.setVariable("USBStatus", USBStatus.ENABLED.getKey());//默认为可用状态
                } else {
                    throw new RuntimeException("[uKey异常] usb扩展设备，端口：" + usbPort + " 打开失败，请检查。");
                }
            } else {
                boolean isOpen = UsbUtil.openOnly(Integer.parseInt(usbPort));
                if (isOpen) {
                    RobotConstant.RESTART_ROBOT = false;//不需要重启机器人
                    ctx.setVariable("USBStatus", USBStatus.ENABLED.getKey());//默认为可用状态
                } else {
                    RobotConstant.RESTART_ROBOT = true;//重启机器人
                    throw new RuntimeException("[uKey异常] usb扩展设备，端口：" + usbPort + " 打开失败，请检查。");
                }
            }
            return;
        }
        Map<String, String> usbMap = ctx.getVariable("usbMap");
        String orginal = usbMap.get(usbPort);
        if (StringUtils.isBlank(orginal)) {
            throw new RobotConfigException("[uKey异常] usb端口未绑定，请检查.");
        }
        log.info("地区：" + ctx.getVariable("addrName") + ", 配置的usb端口：" + usbPort + ",绑定的usb码：" + orginal);
        switch (UsbType.valueOf(actionType)) {
            case active: {
                ctx.setVariable("USBStatus", USBStatus.UNKNOW.getKey());

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
                    Convert.sleep(1);
                    UsbJavaAPI.getUsbJavaAPI().removeUSBDevice(device);
                }

                //激活当前设备
                UsbJavaAPI.getUsbJavaAPI().activeUSBDevice(currentDevice);

                //休眠1秒
                Convert.sleep(1);

                //获取当前端口状态
                USBStatus usbStatus = UsbJavaAPI.getUsbJavaAPI().statusUSBDevice(currentDevice);
                log.info("当前USB编码=" + currentDevice + " 当前USB状态=" + usbStatus.getDescription());
                ctx.setVariable("USBStatus", usbStatus.getKey());
                break;
            }
            case out: {
                List<String> devices = UsbJavaAPI.getUsbJavaAPI().listUSBDevice();
                String device = Convert.getDevice(devices, orginal);
                UsbJavaAPI.getUsbJavaAPI().disableUSBDevice(device);
                break;
            }
            default:
                break;
        }
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
}
