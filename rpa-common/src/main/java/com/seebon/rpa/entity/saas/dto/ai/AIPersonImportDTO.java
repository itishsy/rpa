package com.seebon.rpa.entity.saas.dto.ai;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-25 14:14
 */
@Data
public class AIPersonImportDTO {

    @ExcelProperty(value="客户名称",index=0)
    private String customerName;

    @ExcelProperty(value="参保主体",index=1)
    private String insuredName;

    @ExcelProperty(value="业务类型",index=2)
    private String businessName;

    @ExcelProperty(value="申报单位",index=3)
    private String declareUnit;

    @ExcelProperty(value="申报账号",index=4)
    private String declareAccount;

    private AiPersonMessageDTO aiPersonMessageDTO;


}
