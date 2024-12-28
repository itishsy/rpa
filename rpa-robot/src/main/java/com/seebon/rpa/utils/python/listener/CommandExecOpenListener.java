package com.seebon.rpa.utils.python.listener;

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
public class CommandExecOpenListener implements CommandExecOutputListener {

    private List<String> lineList = Lists.newArrayList();

    @Override
    public void onSuccess(List<String> lines) {
        //log.info("run open command lines=" + JSON.toJSONString(lines));
        this.lineList.clear();
        this.lineList.addAll(lines);
    }

    @Override
    public void onError(List<String> lines) {
        log.info("Failed to exec the open command error lines=" + JSON.toJSONString(lines));
    }

    public List<String> getLineList() {
        return lineList;
    }
}
