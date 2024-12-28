package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.common.utils.JsonUtils;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.excel.WriteExcel;
import com.seebon.rpa.actions.impl.tool.FileUtil;
import com.seebon.rpa.actions.target.impl.DeclareInfoFetchTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.DeclareVerifyType;
import com.seebon.rpa.context.enums.DeleteEmptyRow;
import com.seebon.rpa.context.enums.FileMethod;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.utils.Convert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RobotAction(name = "报盘信息补全",
        order = 40,
        targetType = DeclareInfoFetchTarget.class,
        comment = "报盘导出缺失的字段，需要从网站查询补全，如个人基数、电脑号之类。从网站导出文件，根据人员身份证号，自动填充对应列到报盘文件中")
public class DeclareInfoFetch extends AbstractAction {

    @ActionArgs(value = "获取方式", dict = DeclareVerifyType.class, required = true)
    @Conditions({
            "excel:idCardColIndex,sourceColIndex,updateFieldIndex,deleteEmptyRow,header,updateFieldName",
            "json:jsonList,idCardFieldName,sourceFieldName,updateFieldIndex,deleteEmptyRow,updateFieldName",
            "customPath:customPath,idCardColIndex,sourceColIndex,updateFieldIndex,deleteEmptyRow,header,updateFieldName",
    })
    private String verifyType;

    @Getter
    @ActionArgs(value = "表头行号", comment = "excel的文件中表头行号,默认1开始")
    private Integer header;

    @ActionArgs(value = "数据列表", comment = "json数据列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> jsonList;

    @ActionArgs(value = "身份证属性名", comment = "json身份证属性名", required = true, style = DynamicFieldType.text)
    private String idCardFieldName;

    @ActionArgs(value = "数据来源属性名", comment = "json数据来源属性名", style = DynamicFieldType.text)
    private String sourceFieldName;

    @ActionArgs(value = "文件路径", comment = "文件路径", style = DynamicFieldType.text)
    private String customPath;

    @ActionArgs(value = "身份证所在列", dict = WebExcelNumLine.class, required = true, comment = "excel的文件中身份证所在列")
    private WebExcelNumLine idCardColIndex;

    @ActionArgs(value = "数据来源所在列", dict = WebExcelNumLine.class, required = true, comment = "excel的文件中数据来源所在列")
    private WebExcelNumLine sourceColIndex;

    @ActionArgs(value = "更新报盘所在列", dict = WebExcelNumLine.class, required = true, comment = "报盘文件中需要更新的列")
    private WebExcelNumLine updateFieldIndex;

    @ActionArgs(value = "更新报盘报盘属性名", style = DynamicFieldType.text, comment = "报盘文件中需要更新的列")
    private String updateFieldName;

    @ActionArgs(value = "是否剔除补全为空数据", dict = DeleteEmptyRow.class, comment = "是否剔除补全为空数据")
    private String deleteEmptyRow;

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private WriteExcel writeExcel;

    @Override
    public void run() {
        Map<String, Object> sourceMap = Maps.newHashMap();
        switch (DeclareVerifyType.valueOf(verifyType)) {
            case excel:
                String downloadFileKey = "fetchDeclareInfoDownloadFilePath";
                fileUtil.setActionType(FileMethod.downloadNewFile.toString());
                fileUtil.setVarKey(downloadFileKey);
                fileUtil.run();

                String filePath = ctx.get(downloadFileKey);
                this.excel(sourceMap, filePath);
                ctx.remove(downloadFileKey);
                break;
            case customPath:
                this.excel(sourceMap, customPath);
                break;
            case json:
                this.json(sourceMap);
                break;
            default:
                break;
        }
        DeclareInfoFetchTarget target = (DeclareInfoFetchTarget) this.getVariable("DeclareInfoFetchTarget");
        Integer header = target.getHeader();
        WebExcelNumLine idCardColIndex = target.getIdCardColIndex();
        String filePath = target.getFilePath();
        if (StringUtils.isBlank(filePath) || header != null) {
            if (header == null) {
                header = (Integer) ctx.get(RobotConstant.HEARD_INDEX) + 1;
            }
            this.updateExcelHeader(sourceMap, header, idCardColIndex);
        } else {
            this.updateExcel(sourceMap);
        }
        this.setNumberColumnIndex();
    }

    private void setNumberColumnIndex() {
        DeclareInfoFetchTarget target = (DeclareInfoFetchTarget) this.getVariable("DeclareInfoFetchTarget");
        WebExcelNumLine numberColIndex = target.getNumberColIndex();
        if (numberColIndex == null) {
            return;
        }
        String businessType = ctx.get("businessType");
        if ("1001001".equals(businessType)) {//社保
            ctx.setVariable(RobotConstant.SOCIAL_NUMBER_COL_INDEX, numberColIndex.getIndex());
        }
        if ("1001002".equals(businessType)) {//公积金
            ctx.setVariable(RobotConstant.ACCFUND_NUMBER_COL_INDEX, numberColIndex.getIndex());
        }
    }

    private void updateExcelHeader(Map<String, Object> sourceMap, Integer header, WebExcelNumLine idCardColIndex) {
        log.info("updateExcelHeader sourceMap=" + JSON.toJSONString(sourceMap));
        Set<String> updateFieldCells = Sets.newHashSet();
        List<Integer> removeRows = Lists.newArrayList();
        try {
            Sheet sheet = this.getTarget();
            Cell fieldCell = sheet.getRow(header - 1).getCell(updateFieldIndex.getIndex());
            if (fieldCell != null) {
                String cellValue = fieldCell.getStringCellValue();
                if (StringUtils.isNotBlank(cellValue)) {
                    updateFieldCells.add(cellValue);
                }
            }
            //识别IdCard所在列
            Integer idCardColumnIndex = ctx.get(RobotConstant.ID_CARD_COL_INDEX);
            if (idCardColIndex != null) {
                idCardColumnIndex = idCardColIndex.getIndex();
            }
            int rowSize = sheet.getLastRowNum() + 1;
            for (int i = header; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Object idCardCell = row.getCell(idCardColumnIndex);
                if (idCardCell != null && sourceMap.containsKey(idCardCell.toString())) {
                    Cell cell = row.getCell(updateFieldIndex.getIndex());
                    if (cell == null) {
                        cell = row.createCell(updateFieldIndex.getIndex());
                    }
                    Object value = sourceMap.get(idCardCell.toString());
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    } else {
                        removeRows.add(i);
                    }
                } else {
                    removeRows.add(i);
                }
            }

            this.deleteEmptyRow(removeRows, sheet);

            DeclareInfoFetchTarget declareInfoFetchTarget = (DeclareInfoFetchTarget) this.getVariable("DeclareInfoFetchTarget");
            declareInfoFetchTarget.setWrite(true);
            //申报数据补全
            List<LinkedHashMap<String, Object>> declareList = ctx.get(RobotConstant.DECLARE_LIST);
            List<LinkedHashMap<String, Object>> declareListBq = this.updateCacheList(declareList, updateFieldCells, sourceMap);
            ctx.setVariable(RobotConstant.DECLARE_LIST_BQ, declareListBq);

            //启封补全
            List<LinkedHashMap<String, Object>> qfList = ctx.get(RobotConstant.QF_LIST);
            List<LinkedHashMap<String, Object>> qfListBq = this.updateCacheList(qfList, updateFieldCells, sourceMap);
            ctx.setVariable(RobotConstant.QF_LIST_BQ, qfListBq);

            //转入补全
            List<LinkedHashMap<String, Object>> zrList = ctx.get(RobotConstant.ZR_LIST);
            List<LinkedHashMap<String, Object>> zrListBq = this.updateCacheList(zrList, updateFieldCells, sourceMap);
            ctx.setVariable(RobotConstant.ZR_LIST_BQ, zrListBq);
        } catch (Exception e) {
            log.error("报盘信息补全失败", e);
        }
    }

