package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.common.utils.JsonUtils;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.FileUtil;
import com.seebon.rpa.actions.impl.tool.NumberUtil;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.*;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
@RobotAction(name = "费用明细", order = 50, targetType = NoneTarget.class, comment = "费用明细")
public class FeeDetail extends AbstractAction {

    @ActionArgs(value = "获取方式", dict = DeclareVerifyType.class, required = true)
    @Conditions({
            "excel:totalAmountColIndex,header,periodOfExpense,coverFlag,condition",
            "json:jsonList,headerCols,tplPath,updates,totalAmountFieldName,periodOfExpense,coverFlag,condition",
            "jsonPdf:jsonList,customPath,periodOfExpense,coverFlag,condition",
            "customPath:customPath,totalAmountColIndex,header,periodOfExpense,coverFlag,condition",
    })
    private String verifyType;

    @ActionArgs(value = "表头行号", comment = "缺省，第0行为表头")
    private Integer header;

    @ActionArgs(value = "模板文件路径", comment = "模板文件路径")
    private String tplPath;

    @ActionArgs(value = "表头列", comment = "如个人账号,姓名,转出单位账号")
    private String headerCols;

    @ActionArgs(value = "数据列表", comment = "json数据列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> jsonList;

    @ActionArgs(value = "费用所属期", comment = "费用所属期", dict = PeriodOfExpense.class, required = true)
    private String periodOfExpense;

    @ActionArgs(value = "缴纳总金额属性名", comment = "缴纳总金额属性名", style = DynamicFieldType.text)
    private String totalAmountFieldName;

    @ActionArgs(value = "修改信息", comment = "Json")
    private Map<String, Object> updates;

    @ActionArgs(value = "文件路径", comment = "文件路径", style = DynamicFieldType.text)
    private String customPath;

    @ActionArgs(value = "缴纳总金额所在列", dict = WebExcelNumLine.class, required = true, comment = "配置excel文件缴纳总金额所在列")
    private WebExcelNumLine totalAmountColIndex;

    @ActionArgs(value = "是否覆盖", dict = CoverFee.class)
    private String coverFlag;

    @ActionArgs(value = "满足条件", parsed = false, comment = "内置变量row")
    private String condition;

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private NumberUtil numberUtil;
    @Autowired
    private RobotCommonService robotCommonService;

    @Override
    public void run() {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        switch (DeclareVerifyType.valueOf(verifyType)) {
            case excel:
                String downloadFileKey = "feeDetailDownloadFilePath";
                fileUtil.setActionType(FileMethod.downloadNewFile.toString());
                fileUtil.setVarKey(downloadFileKey);
                fileUtil.run();

                String filePath = ctx.get(downloadFileKey);
                this.excel(dataList, filePath);
                ctx.remove(downloadFileKey);
                this.saveCost(dataList, filePath);
                break;
            case customPath:
                this.excel(dataList, customPath);
                this.saveCost(dataList, customPath);
                break;
            case json:
                String costPath = this.getCostPath();
                this.generateExcel(costPath);
                this.jsonExcel(dataList, costPath);
                this.saveCost(dataList, costPath);
                break;
            default:
                break;
        }
        log.info("费用明细保存完成");
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
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                ctx.setVariable("row", rowMap);
                boolean readable = StringUtils.isBlank(condition) || parse(condition, Boolean.TYPE);
                if (!readable) {
                    continue;
                }
                if (!isEmptyRow(rowMap)) {
                    dataList.add(rowMap);
                }
            }
            excelHelper.close();
            excelHelper.clear();
        } catch (Exception e) {
            log.error("Excel核验异常" + e.getMessage(), e);
            throw new RuntimeException("Excel核验异常" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    private boolean isEmptyRow(LinkedHashMap<String, Object> rowMap) {
        Set<String> strings = rowMap.keySet();
        return !strings.stream().anyMatch(key -> StringUtils.isNotBlank((String) rowMap.get(key)));
    }

    private void jsonExcel(List<Map<String, Object>> dataList, String costPath) {
        Workbook workbook = null;
        header = header == null ? 0 : header;
        try {
            workbook = WorkbookFactory.create(new File(costPath));
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
                LinkedHashMap<String, Object> rowMap = Maps.newLinkedHashMap();
                Convert.fetchMap(headerRow, row, rowMap);
                ctx.setVariable("row", rowMap);
                boolean flag = false;
                for (String key : rowMap.keySet()) {
                    Object value = rowMap.get(key);
                    if (value != null && StringUtils.isNotBlank(value.toString())) {
                        flag = true;
                    }
                }
                if (flag) {
                    dataList.add(rowMap);
                }
            }
            excelHelper.close();
            excelHelper.clear();
        } catch (Exception e) {
            log.error("Excel核验异常" + e.getMessage(), e);
            throw new RuntimeException("Excel核验异常" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    private void generateExcel(String costPath) {
        try {
            File src = new File(tplPath);
            Path dest = (Path) Paths.get(costPath);
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(costPath));
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
            throw new RuntimeException("Excel核验异常" + e.getMessage());
        } finally {
            Convert.releaseExcel(workbook, costPath);
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
        }
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

    private String getCostPath() {
        String instId = ctx.getVariable(RobotConstant.INST_ID);
        String dataPath = ctx.get(RobotConstant.DATA_PATH);
        String accountNumber = ctx.getAccountNumber();
        String tplTypeName = TplTypeCodeEnum.getNameByCode(ctx.get("tplTypeCode"));
        String fileSuffix = StringUtils.substringAfterLast(tplPath, ".");
        String costPath = Convert.appendPath(dataPath, accountNumber + "_" + tplTypeName + "_费用明细_" + instId + "." + fileSuffix);
        ctx.setVariable("costPath", costPath);
        return costPath;
    }

    private void saveCost(List<Map<String, Object>> dataList, String filePath) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        String fileId = robotCommonService.fileUpload(new File(filePath));
        String totalAmount = "0.0";
        switch (DeclareVerifyType.valueOf(verifyType)) {
            case excel:
                totalAmount = this.getTotalAmount(filePath);
                break;
            case customPath:
                totalAmount = this.getTotalAmount(customPath);
                break;
            case json:
                totalAmount = numberUtil.sumListToString(jsonList, totalAmountFieldName);
                break;
            default:
                break;
        }
        HashMap<String, Object> params = Maps.newHashMap();
        params.put("orgName", ctx.get("companyName"));
        params.put("accountNumber", ctx.getAccountNumber());
        params.put("addressName", ctx.get("addrName"));
        params.put("businessType", ctx.get("businessTypeInt"));
        params.put("prefix", ctx.get("tplTypeCode"));
        params.put("periodOfExpense", this.getPeriodOfExpense());
        params.put("totalAmount", totalAmount);
        params.put("fileId", fileId);
        params.put("mapList", dataList);
        if (StringUtils.isNotBlank(coverFlag) && CoverFee.TRUE.equals(CoverFee.valueOf(coverFlag))) {
            params.put("coverFlag", true);
        } else {
            params.put("coverFlag", false);
        }
        log.info("保存费用明细完成,req：" + JSON.toJSONString(params));
        String resp = robotCommonService.savePaidInPersonDataUrl(params);
        log.info("保存费用明细完成,resp：" + resp);
    }

    private String getPeriodOfExpense() {
        if (PeriodOfExpense.lastMonth.equals(PeriodOfExpense.valueOf(periodOfExpense))) {
            return DateUtil.format(DateUtil.offsetMonth(new Date(), -1), "yyyy-MM");
        }
        if (PeriodOfExpense.currentMonth.equals(PeriodOfExpense.valueOf(periodOfExpense))) {
            return DateUtil.format(new Date(), "yyyy-MM");
        }
        if (PeriodOfExpense.nextMonth.equals(PeriodOfExpense.valueOf(periodOfExpense))) {
            return DateUtil.format(DateUtil.offsetMonth(new Date(), 1), "yyyy-MM");
        }
        return "";
    }

    private String getTotalAmount(String filePath) {
        String totalAmount = "0.0";
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            int rowSize = sheet.getLastRowNum() + 1;
            for (int i = header; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Object totalAmountCell = row.getCell(totalAmountColIndex.getIndex());
                if (totalAmountCell == null) {
                    continue;
                }
                totalAmount = numberUtil.numberAdd(totalAmount, totalAmountCell.toString());
            }
        } catch (Exception e) {
            log.error("报盘信息补全失败", e);
            throw new RuntimeException("报盘信息补全失败" + e.getMessage());
        } finally {
            IOUtils.closeQuietly(workbook);
        }
        return totalAmount;
    }
}
