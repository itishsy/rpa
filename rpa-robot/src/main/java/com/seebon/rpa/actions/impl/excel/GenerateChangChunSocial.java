package com.seebon.rpa.actions.impl.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.utils.file.ImageUtil;
import com.spire.xls.ExcelPicture;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@RobotAction(name = "生成长春社保证明文件", targetType = TargetsTarget.class, comment = "生成长春社保医疗增减员证明文件")
public class GenerateChangChunSocial extends AbstractAction {

    @ActionArgs("证明文件路径")
    private String filePath;

    @ActionArgs(value = "公章路径")
    private String sealPath;


    @Override
    public void run() {
        try {
            List<Map<String, Object>> dataList = ctx.get(RobotConstant.DECLARE_LIST);
            String accountNumber = ctx.getAccountNumber();
            Integer declareType = ctx.get("declareType");
            String tplPath = ctx.get("workPath").toString() + "\\tpl\\长春社保医疗增减员证明模版.xls";
            InputStream input = new FileInputStream(tplPath);
            HSSFWorkbook workbook = new HSSFWorkbook(input);
            HSSFSheet sheetOne = workbook.getSheetAt(0);

            HSSFCellStyle style = getBodyCellStyle(workbook);
            int startRow = 4;
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowMap = Maps.newHashMap();
                rowMap.putAll(dataList.get(i));  // 拷贝
                rowMap.put("增加",declareType == 1 ? "/" : "");
                rowMap.put("中断",declareType == 2 ? "/" : "");
                fillRow(rowMap, sheetOne, startRow, String.valueOf(i + 1), style);
                startRow++;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            Workbook wb = new Workbook();
            wb.loadFromStream(new ByteArrayInputStream(bytes));

            //获取工作表
            Worksheet sheet = wb.getWorksheets().get(0);

            //固定的模板字符替换
            sheet.replace("$$单位编码$$","单位编码：" + accountNumber);

            if (StringUtils.isNotBlank(sealPath)) {
                //添加图片到工作表的指定位置
                ExcelPicture pic = sheet.getPictures().add(2, 3, sealPath);
                //设置图片的宽度和高度
                pic.setWidth(160);
                pic.setHeight(160);
            }

            wb.getConverterSetting().setXDpi(500);
            wb.getConverterSetting().setYDpi(500);
            ImageUtil.saveToImage(sheet, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取明细数据单元格样式
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle getBodyCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        HSSFFont font1 = workbook.createFont();
        font1.setFontHeightInPoints((short) 10);
        font1.setFontName("宋体");
        style.setFont(font1);
        return style;
    }

    /**
     * 填充明细数据
     *
     * @param rowMap
     * @param sheet
     * @param startRow
     * @param rowNumber
     * @param style
     */
    private static void fillRow(Map<String, Object> rowMap, HSSFSheet sheet, int startRow, String rowNumber, HSSFCellStyle style) {
        sheet.shiftRows(startRow, sheet.getLastRowNum(), 1);
        HSSFRow row = sheet.createRow(startRow);
        row.setHeight(new Short("400"));

        rowMap.put("序号",rowNumber);

        List<String> keys = Lists.newArrayList("序号", "姓名","证件类型","证件号码","申报工资","增加","中断","","","","","","");
        for (int i = 0; i < keys.size(); i++) {
            HSSFCell cell = row.createCell(i + 1);
            cell.setCellValue(rowMap.getOrDefault(keys.get(i),"") + "");
            cell.setCellStyle(style);
        }
    }
}
