package com.seebon.rpa.actions.impl.excel;

import com.google.common.collect.Lists;
import com.seebon.common.utils.DateUtils;
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
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RobotAction(name = "生成中山公积金补缴证明文件", targetType = TargetsTarget.class, comment = "数据集合(MapList)生成中山公积金补缴证明文件")
public class GenerateSupplementProve extends AbstractAction {

    private static final String TOTAL_TPL_INFO = "     我公司现需为%s名员工补缴公积金费用。现由我公司统一代为办理。请给予办理有关手续。";

    private static final String REASON_INFO1 = "%s名员工于%s份入职，因系统出错导致%s份才新增，现需补缴%s公积金;";
    private static final String REASON_INFO2 = "%s名员工于%s份入职，因系统出错导致%s份才新增，现需补缴%s至%s公积金;";

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

    @Override
    public void run() {
        try {
            InputStream input = new FileInputStream(tplPath);
            HSSFWorkbook workbook = new HSSFWorkbook(input);
            HSSFSheet sheetOne = workbook.getSheetAt(0);

            HSSFCellStyle style = getBodyCellStyle(workbook);

            int startRow = 7;
            for (int i = 0; i < dataList.size(); i++) {
                fillRow(dataList.get(i), sheetOne, startRow, String.valueOf(i + 1), style);
                startRow++;
            }

            HSSFCellStyle totalCellStyle = getTotalCellStyle(workbook);
            fillTotalRow(dataList, sheetOne, startRow, totalCellStyle);

            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR);
            int month = rightNow.get(Calendar.MONTH) + 1;
            String monthStr = month < 10 ? ("0" + month) : (month + "");
            int day = rightNow.get(Calendar.DAY_OF_MONTH);
            String dayStr = day < 10 ? ("0" + day) : (day + "");

            String fyMonth = String.valueOf(year) + "年" + monthStr + "月";

            Map<String, List<Map<String, Object>>> groupMaps = dataList.stream().collect
                    (Collectors.groupingBy(it -> ((String) it.get("缴存开始年月")).concat("-").concat((String) it.get("缴存终止年月"))));
            List<String> reason = Lists.newArrayList();
            for (String key : groupMaps.keySet()) {
                String[] dates = key.split("-");
                if (dates[0].equals(dates[1])) {
                    reason.add(String.format(REASON_INFO1, groupMaps.get(key).size(), dates[0].substring(0, 4) + "年" + dates[0].substring(4, 6) + "月", fyMonth,
                            dates[0].substring(0, 4) + "年" + dates[0].substring(4, 6) + "月"));
                } else {
                    reason.add(String.format(REASON_INFO2, groupMaps.get(key).size(), dates[0].substring(0, 4) + "年" + dates[0].substring(4, 6) + "月", fyMonth,
                            dates[0].substring(0, 4) + "年" + dates[0].substring(4, 6) + "月", dates[1].substring(0, 4) + "年" + dates[1].substring(4, 6) + "月"));
                }
            }

            startRow += 2;
            HSSFCellStyle reasonCellStyle = getReasonCellStyle(workbook);
            for (String re : reason) {
                fillreason(re, sheetOne, startRow, reasonCellStyle);
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
            sheet.replace("${total}", String.format(TOTAL_TPL_INFO, String.valueOf(dataList.size())));
            sheet.replace("${applyDate}", String.valueOf(year) + " 年 " + monthStr + " 月 " + dayStr + " 日 ");
            sheet.replace("${companyName}", companyName);

            //sheet.replace("${reason}", StringUtils.join(reason, ";"));

            sheet.deleteRow(7);

            if (StringUtils.isNotBlank(sealPath)) {
                //添加图片到工作表的指定位置
                ExcelPicture pic = sheet.getPictures().add(8 + dataList.size() + reason.size(), 5, sealPath);
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
        font1.setFontHeightInPoints((short) 12);
        font1.setFontName("宋体");
        style.setFont(font1);
        return style;
    }

    /**
     * 获取合计行的单元格样式
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle getTotalCellStyle(HSSFWorkbook workbook) {
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

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setFontName("宋体");
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static HSSFCellStyle getReasonCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.NONE);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.NONE);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.NONE);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.NONE);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        style.setFont(font);
        return style;
    }

    /**
     * 填充合计数据
     *
     * @param dataList
     * @param sheet
     * @param startRow
     * @param style
     */
    private static void fillTotalRow(List<Map<String, Object>> dataList, HSSFSheet sheet, int startRow, HSSFCellStyle style) {
        sheet.shiftRows(startRow, sheet.getLastRowNum(), 1);
        double amount = dataList.stream().mapToDouble(item -> {
            String startYearMonth = (String) item.get("缴存开始年月") + "01";
            String endYearMonth = (String) item.get("缴存终止年月") + "01";
            int months = DateUtils.monthsBetweenClosure(DateUtils.strToDate(startYearMonth, "yyyyMMdd"), DateUtils.strToDate(endYearMonth, "yyyyMMdd"));
            return Double.valueOf(String.valueOf(item.get("月缴存额"))) * months;
        }).sum();
        HSSFRow row = sheet.createRow(startRow);
        row.setHeight(new Short("900"));

        List<String> cellValues = Lists.newArrayList("合计：", "", "", new BigDecimal(amount).setScale(BigDecimal.ROUND_UP, 2).toString(), "", "");

        for (int i = 0; i < cellValues.size(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(cellValues.get(i));
            cell.setCellStyle(style);
        }

        CellRangeAddress region1 = new CellRangeAddress(startRow, startRow, 0, 2);
        sheet.addMergedRegion(region1);

        CellRangeAddress region2 = new CellRangeAddress(startRow, startRow, 3, 5);
        sheet.addMergedRegion(region2);
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

        HSSFCell cellIndex = row.createCell(0);
        cellIndex.setCellValue(rowNumber);
        cellIndex.setCellStyle(style);

        List<String> keys = Lists.newArrayList("个人账号", "姓名", "月缴存额", "缴存开始年月", "缴存终止年月");
        for (int i = 0; i < keys.size(); i++) {
            HSSFCell cell = row.createCell(i + 1);
            cell.setCellValue(rowMap.get(keys.get(i)) + "");
            cell.setCellStyle(style);
        }
    }

    private static void fillreason(String reason, HSSFSheet sheet, int startRow, HSSFCellStyle style) {
        sheet.shiftRows(startRow, sheet.getLastRowNum(), 1);
        HSSFRow row = sheet.createRow(startRow);
        row.setHeight(new Short("400"));

        HSSFCell cellIndex = row.createCell(0);
        cellIndex.setCellValue(reason);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 5)); // 合并单元格显示
        cellIndex.setCellStyle(style);
    }
}
