package com.seebon.rpa.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("响应对象")
@Data
public class ResponseResult implements Serializable {

    public final static int SUCCESS = 200;
    public final static int ERROR = 500;
    public final static int DENIED = 401;
    public final static String SUCCESS_MESSAGE = "操作成功";

    @ApiModelProperty("状态码")
    private int code;

    @ApiModelProperty("响应信息")
    private String message;

    @ApiModelProperty("数据对象")
    private Object data;
}
