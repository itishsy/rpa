package com.seebon.rpa.actions.impl.excel;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DeclareInfoFetchTarget;
import com.seebon.rpa.actions.target.impl.SheetTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ExcelReadType;
import com.seebon.rpa.context.enums.TextType;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "读取Excel",
        targetType = SheetTarget.class,
        comment = "从Excel表格中读取满足条件的数据，并将结果存放到指定变量。条件为空时，读取全部。row为行数据Map对象")
public class ReadExcel extends AbstractAction {

    public static final String EXCEL_ROW_ITEM = "excelRowItem";

    public static final ScriptEngine js = new ScriptEngineManager().getEngineByName("js");

    @Conditions({
            "cell:rowNumber,colNumber,dataKey",
            "header:fieldMap,condition,header,replaceMap,calculationMap,customInfo,dataKey",
            "multipleHeader:fieldMap,condition,startHeader,header,replaceMap,calculationMap,customInfo,dataKey",
            "index:fieldMap,condition,header,replaceMap,calculationMap,customInfo,dataKey",
            "python:header,dataKey"
    })
    @ActionArgs(value = "读取方式", required = true, dict = ExcelReadType.class)
    private String readType;

    //key为字段名value为包含row对象的表达式
    @ActionArgs(value = "读取字段", comment = "缺省，读取所有列,key为列索引")
    private LinkedHashMap<String, Object> fieldMap;

    @ActionArgs(value = "满足条件", parsed = false, comment = "内置变量row、rowIndex")
    private String condition;

    @ActionArgs(value = "表头开始行号", comment = "缺省，第1行为表头")
    private Integer startHeader;

    @ActionArgs(value = "表头行号", comment = "缺省，第1行为表头")
    private Integer header;

    @ActionArgs(value = "自定义字段信息", comment = "读取excel时追加一些自定义字段")
    private LinkedHashMap<String, Object> customInfo;

    @ActionArgs(value = "行序号")
    private Integer rowNumber;

    @ActionArgs(value = "列序号")
    private Integer colNumber;

    @ActionArgs(value = "内容替换信息", comment = "缺省，内容特殊内容替换，key为列索引或列头名称")
    private Map<String, String> replaceMap;

