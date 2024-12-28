package com.seebon.rpa.entity.robot.enums;

public enum RobotClientStatusEnum {

    Init(0, "初始化"),
    Ready(1, "准备就绪"),
    Executing(2, "正在执行"),
    Closed(3, "已关闭"),
    ProgramException(4, "程序异常"),
    ClientException(5, "客户端异常"),
    Upgrading(6, "正在升级"),
    UpgradeFailed(7, "升级失败"),
    OutLine(8, "离线");



    private Integer status;

    private String statusName;

    RobotClientStatusEnum(Integer status,String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public static String getStatusNameByCode(Integer status) {
        if (status != null) {
            RobotClientStatusEnum[] values = RobotClientStatusEnum.values();
            for (RobotClientStatusEnum item : values) {
                if (item.getStatus().equals(status)) {
                    return item.getStatusName();
                }
            }
        }
        return "";
    }

    public static RobotClientStatusEnum getEnumByCode(Integer status) {
        if (status != null) {
            RobotClientStatusEnum[] values = RobotClientStatusEnum.values();
            for (RobotClientStatusEnum item : values) {
                if (item.getStatus().equals(status)) {
                    return item;
                }
            }
        }
        return null;
    }
}
