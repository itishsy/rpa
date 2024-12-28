package com.seebon.rpa.utils.usb.listener;

import com.google.common.collect.Lists;
import com.seebon.rpa.utils.FileStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * chars increament  ......
 * USB\VID_093A&PID_2510\5&2BD39077&0&7                        : USB
 * USB\VID_1C4F&PID_0002&MI_00\6&F93F708&0&0000                : USB
 * USB\VID_8087&PID_8000\5&345AB59D&0&1                        : Generic USB Hub
 * USB\VID_1C4F&PID_0002\5&2BD39077&0&8                        : USB Composite Device
 * USB\VID_8087&PID_8008\5&33B44002&0&1                        : Generic USB Hub
 * USB\ROOT_HUB20\4&29A40457&0                                 : USB Root Hub
 * USB\ROOT_HUB20\4&2EF88D67&0                                 : USB Root Hub
 * USB\VID_1C4F&PID_0002&MI_01\6&F93F708&0&0001                : USB
 *
 * @author pengdeyi
 */
@Slf4j
public class CommandExec4DeviceListener extends AbstractCommandExecOutputListener {

    private List<String> devices = Lists.newArrayList();

    private List<String> lineList = Lists.newArrayList();

    @Override
    protected void informationProcesser(List<String> lines) {
        //先清空
        this.devices.clear();
        this.lineList.clear();

        //转换
        for (String line : lines) {
            lineList.add(line);
            log.info("device=" + line);
            if (line.contains(":")) {
                String device = line.split(":")[0].trim();
                devices.add(device);
            } else {
                devices.add(line);
            }
        }
        FileStorage.diskSaveDevices(devices);
    }

    public List<String> getDevices() {
        return devices;
    }

    public List<String> getLineList() {
        return lineList;
    }
}