    @ActionArgs(value = "计算字段信息", comment = "文件读取不到的字段，需要通过计算获取")
    private Map<String, String> calculationMap;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        if (ExcelReadType.python.equals(ExcelReadType.valueOf(readType))) {
            SheetTarget target = (SheetTarget) this.getVariable("SheetTarget");
            List<String> commands = Lists.newArrayList();
            commands.add(RobotContext.pythonPath);
            commands.add(RobotContext.workPath + "\\python\\ReadXlsData.py");
            commands.add(target.getFilePath());
            if (header != null) {
                commands.add(header.toString());
            }
            if (target.getSheetIndex() != null) {
                commands.add(target.getSheetIndex().toString());
            }
            List<String> texts = PythonUtil.runCommand("pythonReadExcel", null, this.getTimeout(), commands);
            if (CollectionUtils.isEmpty(texts)) {
                ctx.setVariable(dataKey, "");
            } else {
                JSONObject jsonObj = JSON.parseObject(texts.stream().collect(Collectors.joining(",")), JSONObject.class);
                log.info("jsonObj=" + jsonObj.toJSONString());
                if (jsonObj.get("code") != null && jsonObj.getIntValue("code") == 200) {
                    ctx.setVariable(dataKey, jsonObj.getJSONArray("data"));
                } else {
                    ctx.setVariable(dataKey, "");
                }
            }
            return;
        }
        Sheet sheet = getTarget();
        if (sheet == null) {
            ctx.setVariable(dataKey, null);
            return;
        }
        int rowSize = sheet.getLastRowNum() + 1;
        List<LinkedHashMap<String, Object>> resultList = Lists.newArrayList();
        ExcelReadType type = ExcelReadType.valueOf(readType);
        switch (type) {
            case index:
                for (int i = header; i < rowSize; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    variable("row", row);
                    variable("rowIndex", i);
                    boolean readable = StringUtils.isBlank(condition) || parse(condition, Boolean.TYPE);
                    if (readable) {
                        LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
                        reloadFieldMap();//支持表达式
                        for (String key : fieldMap.keySet()) {
                            Integer value = Integer.valueOf(fieldMap.get(key).toString());
                            Object cellVal = getCellVal(row, value);
                            if (cellVal != null) {
                                if (key.contains(".")) {
                                    String[] keys = key.split("\\.");
                                    Map<String, Object> map = (Map<String, Object>) result.get(keys[0]);
                                    if (!result.containsKey(keys[0]) || map == null) {
                                        map = Maps.newHashMap();
                                    }
                                    map.put(keys[1], cellVal);
                                    result.put(keys[0], map);
                                } else {
                                    result.put(key, cellVal);
                                }

                            }
                        }
                        if (customInfo != null && !customInfo.isEmpty()) {
                            result.putAll(customInfo);
                        }
                        if (calculationMap != null && !calculationMap.isEmpty()) {
                            Map<String, String> calculation = Maps.newHashMap();
                            ctx.setVariable(EXCEL_ROW_ITEM, result);
                            try {
                                for (String key : calculationMap.keySet()) {
                                    String keyText = ELParser.parse(key, ctx.getVariables(), String.class);
                                    String parse = ELParser.parse(calculationMap.get(key), ctx.getVariables(), String.class);
                                    String val = String.valueOf(js.eval(parse));
                                    calculation.put(keyText, val);
                                }
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            } finally {
                                ctx.remove(EXCEL_ROW_ITEM);
                            }
                            result.putAll(calculation);
                        }
                        if (!result.isEmpty()) {
                            resultList.add(result);
                        }
                    }
                }
                ctx.setVariable(dataKey, resultList);
                System.out.println(resultList + "--------------------------------666666666666");
                break;
            case header:
                ExcelHelper excelHelper = ExcelHelper.readExcel(sheet, header);
                String[] headerRow = null;
                String[][] rows = excelHelper.getDatas();
                for (String[] row : rows) {
                    if (null == headerRow) {
                        headerRow = excelHelper.getHeaders();
                        for (int i = 0; i < row.length; i++) {
                            if (headerRow[i].contains(".")) {
                                headerRow[i] = headerRow[i].substring(headerRow[i].lastIndexOf(".") + 1);
                            }
                        }
                    }
                    Map<String, Object> rowMap = Maps.newHashMap();
                    Convert.fetchMap(headerRow, row, rowMap);
                    ctx.setVariable("row", rowMap);
                    System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                    boolean readable = StringUtils.isBlank(condition) || parse(condition, Boolean.TYPE);
                    if (readable) {
                        LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
                        for (String key : fieldMap.keySet()) {
                            String value = fieldMap.get(key).toString();
                            Object val = parse(value, Object.class);
                            if (val instanceof String && val != null && replaceMap != null && !replaceMap.isEmpty() && replaceMap.containsKey(key)) {
                                String replaceInfo = replaceMap.get(key);
                                String[] split = replaceInfo.split(",");
                                for (String str : split) {
                                    String[] replaceArr = str.split(":");
                                    val = ((String) val).replace(replaceArr[0], replaceArr.length < 2 ? "" : replaceArr[1]);
                                }
                                /*String oldText = replaceInfo.get("oldText");
                                String newText = replaceInfo.get("newText");
                                val = ((String) val).replace(oldText, newText);*/
                            }
                            if (key.contains(".")) {
                                String[] keys = key.split("\\.");
                                Map<String, Object> map = (Map<String, Object>) result.get(keys[0]);
                                if (!result.containsKey(keys[0]) || map == null) {
                                    map = Maps.newHashMap();
                                }
                                map.put(keys[1], val);
                                result.put(keys[0], map);
                            } else {
                                result.put(key, val);
                            }
                        }
                        if (customInfo != null && !customInfo.isEmpty()) {
                            result.putAll(customInfo);
                        }
                        if (calculationMap != null && !calculationMap.isEmpty()) {
                            Map<String, String> calculation = Maps.newHashMap();
                            ctx.setVariable(EXCEL_ROW_ITEM, result);
                            try {
                                for (String key : calculationMap.keySet()) {
                                    String keyText = ELParser.parse(key, ctx.getVariables(), String.class);
                                    String parse = ELParser.parse(calculationMap.get(key), ctx.getVariables(), String.class);
                                    String val = String.valueOf(js.eval(parse));
                                    calculation.put(keyText, val);
                                }
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            } finally {
                                ctx.remove(EXCEL_ROW_ITEM);
                            }
                            result.putAll(calculation);
                        }
                        if (!result.isEmpty()) {
                            resultList.add(result);
                        }
                    }
                    ctx.remove("row");
                }
                System.out.println("header resultList =====  " + JsonUtils.toJSon(resultList));
                ctx.setVariable(dataKey, resultList);
                break;
            case multipleHeader:
                ExcelHelper excelHelper2 = ExcelHelper.readExcelMultiple(sheet, startHeader, header);
                String[] headerRow2 = null;
                String[][] rows2 = excelHelper2.getDatas();
                for (String[] row : rows2) {
                    if (null == headerRow2) {
                        headerRow2 = excelHelper2.getHeaders();
                        for (int i = 0; i < row.length; i++) {
                            if (headerRow2[i].contains(".")) {
                                headerRow2[i] = headerRow2[i].replaceAll("\\.", "");
                            }
                        }
                    }
                    Map<String, Object> rowMap = Maps.newLinkedHashMap();
                    Convert.fetchMap(headerRow2, row, rowMap);
                    ctx.setVariable("row", rowMap);
                    System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                    boolean readable = StringUtils.isBlank(condition) || parse(condition, Boolean.TYPE);
                    if (readable) {
                        LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
                        for (String key : rowMap.keySet()) {
                            String value = rowMap.get(key).toString();
                            Object val = parse(value, Object.class);
                            if (val instanceof String && val != null && replaceMap != null && !replaceMap.isEmpty() && replaceMap.containsKey(key)) {
                                String replaceInfo = replaceMap.get(key);
                                String[] split = replaceInfo.split(",");
                                for (String str : split) {
                                    String[] replaceArr = str.split(":");
                                    val = ((String) val).replace(replaceArr[0], replaceArr.length < 2 ? "" : replaceArr[1]);
                                }
                            }
                            if (key.contains(".")) {
                                String[] keys = key.split("\\.");
                                Map<String, Object> map = (Map<String, Object>) result.get(keys[0]);
                                if (!result.containsKey(keys[0]) || map == null) {
                                    map = Maps.newHashMap();
                                }
                                map.put(keys[1], val);
                                result.put(keys[0], map);
                            } else {
                                result.put(key, val);
                            }
                        }
                        if (customInfo != null && !customInfo.isEmpty()) {
                            result.putAll(customInfo);
                        }
                        if (calculationMap != null && !calculationMap.isEmpty()) {
                            Map<String, String> calculation = Maps.newHashMap();
                            ctx.setVariable(EXCEL_ROW_ITEM, result);
                            try {
                                for (String key : calculationMap.keySet()) {
                                    String keyText = ELParser.parse(key, ctx.getVariables(), String.class);
                                    String parse = ELParser.parse(calculationMap.get(key), ctx.getVariables(), String.class);
                                    String val = String.valueOf(js.eval(parse));
                                    calculation.put(keyText, val);
                                }
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            } finally {
                                ctx.remove(EXCEL_ROW_ITEM);
                            }
                            result.putAll(calculation);
                        }
                        if (!result.isEmpty()) {
                            resultList.add(result);
                        }
                    }
                    ctx.remove("row");
                }
                System.out.println("header resultList =====  " + JsonUtils.toJSon(resultList));
                ctx.setVariable(dataKey, resultList);
                break;
            case cell:
                Row row = sheet.getRow(rowNumber);
                String val = null;
                if(null != row && null != row.getCell(colNumber)) {
                    val = row.getCell(colNumber).getStringCellValue();
                }
                ctx.setVariable(dataKey, val);
                break;
            default:
                break;
        }
    }

    private Object getCellVal(Row row, Integer value) {
        Object val = null;
        if (row.getCell(value) == null) {
            return val;
        }
        Cell cell = row.getCell(value);
        switch (cell.getCellType().name()) {
            case "STRING":
                val = cell.getRichStringCellValue().getString();
                if (val != null && replaceMap != null && !replaceMap.isEmpty() && replaceMap.containsKey(String.valueOf(value))) {
                    String replaceInfo = replaceMap.get(String.valueOf(value));
                    String[] split = replaceInfo.split(",");
                    for (String str : split) {
                        String[] replaceArr = str.split(":");
                        val = ((String) val).replace(replaceArr[0], replaceArr.length < 2 ? "" : replaceArr[1]);
                    }
                    /*String oldText = replaceInfo.get("oldText");
                    String newText = replaceInfo.get("newText");*/
//                    val = ((String) val).replace(oldText, newText);
                }
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
            case "BOOLEAN":
                val = cell.getBooleanCellValue();
                break;
            case "BLACK":
                val = "";
                break;
            default:
                val = cell.toString();
                if (StringUtils.isNotBlank((String) val) && null != replaceMap && !replaceMap.isEmpty() && replaceMap.containsKey(value)) {
                    String replaceInfo = replaceMap.get(String.valueOf(value));
                    String[] split = replaceInfo.split(",");
                    for (String str : split) {
                        String[] replaceArr = str.split(":");
                        val = ((String) val).replace(replaceArr[0], replaceArr.length < 2 ? "" : replaceArr[1]);
                    }
                    /*String oldText = replaceInfo.get("oldText");
                    String newText = replaceInfo.get("newText");
                    val = ((String) val).replace(oldText, newText);*/
                }
                break;
        }
        return val;
    }

    /**
     *  判断是否有表达式  如果有  则支持
     */
    private void reloadFieldMap(){
        if(MapUtils.isNotEmpty(fieldMap)){
            if(!fieldMap.keySet().toString().contains("$")) return;
            LinkedHashMap<String, Object> newMap = Maps.newLinkedHashMapWithExpectedSize(fieldMap.size());
            for(String key :fieldMap.keySet()){
                newMap.put(ELParser.parse(key, ctx.getVariables(), String.class),fieldMap.get(key)) ;//key值支持表达式
            }
            fieldMap = newMap;
        }
    }
}
