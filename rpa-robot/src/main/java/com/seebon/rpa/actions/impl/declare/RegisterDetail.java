package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.common.utils.JsonUtils;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.FileUtil;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.DeclareVerifyType;
import com.seebon.rpa.context.enums.FileMethod;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RobotAction(name = "在册明细", order = 50, targetType = NoneTarget.class, comment = "在册明细")
public class RegisterDetail extends AbstractAction {

    @ActionArgs(value = "获取方式", dict = DeclareVerifyType.class, required = true)
    @Conditions({
            "excel:idCardColIndex,nameColIndex,empAccountColIndex,baseColIndex,header,condition",
            "json:jsonList,headerCols,tplPath,idCardFieldName,nameFieldName,empAccountFieldName,baseName,updates,condition",
            "jsonPdf:jsonList,customPath,idCardFieldName,nameFieldName,empAccountFieldName,baseName",
            "customPath:customPath,idCardColIndex,nameColIndex,empAccountColIndex,baseColIndex,header,condition",
    })
    private String verifyType;

    @ActionArgs(value = "表头行号", comment = "缺省，第1行为表头")
    private Integer header;

    @ActionArgs(value = "模板文件路径", comment = "模板文件路径")
    private String tplPath;

    @ActionArgs(value = "表头列", comment = "如个人账号,姓名,转出单位账号")
    private String headerCols;

