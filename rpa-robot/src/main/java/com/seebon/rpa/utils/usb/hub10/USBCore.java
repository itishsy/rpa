package com.seebon.rpa.utils.usb.hub10;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface USBCore extends Library {
    USBCore INSTANTCE = (USBCore) Native.loadLibrary("USBCore", USBCore.class);//MD5为DLL文件名

    public int GetDeviceList(CharReference deviceList, int len);

    public int OpenDevice(int port);

    public void CloseDevice(int device);

    public int GetDeviceUSBCount(int device);

    public int GetDeviceId(int device, CharReference deviceid, int len);

    public int GetDeviceStatus(int device, CharReference status, int len);

    public int WriteDevice(int device, CharReference status, int len);
}
