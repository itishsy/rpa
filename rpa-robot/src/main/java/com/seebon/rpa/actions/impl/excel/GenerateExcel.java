package com.seebon.rpa.actions.impl.excel;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.CreateSheetTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RobotAction(name = "生成Excel文件", targetType = CreateSheetTarget.class, comment = "数据集合(MapList)生成Excel文件")
public class GenerateExcel extends AbstractAction {

    @ActionArgs(value = "数据源")
    private List<Map<String, Object>> mapList;

    @ActionArgs(value = "表头索引", comment = "从0开始")
    private Integer header;

    @ActionArgs(value = "表头列", comment = "如个人账号,姓名,转出单位账号")
    private String headerCols;

    @ActionArgs(value = "是否创建表头", comment = "表头列有值时默认创建，传0或flase时不创建")
    private String createHeader;

    @ActionArgs(value = "汇总数据", comment = "汇总数据的行号(插完数据后的行从0开始)和列(多个列用逗号分隔)")
    private Map<String, String> total;

    @ActionArgs(value = "模板插入起始行", comment = "向模板内插入数据时，从哪一行开始写入数据")
    private Integer insertColNum;

    @ActionArgs(value = "函数计算列", comment = "标识该列的值是通过函数计算得到值")
    private String funcColNum;

    @Override
    public void run() {
        Sheet sheet = getTarget();
        CreateSheetTarget sheetTarget = (CreateSheetTarget) this.getVariable("CreateSheetTarget");
        sheetTarget.setWrite(true);
        List<String> keys = Lists.newArrayList();
        header = (header == null) ? 0 : header;
        FormulaEvaluator formulaEvaluator = null;
        if (StringUtils.isNotBlank(funcColNum)) {
            try {
                formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) sheet.getWorkbook());
            } catch (Exception e) {
                formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) sheet.getWorkbook());
            }
        }
        Row headerRow = sheet.getRow(header);
        if (StringUtils.isNotBlank(headerCols)) {
            keys = Lists.newArrayList(headerCols.split(","));
            if (StringUtils.isBlank(createHeader) || !("0".equals(createHeader) || "false".equals(createHeader))) {
                for (int i = 0; i < keys.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    if (sheetTarget.getStyle() != null) {
                        cell.setCellStyle(sheetTarget.getStyle());
                    }
                    cell.setCellValue("###".equals(keys.get(i)) ? "" : keys.get(i));
                }
            }
        } else {
            keys = Lists.newArrayList();
            short lastCellNum = headerRow.getLastCellNum();
            for (int i = 0; i < lastCellNum; i++) {
                keys.add(getCellVal(headerRow.getCell(i)));
            }
        }

        int rowSize = mapList.size();
        for (int i = 0; i < rowSize; i++) {
            Map<String, Object> map = mapList.get(i);
            map.put("序号", (i + 1) + "");
            Row row = null;
            if (null != insertColNum) {
                // 往表格中间指定位置插入数据行，先将插入位置以后的行向下移动一行，然后在腾出的位置上创建行
                sheet.shiftRows(insertColNum + i, sheet.getLastRowNum(), 1);
                sheet.createRow(insertColNum + i);
                row = sheet.getRow(insertColNum + i);
            } else {
                row = sheet.getRow(i + header + 1);
                if (row == null) {
                    row = sheet.createRow(i + header + 1);
                }
            }
            for (int j = 0; j < keys.size(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                if (sheetTarget.getStyle() != null) {
                    cell.setCellStyle(sheetTarget.getStyle());
                }
                if (map.containsKey(keys.get(j))) {
                    Object o = map.get(keys.get(j));
                    if (o != null) {
                        cell.setCellValue(o.toString());
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
            if (StringUtils.isNotBlank(funcColNum) && formulaEvaluator != null) {
                for (String colNum : funcColNum.split(",")) {
                    Cell cell = row.getCell(Integer.parseInt(colNum));
                    if (cell != null) {
                        CellValue evaluate = formulaEvaluator.evaluate(cell);
                        if (sheetTarget.getStyle() != null) {
                            cell.setCellStyle(sheetTarget.getStyle());
                        }
                        cell.setCellValue(evaluate.formatAsString().replaceAll("\\\"", ""));
                    }
                }
            }
        }
        if (total != null && total.size() > 0 && CollectionUtils.isNotEmpty(mapList)) {
            for (Map.Entry<String, String> entry : total.entrySet()) {
                Row row = sheet.getRow(header + rowSize + 1 + Integer.valueOf(entry.getKey()));
                String cols = entry.getValue();

                if (row != null && StringUtils.isNotBlank(cols)) {
                    String[] split = cols.split(",");
                    for (String rowNum : split) {
                        Cell cell = row.createCell(Integer.valueOf(rowNum));
                        if (sheetTarget.getStyle() != null) {
                            cell.setCellStyle(sheetTarget.getStyle());
                        }
                        final List<String> finalKeys = keys;
                        Double sum = mapList.stream().mapToDouble(p -> Double.valueOf(p.get(finalKeys.get(Integer.valueOf(rowNum))).toString())).sum();
                        cell.setCellValue(sum.toString());
                    }
                }
            }
        }
    }

    private String getCellVal(Cell cell) {
        String val = "";
        if (cell == null) {
            return val;
        }
        switch (cell.getCellType().name()) {
            case "STRING":
                val = cell.getRichStringCellValue().getString();
                break;
            case "NUMERIC":
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date dateCellValue = cell.getDateCellValue();
                    val = new SimpleDateFormat("yyyy/MM/dd").format(dateCellValue);
                } else {
                    String s2 = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                    val = new BigDecimal(s2).stripTrailingZeros().toPlainString();
                }
                break;
            case "BLACK":
                val = "";
                break;
            default:
                val = cell.toString();
                break;
        }
        return val;
    }
}
