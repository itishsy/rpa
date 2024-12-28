package com.seebon.rpa.actions.impl.excel;

import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@RobotAction(name = "生成佛山公积金撤销增减员证明文件", targetType = TargetsTarget.class, comment = "数据集合(MapList)生成佛山公积金撤销增减员证明文件")
public class GenerateSupplement extends AbstractAction {

    private static final String TOTAL_TPL_JY_INFO =
            "         由于我司本月申报操作有误，误将以下员工操作封存。\n" +
                    "                                                   \n" +
                    "         望公积金管理中心给予审批撤销封存业务。\n";

    private static final String TOTAL_TPL_ZY_INFO =
            "         由于我司本月申报操作有误，误将以下员工操作启封转入。\n" +
                    "                                                   \n" +
                    "         望公积金管理中心给予审批撤销启封转入业务。\n";

    @ActionArgs("模板文件路径")
    private String tplPath;

    @ActionArgs("证明文件路径")
    private String filePath;

    @ActionArgs("工作目录")
    private String workPath;

    @ActionArgs("公司名称")
    private String companyName;

    @ActionArgs(value = "数据源")
    private List<Map<String, Object>> dataList;

    @ActionArgs(value = "公章路径")
    private String sealPath;

    @ActionArgs(value = "撤销类型")
    private Integer RevocationType;

    @Override
    public void run() {
        try {
            InputStream input = new FileInputStream(tplPath);
            HSSFWorkbook workbook = new HSSFWorkbook(input);
            HSSFSheet sheetOne = workbook.getSheetAt(0);

            HSSFCellStyle style = getBodyCellStyle(workbook);

            int startRow = 10;
            for (int i = 0; i < dataList.size(); i++) {
                fillRow(dataList.get(i), sheetOne, startRow, String.valueOf(i + 1), style);
                startRow++;
            }

            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR);
            int month = rightNow.get(Calendar.MONTH) + 1;
            String monthStr = month < 10 ? ("0" + month) : (month + "");
            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            String dayStr = day < 10 ? ("0" + day) : (day + "");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            Workbook wb = new Workbook();
            wb.loadFromStream(new ByteArrayInputStream(bytes));

            //获取工作表
            Worksheet sheet = wb.getWorksheets().get(0);

            //固定的模板字符替换
            if (RevocationType == 2) {
                sheet.replace("${total}", String.format(TOTAL_TPL_JY_INFO, String.valueOf(dataList.size())));

            } else {
                sheet.replace("${total}", String.format(TOTAL_TPL_ZY_INFO, String.valueOf(dataList.size())));
            }
            sheet.replace("${applyDate}", String.valueOf(year) + " 年 " + monthStr + " 月 " + dayStr + " 日 ");
            sheet.replace("${companyName}", companyName);
            sheet.deleteRow(7);

            if (StringUtils.isNotBlank(sealPath)) {
                //添加图片到工作表的指定位置
                ExcelPicture pic = sheet.getPictures().add(8 + dataList.size(), 5, sealPath);
                //设置图片的宽度和高度
                pic.setWidth(160);
                pic.setHeight(160);
            }

            wb.getConverterSetting().setXDpi(500);
            wb.getConverterSetting().setYDpi(500);
            BufferedImage[] bufferedImages = ImageUtil.getBufferedImages(sheet, workPath);
            BufferedImage image = bufferedImages[0];
            if (bufferedImages.length > 1) {
                image = ImageUtil.mergeImage(bufferedImages);
            }
            ImageIO.write(image, filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toUpperCase(), new File(filePath));
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

        List<String> keys = Lists.newArrayList("姓名", "证件号码");
        for (int i = 0; i < keys.size(); i++) {
            HSSFCell cell = row.createCell(i + 1);
            cell.setCellValue(rowMap.get(keys.get(i)) + "");
            cell.setCellStyle(style);
        }
    }
}
