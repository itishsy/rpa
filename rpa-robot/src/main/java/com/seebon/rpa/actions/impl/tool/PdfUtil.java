package com.seebon.rpa.actions.impl.tool;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.FileTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.ActionType;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.utilities.PdfTable;
import com.spire.pdf.utilities.PdfTableExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ActionUtils
@RobotAction(name = "PDF操作", targetType = FileTarget.class, comment = "PDF操作")
public class PdfUtil extends AbstractAction {

    @Conditions({"getPayStartMonth:actionArgs,resultKey",
            "getRegisterNumber:mapInfo,resultKey",
            "readPdf:mapInfo,header,regex,resultKey",
            "readHaiKouRegistered:mapInfo,header,resultKey",
            "readHangZhouRegistered:pdfContentSplitter,pdfTableRowSplitter,mapInfo,header,resultKey",
            "getTableCount:resultKey"
    })
    @ActionArgs(value = "操作类型", required = true, dict = ActionType.class)
    private String actionType;

    @ActionArgs(value = "操作参数")
    private String actionArgs;

    @ActionArgs(value = "PDF内容分割符", required = true)
    private String pdfContentSplitter;

    @ActionArgs(value = "PDF表格行分割符", required = true)
    private String pdfTableRowSplitter;

    @ActionArgs(value = "读取信息", required = true)
    private Map<String, Integer> mapInfo;

    @ActionArgs(value = "表头行号", comment = "缺省，第1行为表头")
    private Integer header;

    @ActionArgs(value = "满足正则", parsed = false)
    private String regex;

    @ActionArgs(value = "结果变量", required = true)
    private String resultKey;

