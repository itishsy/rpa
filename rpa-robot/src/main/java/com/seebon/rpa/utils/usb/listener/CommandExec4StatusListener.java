package com.seebon.rpa.utils.usb.listener;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.utils.usb.constants.USBStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * condition the device status....
 *
 * @author pengdeyi
 */
@Slf4j
public class CommandExec4StatusListener implements CommandExecOutputListener {

    private USBStatus status = USBStatus.UNKNOW;

    @Override
    public void onSuccess(List<String> lines) {
        log.info("run status command lines=" + JSON.toJSONString(lines));
        try {
            for (String line : lines) {
                if (line.contains("Driver is running.")) {
                    status = USBStatus.ENABLED;
                }
                if (line.contains("Device is not present.")) {
                    status = USBStatus.EJECT;
                }
                if (line.contains("Device is disabled.")) {
                    status = USBStatus.DISABLED;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(List<String> lines) {
        throw new RuntimeException("Failed to exec the status command error." + JSON.toJSONString(lines));
    }

    public USBStatus getStatus() {
        return status;
    }
}
