package com.seebon.rpa.utils.usb.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * command 4 the output value
 *
 * @author pengdeyi
 */
public abstract class AbstractCommandExecOutputListener implements CommandExecOutputListener {

    @Override
    public void onSuccess(List<String> lines) {
        List<String> results = Lists.newArrayList();
        for (String line : lines) {
            if (line.toUpperCase().indexOf("HUB") > -1 || line.indexOf("VID") < 0 || line.indexOf("PID") < 0) {
                continue;
            }
            results.add(line);
        }
        this.informationProcesser(results);
    }

    @Override
    public void onError(List<String> lines) {
        throw new RuntimeException("Failed to exec the command error." + JSON.toJSONString(lines));
    }

    protected abstract void informationProcesser(List<String> lines);
}
