package com.seebon.rpa.entity.agent.dto.openApi;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-17 10:33:05
 */
@ApiModel("参保接口录入mongo数据信息")
@Data
@ExcelIgnoreUnannotated
@Document(collection = "employee_declare_open_info")
public class EmployeeDeclareInfoDTO {

    @ApiModelProperty("id")
    private String id;

    @ExcelProperty(index = 0, value = "批次号")
    @ColumnWidth(20)
    @ApiModelProperty("批次号")
    private String batchNumber;

    @ExcelProperty(index = 3, value = "参保城市")
    @ColumnWidth(20)
    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ExcelProperty(index = 4, value = "申报类型")
    @ColumnWidth(20)
    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("申报类型名称")
    private String declareTypeName;

    @ExcelProperty(index = 1, value = "姓名")
    @ColumnWidth(20)
    @ApiModelProperty("员工姓名")
    private String name;

    @ExcelProperty(index = 2, value = "证件号码")
    @ColumnWidth(20)
    @ApiModelProperty("员工证件号码")
    private String idCard;

    @ApiModelProperty("员工证件类型")
    private String cardType;

    @ApiModelProperty("单位申报账户")
    private String accountNumber;

    private String fullAccountNumber;

    @ExcelProperty(index = 6, value = "缴纳起始月")
    @ColumnWidth(20)
    @ApiModelProperty("缴纳起始月")
    private String insuredDate;

    @ApiModelProperty("补缴开始年月")
    private String insuredBeginDate;

    @ApiModelProperty("补缴截止年月")
    private String insuredEndDate;

    @ExcelProperty(index = 5, value = "申报状态")
    @ColumnWidth(20)
    @ApiModelProperty("社保参保险种，多个险种用|隔开")
    private String items;

    @ExcelProperty(index = 7, value = "缴纳基数")
    @ColumnWidth(20)
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

    @ExcelProperty(index = 8, value = "原因")
    @ColumnWidth(20)
    @ApiModelProperty("补充失败信息")
    private String suppErrorMsg;

    @ApiModelProperty("失败信息")
    private String errorMsg;

    @ApiModelProperty("备注说明")
    private String explain;

    @ApiModelProperty("入库状态 1：已入库，0：未入库")
    private Integer warehousingStatus = 0;

    @ApiModelProperty("失败类型 0：校验失败，1：申报失败")
    private Integer failType;

    @ApiModelProperty("检验状态 0：校验失败，1：校验成功 2 撤回作废")
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

    @ApiModelProperty("是否预警，1：是，0：否")
    private Integer warnNotice = 0;

    // 是否校验减员其他申报账户 0校验  1校验
    @ExcelIgnore
    private Integer igCheckOtherAccount;

    @ApiModelProperty("失败信息")
    private String failMsg;
}
