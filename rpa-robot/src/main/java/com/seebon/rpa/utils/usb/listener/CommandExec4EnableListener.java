package com.seebon.rpa.utils.usb.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * enable command....
 *
 * @author pengdeyi
 */
@Slf4j
public class CommandExec4EnableListener implements CommandExecOutputListener {

    @Override
    public void onSuccess(List<String> lines) {
        log.info("run enable command lines=" + JSON.toJSONString(lines));
    }

    @Override
    public void onError(List<String> lines) {
        throw new RuntimeException("Failed to exec the enable command error." + JSON.toJSONString(lines));
    }
}
