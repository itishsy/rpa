package com.seebon.rpa.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @Author hao
 * @Date 2022/10/29 0:18
 * @Version 1.0
 **/
public class BeanCopyUtils extends BeanUtils {

    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        return copyListProperties(sources, target, null);
    }


    public static <S,T> List<T> copyListProperties(List<S> sources, Supplier<T> target, BeanCopyUtilsCallBack<S,T> callBack){
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source,t);
            list.add(t);
            if (callBack != null){
                callBack.callback(source,t);
            }
        }
        return list;
    }
}
