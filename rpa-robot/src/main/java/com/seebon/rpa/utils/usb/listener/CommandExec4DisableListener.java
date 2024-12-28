package com.seebon.rpa.utils.usb.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * command exec 4 disable listener....
 *
 * @author pengdeyi
 */
@Slf4j
public class CommandExec4DisableListener implements CommandExecOutputListener {

    private List<String> devices = Lists.newArrayList();

    @Override
    public void onSuccess(List<String> lines) {
        log.info("run disable command lines=" + JSON.toJSONString(lines));
        this.setDevices(lines);
    }

    @Override
    public void onError(List<String> lines) {
        throw new RuntimeException("Failed to exec the disable command error." + JSON.toJSONString(lines));
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
    }
}
