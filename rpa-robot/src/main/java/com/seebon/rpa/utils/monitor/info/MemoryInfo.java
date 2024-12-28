package com.seebon.rpa.utils.monitor.info;

import cn.hutool.core.io.FileUtil;
import com.sun.management.OperatingSystemMXBean;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

/**
 * 系统内存
 */
public class MemoryInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DecimalFormat format = new DecimalFormat("#0.00");
    private static final OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * 获取系统内存总容量
     *
     * @return
     */
    public final long getTotalMemory() {
        return mem.getTotalPhysicalMemorySize();
    }

    /**
     * 获取系统可用内存容量
     *
     * @return
     */
    public final long getFreeMemory() {
        return mem.getFreePhysicalMemorySize();
    }

    /**
     * 获取系统内存使用率
     *
     * @return
     */
    public float getUseRate() {
        return (float) (((getTotalMemory() - getFreeMemory()) * 1.0 / getTotalMemory()) * 100);
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("系统内存总容量:" + FileUtil.readableFileSize(getTotalMemory()).replace(" ", ""));
        builder.append(" 系统内存可用容量:" + FileUtil.readableFileSize(getFreeMemory()).replace(" ", ""));
        builder.append(" 系统内存使用率:" + format.format(getUseRate()) + "%");
        return builder.toString();
    }
}