    private void updateExcel(Map<String, Object> sourceMap) {
        log.info("sourceMap=" + JSON.toJSONString(sourceMap));
        Set<String> updateFieldCells = Sets.newHashSet();
        List<Integer> removeRows = Lists.newArrayList();
        try {
            Sheet sheet = this.getTarget();
            int rowSize = sheet.getLastRowNum() + 1;
            //识别IdCard所在列
            int idCardColumnIndex = -1;

            for (int i = 0; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if (idCardColumnIndex < 0) {
                    int columnSize = row.getPhysicalNumberOfCells();
                    for (int j = 0; j < columnSize; j++) {
                        Cell fieldCell = row.getCell(updateFieldIndex.getIndex());
                        if (fieldCell != null) {
                            String cellValue = fieldCell.getStringCellValue();
                            if (StringUtils.isNotBlank(cellValue)) {
                                updateFieldCells.add(cellValue);
                            }
                        }
                        Object idCardCell = row.getCell(j);
                        if (idCardCell != null && sourceMap.containsKey(idCardCell.toString())) {
                            idCardColumnIndex = j;
                            Cell cell = row.getCell(updateFieldIndex.getIndex());
                            if (cell == null) {
                                cell = row.createCell(updateFieldIndex.getIndex());
                            }
                            Object value = sourceMap.get(idCardCell.toString());
                            if (value != null) {
                                cell.setCellValue(value.toString());
                            } else {
                                removeRows.add(i);
                            }
                        }
                    }
                } else {
                    Cell fieldCell = row.getCell(updateFieldIndex.getIndex());
                    if (fieldCell != null) {
                        String cellValue = fieldCell.getStringCellValue();
                        if (StringUtils.isNotBlank(cellValue)) {
                            updateFieldCells.add(cellValue);
                        }
                    }
                    Object idCardCell = row.getCell(idCardColumnIndex);
                    if (idCardCell != null && sourceMap.containsKey(idCardCell.toString())) {
                        Cell cell = row.getCell(updateFieldIndex.getIndex());
                        if (cell == null) {
                            cell = row.createCell(updateFieldIndex.getIndex());
                        }
                        Object value = sourceMap.get(idCardCell.toString());
                        if (value != null) {
                            cell.setCellValue(value.toString());
                        } else {
                            removeRows.add(i);
                        }
                    }
                }
            }

            this.deleteEmptyRow(removeRows, sheet);

            DeclareInfoFetchTarget declareInfoFetchTarget = (DeclareInfoFetchTarget) this.getVariable("DeclareInfoFetchTarget");
            declareInfoFetchTarget.setWrite(true);
            //申报数据补全
            List<LinkedHashMap<String, Object>> declareList = ctx.get(RobotConstant.DECLARE_LIST);
            List<LinkedHashMap<String, Object>> declareListBq = this.updateCacheList(declareList, updateFieldCells, sourceMap);
            ctx.setVariable(RobotConstant.DECLARE_LIST_BQ, declareListBq);

            //启封补全
            List<LinkedHashMap<String, Object>> qfList = ctx.get(RobotConstant.QF_LIST);
            List<LinkedHashMap<String, Object>> qfListBq = this.updateCacheList(qfList, updateFieldCells, sourceMap);
            ctx.setVariable(RobotConstant.QF_LIST_BQ, qfListBq);

            //转入补全
            List<LinkedHashMap<String, Object>> zrList = ctx.get(RobotConstant.ZR_LIST);
            List<LinkedHashMap<String, Object>> zrListBq = this.updateCacheList(zrList, updateFieldCells, sourceMap);
            ctx.setVariable(RobotConstant.ZR_LIST_BQ, zrListBq);
        } catch (Exception e) {
            log.error("报盘信息补全失败", e);
        }
    }

