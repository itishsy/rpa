package com.seebon.rpa.utils;

@FunctionalInterface
public interface BeanCopyUtilsCallBack<S, T> {

    void callback(S t, T s);
}
