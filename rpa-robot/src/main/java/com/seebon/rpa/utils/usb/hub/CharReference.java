package com.seebon.rpa.utils.usb.hub;

import com.sun.jna.ptr.ByReference;

public class CharReference extends ByReference {

    public CharReference() {
        this((byte) 0);//无
    }

    public CharReference(byte value) {
        super(1000);
        this.setValue(value);
    }

    public void setValue(byte value) {
        this.getPointer().setByte(0L, value);
    }

    public void setValue(long offset, byte value) {
        this.getPointer().setByte(offset, value);
    }

    public byte getByte(int pos) {
        return this.getPointer().getByte(pos);
    }

    public void Init(String input) {
        for (int i = 0; i < input.length(); i++) {
            this.getPointer().setChar((long) i, input.charAt(i));//字符串初始化
        }
    }

    public void init(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            this.getPointer().setByte((long) i, bytes[i]);//字节数组初始化
        }
    }

    public byte[] getBytes(int len) {
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = this.getPointer().getByte(i);
        }
        return ret;
    }
}
