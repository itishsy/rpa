package com.seebon.rpa.utils.usb.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * condition the device status....
 *
 * @author pengdeyi
 */
@Slf4j
public class CommandExec4RemoveListener implements CommandExecOutputListener {

    @Override
    public void onSuccess(List<String> lines) {
        log.info("run remove command lines=" + JSON.toJSONString(lines));
    }

    @Override
    public void onError(List<String> lines) {
        throw new RuntimeException("Failed to exec the remove command error." + JSON.toJSONString(lines));
    }
}
