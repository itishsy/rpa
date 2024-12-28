package com.seebon.rpa.actions.impl.excel;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.DateUtils;
import com.seebon.common.utils.RegexUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.SheetTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ExcelWriteType;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RobotAction(name = "更新Excel", targetType = SheetTarget.class, comment = "更新Excel表格内容")
public class WriteExcel extends AbstractAction {

    @Conditions({"removeRow:rowIndex,headerIndex",
            "removeRowByCond:cond,headerIndex",
            "removeColumn:colIndex,colName,headerIndex",
            "updateCell:val", "updateColumn:val,rowIndex,cond",
            "updateColumnType:val,rowIndex", "removeCellByCond:cond,colIndex", "updateCellByCondition:cond,val,reCreateCell", "deleteColumn:colIndex","updateCellStyle:colIndex",
            "updateCellFormat:val", "resaveExcel:oldExcelPath,newExcelPath"})
    @ActionArgs(value = "更新类型", required = true, dict = ExcelWriteType.class)
    private String writeType;

    @ActionArgs(value = "列索引", comment = "0开始，多个逗号隔开")
    private String colIndex;

    @ActionArgs(value = "列名", comment = "列名，多个逗号隔开")
    private String colName;

    @ActionArgs(value = "列头索引", comment = "0开始")
    private Integer headerIndex;

    @ActionArgs(value = "行索引", comment = "0开始，多个逗号隔开")
    private String rowIndex;

    @ActionArgs(value = "赋值", comment = "赋值")
    private String val;

    @ActionArgs(value = "是否重新创建单元格", comment = "是否重新创建单元格，可填值有：是-1-true，否-0-false")
    private String reCreateCell;

    @ActionArgs(value = "条件", comment = "条件")
    private String cond;

    @ActionArgs(value = "旧文件地址", comment = "旧文件地址")
    private String oldExcelPath;

    @ActionArgs(value = "新文件地址", comment = "新文件地址")
    private String newExcelPath;