    @Override
    public void run() {
        File file = getTarget();
        PDDocument doc = null;
        try {
            doc = PDDocument.load(file);
            switch (ActionType.valueOf(actionType)) {
                case getPayStartMonth: {
                    if (StringUtils.isBlank(resultKey)) {
                        throw new RobotConfigException("PDF操作方法，参数不能为空");
                    }
                    ctx.setVariable(resultKey, getPayStartMonth(doc));
                    break;
                }
                case getRegisterNumber: {
                    if (StringUtils.isBlank(resultKey)) {
                        throw new RobotConfigException("PDF操作方法，参数不能为空");
                    }
                    ctx.setVariable(resultKey, getRegisterNumber(doc));
                    break;
                }
                case readPdf: {
                    if (StringUtils.isBlank(resultKey)) {
                        throw new RobotConfigException("PDF操作方法，参数不能为空");
                    }
                    ctx.setVariable(resultKey, getPdfList(doc));
                    break;
                }
                case readHaiKouRegistered: {
                    if (StringUtils.isBlank(resultKey)) {
                        throw new RobotConfigException("PDF操作方法，参数不能为空");
                    }
                    ctx.setVariable(resultKey, this.getHaiKouList(doc));
                    break;
                }
                case readHangZhouRegistered: {
                    if (StringUtils.isBlank(resultKey)) {
                        throw new RobotConfigException("PDF操作方法，参数不能为空");
                    }
                    ctx.setVariable(resultKey, this.getHangZhouPdfList(doc));
                    break;
                }
                case getTableCount:{
                    if (StringUtils.isBlank(resultKey)) {
                        throw new RobotConfigException("PDF操作方法，参数不能为空");
                    }
                    ctx.setVariable(resultKey, this.getRecordCount(file));
                    break;
                }
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                doc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getPayStartMonth(PDDocument doc) throws Exception {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(doc);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String[] texts = text.split("\r\n");
        String payStartMonth = null;
        for (String value : texts) {
            if (StringUtils.isBlank(value)) {
                continue;
            }
            if (!value.startsWith("缴费起始年月")) {
                continue;
            }
            payStartMonth = value.replace("缴费起始年月 ", "");
        }
        log.info("payStartMonth=" + payStartMonth);
        if (payStartMonth.contains("年")) {
            return payStartMonth.replace("年", "-").replace("月", "");
        }
        return payStartMonth.substring(0, 4) + "-" + payStartMonth.substring(4, 6);
    }

    private Map<String, Object> getRegisterNumber(PDDocument doc) throws Exception {
        Map<String, Object> result = Maps.newHashMap();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(doc);
        if (StringUtils.isBlank(text)) {
            return result;
        }
        String[] texts = text.split("\r\n");
        String text1 = texts[10];
        String[] split = text1.split(" ");
        for (String key : mapInfo.keySet()) {
            Integer value = (Integer) mapInfo.get(key);
            result.put(key, split[value]);
        }
        return result;
    }

    private List<Map<String, Object>> getHangZhouPdfList(PDDocument doc) throws Exception {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(doc);
        String[] texts = text.split(pdfContentSplitter);
        for (int i = header; i < texts.length; i++) {
            String rowText = texts[i];
            String[] row = rowText.split(pdfTableRowSplitter);
            Map<String, Object> item = Maps.newHashMap();
            for (String key : mapInfo.keySet()) {
                Integer index = mapInfo.get(key);
                item.put(key, row[index]);
            }
            dataList.add(item);
        }
        return dataList;
    }

    private List<Map<String, Object>> getPdfList(PDDocument doc) throws Exception {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(doc);
        String[] texts = text.split("\r\n");
        for (int i = header; i < texts.length; i++) {
            String rowText = texts[i];
            rowText = rowText.replaceAll(" ", "");
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(rowText);
            if (matcher.find()) {
                Map<String, Object> item = Maps.newHashMap();
                for (String key : mapInfo.keySet()) {
                    Integer index = (Integer) mapInfo.get(key);
                    item.put(key, matcher.group(index));
                }
            }
        }
        return dataList;
    }

    private List<Map<String, Object>> getHaiKouList(PDDocument doc) throws Exception {
        List<Map<String, Object>> dataList = Lists.newArrayList();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(doc);
        String[] texts = text.split("\r\n");
        for (int i = header; i < texts.length; i++) {
            String rowText = texts[i];
            if (rowText.contains("总计：")) {
                continue;
            }
            rowText = rowText.replaceAll("0 . 00", "0.00");
            String[] rowTexts = rowText.split(" ");
            Map<String, Object> item = Maps.newHashMap();
            for (String key : mapInfo.keySet()) {
                Integer index = (Integer) mapInfo.get(key);
                item.put(key, rowTexts[index]);
            }
            dataList.add(item);
        }
        log.info("dataList=" + JSON.toJSONString(dataList));
        return dataList;
    }

    private Map<String,Object> getRecordCount(File file){
        PdfDocument pdf = new PdfDocument();
        //pdf.loadFromFile("E:\\360MoveData\\Users\\lrw66\\Desktop\\社会保险费缴费申报表（适用单位缴费人）.pdf");
        pdf.loadFromFile(file.getAbsolutePath());
        //抽取表格
        PdfTableExtractor extractor = new PdfTableExtractor(pdf);
        com.spire.pdf.utilities.PdfTable[] tableLists ;
        Map<String,Object> data = Maps.newLinkedHashMap();
        end:
        for (int page = 0; page < pdf.getPages().getCount(); page++) {
            tableLists = extractor.extractTable(page);
            if (tableLists != null && tableLists.length > 0) {
                for (PdfTable table : tableLists)
                {
                    int row = table.getRowCount();
                    for (int i = 0; i < row; i++)
                    {    //创建StringBuilder类的实例
                        String text = table.getText(i, 0).replace("\n", "");
                        if("序号".equals(text)){
                            continue ;
                        }
                        if ("合计".contains(text)) {
                            break end;
                        }
                        data.put("count",Integer.valueOf(text));
                        data.put("monthStart"+i,table.getText(i, 6).replace("\n", ""));
                        data.put("monthEnd"+i,table.getText(i, 7).replace("\n", ""));
                        data.put("insurance"+i,table.getText(i, 3).replace("\n", ""));
                        data.put("feeType"+i,table.getText(i, 4).replace("\n", ""));
                        data.put("amt"+i,table.getText(i, 12).replace("\n", "").replace(",",""));

                    }
                }
            }
        }
        return data;
    }


}
