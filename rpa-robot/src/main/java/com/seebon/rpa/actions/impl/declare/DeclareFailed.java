package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.common.utils.JsonUtils;
import com.seebon.excel.reader.ExcelHelper;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.excel.WriteExcel;
import com.seebon.rpa.actions.impl.tool.FileUtil;
import com.seebon.rpa.actions.target.impl.DeclareFailedSheetTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.*;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RobotAction(name = "报盘失败处理", order = 20, targetType = DeclareFailedSheetTarget.class, comment = "读取网站反馈失败原因，回写到H2相应状态。并将报盘文件中的异常数据剔除")
public class DeclareFailed extends AbstractAction {

    @ActionArgs(value = "获取方式", dict = DeclareVerifyType.class, required = true)
    @Conditions({
            "excel:readType,charsetName",
            "customPath:readType,customPath,charsetName",
            "json:jsonList,idCardFieldName,failedFieldName,deleteErrorData,deleteErrorDataPath,dataKey",
    })
    private String verifyType;

    @ActionArgs(value = "文件路径", comment = "文件路径", style = DynamicFieldType.text)
    private String customPath;

    @ActionArgs(value = "读取方式", required = true, dict = ExcelReadType.class)
    @Conditions({
            "header:verifyType,header,customPath,idCardColName,failedColName,deleteErrorData,deleteErrorDataPath,charsetName,dataKey",
            "index:verifyType,header,customPath,idCardColIndex,failedColIndex,deleteErrorData,deleteErrorDataPath,charsetName,dataKey",
    })
    private String readType;

    @ActionArgs(value = "表头行号", comment = "缺省，第1行为表头")
    private Integer header;

    @ActionArgs(value = "身份证个人号列名称", style = DynamicFieldType.text, required = true, comment = "身份证、个人社保号、个人公积金号列名称")
    private String idCardColName;

    @ActionArgs(value = "失败原因列名称", style = DynamicFieldType.text, required = true, comment = "失败原因列名称")
    private String failedColName;

    @ActionArgs(value = "身份证号所在列", dict = WebExcelNumLine.class, required = true, comment = "身份证号所在列")
    private WebExcelNumLine idCardColIndex;

    @ActionArgs(value = "失败原因所在列", dict = WebExcelNumLine.class, required = true, comment = "失败原因所在列")
    private WebExcelNumLine failedColIndex;