    @Override
    public void run() {
        Sheet sheet = getTarget();
        if (sheet == null) {
            return;
        }
        SheetTarget sheetTarget = (SheetTarget) this.getVariable("SheetTarget");
        sheetTarget.setWrite(true);
        ExcelWriteType type = ExcelWriteType.valueOf(writeType);
        switch (type) {
            case removeRow: {
                String[] rows = rowIndex.split(",");
                int delStart = -1;
                for (String r : rows) {
                    int del = Integer.parseInt(r);
                    Row row = sheet.getRow(del);
                    if (row != null) {
                        sheet.removeRow(row);
                        if (delStart < 0) {
                            delStart = del;
                        } else {
                            delStart = Math.min(delStart, del);
                        }
                    }
                }
                if (delStart >= 0) {
                    removeBlank(sheet, delStart);
                }
                break;
            }
            case removeRowByCond: {
                int size = sheet.getPhysicalNumberOfRows();
                int delStart = -1;
                Integer removeRowSize = ctx.get("removeRowSize");
                if (removeRowSize == null) {
                    removeRowSize = 0;
                }
                headerIndex = headerIndex == null ? headerIndex = 0 : headerIndex;
                for (int i = headerIndex+1; i < size; i++) {
                    Row row = sheet.getRow(i);
                    variable("row", row);
                    if (parse(cond, Boolean.class)) {
                        if (row != null) {
                            sheet.removeRow(row);
                            removeRowSize = removeRowSize + 1;
                        }
                        if (delStart < 0) {
                            delStart = i;
                        }
                    }
                }
                if (delStart >= 0) {
                    removeBlank(sheet, 1);
                }
                log.info("删除行数：{}", removeRowSize);
                ctx.setVariable("removeRowSize", removeRowSize);
                break;
            }
            case removeColumn: {
                if (StringUtils.isNotBlank(colIndex)) {
                    String[] cols = colIndex.split(",");
                    for (String col : cols) {
                        sheet.removeColumnBreak(Integer.parseInt(col));
                    }
                }
                if (StringUtils.isNotBlank(colName)) {
                    String[] split = colName.split(",");
                    List<String> colNames = Arrays.asList(split);
                    //遍历所有列
                    Row row = sheet.getRow(headerIndex);
                    for (int j = row.getLastCellNum(); j >= 1; j--) {
                        Cell cell = row.getCell(j - 1);
                        String columnName = cell.getStringCellValue();
                        if (StringUtils.isNotBlank(columnName)) {
                            columnName = columnName.trim();
                        }
                        //判断列是否是需要删除的
                        if (colNames.contains(columnName)) {
                            //删除指定列
                            sheet.removeColumnBreak(j - 1);
                        }
                    }
                }
                break;
            }
            case updateColumn: {
                JSONObject jsonObject = JSONObject.parseObject(val);
                int rowNum = sheet.getPhysicalNumberOfRows();
                for (String pos : jsonObject.keySet()) {
                    for (int r = Integer.parseInt(rowIndex); r < rowNum; r++) {
                        Row row = sheet.getRow(r);
                        if (row == null) {
                            continue;
                        }
                        ctx.setVariable("row", row);
                        int c = Integer.parseInt(pos);
                        Cell cell = row.getCell(c);
                        if (cell == null) {
                            cell = row.createCell(c);
                        }
                        ctx.setVariable("cellValue", cell.getStringCellValue());
                        if (!parse(StringUtils.isBlank(cond) ? "true" : cond, Boolean.class)) {
                            continue;
                        }
                        String v = jsonObject.getString(pos);
                        cell.setCellValue(parse(v, String.class));
                    }
                }
                ctx.remove("row");
                break;
            }
            case updateColumnType: {
                JSONObject jsonObject = JSONObject.parseObject(val);
                int rowNum = sheet.getPhysicalNumberOfRows();
                for (String pos : jsonObject.keySet()) {
                    for (int r = Integer.parseInt(rowIndex); r < rowNum; r++) {
                        Row row = sheet.getRow(r);
                        int c = Integer.parseInt(pos);
                        Cell cell = row.getCell(c);
                        String cellValue = getCellVal(cell);
                        if (cell == null && StringUtils.isBlank(cellValue)) {
                            break;
                        }
                        Integer cellType = jsonObject.getInteger(pos);
                        if (cellType == 1) { //数值
                            if (StringUtils.isNumeric(cellValue)) {
                                cell.setCellValue(Double.valueOf(cellValue));
                            } else if (cellValue.matches("[+-]?[0-9]+(\\.[0-9]+)?")) {
                                cell.setCellValue(Float.valueOf(cellValue));
                            }
                            cell.setCellType(CellType.NUMERIC);
                        } else if (cellType == 2) { // 日期
                            String dateFormat = getDateFormat(cellValue);
                            if (StringUtils.isNotBlank(dateFormat)) {
                                CellStyle cellStyleDate = sheet.getWorkbook().createCellStyle();
                                DataFormat format = sheet.getWorkbook().createDataFormat();
                                cellStyleDate.setDataFormat(format.getFormat(dateFormat));
                                cell.setCellValue(DateUtils.strToDate(cellValue, dateFormat));
                                cell.setCellStyle(cellStyleDate);
                            }
                        } else if (cellType == 3) {
                            cell.setCellType(CellType.STRING);
                            cell.setCellValue(cellValue);
                        }
                        if (cellType == 4) {
                            String date = cell.getStringCellValue();

                            DataFormat format=sheet.getWorkbook().createDataFormat();
                            CellStyle cellStyleDate  = sheet.getWorkbook().createCellStyle();
                            cellStyleDate.setDataFormat(format.getFormat("yyyy-MM"));//保留1位小数点
                            String time = date;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                            Date parse = null;
                            try {
                                parse = dateFormat.parse(time);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            cell.setCellValue(parse);
                            cell.setCellStyle(cellStyleDate);
                        }
                    }
                }
                break;
            }
            case updateCell: {
                JSONObject jsonObject = JSONObject.parseObject(val);
                for (String pos : jsonObject.keySet()) {
                    String first = pos.split(",")[0];
                    if (ELParser.isExpression(first)) {
                        first = parse(first, String.class);
                    }
                    String second = pos.split(",")[1];
                    if (ELParser.isExpression(second)) {
                        second = parse(second, String.class);
                    }
                    int r = Integer.parseInt(first);
                    int c = Integer.parseInt(second);
                    Row row = sheet.getRow(r);
                    Cell cell = row.getCell(c);
                    if (cell == null) {
                        cell = row.createCell(c);
                    }
                    String v = jsonObject.getString(pos);
                    cell.setCellValue(parse(v, String.class));
                }
                break;
            }
            case updateCellFormat: {
                JSONObject jsonObject = JSONObject.parseObject(val);
                for (String pos : jsonObject.keySet()) {
                    String first = pos.split(",")[0];
                    if (ELParser.isExpression(first)) {
                        first = parse(first, String.class);
                    }
                    String second = pos.split(",")[1];
                    if (ELParser.isExpression(second)) {
                        second = parse(second, String.class);
                    }
                    int r = Integer.parseInt(first);
                    int c = Integer.parseInt(second);
                    Row row = sheet.getRow(r);
                    Cell cell = row.getCell(c);
                    if (cell == null) {
                        cell = row.createCell(c);
                    }

                    Integer cellType = jsonObject.getInteger(pos);
                    String cellValue = getCellVal(cell);

                    if (cellType == 1) { //数值
                        if (StringUtils.isNumeric(cellValue)) {
                            cell.setCellValue(Double.valueOf(cellValue));
                        } else if (cellValue.matches("[+-]?[0-9]+(\\.[0-9]+)?")) {
                            cell.setCellValue(Float.valueOf(cellValue));
                        }
                        cell.setCellType(CellType.NUMERIC);
                    } else if (cellType == 2) { // 日期
                        String dateFormat = getDateFormat(cellValue);
                        if (StringUtils.isNotBlank(dateFormat)) {
                            CellStyle cellStyleDate = sheet.getWorkbook().createCellStyle();
                            DataFormat format = sheet.getWorkbook().createDataFormat();
                            cellStyleDate.setDataFormat(format.getFormat(dateFormat));
                            cell.setCellValue(DateUtils.strToDate(cellValue, dateFormat));
                            cell.setCellStyle(cellStyleDate);
                        }
                    } else if (cellType == 3) {
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(cellValue);
                    }
                    if (cellType == 4) {
                        String date = cell.getStringCellValue();

                        DataFormat format=sheet.getWorkbook().createDataFormat();
                        CellStyle cellStyleDate  = sheet.getWorkbook().createCellStyle();
                        cellStyleDate.setDataFormat(format.getFormat("yyyy-MM"));//保留1位小数点
                        String time = date;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                        Date parse = null;
                        try {
                            parse = dateFormat.parse(time);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        cell.setCellValue(parse);
                        cell.setCellStyle(cellStyleDate);
                    }
                }
                break;
            }
            case resaveExcel: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\ExcelResave.py");
                commands.add(oldExcelPath);
                commands.add(newExcelPath);
                PythonUtil.runCommand("重新保存excel", "转换成功", this.getTimeout(), commands);
            }
            case removeCellByCond: {
                int size = sheet.getPhysicalNumberOfRows();
                if (StringUtils.isBlank(colIndex)) {
                    break;
                }
                for (int i = 1; i < size; i++) {
                    Row row = sheet.getRow(i);
                    variable("row", row);
                    if (parse(cond, Boolean.class)) {
                        String[] cols = colIndex.split(",");
                        for (String cellIndex : cols) {
                            Cell cell = row.getCell(Integer.valueOf(cellIndex));
                            if (cell != null) {
                                row.removeCell(cell);
                            }
                        }
                    }
                }
                break;
            }
            case updateCellByCondition: {
                int size = sheet.getPhysicalNumberOfRows();
                for (int i = 1; i < size; i++) {
                    Row row = sheet.getRow(i);
                    ctx.setVariable("row", row);
                    //variable("row", row);
                    if (parse(cond, Boolean.class)) {
                        JSONObject jsonObject = JSONObject.parseObject(val);
                        for (String cellIndex : jsonObject.keySet()) {
                            Cell cell = row.getCell(Integer.valueOf(cellIndex));
                            if ((StringUtils.isNotBlank(reCreateCell) &&
                                    Lists.newArrayList("是", "1", "true").contains(reCreateCell)) || cell == null) {
                                CellStyle cellStyle = null;
                                if (cell!=null) {
                                    cellStyle = cell.getCellStyle();
                                }
                                cell = row.createCell(Integer.valueOf(cellIndex));
                                if (cellStyle != null) {
                                    cell.setCellStyle(cellStyle);
                                }
                            }
                            String v = jsonObject.getString(cellIndex);
                            cell.setCellValue(parse(v, String.class));
                        }
                    }
                    ctx.remove("row");
                }
                break;
            }
            case deleteColumn: {
                String[] cols = colIndex.split(",");
                for (int i = 0; i < cols.length; i++) {
                    int columnToDelete = Integer.parseInt(cols[i]);
                    deleteColumn(sheet, columnToDelete - i);
                }
                break;
            }
            default:
                break;
        }
    }

    public void deleteColumn(Sheet sheet, int columnToDelete) {
        for (int rId = 0; rId <= sheet.getLastRowNum(); rId++) {
            Row row = sheet.getRow(rId);
            for (int cID = columnToDelete; cID <= row.getLastCellNum(); cID++) {
                Cell cOld = row.getCell(cID);
                if (cOld != null) {
                    row.removeCell(cOld);
                }
                Cell cNext = row.getCell(cID + 1);
                if (cNext != null) {
                    Cell cNew = row.createCell(cID, cNext.getCellTypeEnum());
                    cloneCell(cNew, cNext);
                    if (rId == 0) {
                        sheet.setColumnWidth(cID, sheet.getColumnWidth(cID + 1));
                    }
                }
            }
        }
    }

    private void cloneCell(Cell cNew, Cell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());

        if (CellType.BOOLEAN == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getBooleanCellValue());
        } else if (CellType.NUMERIC == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getNumericCellValue());
        } else if (CellType.STRING == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getStringCellValue());
        } else if (CellType.ERROR == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getErrorCellValue());
        } else if (CellType.FORMULA == cNew.getCellTypeEnum()) {
            cNew.setCellValue(cOld.getCellFormula());
        }
    }


    /**
     * 移除空行
     *
     * @param sheet
     * @param start
     */
    public void removeBlank(Sheet sheet, int start) {
        // 遍历所有行
        for (int i = sheet.getLastRowNum(); i >= sheet.getFirstRowNum(); i--) {
            Row row = sheet.getRow(i);
            if ((i + 1) > sheet.getLastRowNum()) {
                continue;
            }
            // 判断行是否为空白行
            if (this.isBlankCellVal(row) && ((headerIndex != null && headerIndex < i) || headerIndex == null)) {
                log.info("startRow=" + (i + 1) + ",endRow=" + sheet.getLastRowNum());
                // 移动行，覆盖空白行
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
            }
        }
    }

    private boolean isBlankCellVal(Row row) {
        if (row == null) {
            return true;
        }
        //列数
        int cellNum = row.getPhysicalNumberOfCells();
        if (cellNum == 0) {
            return true;
        }
        int blankCellNum = 0;
        for (int num = 0; num < cellNum; num++) {
            if (row.getCell(num) == null || StringUtils.isBlank(this.getCellVal(row.getCell(num)))) {
                blankCellNum += 1;
            }
        }
        log.info("第" + row.getRowNum() + "行，列数：" + cellNum + ",空白列数：" + blankCellNum);
        if (blankCellNum == cellNum) {
            return true;
        }
        return false;
    }

    /**
     * 修复移除空行对应向上偏移
     *
     * @param sheet
     * @param start
     * @param size
     */
    private void removeBlank(Sheet sheet, int start, int size) {
        for (int i = start; i < size; i++) {
            if (i < 0) {
                continue;
            }
            Row row = sheet.getRow(i);
            if (row == null || (row.getCell(0) == null && row.getCell(1) == null)) {
                sheet.shiftRows(i + 1, size, -1, true, false);
                i--;
                //减去一条空行，总行数减一。
                size--;
            }
        }
    }

    private String getDateFormat(String strDate) {
        if (RegexUtil.isDate(strDate)) {
            if (strDate.contains("/") && strDate.indexOf("/") == 4) {
                if (strDate.length() > 8) {
                    return "yyyy/MM/dd";
                } else {
                    return "yyyy/M/d";
                }
            } else if (strDate.contains(".")) {
                return "yyyy.MM.dd";
            } else if (strDate.contains("-")) {
                return "yyyy-MM-dd";
            } else if (strDate.contains("/") && strDate.indexOf("/") == 2) {
                return "dd/MM/yyyy";
            } else if (strDate.contains("_")) {
                return "yyyy_MM_dd";
            } else if (strDate.length() == 8 && !strDate.contains("/") && !strDate.contains("-")) {
                return "yyyyMMdd";
            }
        }
        return "";
    }

    private String getCellVal(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.toString();
    }
    public static void updateDateStyle(Sheet sheet, int colIndex) throws IOException, ParseException {
        HSSFWorkbook workbook = null;
        for (Row row : sheet) {
            // 获取第六列的单元格
            Cell cell = row.getCell(colIndex); // 第六列的索引是5（从0开始）

            // 检查单元格是否为空或值是否为空字符串
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !cell.getStringCellValue().trim().isEmpty()) {
                String date = cell.getStringCellValue();
                System.out.println("第" + (row.getRowNum() + 1) + "行第六列的值" + date);

                HSSFDataFormat df = workbook.createDataFormat();
                HSSFCellStyle hssfCellStyleDouble = workbook.createCellStyle();
                hssfCellStyleDouble.setDataFormat(df.getFormat("yyyy-MM"));

                String time = date;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                try {
                    Date parse = dateFormat.parse(time);
                    cell.setCellValue(parse);
                    cell.setCellStyle(hssfCellStyleDouble);
                } catch (ParseException e) {
                    System.err.println("无法解析日期: " + time);
                    e.printStackTrace();
                }
            } else {
                System.out.println("第" + (row.getRowNum() + 1) + "行第六列的值为空，不进行修改");
            }
        }
    }
}
