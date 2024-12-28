package com.seebon.rpa.actions.impl.declare;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.excel.WriteExcel;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.AttFileKey;
import com.seebon.rpa.context.enums.EmployeeFileGroup;
import com.seebon.rpa.entity.saas.po.system.SysDataDict;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "员工附件", order = 50, targetType = NoneTarget.class, comment = "员工附件")
public class EmployeeFile extends AbstractAction {

    @ActionArgs(value = "数据列表")
    private List<Map<String, Object>> dataList;

    @ActionArgs(value = "附件类型", dict = AttFileKey.class, comment = "附件类型和附件类型名称二选一")
    private AttFileKey fileKey;

    @ActionArgs(value = "附件类型名称", style = DynamicFieldType.text, comment = "附件类型和附件类型名称二选一")
    private String fileName;

    @ActionArgs(value = "是否分组", dict = EmployeeFileGroup.class)
    private String groupType;

    @ActionArgs(value = "附件后缀", style = DynamicFieldType.text)
    private String fileSuffix;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RobotCommonService robotCommonService;
    @Autowired
    private WriteExcel writeExcel;

    @Override
    public void run() {
        if (StringUtils.isBlank(fileName) && fileKey == null) {
            throw new RuntimeException("附件类型名称或附件类型不能为空");
        }
        if (CollectionUtils.isEmpty(dataList)) {
            dataList = ctx.get(RobotConstant.DECLARE_LIST);
        }
        SysDataDict dict = this.getSysDataDict();
        String attFileName = dict.getDictName();
        String attFileCode = dict.getDictCode();
        List<String> idCards = dataList.stream().map(it -> (String) it.get(RobotConstant.ID_CARD_KEY_NAME)).collect(Collectors.toList());
        Map<String, Object> params = Maps.newHashMap();
        params.put("addrName", ctx.get("addrName"));
        params.put("fileType", attFileCode);
        params.put("idCards", idCards);
        String respStr = robotCommonService.getEmployeeFiles(params);
        Map<String, Object> map = JSON.parseObject(respStr, Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
        if (MapUtils.isEmpty(dataMap)) {
            ctx.setVariable(dataKey, Lists.newArrayList());
            this.updateDeclareFail(dataList, attFileName);
            return;
        }
        Map<String, List<String>> groupMap = Maps.newHashMap();
        Map<String, String> urlMap = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            List<String> urls = (List<String>) entry.getValue();
            String groupKey = urls.stream().collect(Collectors.joining("-"));
            List<String> idCardVals = groupMap.get(groupKey);
            if (CollectionUtils.isEmpty(idCardVals)) {
                idCardVals = Lists.newArrayList();
            }
            idCardVals.add(entry.getKey());
            groupMap.put(groupKey, idCardVals);

            List<String> fileUrls = this.downloadFile((List<String>) entry.getValue(), urlMap, attFileName);
            dataList.stream().filter(item -> ((String) item.get(RobotConstant.ID_CARD_KEY_NAME)).equals(entry.getKey())).forEach(item -> {
                item.put(attFileName, fileUrls);
            });
        }
        log.info("员工附件分组：" + JSON.toJSONString(groupMap));

        //筛选申报资料不齐全的人员
        List<Map<String, Object>> declareNotFull = dataList.stream().filter(item -> item.get(attFileName) == null).collect(Collectors.toList());
        this.updateDeclareFail(declareNotFull, attFileName);
        //筛选申报资料齐全的人员数据
        List<Map<String, Object>> declareNewList = Lists.newArrayList();
        for (Map<String, Object> declareMap : dataList) {
            Object value = declareMap.get(attFileName);
            if (value == null) {
                continue;
            }
            declareNewList.add(declareMap);
        }
        List<String> noFileIdCards = declareNotFull.stream().filter(vo -> vo.get(RobotConstant.ID_CARD_KEY_NAME) != null).map(vo -> vo.get(RobotConstant.ID_CARD_KEY_NAME).toString()).collect(Collectors.toList());
        this.deleteErrorData(noFileIdCards);
        //分组数据
        ctx.setVariable(RobotConstant.DECLARE_LIST, declareNewList);
        if (StringUtils.isNotBlank(groupType) && EmployeeFileGroup.TRUE.equals(EmployeeFileGroup.valueOf(groupType))) {
            ctx.setVariable(dataKey, this.getGroupDeclareList(groupMap, declareNewList));
        } else {
            ctx.setVariable(dataKey, Lists.newArrayList(urlMap.values()));
        }
        log.info(dataKey + "=" + JSON.toJSONString(urlMap));
    }

