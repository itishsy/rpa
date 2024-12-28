package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人参数定义
 */
@Data
@ApiModel(value = "机器人参数定义")
@Table(name = "robot_args_define")
public class RobotArgsDefine extends Identity {

    @Column
    @ApiModelProperty(value = "参数域")
    private String scope;

    @Column
    @ApiModelProperty(value = "robot_app_form_group主键")
    private Integer formGroupId;

    @Column
    @ApiModelProperty(value = "参数编码")
    private String argsCode;

    @Column
    @ApiModelProperty(value = "字段Key")
    private String fieldKey;

    @Column
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @Column
    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @Column
    @ApiModelProperty(value = "字典Key")
    private String dictKey;

    @Column
    @ApiModelProperty(value = "显示顺序")
    private Integer displayOrder;

    @Column
    @ApiModelProperty(value = "显示分组")
    private String groupName;

    @Column
    @ApiModelProperty(value = "是否必填")
    private Integer required;

    @Column
    @ApiModelProperty(value = "说明")
    private String comment;

    @Column
    @ApiModelProperty(value = "图片上传数量")
    private Integer quantity;

    @Column
    @ApiModelProperty(value = "联动条件拼接")
    private String cond;

    @Column
    @ApiModelProperty("信息配置下拉选项逗号拼接")
    private String dropDownOption;

    @Column
    @ApiModelProperty("显隐性  0--显示；1--隐藏")
    private Integer showOrHidden;

    @Column
    @ApiModelProperty("是否删除  0--否；1--是")
    private Integer isDelete;

}
