package com.seebon.rpa.utils.monitor.info;

import cn.hutool.core.io.FileUtil;
import lombok.Data;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * <p>系统磁盘</p>
 *
 * @author Dai Yuanchuan
 **/
@Data
public class DiskInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DecimalFormat format = new DecimalFormat("#0.00");

    /**
     * 磁盘名称
     */
    private String name;

    /**
     * 获取总容量
     */
    private long totalSpace;

    /**
     * 获取剩余容量
     */
    private long usableSpace;

    /**
     * 获取已经使用的容量
     */
    private long freeSpace;

    /**
     * 获取使用率
     */
    private float useRate;

    public long getFreeSpace() {
        return totalSpace - usableSpace;
    }

    public float getUseRate() {
        return (float) ((getFreeSpace() * 1.0 / getTotalSpace()) * 100);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("磁盘名称:" + getName());
        builder.append(" 总容量:" + FileUtil.readableFileSize(getTotalSpace()).replace(" ", ""));
        builder.append(" 剩余容量:" + FileUtil.readableFileSize(getUsableSpace()).replace(" ", ""));
        builder.append(" 已使用容量:" + FileUtil.readableFileSize(getFreeSpace()).replace(" ", ""));
        builder.append(" 使用率:" + format.format(getUseRate()) + "%");
        return builder.toString();
    }
}