    private List<String> downloadFile(List<String> fileUrls, Map<String, String> urlMap, String fileName) {
        List<String> files = Lists.newArrayList();
        String dataPath = ctx.get("dataPath");
        String accountNumber = ctx.getAccountNumber();
        for (String urlStr : fileUrls) {
            String uuid = UUIDGenerator.uuidStringWithoutLine();
            String value = urlMap.get(urlStr);
            if (StringUtils.isNotBlank(value)) {
                files.add(value);
                continue;
            }
            String filePath = "";
            try {
                if (StringUtils.isBlank(fileSuffix)) {
                    fileSuffix = urlStr.substring(urlStr.lastIndexOf(".") + 1, urlStr.length());
                }
                filePath = dataPath + "\\" + accountNumber + "_" + fileName + "_" + uuid + "." + fileSuffix;
                filePath = filePath.replace("\\\\", "\\");
                HttpUtil.downloadFile(urlStr, filePath);
                files.add(filePath);
                urlMap.put(urlStr, filePath);
            } catch (Exception e) {
                log.error("下载文件失败", e);
                throw new RuntimeException("下载文件失败");
            }
        }
        return files;
    }

    private void updateDeclareFail(List<Map<String, Object>> list, String attFileName) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        String batchCode = ctx.getVariable(RobotConstant.INST_ID);
        for (Map<String, Object> item : list) {
            String sql = "update robot_execution_data set robotExecStatus =2,declareStatus =5,failType=1,failReason=? WHERE idCard = ? and batch_code=?";
            jdbcTemplate.update(sql, "缺资料：员工减员请上传 " + attFileName + " 资料.", item.get(RobotConstant.ID_CARD_KEY_NAME), batchCode);
        }
    }

    private void deleteErrorData(List<String> idCards) {
        if (CollectionUtils.isEmpty(idCards)) {
            return;
        }
        String filePath = ctx.get(RobotConstant.FILE_PATH);
        Integer sheetIndex = ctx.get(RobotConstant.SHEET_INDEX);
        Integer heardIndex = ctx.get(RobotConstant.HEARD_INDEX);
        Integer idCardColIndex = ctx.get(RobotConstant.ID_CARD_COL_INDEX);
        Workbook workbook = null;
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int rowSize = sheet.getLastRowNum() + 1;
            List<Integer> removeRows = Lists.newArrayList();
            DataFormatter dataFormatter = new DataFormatter();
            for (int i = (heardIndex + 1); i < rowSize; i++) {
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
        } catch (Exception e) {
            log.error("删除缺资料异常数据失败" + e.getMessage(), e);
            throw new RuntimeException("删除缺资料异常数据失败" + e.getMessage());
        } finally {
            Convert.releaseExcel(workbook, filePath);
        }
    }

    private List<List<Map<String, Object>>> getGroupDeclareList(Map<String, List<String>> groupMap, List<Map<String, Object>> declareNewList) {
        Map<String, List<Map<String, Object>>> groupDeclareMap = Maps.newHashMap();
        for (String groupKey : groupMap.keySet()) {
            List<Map<String, Object>> groupDeclares = groupDeclareMap.get(groupKey);
            if (CollectionUtils.isEmpty(groupDeclares)) {
                groupDeclares = Lists.newArrayList();
            }
            List<String> idCardValues = groupMap.get(groupKey);
            for (Map<String, Object> declareNewMap : declareNewList) {
                Object idCard = declareNewMap.get(RobotConstant.ID_CARD_KEY_NAME);
                if (idCard != null && idCardValues.contains(idCard)) {
                    groupDeclares.add(declareNewMap);
                }
            }
            groupDeclareMap.put(groupKey, groupDeclares);
        }
        List<List<Map<String, Object>>> groupDeclareList = Lists.newArrayList();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupDeclareMap.entrySet()) {
            groupDeclareList.add(entry.getValue());
        }
        log.info("groupDeclareList=" + JSON.toJSONString(groupDeclareList));
        return groupDeclareList;
    }

    private SysDataDict getSysDataDict() {
        if (StringUtils.isBlank(fileName)) {
            SysDataDict dict = new SysDataDict();
            dict.setDictCode(fileKey.getKey());
            dict.setDictName(fileKey.getName());
            return dict;
        }
        String resp = robotCommonService.getByKeyAndName("10014", fileName);
        if (StringUtils.isBlank(resp)) {
            throw new RuntimeException("附件类型名称不存在");
        }
        JSONObject  jsonObject=JSONObject.parseObject(resp);
        SysDataDict dict = jsonObject.getObject("data", SysDataDict.class);
        if (dict == null) {
            throw new RuntimeException("附件类型名称不存在");
        }
        return dict;
    }
}
