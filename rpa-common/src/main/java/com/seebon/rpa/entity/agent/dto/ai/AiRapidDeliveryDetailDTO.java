package com.seebon.rpa.entity.agent.dto.ai;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@ApiModel(value ="ai_rapid_delivery_detail")
@Document(collection="ai_rapid_delivery_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiRapidDeliveryDetailDTO{

//    @Id
//    private String id;
    @ApiModelProperty("id")
    private String _id;

    @ApiModelProperty("随机id")
    private String uuid;

    @ApiModelProperty("创建id")
    private Integer createId;

    @ApiModelProperty("客户id")
    private Integer customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("业务类型 1社保 2公积金 3人员信息")
    private Integer type;

    @ApiModelProperty("业务类型名")
    private String typeName;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("字段集合")
    private List<AiConstantColumnDTO> dynamicColumns;

    @ApiModelProperty("上传的文件")
    private List<AiRapidDeliveryFileDTO> fileList;

    @ApiModelProperty("参保地区")
    private String address;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("status 0待办 1完成")
    private Integer status;

    @ApiModelProperty("事务类型 0 Ai便捷导入 1 地区导入")
    private Integer workType;

    @ApiModelProperty("待办事务总记录数")
    private Integer addressCount;

    @ApiModelProperty("修改时间")
    private String updateTime;

    @ApiModelProperty("修改人")
    private String updatePerson;

    @ApiModelProperty("已办理数")
    private Integer processedCount;

    @ApiModelProperty("未办理数")
    private Integer notProcessedCount;

    @ApiModelProperty("增员总数")
    private Integer addCount;

    @ApiModelProperty("减员总数")
    private Integer reduceCount;

    @ApiModelProperty("补缴总数")
    private Integer mendCount;

    @ApiModelProperty("未办理增员总数")
    private Integer notProcessedAddCount;

    @ApiModelProperty("未办理减员总数")
    private Integer notProcessedReduceCount;

    @ApiModelProperty("未办理补缴总数")
    private Integer notProcessedMendCount;

    @ApiModelProperty("已办理增员总数")
    private Integer processedAddCount;

    @ApiModelProperty("已办理减员总数")
    private Integer processedReduceCount;

    @ApiModelProperty("已办理补缴总数")
    private Integer processedMendCount;

    @ApiModelProperty("是否置灰")
    private boolean isGrey=false;

    @ApiModelProperty("核验批次号")
    private String checkBatchNumber;

    @ApiModelProperty("证件号码")
    private String cardNo;

    @ApiModelProperty("参保主体")
    private String insuredEntity;



}
