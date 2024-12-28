package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-09-21 14:34
 */

@Data
public class AiPersonOperationDTO{

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("随机id")
    private String uuid;

    @ApiModelProperty("地区")
    private String address;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("客户id")
    private Integer customerId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("uuid集合")
    private List<String> ids;

    @ApiModelProperty("Excel导出表头集合")
    private List<String> excelHeads;

    @ApiModelProperty("类型集合")
    private List<Integer> types;

    @ApiModelProperty("批次号")
    private String batchNumber;

    @ApiModelProperty("错误信息集合")
    private Map<String, String> errorMap;

    @ApiModelProperty("导入类型 0 ai导入 1 地区导入")
    private Integer workType;

    @ApiModelProperty("批次号集合")
    private List<String> batchNumberList;

    @ApiModelProperty("校验批次号：前端生成用于查询校验进度")
    private String checkNo;



}
