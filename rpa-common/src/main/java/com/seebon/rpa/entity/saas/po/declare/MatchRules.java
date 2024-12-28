package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 *
 *
 * @author lisongyang
 * @since 2023-07-27 15:24:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "match_rules")
public class MatchRules {

    @Id
    @ApiModelProperty(value = "自动增长ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 初始状态：1待申报，2申报中，3部分成功，4申报成功，5申报失败,6待提交,7待处理，8已处理
     */
    @Column
    @ApiModelProperty(value = "初始状态,1待申报，2申报中，3部分成功，4申报成功，5申报失败,6待提交")
    private Integer initialStatus;
    /**
     * 触发条件：1超时匹配，2规则匹配
     */
    @Column
    @ApiModelProperty(value = "触发条件：1超时匹配，2规则匹配")
    private Integer triggerConditions;
    /**
     * 规则，超时的规则
     */
    @Column
    @ApiModelProperty(value = "规则，超时的规则")
    private Integer rules;
    /**
     * 移交状态：1待申报，2申报中，3部分成功，4申报成功，5申报失败,6待提交
     */
    @Column
    @ApiModelProperty(value = "移交状态：1,待处理")
    private Integer changeStatus;
    /**
     * 是否启用，0启用，1关闭
     */
    @Column
    @ApiModelProperty(value = "是否启用，0关闭，1启用")
    private Integer useStatus;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @Column
    @ApiModelProperty(value = "创建人id")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "修改人id")
    private Integer updateId;

}

