package com.seebon.rpa.entity.saas.vo.system.customer;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerContactPersonList;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerContractList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 17:53:51
 */
@ApiModel("客户vo")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class CustomerBaseVO extends CustomerBase {

    @ApiModelProperty("平台方名称")
    private String platformName;

    @ExcelProperty(value = "已开通城市",index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("开通城市数")
    private Integer activeCityNumber;

    @ExcelProperty(value = "已开通账户",index = 5)
    @ColumnWidth(20)
    @ApiModelProperty("开通申报账户数")
    private Integer activeAccountNumber;

    @ApiModelProperty("服务模块")
    private List<CustomerModuleListVO> moduleList;

    @ExcelProperty(value = "服务模块",index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("服务模块名称")
    private String moduleName;

    @ExcelProperty(value = "状态",index = 9)
    @ColumnWidth(20)
    @ApiModelProperty("状态名称")
    private String statusName;

    @ExcelProperty(value = "业务类型",index = 2)
    @ColumnWidth(15)
    @ApiModelProperty("业务类型")
    private String businessTypeName;

    @ExcelProperty(value = "合同开始日期",index = 7)
    @ColumnWidth(20)
    @ApiModelProperty("合同开始日期")
    private String contractBeginDate;

    @ExcelProperty(value = "合同结束日期",index = 8)
    @ColumnWidth(20)
    @ApiModelProperty("合同结束日期")
    private String contractEndDate;

    @ApiModelProperty("客户合同信息")
    private List<CustomerContractList> contractList;

    @ApiModelProperty("客户对接人信息")
    private List<CustomerContactPersonList> contactPersonList;

    @ApiModelProperty("客户管理员账户信息")
    private SysUser user;

    private String customerName;

}
