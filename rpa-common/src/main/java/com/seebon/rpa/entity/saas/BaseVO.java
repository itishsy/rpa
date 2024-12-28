package com.seebon.rpa.entity.saas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author created By charles
 * @Description:
 * @Date 2020/8/6 10:37
 * @Modifide By:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseVO<T> {

    public  final static String SUCCESS_CODE = "1";
    public  final static String FAIL_REPEAT = "2";
    public  final static String FAIL_NOT_FOUND = "3";

    private String code;
    private String msg;
    private Integer index;
    private  Integer size;
    private  Integer total;
    private T data;
}
