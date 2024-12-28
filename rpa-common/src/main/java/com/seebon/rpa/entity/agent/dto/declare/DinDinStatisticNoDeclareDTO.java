package com.seebon.rpa.entity.agent.dto.declare;

import lombok.Data;

import java.util.Date;

/**
 * 钉钉统计增减员未申报数据
 */

@Data
public class DinDinStatisticNoDeclareDTO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 1社保  2公积金
     */
    private Integer businessType;

    /**
     * 地区名
     */
    private String addrName;
    /**
     * 地区Id
     */
    private Integer addrId;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 未完成数据最早提交时间
     */
    private Date minSubmitTime;

    /**
     * 总未完成数量
     */
    private Integer num;

    /**
     * 距离当前间隔时间（小时）
     */
    private Double intervalTime;

    /**
     * 城市类型 1:可自动跑的城市  2:不可自动跑的城市
     */
    private Integer cityType;

    /**
     * 城市状态 1配置  2测试  3运维
     */
    private Integer cityStatus;

    /**
     * 待申报
     */
    private Integer waitDeclareCount;

    /**
     * 申报中
     */
    private Integer declaringCount;

    /**
     * 网站审核中
     */
    private Integer waitAuditCount;

    /**
     * 申报账户聚合
     */
    private String accountGroupConcat;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 消息文本
     */
    private String msgContent;
    /**
     * 用户名
     */
    private String userName;

}
