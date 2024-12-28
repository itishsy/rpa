package com.seebon.rpa.actions.impl.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public class Test {



    public static void main(String[] args) {
        parseZsjData("train.jsonl");
        parseZsjData("dev.jsonl");
    }

    public static void parseJiuYanData() {
        String filePath = "F:\\download\\新建文件夹\\九焱政策信息导出.xlsx";

        File file = new File(filePath);

        try {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            int rowSize = sheet.getLastRowNum() + 1;

            int pageSize = 50;
            int pageCount = rowSize / pageSize;
            int ys = rowSize % pageSize;
            if (ys > 0) {
                pageCount = pageCount+1;
            }

            for (int page=1; page<=pageCount; page++) {
                int start = (page-1) * 50 + 1;
                int end = page*50;

                if (end>=rowSize) {
                    end = rowSize - 1;
                }
                File jsonlFile = new File("F:\\download\\新建文件夹\\数据集_" +page+"\\dev.jsonl");
                if (!jsonlFile.exists()) {
                    jsonlFile.getParentFile().mkdirs();
                }
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonlFile), "utf-8");
                for (int i=start; i<=end; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || row.getCell(4)==null) {
                        continue;
                    }

                    String text = row.getCell(4).getStringCellValue();
                    text = text.trim().replace("&nbsp;", "");
                    if (Lists.newArrayList("无","否", "&nbsp;无").contains(text)) {
                        continue;
                    }
                    Map<String, Object> result = Maps.newHashMap();

                    List<Map<String, String>> msgList = Lists.newArrayList();

                    List<String> userContents = Lists.newArrayList();
                    String addrName = row.getCell(0).getStringCellValue();
                    if (StringUtils.isNotBlank(addrName)) {
                        userContents.add(String.format("城市#%s", addrName));
                    }
                    String businessType = row.getCell(1).getStringCellValue();
                    if (StringUtils.isNotBlank(businessType)) {
                        userContents.add(String.format("业务类型#%s", businessType));
                    }
                    String group1 = row.getCell(2).getStringCellValue();
                    if (StringUtils.isNotBlank(group1)) {
                        userContents.add(String.format("一级分类#%s", group1));
                    }
                    String group2 = row.getCell(3)!=null?row.getCell(3).getStringCellValue():null;
                    if (StringUtils.isNotBlank(group2)) {
                        userContents.add(String.format("二级分类#%s", group2));
                    }

                    Map<String, String> msgItem = Maps.newHashMap();
                    msgItem.put("role", "user");
                    msgItem.put("content", StringUtils.join(userContents, "*"));
                    msgList.add(msgItem);

                    Map<String, String> msgItem1 = Maps.newHashMap();
                    msgItem1.put("role", "assistant");
                    msgItem1.put("content", text);
                    msgList.add(msgItem1);

                    result.put("messages", msgList);
                    writer.write(JsonUtils.beanToJson(result).concat("\n"));
                }
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseZsjData(String fileName) {
        String filePath = "F:\\download\\11111\\AH-1Z.xlsx";

        File file = new File(filePath);

        try {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            int rowSize = sheet.getLastRowNum() + 1;
            File jsonlFile = new File("F:\\download\\11111\\".concat(fileName)); // train.jsonl

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonlFile), "utf-8");

            for (int i = 1; i < rowSize; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, Object> result = Maps.newHashMap();

                List<Map<String, String>> msgList = Lists.newArrayList();

                Map<String, String> msgItem = Maps.newHashMap();
                msgItem.put("role", "user");
                msgItem.put("content", row.getCell(0).getStringCellValue());
                msgList.add(msgItem);

                Map<String, String> msgItem1 = Maps.newHashMap();
                msgItem1.put("role", "assistant");
                String text = row.getCell(1).getStringCellValue();
                msgItem1.put("content", text);
                msgList.add(msgItem1);

                result.put("messages", msgList);
                writer.write(JsonUtils.beanToJson(result).concat("\n"));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