    @ActionArgs(value = "json数据列表", comment = "json数据列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> jsonList;

    @ActionArgs(value = "身份证号属性名", comment = "json身份证号属性名", style = DynamicFieldType.text, required = true)
    private String idCardFieldName;

    @ActionArgs(value = "失败原因属性名", comment = "json失败原因属性名", style = DynamicFieldType.text, required = true)
    private String failedFieldName;

    @ActionArgs(value = "剔除异常数据", dict = DeleteErrorData.class)
    private String deleteErrorData;

    @ActionArgs(value = "剔除异常数据文件", comment = "剔除异常数据文件", style = DynamicFieldType.text)
    private String deleteErrorDataPath;

    @ActionArgs(value = "原始文件编码", dict = CharsetName.class, comment = "不填默认utf-8")
    private String charsetName;

    @ActionArgs(value = "结果变量", style = DynamicFieldType.text)
    private String dataKey;

    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WriteExcel writeExcel;

    @Override
    public void run() {
        Map<String, Object> numberMap = this.getNumberMap();
        DeclareVerifyType declareVerifyType = DeclareVerifyType.valueOf(verifyType);
        switch (declareVerifyType) {
            case excel:
                String downloadFileKey = "declareFailedDownloadFilePath";
                fileUtil.setActionType(FileMethod.downloadNewFile.toString());
                fileUtil.setVarKey(downloadFileKey);
                fileUtil.run();

                String filePath = ctx.get(downloadFileKey);
                this.excel(filePath, numberMap);
                ctx.remove(downloadFileKey);
                break;
            case customPath:
                this.excel(customPath, numberMap);
                break;
            case json:
                this.json(numberMap);
                break;
            default:
                break;
        }
    }

    private Map<String, Object> getNumberMap() {
        Map<String, Object> numberMap = Maps.newHashMap();
        String filePath = ctx.get(RobotConstant.FILE_PATH);
        Integer headerIndex = ctx.get(RobotConstant.HEARD_INDEX);
        Integer idCardColumnIndex = ctx.get(RobotConstant.ID_CARD_COL_INDEX);
        if (headerIndex == null || idCardColumnIndex == null) {
            return Maps.newHashMap();
        }
        Integer socialNumberColumnIndex = ctx.get(RobotConstant.SOCIAL_NUMBER_COL_INDEX);
        Integer accfundNumberColumnIndex = ctx.get(RobotConstant.ACCFUND_NUMBER_COL_INDEX);
        String businessType = ctx.get("businessType");
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            int rowSize = sheet.getLastRowNum() + 1;
            for (int i = headerIndex; i < rowSize; i++) {
                Row row = sheet.getRow(i + 1);
                if (row == null) {
                    continue;
                }
                Cell idCardCell = row.getCell(idCardColumnIndex);
                if (idCardCell == null) {
                    continue;
                }
                if ("1001001".equals(businessType) && socialNumberColumnIndex != null) {//社保
                    Cell socialNumberCell = row.getCell(socialNumberColumnIndex);
                    if (socialNumberCell != null) {
                        String cellValue = socialNumberCell.getStringCellValue();
                        if (StringUtils.isNotBlank(cellValue)) {
                            numberMap.put(cellValue, idCardCell.getStringCellValue());
                        }
                    }
                }
                if ("1001002".equals(businessType) && accfundNumberColumnIndex != null) {//公积金
                    Cell accfundNumberCell = row.getCell(accfundNumberColumnIndex);
                    if (accfundNumberCell != null) {
                        String cellValue = accfundNumberCell.getStringCellValue();
                        if (StringUtils.isNotBlank(cellValue)) {
                            numberMap.put(cellValue, idCardCell.getStringCellValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("读取Excel异常" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(workbook);
        }
        return numberMap;
    }

    private void excel(String filePath, Map<String, Object> numberMap) {
        if (StringUtils.isBlank(filePath)) {
            return;
        }
        //csv自动转换xlsx
        filePath = fileUtil.csvToExcel(filePath);
        Workbook workbook = null;
        DeclareFailedSheetTarget sheetTarget = (DeclareFailedSheetTarget) this.getVariable("DeclareFailedSheetTarget");
        try {
            Set<String> idCards = Sets.newHashSet();
            List<Map<String, Object>> rowList = Lists.newArrayList();
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(sheetTarget.getSheetIndex());
            ExcelReadType excelReadType = ExcelReadType.valueOf(readType);
            switch (excelReadType) {
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
                        Object idCard = rowMap.get(idCardColName);
                        Object failedInfo = rowMap.get(failedColName);
                        if (null == idCard || null == failedInfo) {
                            continue;
                        }
                        if (isValidIdCard(idCard.toString())) {
                            this.update(idCard.toString(), failedInfo.toString());
                            idCards.add(idCard.toString());
                            rowList.add(rowMap);
                        } else {
                            Object idCardValue = numberMap.get(idCard.toString());
                            if (idCardValue != null) {
                                this.update(idCardValue.toString(), failedInfo.toString());
                                idCards.add(idCardValue.toString());
                                rowList.add(rowMap);
                            }
                        }
                        ctx.setVariable("row", rowMap);
                        System.out.println("row =====  " + JsonUtils.toJSon(rowMap));
                    }
                    if (StringUtils.isNotBlank(dataKey)) {
                        ctx.setVariable(dataKey, rowList);
                        log.info(dataKey + " = " + JSON.toJSONString(rowList));
                    }
                    break;
                case index:
                    List<Row> rolList = Lists.newArrayList();
                    int rowSize = sheet.getLastRowNum() + 1;
                    if (header == null) {
                        header = 0;
                    }
                    for (int i = header; i < rowSize; i++) {
                        Row row = sheet.getRow(i);
                        if (row == null) {
                            continue;
                        }
                        Object idCard = Convert.getCellVal(row, idCardColIndex.getIndex());
                        Object failedInfo = Convert.getCellVal(row, failedColIndex.getIndex());
                        if (idCard == null || failedInfo == null) {
                            continue;
                        }
                        if (isValidIdCard(idCard.toString())) {
                            this.update(idCard.toString(), failedInfo.toString());
                            idCards.add(idCard.toString());
                            rolList.add(row);
                        } else {
                            Object idCardValue = numberMap.get(idCard.toString());
                            if (idCardValue != null) {
                                this.update(idCardValue.toString(), failedInfo.toString());
                                idCards.add(idCardValue.toString());
                                rolList.add(row);
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(dataKey)) {
                        ctx.setVariable(dataKey, rolList);
                        log.info(dataKey + " = " + rolList.size());
                    }
                    break;
                default:
                    break;
            }
            this.deleteErrorData(idCards);
        } catch (Exception e) {
            log.error("报盘失败处理异常" + e.getMessage(), e);
            if(!(e instanceof  RuntimeSkipTo)){
                throw new RuntimeException("报盘失败处理异常" + e.getMessage());
            }
        } finally {
            IOUtils.closeQuietly(workbook);
        }
    }

    private void json(Map<String, Object> numberMap) {
        Set<String> idCards = Sets.newHashSet();
        for (Map<String, Object> map : jsonList) {
            String idCard = String.valueOf(map.get(idCardFieldName));
            Object failedInfo = map.get(failedFieldName);
            if (StringUtils.isBlank(idCard) || failedInfo == null ||
                    StringUtils.isBlank(failedInfo.toString()) || "null".equals(failedInfo.toString())) {
                continue;
            }
            if (isValidIdCard(idCard)) {
                this.update(idCard, failedInfo.toString());
                idCards.add(idCard);
            } else {
                Object idCardValue = numberMap.get(idCard);
                if (idCardValue != null) {
                    this.update(idCardValue.toString(), failedInfo.toString());
                    idCards.add(idCardValue.toString());
                }
            }
        }
        if (StringUtils.isNotBlank(dataKey)) {
            ctx.setVariable(dataKey, jsonList);
            log.info(dataKey + " = " + JSON.toJSONString(jsonList));
        }
        this.deleteErrorData(idCards);
    }

    private void deleteErrorData(Set<String> idCards) {
        if (StringUtils.isBlank(deleteErrorData)) {
            log.info("不删除失败数据");
            return;
        }
        if (DeleteErrorData.excel.equals(DeleteErrorData.valueOf(deleteErrorData)) || DeleteErrorData.both.equals(DeleteErrorData.valueOf(deleteErrorData))) {
            header = header == null ? 0 : header;
            Sheet sheet = getTarget();
            int rowSize = sheet.getLastRowNum() + 1;
            int idCardColIndex = ctx.get(RobotConstant.ID_CARD_COL_INDEX);
            DeclareFailedSheetTarget sheetTarget = (DeclareFailedSheetTarget) this.getVariable("DeclareFailedSheetTarget");
            sheetTarget.setWrite(true);
            List<Integer> removeRows = Lists.newArrayList();
            DataFormatter dataFormatter = new DataFormatter();
            for (int i = header; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String idCard = dataFormatter.formatCellValue(row.getCell(idCardColIndex));
                if (idCards.contains(idCard)) {
                    removeRows.add(i);
                }
            }
            if (removeRows.size() > 0) {
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
        if (DeleteErrorData.declareList.equals(DeleteErrorData.valueOf(deleteErrorData)) || DeleteErrorData.both.equals(DeleteErrorData.valueOf(deleteErrorData))) {
            List<Map<String, Object>> declareNewList = Lists.newArrayList();
            List<LinkedHashMap<String, Object>> declareList = ctx.get(RobotConstant.DECLARE_LIST);
            for (Map<String, Object> map : declareList) {
                Object idCard = map.get(RobotConstant.ID_CARD_KEY_NAME);
                if (idCard == null) {
                    continue;
                }
                if (!idCards.contains(idCard)) {
                    declareNewList.add(map);
                }
            }
            ctx.setVariable(RobotConstant.DECLARE_LIST, declareNewList);
        }
        if (DeleteErrorData.custom.equals(DeleteErrorData.valueOf(deleteErrorData))) {
            this.removeRows(idCards, deleteErrorDataPath);
        }
        if (DeleteErrorData.qfPath.equals(DeleteErrorData.valueOf(deleteErrorData))) {
            String qfPath = ctx.get(RobotConstant.QF_PATH);
            List<LinkedHashMap<String, Object>> qfList = ctx.get(RobotConstant.QF_LIST);
            this.removeRows(idCards, qfPath);
            if (qfList.size() == idCards.size()) {
                throw new RuntimeSkipTo(failedSkipTo);
            }
        }
        if (DeleteErrorData.zrPath.equals(DeleteErrorData.valueOf(deleteErrorData))) {
            String zrPath = ctx.get(RobotConstant.ZR_PATH);
            List<LinkedHashMap<String, Object>> zrList = ctx.get(RobotConstant.ZR_LIST);
            this.removeRows(idCards, zrPath);
            if (zrList.size() == idCards.size()) {
                throw new RuntimeSkipTo(failedSkipTo);
            }
        }
    }

    private void removeRows(Set<String> idCards, String filePath) {
        header = header == null ? 0 : header;
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            int rowSize = sheet.getLastRowNum() + 1;
            List<Integer> removeRows = Lists.newArrayList();
            //识别IdCard所在列
            int idCardColumnIndex = -1;
            for (int i = header; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if (idCardColumnIndex < 0) {
                    int columnSize = row.getPhysicalNumberOfCells();
                    for (int j = 0; j < columnSize; j++) {
                        Object idCardCell = row.getCell(j);
                        if (idCardCell != null && idCards.contains(idCardCell.toString())) {
                            idCardColumnIndex = j;
                            removeRows.add(i);
                        }
                    }
                } else {
                    Object idCardCell = row.getCell(idCardColumnIndex);
                    if (idCardCell != null && idCards.contains(idCardCell.toString())) {
                        removeRows.add(i);
                    }
                }
            }
            if (removeRows.size() > 0) {
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
            log.error("删除报盘异常数据失败" + e.getMessage(), e);
            throw new RuntimeException("删除报盘异常数据失败" + e.getMessage());
        } finally {
            Convert.releaseExcel(workbook, filePath);
        }
    }

    private void update(String idCard, String failReason) {
        String batchCode = ctx.get(RobotConstant.INST_ID);
        String sql = "update robot_execution_data set robotExecStatus =1,declareStatus =2,failReason=? WHERE batch_code = ? and idCard = ?";
        jdbcTemplate.update(sql, failReason, batchCode, idCard);
    }

    private static boolean isValidIdCard(String id) {
        String regex = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}([0-9Xx])$";
        return id.matches(regex);
    }
}