    @ActionArgs(value = "数据列表", comment = "json数据列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> jsonList;

    @ActionArgs(value = "身份证属性名", comment = "json身份证属性名", required = true, style = DynamicFieldType.text)
    private String idCardFieldName;

    @ActionArgs(value = "姓名属性名", comment = "json姓名属性名", required = true, style = DynamicFieldType.text)
    private String nameFieldName;

    @ActionArgs(value = "个人账号属性名", comment = "json个人社保号/个人公积金号属性名", style = DynamicFieldType.text)
    private String empAccountFieldName;

    @ActionArgs(value = "基数属性名", comment = "基数属性名", style = DynamicFieldType.text)
    private String baseName;

    @ActionArgs(value = "修改信息", comment = "Json")
    private Map<String, Object> updates;

    @ActionArgs(value = "文件路径", comment = "文件路径", style = DynamicFieldType.text)
    private String customPath;

    @ActionArgs(value = "身份证所在列", dict = WebExcelNumLine.class, required = true, comment = "配置excel文件身份证所在列")
    private WebExcelNumLine idCardColIndex;

    @ActionArgs(value = "姓名所在列", dict = WebExcelNumLine.class, required = true, comment = "配置excel文件姓名所在列")
    private WebExcelNumLine nameColIndex;

    @ActionArgs(value = "个人账号所在列", dict = WebExcelNumLine.class, comment = "配置excel文件个人社保号/个人公积金号所在列")
    private WebExcelNumLine empAccountColIndex;

    @ActionArgs(value = "基数所在列", dict = WebExcelNumLine.class, comment = "配置excel文件基数所在列")
    private WebExcelNumLine baseColIndex;

    @ActionArgs(value = "满足条件", parsed = false, comment = "内置变量row")
    private String condition;

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private RobotCommonService robotCommonService;

    @Override
    public void run() {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        switch (DeclareVerifyType.valueOf(verifyType)) {
            case excel:
                String downloadFileKey = "registerDetailDownloadFilePath";
                fileUtil.setActionType(FileMethod.downloadNewFile.toString());
                fileUtil.setVarKey(downloadFileKey);
                fileUtil.run();

                String filePath = ctx.get(downloadFileKey);
                this.excel(dataList, filePath);
                ctx.remove(downloadFileKey);
                this.saveRegister(dataList, filePath);
                break;
            case customPath:
                this.excel(dataList, customPath);
                this.saveRegister(dataList, customPath);
                break;
            case json:
                String registerPath = this.getRegisterPath();
                Map<String, Integer> colIndexMap = Maps.newHashMap();
                this.generateExcel(colIndexMap, registerPath);
                this.jsonExcel(dataList, colIndexMap, registerPath);
                this.saveRegister(dataList, registerPath);
                break;
            case jsonPdf:
                this.jsonPdf(dataList);
                this.saveRegister(dataList, customPath);
                break;
            default:
                break;
        }
        log.info("在册保存完成");
    }

    private void excel(List<Map<String, Object>> dataList, String filePath) {
        //csv自动转换xlsx
        filePath = fileUtil.csvToExcel(filePath);
        Workbook workbook = null;
        header = header == null ? 1 : header;
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
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
                String idCard = row[idCardColIndex.getIndex()];
                if (StringUtils.isBlank(idCard) || Validator.hasChinese(idCard)) {
                    continue;
                }
                String name = null;
                if (nameColIndex != null) {
                    name = row[nameColIndex.getIndex()];
                }
                String empAccount = null;
                if (empAccountColIndex != null) {
                    empAccount = row[empAccountColIndex.getIndex()];
                }
                String base = null;
                if (baseColIndex != null) {
                    base = row[baseColIndex.getIndex()];
                }
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                ctx.setVariable("row", rowMap);
                boolean readable = StringUtils.isBlank(condition) || parse(condition, Boolean.TYPE);
                if (!readable) {
                    continue;
                }
                if (nameColIndex != null) {
                    Map<String, Object> dataMap = Maps.newHashMap();
                    for (String key : rowMap.keySet()) {
                        Object value = rowMap.get(key);
                        if (value != null) {
                            if (idCard.equals(value.toString())) {
                                dataMap.put("idCard", value);
                                if (empAccount != null && value.toString().equals(empAccount)) {
                                    dataMap.put("empAccount", value);
                                }
                            } else if (name.equals(value.toString())) {
                                dataMap.put("empName", value);
                            } else if (empAccount != null && value.toString().equals(empAccount)) {
                                dataMap.put("empAccount", value);
                            } else if (base != null && value.toString().equals(base)) {
                                dataMap.put("base", value);
                            } else {
                                dataMap.put("otherInfo." + key, value);
                            }
                        } else {
                            dataMap.put("otherInfo." + key, "");
                        }
                    }
                    dataList.add(dataMap);
                }
                ctx.remove("row");
            }
            excelHelper.close();
            excelHelper.clear();
        } catch (Exception e) {
            log.error("Excel核验异常" + e.getMessage(), e);
            throw new RuntimeException("Excel核验异常" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    private void jsonExcel(List<Map<String, Object>> dataList, Map<String, Integer> colIndexMap, String registerPath) {
        Workbook workbook = null;
        header = header == null ? 0 : header;
        try {
            workbook = WorkbookFactory.create(new File(registerPath));
            Sheet sheet = workbook.getSheetAt(0);
            ExcelHelper excelHelper = ExcelHelper.readExcel(sheet, header + 1);
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
                String idCard = row[colIndexMap.get("idCardColIndex")];
                if (StringUtils.isBlank(idCard) || Validator.hasChinese(idCard)) {
                    continue;
                }
                String name = row[colIndexMap.get("nameColIndex")];
                String empAccount = "";
                if (colIndexMap.containsKey("empAccountColIndex")) {
                    empAccount = row[colIndexMap.get("empAccountColIndex")];
                }
                String base = null;
                if (colIndexMap.containsKey("baseColIndex")) {
                    base = row[colIndexMap.get("baseColIndex")];
                }
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                ctx.setVariable("row", rowMap);
                boolean readable = StringUtils.isBlank(condition) || parse(condition, Boolean.TYPE);
                if (!readable) {
                    continue;
                }
                Map<String, Object> dataMap = Maps.newHashMap();
                for (String key : rowMap.keySet()) {
                    Object value = rowMap.get(key);
                    if (value != null) {
                        if (idCard.equals(value.toString())) {
                            dataMap.put("idCard", value);
                            if (StringUtils.isNotBlank(empAccount) && value.toString().equals(empAccount)) {
                                dataMap.put("empAccount", value);
                            }
                        } else if (name.equals(value.toString())) {
                            dataMap.put("empName", value);
                        } else if (StringUtils.isNotBlank(empAccount) && value.toString().equals(empAccount)) {
                            dataMap.put("empAccount", value);
                        } else if (StringUtils.isNotBlank(base) && base.equals(value.toString())) {
                            dataMap.put("base", value);
                        } else {
                            dataMap.put("otherInfo." + key, value);
                        }
                    } else {
                        dataMap.put("otherInfo." + key, "");
                    }
                }
                dataList.add(dataMap);
                ctx.remove("row");
            }
            excelHelper.close();
            excelHelper.clear();
        } catch (Exception e) {
            log.error("Excel核验异常" + e.getMessage(), e);
            throw new RuntimeException("Excel核验异常" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    private void jsonPdf(List<Map<String, Object>> dataList) {
        int rowSize = jsonList.size();
        for (int i = 0; i < rowSize; i++) {
            Map<String, Object> rowMap = jsonList.get(i);
            Map<String, Object> dataMap = Maps.newHashMap();
            for (String key : rowMap.keySet()) {
                Object value = rowMap.get(key);
                if (value != null) {
                    if (key.equals(idCardFieldName)) {
                        dataMap.put("idCard", value);
                    } else if (key.equals(nameFieldName)) {
                        dataMap.put("empName", value);
                    } else if (StringUtils.isNotBlank(empAccountFieldName) && key.equals(empAccountFieldName)) {
                        dataMap.put("empAccount", value);
                    } else if (StringUtils.isNotBlank(baseName) && key.equals(baseName)) {
                        dataMap.put("base", value);
                    } else {
                        dataMap.put("otherInfo." + key, value);
                    }
                } else {
                    dataMap.put("otherInfo." + key, "");
                }
            }
            dataList.add(dataMap);
        }
    }

    private void generateExcel(Map<String, Integer> colIndexMap, String registerPath) {
        try {
            File src = new File(tplPath);
            Path dest = (Path) Paths.get(registerPath);
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(registerPath));
            Sheet sheet = workbook.getSheetAt(0);
            List<String> keys = Lists.newArrayList();
            header = (header == null) ? 0 : header;
            Row headerRow = sheet.getRow(header);
            if (StringUtils.isNotBlank(headerCols)) {
                keys = Lists.newArrayList(headerCols.split(","));
            } else {
                keys = Lists.newArrayList();
                short lastCellNum = headerRow.getLastCellNum();
                for (int i = 0; i < lastCellNum; i++) {
                    keys.add(Convert.getCellVal(headerRow.getCell(i)));
                }
            }
            CellStyle cellStyle = Convert.getCellStyle(workbook);
            int rowSize = jsonList.size();
            for (int i = 0; i < rowSize; i++) {
                Map<String, Object> map = jsonList.get(i);
                map.put("序号", (i + 1) + "");
                Row row = sheet.getRow(i + header + 1);
                if (row == null) {
                    row = sheet.createRow(i + header + 1);
                }
                for (int j = 0; j < keys.size(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        cell = row.createCell(j);
                    }
                    cell.setCellStyle(cellStyle);
                    if (map.containsKey(keys.get(j))) {
                        String key = keys.get(j);
                        if (key.equals(idCardFieldName)) {
                            colIndexMap.put("idCardColIndex", j);
                        } else if (key.equals(nameFieldName)) {
                            colIndexMap.put("nameColIndex", j);
                        } else if (key.equals(empAccountFieldName)) {
                            colIndexMap.put("empAccountColIndex", j);
                        } else if (key.equals(baseName)) {
                            colIndexMap.put("baseColIndex", j);
                        }
                        Object o = map.get(keys.get(j));
                        if (o != null) {
                            cell.setCellValue(this.getCellVal(key, o.toString()));
                        } else {
                            cell.setCellValue("");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("生成Excel异常" + e.getMessage(), e);
            throw new RuntimeException("生成Excel异常" + e.getMessage(), e);
        } finally {
            Convert.releaseExcel(workbook, registerPath);
        }
    }

    private String getCellVal(String key, String cellValue) {
        if (updates == null) {
            return cellValue;
        }
        Object update = updates.get(key);
        if (update == null) {
            return cellValue;
        }
        if (!update.toString().contains("=")) {
            return update.toString();
        }//{"aac004":"1=女|2=男","cbxz":"210=失业保险|110=养老保险|410=工伤保险","aac227":"A0A=在职人员"}
        /*String[] updateList = update.toString().split("\\|");
        for (String itemKey : updateList) {
            String[] itemList = itemKey.split("=");
            if (cellValue.equals(itemList[0])) {
                return itemList[1];
            }
        }*/
        //第三方平台有时候返回的值是拼接的  如参保险种 "cbxz":"210,410,110"
        String[] _keys = null;
        if (cellValue.contains(",")) {
            _keys = cellValue.split(",");
        } else if (cellValue.contains("\\|")) {
            _keys = cellValue.split("\\|");
        } else {
            _keys = new String[]{cellValue};
        }
        //转换
        Set<String> res = Sets.newLinkedHashSet();
        for (String s : update.toString().split("\\|")) {
            String[] d = s.split("=");
            if (Arrays.binarySearch(_keys, d[0]) >= 0) {
                res.add(d[1]);
            }
        }
        if (res.size() > 0) {
            return String.join(",", res);
        }
        return cellValue;
    }

    private String getRegisterPath() {
        String instId = ctx.getVariable(RobotConstant.INST_ID);
        String dataPath = ctx.get(RobotConstant.DATA_PATH);
        String accountNumber = ctx.getAccountNumber();
        String tplTypeName = TplTypeCodeEnum.getNameByCode(ctx.get("tplTypeCode"));
        String fileSuffix = StringUtils.substringAfterLast(tplPath, ".");
        String registerPath = Convert.appendPath(dataPath, accountNumber + "_" + tplTypeName + "_在册明细_" + instId + "." + fileSuffix);
        ctx.setVariable("registerPath", registerPath);
        return registerPath;
    }

    private void saveRegister(List<Map<String, Object>> dataList, String filePath) {
        String fileId = robotCommonService.fileUpload(new File(filePath));
        Map<String, Object> registerMap = Maps.newHashMap();
        registerMap.put("addrName", ctx.get("addrName"));
        registerMap.put("businessType", Convert.getBusinessType(ctx.get("businessType")));
        registerMap.put("accountNumber", ctx.getAccountNumber());
        registerMap.put("tplTypeCode", ctx.get("tplTypeCode"));
        registerMap.put("dataMonth", DateUtil.date().toString("yyyy-MM"));
        registerMap.put("registerNumber", dataList.size());
        registerMap.put("details", dataList);
        Map<String, Object> fileMap = Maps.newHashMap();
        fileMap.put("fileId", fileId);
        registerMap.put("files", Lists.newArrayList(fileMap));

        log.info("保存在册开始,在册人数：reqJson=" + JSON.toJSONString(registerMap));
        String resp = robotCommonService.saveRegister(registerMap);
        log.info("保存在册完成,在册人数：respJson=" + dataList.size() + ",保存结果：" + resp);
    }
}
