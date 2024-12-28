package com.seebon.rpa.entity.saas.dto.system;

import com.seebon.rpa.entity.robot.SaasRobotFlowStep;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * @author dpl
 */
@Data
public class SysDesktopApplicationsDTO {
    @Id
    private Integer id;
    
    @ApiModelProperty("创建时间")
    private Date createTime;
    
    @ApiModelProperty("修改时间")
    private Date updateTime;
    
    @ApiModelProperty("创建人")
    private Integer creator;
    
    @ApiModelProperty("最后更新人")
    private Integer updater;
    
    @ApiModelProperty("操作类型： 1 登录/ 2核验/ 3开户/ 4启封/ 5转入/ 6封存/ 7补缴/ 8基数调整/ 9其他")
    private Integer type;
    
    @ApiModelProperty("业务类型, 社保或公积金的编码,  社保: 1001001  公积金: 1001002")
    private String businessType;
    
    @ApiModelProperty("地区id")
    private String addressId;
    
    @ApiModelProperty("地区名称")
    private String addressName;
    
    @ApiModelProperty("网址")
    private String url;
    
    @ApiModelProperty("名称")
    private String name;
    
    @ApiModelProperty("浏览器事件")
    private String browserEvent;
    
    @ApiModelProperty("返回结果配置")
    private String resultConfig;

    @ApiModelProperty("创建人姓名")
    private String creatorName;

    @ApiModelProperty("操作类型")
    private String typeName;

    @ApiModelProperty("视频路径")
    private String recVideoUrl;

    @ApiModelProperty("转换出的步骤")
    private List<SaasRobotFlowStep> saasRobotFlowStepList;
}
