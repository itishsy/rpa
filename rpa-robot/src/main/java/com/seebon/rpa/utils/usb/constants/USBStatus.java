package com.seebon.rpa.utils.usb.constants;

/**
 * The Usb usable status.............
 *
 * @author pengdeyi
 */
public enum USBStatus {

    DISABLED(1, "当前设备已禁用"),

    ENABLED(0, "当前设备可用状态"),

    EJECT(-1, "当前设备已弹出"),

    UNKNOW(-2, "当前设备未知状态");

    private int key;

    private String description;

    private USBStatus(int key, String description) {
        this.key = key;
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