    private void deleteEmptyRow(List<Integer> removeRows, Sheet sheet) {
        if (StringUtils.isNotBlank(deleteEmptyRow) && DeleteEmptyRow.TRUE.equals(DeleteEmptyRow.valueOf(deleteEmptyRow))) {
            if (CollectionUtils.isEmpty(removeRows)) {
                return;
            }
            int delStart = -1;
            for (int k : removeRows) {
                Row row = sheet.getRow(k);
                if (row != null) {
                    sheet.removeRow(row);
                    if (delStart < 0) {
                        delStart = k;
                    } else {
                        delStart = Math.min(delStart, k);
                    }
                }
            }
            if (delStart >= 0) {
                writeExcel.removeBlank(sheet, delStart);
            }
        }
    }

    private void excel(Map<String, Object> sourceMap, String filePath) {
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
                String sourceValue = row[sourceColIndex.getIndex()];
                if (StringUtils.isBlank(idCard) || StringUtils.isBlank(sourceValue)) {
                    continue;
                }
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                ctx.setVariable("row", rowMap);
                sourceMap.put(idCard, sourceValue);
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

    private void json(Map<String, Object> sourceMap) {
        for (Map<String, Object> map : jsonList) {
            ctx.setVariable("row", map);
            String idCard = String.valueOf(map.get(idCardFieldName));
            if (StringUtils.isBlank(idCard)) {
                continue;
            }
            String sourceValue = String.valueOf(map.get(sourceFieldName));
            sourceMap.put(idCard, sourceValue);
        }
    }

    private List<LinkedHashMap<String, Object>> updateCacheList(List<LinkedHashMap<String, Object>> list, Set<String> updateFieldCells, Map<String, Object> sourceMap) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        List<LinkedHashMap<String, Object>> notEmptyList = Lists.newArrayList();
        for (LinkedHashMap<String, Object> map : list) {
            Object idCardValue = map.get(RobotConstant.ID_CARD_KEY_NAME);
            if (idCardValue == null) {
                continue;
            }
            boolean flag = false;
            if (StringUtils.isNotBlank(updateFieldName)) {
                Object value = sourceMap.get(idCardValue.toString());
                if (value == null) {
                    continue;
                }
                map.put(updateFieldName, value);
                flag = true;
            } else {
                for (String updateKey : map.keySet()) {
                    if (updateFieldCells.contains(updateKey)) {
                        Object value = sourceMap.get(idCardValue.toString());
                        if (value == null) {
                            continue;
                        }
                        map.put(updateKey, value);
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                notEmptyList.add(map);
            }
        }
        return notEmptyList;
    }
}
