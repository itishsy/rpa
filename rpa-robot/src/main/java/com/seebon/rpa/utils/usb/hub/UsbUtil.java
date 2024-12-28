package com.seebon.rpa.utils.usb.hub;

import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UsbUtil {

    /**
     * 只打开指定usb端口位置
     *
     * @param pos 0~47
     */
    public static boolean openOnly(int pos) {
        int device = 0;
        try {
            CharReference deviceList = new CharReference();
            int len = USBCore.INSTANTCE.GetDeviceList(deviceList, 20);
            log.info("检测到电脑有：" + len + " 个可用设备");
            if (len < 1) {
                log.info("检测到电脑无可用设备");
                return false;
            }
            for (int i = 0; i < len; i++) {
                byte port = deviceList.getByte(i);
                log.info("尝试设备:" + port);
                try {
                    Convert.sleep(1);
                    //在重新打开
                    device = USBCore.INSTANTCE.OpenDevice(port);
                    if (device <= 0) {
                        log.info("打开设备:" + port + " 失败");
                    } else {
                        log.info("打开设备:" + port + " 成功");
                        Convert.sleep(1);
                        int usbSize = USBCore.INSTANTCE.GetDeviceUSBCount(device);
                        log.info("设备拥有 " + usbSize + " 个USB端口");
                        byte[] bytes = new byte[usbSize];
                        UsbUtil.writeDevice(device, bytes, usbSize);
                        bytes[pos - 1] = 1;
                        UsbUtil.writeDevice(device, bytes, usbSize);
                        log.info("打开端口:" + pos + " 成功");
                        return true;
                    }
                } catch (Exception e) {
                    log.error("设备打开异常." + e.getMessage(), e);
                }
            }
        } finally {
            UsbUtil.closeDevice(device);
        }
        return false;
    }

    private static void closeDevice(int device) {
        log.info("关闭设备,device=" + device);
        try {
            USBCore.INSTANTCE.CloseDevice(device);
            log.info("关闭设备,device=" + device + ",完成.");
        } catch (Exception e) {
            log.error("设备关闭异常." + e.getMessage(), e);
        }
    }

    /**
     * 更改对应口的状态
     */
    private static void writeDevice(int device, byte[] bytes, int size) {
        CharReference status = new CharReference();
        status.init(bytes);
        USBCore.INSTANTCE.WriteDevice(device, status, size);
        Convert.sleep(2);
    }
}
