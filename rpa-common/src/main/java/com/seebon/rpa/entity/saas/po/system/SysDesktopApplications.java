package com.seebon.rpa.entity.saas.po.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author dpl
 */
@Data
@Table(name = "sys_desktop_applications")
@ApiModel("桌面应用表")
public class SysDesktopApplications implements Serializable {

    @Id
    private Integer id;
    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;
    @Column
    @ApiModelProperty("修改时间")
    private Date updateTime;
    @Column
    @ApiModelProperty("创建人")
    private Integer creator;
    @Column
    @ApiModelProperty("最后更新人")
    private Integer updater;
    @Column
    @ApiModelProperty("操作类型： 1 登录/ 2核验/ 3开户/ 4启封/ 5转入/ 6封存/ 7补缴/ 8基数调整/ 9其他")
    private Integer type;
    @Column
    @ApiModelProperty("业务类型, 社保或公积金的编码,  社保: 1001001  公积金: 1001002")
    private String businessType;
    @Column
    @ApiModelProperty("地区id")
    private String addressId;
    @Column
    @ApiModelProperty("地区名称")
    private String addressName;
    @Column
    @ApiModelProperty("网址")
    private String url;
    @Column
    @ApiModelProperty("名称")
    private String name;
    @Column
    @ApiModelProperty("浏览器事件")
    private String browserEvent;

//    @Column
//    @ApiModelProperty("返回结果列")
//    private Integer resultColumnIndex;
//
//    @Column
//    @ApiModelProperty("数据开始行")
//    private Integer resultRowIndex;
//
//    @Column
//    @ApiModelProperty("身份证所在的列")
//    private Integer resultUniqueColumnIndex;
//
//    @Column
//    @ApiModelProperty("个人编号所在列")
//    private Integer resultEmpAccountColumnIndex;

    @Column
    @ApiModelProperty("创建人姓名")
    private String creatorName;

    @Column
    @ApiModelProperty("视频录制文件ID")
    private String fileId;

    @Column
    @ApiModelProperty("resultColumnIndex / resultRowIndex / resultUniqueColumnIndex / resultEmpAccountColumnIndex的替换JSON串")
    private String fileResult;
}
