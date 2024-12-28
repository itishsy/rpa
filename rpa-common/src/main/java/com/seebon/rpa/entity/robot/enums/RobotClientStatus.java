package com.seebon.rpa.entity.robot.enums;

/**
 * 设备状态
 * New 初始化状态、用户未登录;
 * Runnable 机器人已启动，准备就绪 ;
 * Running 机器人正在执行任务中;
 * Closed 关闭;
 * RobotError 机器人程序内部异常;
 * ClientError 客户端内部异常;
 * Upgrading 机器人正在升级;
 * UpgradeFailed 机器人升级失败;
 * Offline 离线状态，超过10分钟没有心跳
 */
public enum RobotClientStatus {
    New(0, "New", "初始化状态、用户未登录"),
    Runnable(1, "Runnable", "机器人已启动，准备就绪"),
    Running(2, "Running", "机器人正在执行任务中"),
    Closed(3, "Closed", "关闭"),
    RobotError(4, "RobotError", "机器人程序内部异常"),
    ClientError(5, "ClientError", "客户端内部异常"),
    Upgrading(6, "Upgrading", "机器人正在升级"),
    UpgradeFailed(7, "UpgradeFailed", "机器人升级失败"),
    Offline(8, "Offline", "离线状态，超过10分钟没有心跳"),
    Unused(9, "Unused", "闲置"),
    ;

    // 常量编码
    private Integer index;
    // 常量编码
    private String code;
    // 对应常量值
    private String value;

    RobotClientStatus(Integer index, String code, String value) {
        this.index = index;
        this.code = code;
        this.value = value;
    }

    public static String getByIndex(Integer index) {
        for (RobotClientStatus status : RobotClientStatus.values()) {
            if (status.getIndex().equals(index)) {
                return status.getCode();
            }
        }
        return null;
    }

    public static String getValueByIndex(Integer index) {
        for (RobotClientStatus status : RobotClientStatus.values()) {
            if (status.getIndex().equals(index)) {
                return status.getValue();
            }
        }
        return null;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
