package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.lang.Validator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.excel.WriteExcel;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.NonRecruitment;
import com.seebon.rpa.context.enums.QuitFlow;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "工资基数", order = 50, targetType = NoneTarget.class, comment = "工资基数")
public class SalaryBase extends AbstractAction {

    @ActionArgs(value = "表头行号", comment = "缺省，第0行为表头")
    private Integer header;

    @ActionArgs(value = "文件路径", comment = "文件路径")
    private String filePath;

    @ActionArgs(value = "身份证所在列", dict = WebExcelNumLine.class, required = true, comment = "身份证所在列")
    private WebExcelNumLine idCardColIndex;

    @ActionArgs(value = "工资所在列", dict = WebExcelNumLine.class, required = true, comment = "工资所在列")
    private WebExcelNumLine salaryColIndex;

    @ActionArgs(value = "剔除非增员名单人员", dict = NonRecruitment.class, comment = "填是或否, 默认否")
    private String isDeleteNotAddRoster;

    @Autowired
    private WriteExcel writeExcel;

    @Override
    public void run() {
        List<LinkedHashMap<String, Object>> salaryList = Lists.newArrayList();
        Map<String, String> colNameMap = this.readExcel(salaryList);
        String salaryColName = colNameMap.get("salaryColName");
        String idCardColName = colNameMap.get("idCardColName");
        if (CollectionUtils.isEmpty(salaryList)) {
            log.info("没有数据");
            ctx.setVariable("salaryList", Lists.newArrayList());
            return;
        }
        List<Map<String, Object>> declareList = ctx.get(RobotConstant.DECLARE_LIST);
        Map<String, Object> declareMap = this.getDataMap(declareList, salaryColName, idCardColName);
        for (LinkedHashMap<String, Object> salaryMap : salaryList) {
            Object salaryVal = declareMap.get(salaryMap.get(RobotConstant.ID_CARD_KEY_NAME).toString());
            if (salaryVal != null) {
                salaryMap.put(salaryColName, salaryVal);
            }
        }
        salaryList = salaryList.stream().filter(vo -> vo.get(salaryColName) != null &&
                (StringUtils.isEmpty(isDeleteNotAddRoster) || !NonRecruitment.TRUE.equals(NonRecruitment.valueOf(isDeleteNotAddRoster)) || declareMap.get(vo.get(RobotConstant.ID_CARD_KEY_NAME).toString()) != null) &&
                StringUtils.isNotBlank(vo.get(salaryColName).toString())).collect(Collectors.toList());
        ctx.setVariable("salaryList", Lists.newArrayList());
        this.updateExcelSalary(salaryList, salaryColName, idCardColName);
    }

    private Map<String, String> readExcel(List<LinkedHashMap<String, Object>> salaryList) {
        Map<String, String> colNameMap = Maps.newHashMap();
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
                String salary = row[salaryColIndex.getIndex()];
                if (StringUtils.isNotBlank(salary)) {
                    continue;
                }
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                rowMap.put(RobotConstant.ID_CARD_KEY_NAME, idCard);
                String salarykeyName = headerRow[salaryColIndex.getIndex()];
                if (StringUtils.isNotBlank(salarykeyName)) {
                    rowMap.put(salarykeyName.replace("*", ""), salary);
                }
                ctx.setVariable("row", rowMap);
                salaryList.add(rowMap);
            }

            String salaryColName = headerRow[salaryColIndex.getIndex()];
            if (StringUtils.isNotBlank(salaryColName)) {
                salaryColName = salaryColName.replace("*", "");
            }
            String idCardColName = headerRow[idCardColIndex.getIndex()];
            if (StringUtils.isNotBlank(idCardColName)) {
                idCardColName = idCardColName.replace("*", "");
            }
            colNameMap.put("salaryColName", salaryColName);
            colNameMap.put("idCardColName", idCardColName);
            excelHelper.close();
            excelHelper.clear();
        } catch (Exception e) {
            log.error("Excel核验异常" + e.getMessage(), e);
            throw new RuntimeException("Excel核验异常" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(workbook);
        }
        return colNameMap;
    }

    private void updateExcelSalary(List<LinkedHashMap<String, Object>> salaryList, String salaryColName, String idCardColName) {
        Map<String, Object> salaryMap = this.getDataMap(salaryList, salaryColName, idCardColName);
        Workbook workbook = null;
        List<Integer> removeRows = Lists.newArrayList();
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            int rowSize = sheet.getLastRowNum() + 1;
            CellStyle cellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat(); // 此处设置数据格式
            cellStyle.setDataFormat(dataFormat.getFormat("0.00_ "));// 最关键的是'_ '，最后有个空格别忘了，空格是必须的
            for (int i = header; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Object idCardCellVal = Convert.getCellVal(row, idCardColIndex.getIndex());
                if (idCardCellVal == null || StringUtils.isBlank(idCardCellVal.toString())) {
                    continue;
                }
                Object salaryCellVal = Convert.getCellVal(row, salaryColIndex.getIndex());
                if (salaryCellVal != null && StringUtils.isNotBlank(salaryCellVal.toString())) {
                    removeRows.add(i);
                    continue;
                }
                Cell cell = row.getCell(salaryColIndex.getIndex());
                if (cell == null) {
                    cell = row.createCell(salaryColIndex.getIndex());
                }
                Object salaryVal = salaryMap.get(idCardCellVal.toString());
                if (salaryVal == null) {
                    removeRows.add(i);
                    continue;
                }
                cell.setCellStyle(cellStyle);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(Double.valueOf(salaryVal.toString()));
            }

            if (CollectionUtils.isNotEmpty(removeRows)) {
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
        } catch (Exception e) {
            log.error("生成Excel异常" + e.getMessage(), e);
            throw new RuntimeException("生成Excel异常" + e.getMessage(), e);
        } finally {
            Convert.releaseExcel(workbook, filePath);
        }
    }

    private Map<String, Object> getDataMap(List<? extends Map<String, Object>> dataList, String salaryColName, String idCardColName) {
        Map<String, Object> dataMap = dataList.stream().filter(vo -> vo.get(RobotConstant.ID_CARD_KEY_NAME) != null && vo.get(salaryColName) != null)
                .collect(Collectors.toMap(k -> k.get(RobotConstant.ID_CARD_KEY_NAME).toString(), v -> v.get(salaryColName),
                        (oldValue, newValue) -> oldValue));
        if (MapUtils.isEmpty(dataMap)) {
            dataMap = dataList.stream().filter(vo -> vo.get(idCardColName) != null && vo.get(salaryColName) != null)
                    .collect(Collectors.toMap(k -> k.get(idCardColName).toString(), v -> v.get(salaryColName),
                            (oldValue, newValue) -> oldValue));
        }
        return dataMap;
    }
}
