package com.seebon.rpa.entity.robot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
public class RobotTrackDetailVO {
    private String processName;

    private String processTime;

    private String machineCode;

    @ApiModelProperty(value = "预计时长(单位：分)")
    private Long preTime;

    @ApiModelProperty(value = "预计开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preStartTime;

    @ApiModelProperty(value = "预计结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preEndTime;

    @ApiModelProperty(value = "实际时长(单位：分)")
    private Long praTime;

    @ApiModelProperty(value = "实际开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date praStartTime;

    @ApiModelProperty(value = "实际结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date praEndTime;

    public RobotTrackDetailVO(String processName, String processTime) {
        this.processName = processName;
        this.processTime = processTime;
    }

    public RobotTrackDetailVO(String processName, String processTime, String machineCode) {
        this.processName = processName;
        this.processTime = processTime;
        this.machineCode = machineCode;
    }
}