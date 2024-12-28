package com.seebon.rpa.utils.usb.api;

import com.seebon.common.utils.Collections3;
import com.seebon.rpa.utils.FileStorage;
import com.seebon.rpa.utils.usb.constants.Commands;
import com.seebon.rpa.utils.usb.constants.USBStatus;
import com.seebon.rpa.utils.usb.core.UsbCommandCore;
import com.seebon.rpa.utils.usb.listener.*;
import com.seebon.rpa.utils.usb.utils.ShellUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * The API 4 operate the USB device......
 *
 * @author pengdeyi
 */
@Slf4j
public final class UsbJavaAPI {

    private UsbJavaAPI() {

    }

    /**
     * installer class
     *
     * @author pengdeyi
     */
    private static class Installer {
        public static final UsbJavaAPI INSTANCE = new UsbJavaAPI();
    }

    /**
     * strict singleton instance
     *
     * @return
     */
    public static UsbJavaAPI getUsbJavaAPI() {
        String sysName = System.getProperty("os.name");
        // not support to other plateform
        if (!sysName.toUpperCase().contains("WINDOWS")) {
            // throw new USBException(USBException.CODE_ENVIRONMENT_PLATEFORM_ER, "Please check the jar execute environment..");
        }
        // checking the path value
        String pathValue = System.getenv("Path");
        if (!pathValue.contains("devcon")) {
            // throw new USBException(USBException.CODE_ENVIRONMENT_PATH_ERR, "Please check the environment path has contains devcon..");
        }
        return Installer.INSTANCE;
    }

    /**
     * active the usb device by citykey and accountKey
     *
     * @param orginal
     */
    public void activeUSBDevice(String orginal) {
        UsbCommandCore.enableCommand(orginal);
    }

    /**
     * disable the usb device by citykey and accountKey
     *
     * @param orginal
     */
    public void disableUSBDevice(String orginal) {
        CommandExec4DisableListener listener = new CommandExec4DisableListener();
        UsbCommandCore.disableCommand(orginal, listener);
        List<String> lines = listener.getDevices();
        //是否需要Restart
        if (this.isRestart(lines)) {
            //先Restart一次
            UsbCommandCore.restartCommand(orginal);
            //再次禁用
            UsbCommandCore.disableCommand(orginal, listener);
            //再判断是否需要Restart
            if (this.isRestart(lines)) {
                throw new RuntimeException("USB端口禁用失败，请检查是否有其他证书应用占用端口。");
            }
        }
    }


    /**
     * list the usb device
     */
    public List<String> listUSBDevice() {
        //最新的usb
        CommandExec4DeviceListener listener = new CommandExec4DeviceListener();
        UsbCommandCore.listCommand(listener);
        List<String> devices = listener.getDevices();

        //默认的usb
        List<String> deviceCaches = FileStorage.loadDeviceFromDisk();

        log.info("最新的usb size=" + devices.size() + ",默认的usb size=" + deviceCaches.size());

        //新的usb
        return Collections3.subtractEx(devices, deviceCaches);
    }

    /**
     * list the usb device
     */
    public List<String> getLineList() {
        CommandExec4DeviceListener listener = new CommandExec4DeviceListener();
        UsbCommandCore.listCommand(listener);
        return listener.getLineList();
    }

    /**
     * status the usb device
     */
    public USBStatus statusUSBDevice(String device) {
        CommandExec4StatusListener listener = new CommandExec4StatusListener();
        ShellUtils.runCommand(Commands.parseCommand(Commands.STATUS_DEVICE, device), listener);
        return listener.getStatus();
    }

    /**
     * remove the usb device
     */
    public void removeUSBDevice(String device) {
        CommandExec4RemoveListener listener = new CommandExec4RemoveListener();
        UsbCommandCore.removeCommand(device, listener);
    }

    /**
     * rescan the usb device
     */
    public void rescanUSBDevice() {
        CommandExec4RescanListener listener = new CommandExec4RescanListener();
        UsbCommandCore.rescanCommand(listener);
    }

    private boolean isRestart(List<String> lines) {
        for (String line : lines) {
            if (line.contains("restart")) {
                return true;
            }
        }
        return false;
    }
}
