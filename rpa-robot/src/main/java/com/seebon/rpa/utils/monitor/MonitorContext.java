package com.seebon.rpa.utils.monitor;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.seebon.rpa.utils.monitor.info.DiskInfo;
import com.seebon.rpa.utils.monitor.info.MemoryInfo;
import com.seebon.rpa.utils.monitor.info.MonitorInfo;
import com.seebon.rpa.utils.monitor.info.RuntimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.util.List;

@Slf4j
public final class MonitorContext {

    public static boolean isStartTask() {
        MonitorInfo monitorInfo = MonitorContext.getMonitorInfo();
        //1、磁盘空间
        List<DiskInfo> diskInfos = monitorInfo.getDiskList();
        for (DiskInfo diskInfo : diskInfos) {
            log.info(diskInfo.toString());
            if (diskInfo.getUseRate() >= 90) {
                log.info("磁盘：" + diskInfo.getName() + " 使用率已超过：" + diskInfo.getUseRate() + "% 暂时停止新开任务.");
                return false;
            }
        }

        //2、系统内存
        MemoryInfo memoryInfo = monitorInfo.getMemoryInfo();
        if (memoryInfo.getUseRate() >= 95) {
            log.info("系统内存使用率已超过：" + memoryInfo.getUseRate() + "% 暂时停止新开任务.");
            return false;
        }
        log.info(memoryInfo.toString());

        //3、系统cpu
        CpuInfo cpuInfo = monitorInfo.getCpuInfo();
        if (cpuInfo.getUsed() >= 95) {
            log.info("系统cpu使用率已超过：" + cpuInfo.getUsed() + "% 暂时停止新开任务.");
            return false;
        }
        log.info("系统CPU：" + cpuInfo.toString());

        //4、运行内存
        RuntimeInfo runtimeInfo = monitorInfo.getRuntimeInfo();
        log.info(runtimeInfo.toString());

        return true;
    }

    public static MonitorInfo getMonitorInfo() {
        MonitorInfo monitorInfo = new MonitorInfo();
        //1、磁盘空间
        List<DiskInfo> diskList = Lists.newArrayList();
        File[] disks = File.listRoots();
        for (File file : disks) {
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setName(file.getPath());
            diskInfo.setTotalSpace(file.getTotalSpace());
            diskInfo.setUsableSpace(file.getUsableSpace());
            diskList.add(diskInfo);
        }
        monitorInfo.setDiskList(diskList);

        //2、系统内存
        monitorInfo.setMemoryInfo(new MemoryInfo());

        //3、系统cpu
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        cpuInfo.setTicks(null);
        cpuInfo.setCpuModel("");
        monitorInfo.setCpuInfo(cpuInfo);

        //4、运行内存
        monitorInfo.setRuntimeInfo(new RuntimeInfo());

        return monitorInfo;
    }
}
