package com.seebon.rpa.utils.monitor.info;

import cn.hutool.system.oshi.CpuInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>监控对象</p>
 *
 * @author xfz
 **/
@Data
@NoArgsConstructor
public class MonitorInfo {
    /**
     * 系统CPU
     */
    private CpuInfo cpuInfo;

    /**
     * 系统磁盘
     */
    private List<DiskInfo> diskList;
    /**
     * 系统内存
     */
    private MemoryInfo memoryInfo;

    /**
     * JVM内存
     */
    private RuntimeInfo runtimeInfo;
}
