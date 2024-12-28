package com.seebon.rpa.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhenShijun
 * @dateTime 2021-04-07 17:12:24
 */
@Data
@Document(collection = "sys_operation_log")
public class SysLog implements Serializable {

    private ObjectId id;

    private String callType;

    private String appKey;

    private Integer custId; // 一级站点id

    private String company; // 公司名称

    private Integer userId; // 用户id

    private String userName; //用户名

    private String realName; // 用户姓名

    private String moduleName; //模块名称

    private String pageName; // 页面名称

    private String operation; // 操作名称

    private String method; // 调用方法

    private String url; // 请求的url

    private Long time; // 响应时间, 单位毫秒

    private String prestatus; // 数据变更前的状态

    private String poststatus; // 数据变更后的状态

    private Integer state; // 调用是否成功 1：返回正常结果，0：返回非正常结果，2：异常

    private Object result; // 返回结果

    private String params; //参数

    private String ip; //ip地址

    private Date createTime; //操作时间
}
