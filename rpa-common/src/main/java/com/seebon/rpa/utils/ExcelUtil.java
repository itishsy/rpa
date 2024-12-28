package com.seebon.rpa.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ZhenShijun
 * @dateTime 2022-05-30 16:43:03
 */
public class ExcelUtil {
    // 最小列数目
    public static final int MIN_ROW_COLUMN_COUNT = 1;
    private static final Pattern pattern = Pattern.compile("#(.*)#");

    public static String[] readHeader(InputStream inputStream, int sheetIndex, int headerNum, boolean filterEmpty) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        if (sheetIndex > workbook.getNumberOfSheets()) {
            return null;
        }
        Sheet sheet = workbook.getSheetAt(sheetIndex);

        int headerIndex = headerNum - 1;
        Row header = sheet.getRow(headerIndex);
        int columnLength = Math.max(header.getLastCellNum(), MIN_ROW_COLUMN_COUNT);
        String[] headers = new String[columnLength];
        // 遍历每一层header，header的名字从最顶层到最底层以.分割排列，例如: A.B.C
        String[][] multiHeaders = new String[headerNum][columnLength];
        for (int i = 0; i <= headerIndex; i++) {
            Row row = sheet.getRow(i);
            // 初始化headers，每一列的标题
            for (int columnIndex = 0; columnIndex < columnLength; columnIndex++) {
                // Cell cell = row.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);
                Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = _getCellValue(cell).trim();
                //如果单元格为空，可能是单元格组合，也可能是真的为空
                multiHeaders[i][columnIndex] = cellValue;
            }
        }
        return _reductionHeader(multiHeaders, headers);
    }

    public static Map<String, Object> getManyExcelHeads(InputStream input, String fileName) {
        List<String> heads = new ArrayList<>();
        List<String> wxHeads = new ArrayList<>();
        Workbook workbook = null;
        Map<String, Object> result = Maps.newHashMap();
        try {
            if (fileName.lastIndexOf(".xlsx") > 0) {
                workbook = new XSSFWorkbook(input);
            } else {
                workbook = new HSSFWorkbook(input);
            }
            result.put("workbook", workbook);
            Integer sheetSize = workbook.getNumberOfSheets();
            if (sheetSize == 0) {
                result.put("heads", heads);
                return result;
            }
            Integer tempSheetIndex = null;
            for (int sheetIndex = 0; sheetIndex < sheetSize; sheetIndex++) {
                if (tempSheetIndex != null) {
                    break;
                }
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Integer rowIndex = 20; // 遍历前20行,识别不到判断为空表
                for (int ridx = 0; ridx <= rowIndex; ridx++) {
                    Row hssfRow = sheet.getRow(ridx);
                    if (hssfRow == null) {
                        continue;
                    }
                    int lastCellNum = (int) hssfRow.getLastCellNum();
                    boolean blackRow = true;
                    Cell hssfCell = null;
                    for (int i = 0; i <= lastCellNum; i++) {
                        hssfCell = hssfRow.getCell(i);
                        if (hssfCell != null && StringUtils.isNotBlank(hssfCell.toString())) {
                            blackRow = false;
                            break;
                        }
                    }

                    if (blackRow) {
                        continue;
                    }

                    String cellName = hssfCell.toString();
                    if (StringUtils.containsIgnoreCase(cellName, "#")) {
                        rowIndex = ridx;
                        break;
                    }
                }
                Row fieldRow = sheet.getRow(rowIndex);
                if (fieldRow == null) {
                    continue;
                }
                Integer columnSize = fieldRow.getPhysicalNumberOfCells();
                int i = 0;
                for (int j = 0; j < columnSize; ) {
                    if (fieldRow.getCell(i) == null) { //跳过空字段
                        i++;
                        continue;
                    }
                    j++;
                    String columnKey = fieldRow.getCell(i).toString();
                    Matcher m = pattern.matcher(columnKey);
                    String head = "";
                    while (m.find()) {
                        head = m.group(1);
                    }
                    if (StringUtils.isNotEmpty(head)) {
                        heads.add(head);
                        if (tempSheetIndex == null) {
                            tempSheetIndex = sheetIndex;
                        }
                    } else {
                        if (StringUtils.isNotBlank(columnKey)) {
                            wxHeads.add(columnKey);
                        }
                    }
                    i++;
                }
            }
            result.put("tempSheetIndex", tempSheetIndex);
            result.put("heads", heads);
            result.put("wxHeads", wxHeads);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> getManyExcelHeadsInfo(InputStream input, String fileName) {
        Workbook workbook = null;
        Map<String, Object> result = Maps.newHashMap();
        List<Map<String, Object>> cols = Lists.newArrayList();
        try {
            if (fileName.lastIndexOf(".xlsx") > 0) {
                workbook = new XSSFWorkbook(input);
            } else {
                workbook = new HSSFWorkbook(input);
            }
            Integer sheetSize = workbook.getNumberOfSheets();
            if (sheetSize == 0) {
                return result;
            }
            Integer tempSheetIndex = null;
            Integer heardIndex = null;
            for (int sheetIndex = 0; sheetIndex < sheetSize; sheetIndex++) {
                if (tempSheetIndex != null) {
                    break;
                }
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                Integer rowIndex = 20; // 遍历前20行,识别不到判断为空表
                for (int ridx = 0; ridx <= rowIndex; ridx++) {
                    Row hssfRow = sheet.getRow(ridx);
                    if (hssfRow == null) {
                        continue;
                    }
                    int lastCellNum = (int) hssfRow.getLastCellNum();
                    boolean blackRow = true;
                    Cell hssfCell = null;
                    for (int i = 0; i <= lastCellNum; i++) {
                        hssfCell = hssfRow.getCell(i);
                        if (hssfCell != null && StringUtils.isNotBlank(hssfCell.toString())) {
                            blackRow = false;
                            break;
                        }
                    }

                    if (blackRow) {
                        continue;
                    }

                    String cellName = hssfCell.toString();
                    if (StringUtils.containsIgnoreCase(cellName, "#")) {
                        rowIndex = ridx;
                        break;
                    }
                }
                Row fieldRow = sheet.getRow(rowIndex);
                if (fieldRow == null) {
                    continue;
                }
                Integer columnSize = fieldRow.getPhysicalNumberOfCells();
                int i = 0;
                for (int j = 0; j < columnSize; ) {
                    if (fieldRow.getCell(i) == null) { //跳过空字段
                        i++;
                        continue;
                    }
                    j++;
                    String columnKey = fieldRow.getCell(i).toString();
                    Matcher m = pattern.matcher(columnKey);
                    String head = "";
                    while (m.find()) {
                        head = m.group(1);
                    }
                    if (StringUtils.isNotEmpty(head)) {
                        Map<String, Object> item = Maps.newHashMap();
                        item.put("fieldName", head);
                        item.put("colIndex", i);
                        cols.add(item);
                        if (tempSheetIndex == null) {
                            tempSheetIndex = sheetIndex;
                        }
                        if (heardIndex == null) {
                            heardIndex = rowIndex -1;
                        }
                    }
                    i++;
                }
            }
            result.put("cols", cols);
            result.put("tempSheetIndex", tempSheetIndex);
            result.put("heardIndex", heardIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 功能说明：将二维header转换为一维数组
     * 参数说明：
     *
     * @author IBM
     * 2013-11-28 下午3:47:27
     */
    private static String[] _reductionHeader(String[][] multiHeaders, String[] headers) {
        for (int row = 0; row < multiHeaders.length; row++) {
            for (int column = 0; column < multiHeaders[row].length; column++) {
                if (row == 0) {
                    headers[column] = multiHeaders[row][column];
                } else {
                    if (!"".equals(headers[column])) {
                        if (!"".equals(multiHeaders[row][column])) {
                            headers[column] += "." + multiHeaders[row][column];
                        }
                    } else {
                        if (!"".equals(multiHeaders[row][column])) {
                            headers[column] = multiHeaders[row][column];
                        }
                    }
                }

            }
        }
        return headers;
    }

    /**
     * 功能说明： 读取每个单元格中的内容
     * 参数说明：
     *
     * @author system
     * 2013-11-28 下午3:27:43
     */
    private static String _getCellValue(Cell cell) {
        // 如果单元格为空的，则返回空字符串
        if (cell == null) {
            return "";
        }

        // 根据单元格类型，以不同的方式读取单元格的值
        String value = "";
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cell.getDateCellValue());
                } else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    value = df.format(cell.getNumericCellValue());
                    if (value.endsWith(".0")) {
                        value = value.replace(".0", "");
                    }
                    if (value.endsWith(".00")) {
                        value = value.replace(".00", "");
                    }
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue() ? "TRUE" : "FALSE";
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            default:
        }
        return value;
    }
}
