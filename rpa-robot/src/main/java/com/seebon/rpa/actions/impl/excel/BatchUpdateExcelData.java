package com.seebon.rpa.actions.impl.excel;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.SheetTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.UpdateExcelSourceType;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.*;

@Slf4j
@RobotAction(name = "批量更新Excel数据",
        order = 40,
        targetType = SheetTarget.class,
        comment = "根据数据数据来源更新目标Excel表格数据")
public class BatchUpdateExcelData extends AbstractAction {

    @ActionArgs(value = "数据来源于", dict = UpdateExcelSourceType.class, required = true)
    @Conditions({
            "excel:sourcePath,header,matchSourceColumn,sourceColumn,matchTargetColumn,targetColumn",
            "json:sourceList,matchSourceField,sourceField,matchTargetField,targetField",
            "list:sourceList,matchSourceField,sourceField,matchTargetField,targetField",
    })
    private String sourceType;


    @ActionArgs(value = "Excel文件路径", style = DynamicFieldType.text)
    private String sourcePath;
    @ActionArgs(value = "表头行号", comment = "缺省，第1行为表头")
    private Integer header;

    @ActionArgs(value = "数据来源对象", comment = "json或list对象", style = DynamicFieldType.text)
    private List<Map<String, Object>> sourceList;

    @ActionArgs(value = "源匹配列", dict = WebExcelNumLine.class, required = true, comment = "指定匹配列，通常为唯一标识的数据所在列，如身份证号、人员编号等")
    private WebExcelNumLine matchSourceColumn;

    @ActionArgs(value = "源数据列", dict = WebExcelNumLine.class, required = true, comment = "源数据所在列")
    private WebExcelNumLine sourceColumn;

    @ActionArgs(value = "源匹配字段名", dict = WebExcelNumLine.class, required = true, comment = "填写数据源中字段或属性名称，用于匹配目标列")
    private String matchSourceField;

    @ActionArgs(value = "源数据字段名", dict = WebExcelNumLine.class, required = true, comment = "填写数据源中字段或属性名称，写入到目标列中")
    private String sourceField;

    @ActionArgs(value = "目标匹配字段", dict = WebExcelNumLine.class, required = true, comment = "指定匹配列，通常为唯一标识的数据所在列，如身份证号、人员编号等")
    private WebExcelNumLine matchTargetColumn;

    @ActionArgs(value = "目标更新字段", dict = WebExcelNumLine.class, required = true, comment = "源数据所在列")
    private WebExcelNumLine targetColumn;

    @Override
    public void run() {
        Map<String,Object> sourceMap = new HashMap<>();
        switch (UpdateExcelSourceType.valueOf(sourceType)) {
            case excel:
                Workbook workbook = null;
                header = header == null ? 0 : header;
                try {
                    workbook = WorkbookFactory.create(new File(sourcePath));
                    Sheet sheet = workbook.getSheetAt(0);
                    int rowSize = sheet.getLastRowNum() + 1;
                    for (int i = header; i < rowSize; i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            sourceMap.put(row.getCell(matchSourceColumn.getIndex()).getStringCellValue(), row.getCell(sourceColumn.getIndex()).getStringCellValue());
                        }
                    }
                } catch (Exception e) {
                    log.error("删除源Excel数据失败" + e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(workbook);
                }
                break;
            case list:
            case json:
                for(Map<String, Object> map : sourceList){
                    String sourceKey = null, sourceValue = null;
                    for (String key : map.keySet()){
                        sourceKey = key.equals(matchSourceField) ? map.get(key) + "" : null;
                        sourceValue = key.equals(sourceField) ? map.get(key) + "" : null;
                    }
                    sourceMap.put(sourceKey, sourceValue);
                }
                break;
            default:
            break;
        }
        SheetTarget sheetTarget = (SheetTarget) this.getVariable("SheetTarget");
        sheetTarget.setWrite(true);
        this.updateExcel(sourceMap);
    }

    private void updateExcel(Map<String, Object> sourceMap) {
        log.info("sourceMap=" + JSON.toJSONString(sourceMap));
        try {
            Sheet sheet = this.getTarget();
            int rowSize = sheet.getLastRowNum() + 1;
            for (int i = 0; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                DataFormatter dataFormatter = new DataFormatter();
                String matchTargetKey = dataFormatter.formatCellValue(row.getCell(matchTargetColumn.getIndex()));
                if(sourceMap.containsKey(matchTargetKey)) {
                    Cell cell = row.getCell(targetColumn.getIndex());
                    if (cell == null) {
                        cell = row.createCell(targetColumn.getIndex());
                    }
                    cell.setCellValue(sourceMap.get(matchTargetKey) + "");
                }
            }
        } catch (Exception e) {
            log.error("update Excel Exception", e);
        }
    }
}
