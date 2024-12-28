package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
public class EmployeeInsuranceEditDTO {

        @ApiModelProperty("id")
        private String id;

        @ApiModelProperty("批次号")
        private String batchNumber;

        @ApiModelProperty("参保地名称")
        private String addrName;

        @ApiModelProperty("业务类型")
        private Integer businessType;

        @ApiModelProperty("业务类型名称")
        private String businessTypeName;

        @ApiModelProperty("申报类型")
        private Integer declareType;

        @ApiModelProperty("申报类型名称")
        private String declareTypeName;

        @ApiModelProperty("员工姓名")
        private String name;

        @ApiModelProperty("员工证件号码")
        private String idCard;

        @ApiModelProperty("员工证件类型")
        private String cardType;

        @ApiModelProperty("单位申报账户")
        private String accountNumber;

        @ApiModelProperty("缴纳起始月")
        private String insuredDate;

        @ApiModelProperty("补缴开始年月")
        private String insuredBeginDate;

        @ApiModelProperty("补缴截止年月")
        private String insuredEndDate;

        @ApiModelProperty("社保参保险种，多个险种用|隔开")
        private String items;

        @ApiModelProperty("参保基数")
        private String tbBase;

        @ApiModelProperty("公积金单位比例")
        private String compRatio;

        @ApiModelProperty("公积金个人比例")
        private String empRatio;

        @ApiModelProperty("补充单位比例")
        private String suppCompRatio;

        @ApiModelProperty("补充个人比例")
        private String suppEmpRatio;

        @ApiModelProperty("报盘信息")
        private Map<String, String> declareInfo;

        @ApiModelProperty("申报状态")
        private String status = "待申报";

        @ApiModelProperty("补充申报状态（0作废，1待申报，2申报中，4申报成功，5申报失败）")
        private String suppStatus;

        @ApiModelProperty("失败信息")
        private String errorMsg;

        @ApiModelProperty("备注说明")
        private String explain;

        @ApiModelProperty("入库状态 1：已入库，0：未入库")
        private Integer warehousingStatus = 0;

        @ApiModelProperty("失败类型 0：校验失败，1：申报失败")
        private Integer failType;

        @ApiModelProperty("检验状态 0：校验失败，1：校验成功")
        private Integer validateStatus;

        @ApiModelProperty("创建时间")
        private Date createTime;

        @ApiModelProperty("险种申报明细信息")
        private List<Map<String, Object>> itemDetails;

        @ApiModelProperty("补缴申报明细信息")
        private Map<String, Object> suppItemDetails;

        @ApiModelProperty("所属客户id")
        private Integer custId;

        @ApiModelProperty("客户名称")
        private String custName;

        @ApiModelProperty("创建用户id")
        private Integer createId;

        @ApiModelProperty("创建人")
        private String createName;

        @ApiModelProperty("创建年月")
        private String createMonth;

        @ApiModelProperty("险种")
        private String prodcutName;

        @ApiModelProperty("申报状态（0作废，1待申报，2申报中，3部分成功，4申报成功，5申报失败，6待提交）")
        private Integer declareStatus;



}
