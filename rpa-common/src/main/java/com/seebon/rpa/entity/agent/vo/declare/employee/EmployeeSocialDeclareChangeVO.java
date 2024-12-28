package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.seebon.rpa.entity.agent.dto.declare.employee.EmployeeFilestoreDTO;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChange;
import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeSuppDetail;
import com.seebon.rpa.entity.robot.vo.RobotTrackVO;
import com.seebon.rpa.entity.saas.po.system.SysFilestore;
import com.seebon.rpa.utils.EasyexcelConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/4/24 16:08
 * @Version 1.0
 **/
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class EmployeeSocialDeclareChangeVO extends EmployeeSocialDeclareChange {

    private List<EmployeeSocialDeclareChangeItemBaseVO> base;

    private List<EmployeeSocialDeclareChangeSuppDetail> details;

    @ExcelProperty(value = "申报状态", index = 7, converter = EasyexcelConverter.class)
    @ColumnWidth(20)
    private Integer status;

    private Integer employeeFileStoreId;

    private String telephone;

    private String itemId;

    private String createName;

    private String companyNameAndAccountNumber;

    private String orgName; // 申报单位

    @ExcelProperty(index = 8, value = "原因")
    @ColumnWidth(20)
    private String failCase;

    @ExcelProperty(index = 6, value = "缴纳基数")
    @ColumnWidth(20)
  //  @NumberFormat(roundingMode = RoundingMode.CEILING)
    private String roundEmpTbBase;

    private String cardType;

    private String strInsuredDate;

    private Integer businessType;

    private String machineCode;

    private String clientName;

    private List<EmployeeFilestoreDTO> empFiles;

    private List<SysFilestore> sysFilestores;

    //注解
    private String annotate;
    private String processComment;
    //1待处理，2已处理
    private Integer exportStatus;
    private Integer declareTime;

    private String failType;

    @ApiModelProperty("忽略失败标记，1：忽略，其他：不忽略")
    private Integer ignoreFlag;

    @ExcelProperty(index = 9, value = "标记")
    @ColumnWidth(20)
    private String ignoreFlagStr;

    private String failItemName;

    private List<String> operationTypes;

    private RobotTrackVO robotTrackVO;

}
