package com.seebon.rpa.entity.saas.dto.business;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-07-11 14:07
 */
@Data
@ApiModel("业务场景提醒excel")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class BusinessSceneWarnExcelDTO {

    @ExcelProperty(index=0, value="参保城市")
    @ApiModelProperty("参保城市")
    private String addrName;

    @ExcelProperty(index=1, value="业务类型")
    @ApiModelProperty("业务类型 1社保 2公积金")
    private String businessTypeName;

    @ExcelProperty(index=2, value="申报网站")
    @ApiModelProperty("申报网址 1 全险系统 2 养老系统 3 医疗系统 4 单工伤 5 工伤系统")
    private String declareWebsiteStr;

    @ExcelProperty(index=3, value="申报类型")
    @ApiModelProperty("申报类型 1：增员，2：减员，3：调基，5：补缴")
    private String declareName;

    @ExcelProperty(index=4, value="当前节点")
    @ApiModelProperty("当前节点")
    private String functionPointName;

    @ExcelProperty(index=5, value="提示信息")
    @ApiModelProperty("提示信息")
    private String warnMessage;

    @ExcelProperty(index=6, value="申报回盘")
    @ApiModelProperty("申报状态，1：待申报，2：申报中，4：申报成功，5：申报失败")
    private String declareStatus;

    @ExcelProperty(index=7, value="下一节点")
    @ApiModelProperty("下一节点")
    private String nextDeclareName;

    @ExcelProperty(index=8, value="注解")
    @ApiModelProperty("注解")
    private String annotate;

    @ExcelProperty(index=9, value="替换提示")
    @ApiModelProperty("替换提示")
    private String replaceWarn;

    @ExcelProperty(index=10, value="场景提示信息")
    @ApiModelProperty("场景提示信息")
    private String resultMsg;

    @ExcelProperty(index=11, value="场景说明")
    @ApiModelProperty("场景说明")
    private String scenarioDescription;

    @ExcelProperty(index=12, value="是否新场景")
    @ApiModelProperty("是否上线 0 否 1是")
    private String live;
}
