package com.seebon.rpa.entity.saas.vo.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2022/11/25 19:39
 * @Modified By:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyResultVO<T> implements Serializable {
    public final static String SUCCESS = "10";
    public final static String FAILURE = "20";

    @ApiModelProperty("响应状态码")
    private String code;

    @ApiModelProperty("响应描述信息")
    private String msg;

    @ApiModelProperty("响应数据对象")
    private T data;

    public PolicyResultVO(String code) {
        this.code = code;
    }


    public PolicyResultVO(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public PolicyResultVO(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static PolicyResultVO success(Object... data) {
        if (null == data || data.length == 0) {
            return new PolicyResultVO<>(SUCCESS, "操作成功");
        }
        return new PolicyResultVO<>(SUCCESS, data[0]);
    }

    public static PolicyResultVO failure(Object... data) {
        if (null == data || data.length == 0) {
            return new PolicyResultVO<>(FAILURE, "操作失败");
        }
        return new PolicyResultVO(FAILURE, data[0]);
    }

    public static PolicyResultVO failure(String data) {
        if (StringUtils.isNotBlank(data)) {
            return new PolicyResultVO(FAILURE, data);
        }
        return new PolicyResultVO<>(FAILURE, "操作失败");
    }
}
