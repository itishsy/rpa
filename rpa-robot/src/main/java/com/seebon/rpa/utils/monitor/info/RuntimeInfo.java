package com.seebon.rpa.utils.monitor.info;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 运行时信息，包括内存总大小、已用大小、可用大小等
 *
 * @author looly
 */
@Data
@NoArgsConstructor
public class RuntimeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Runtime runtime = Runtime.getRuntime();

    /**
     * 获得运行时对象
     *
     * @return {@link Runtime}
     */
    public final Runtime getRuntime() {
        return runtime;
    }

    /**
     * 获得JVM最大内存
     *
     * @return 最大内存
     */
    public final long getMaxMemory() {
        return runtime.maxMemory();
    }

    /**
     * 获得JVM已分配内存
     *
     * @return 已分配内存
     */
    public final long getTotalMemory() {
        return runtime.totalMemory();
    }

    /**
     * 获得JVM已分配内存中的剩余空间
     *
     * @return 已分配内存中的剩余空间
     */
    public final long getFreeMemory() {
        return runtime.freeMemory();
    }

    /**
     * 获得JVM最大可用内存
     *
     * @return 最大可用内存
     */
    public final long getUsableMemory() {
        return runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JVM最大内存:" + FileUtil.readableFileSize(getMaxMemory()).replace(" ", ""));
        builder.append(" JVM已分配内存:" + FileUtil.readableFileSize(getTotalMemory()).replace(" ", ""));
        builder.append(" JVM已分配内存中的剩余空间:" + FileUtil.readableFileSize(getFreeMemory()).replace(" ", ""));
        builder.append(" JVM最大可用内存:" + FileUtil.readableFileSize(getUsableMemory()).replace(" ", ""));
        return builder.toString();
    }
}
