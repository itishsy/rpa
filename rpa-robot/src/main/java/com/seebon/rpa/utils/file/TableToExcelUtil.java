package com.seebon.rpa.utils.file;

import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author created By charles
 * @Description:
 * @Date 2023/2/16 14:39
 * @Modifide By:
 */
public class TableToExcelUtil {

    /**
     * @param sheetName
     * @param html
     * @throws FileNotFoundException zyn
     *                               2012-12-21 下午1:44:02
     */
    @SuppressWarnings("unchecked")
    public static void createExcelFormTable(String path, String sheetName, String html, int headNum) throws FileNotFoundException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        CellStyle headStyle = createHeadStyle(wb);
        CellStyle bodyStyle = createBodyStyle(wb);
        FileOutputStream os = new FileOutputStream(path + sheetName);
        SAXBuilder sb = new SAXBuilder();
        html = html.replaceAll("\n", "");
        html = "<table>" + html.split("<table>")[1].split("</table>")[0] + "</table>";
        ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes());
        try {
            Document document = sb.build(is);
            //获取table节点
            Element root = document.getRootElement();
            //获取tr的list
            List<Element> children = root.getChildren();

            List<Element> trList = new ArrayList<>();
            List<Element> head = children.get(0).getChildren();
            trList.addAll(head);

            List<Element> body = children.get(1).getChildren();
            trList.addAll(body);

            //循环创建行
            for (int i = 0; i < trList.size(); i++) {
                HSSFRow row = sheet.createRow(i);
                List<Element> tdList = trList.get(i).getChildren("td");
                //该行td的序号
                for (int ii = 0; ii < tdList.size(); ii++) {
                    row.createCell(ii);
                    HSSFCell cell = row.getCell(ii);
                    //判断是否为表头，使用对应的excel格式
                    if (i < headNum) {
                        cell.setCellStyle(headStyle);
                    } else {
                        cell.setCellStyle(bodyStyle);
                    }
                    cell.setCellValue(getInnerText(tdList.get(ii)));

                }

            }
            List<CellRangeAddress> cellArea = getCellArea(trList);
            if (!cellArea.isEmpty()) {
                for (CellRangeAddress cellRangeAddress : cellArea) {
                    sheet.addMergedRegion(cellRangeAddress);
                }
            }

            wb.write(os);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Map<String, Object>> parseListFormTable(String html) throws Exception {
        SAXBuilder sb = new SAXBuilder();
        html = html.replaceAll("\n", "");
        html = "<table>" + html.split("<table>")[1].split("</table>")[0] + "</table>";
        ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes("utf-8"));
        List<Map<String, Object>> listData = Lists.newArrayList();
        try {
            Document document = sb.build(is);
            //获取table节点
            Element root = document.getRootElement();
            //获取tr的list
            List<Element> children = root.getChildren();

            List<Element> children1 = children.get(0).getChildren();
            List<Element> head = children1.get(0).getChildren("th");
            Map<Integer, String> headInfo = Maps.newHashMap();
            for (int i = 0; i < head.size(); i++) {
                headInfo.put(i, getInnerText(head.get(i)));
            }

            List<Element> trList = new ArrayList<>();
            List<Element> body = children.get(1).getChildren();
            trList.addAll(body);

            //循环创建行

            for (int i = 0; i < trList.size(); i++) {
                List<Element> tdList = trList.get(i).getChildren("td");
                Map<String, Object> data = Maps.newHashMap();
                for (int ii = 0; ii < tdList.size(); ii++) {
                    data.put(headInfo.get(ii), getInnerText(tdList.get(ii)));
                }
                listData.add(data);
            }
        } catch (Exception e) {
            throw e;
        }
        return listData;
    }

    /**
     * 导出excel表格二维数组：0为文字占用格，1为横向被合并格，2为纵向合并格
     *
     * @param trList
     * @return zyn
     * 2012-12-21 下午1:35:40
     */
    private static List<CellRangeAddress> getCellArea(List<Element> trList) {
        //获取table单元格矩阵
        Element headtr = trList.get(0);
        List<Element> headTdList = headtr.getChildren("td");
        //每行的未经合并的单元格个数
        int cols = 0;
        for (Element e : headTdList) {
            System.out.println("#" + e.getText());
            int colspan = Integer.valueOf(null == e.getAttributeValue("colspan") ? "0" : e.getAttributeValue("colspan"));
            if (colspan == 0) {
                colspan = 1;
            }
            cols += colspan;
        }
        //初始化单元格矩阵
        int[][] area = new int[trList.size()][cols];
        List<CellRangeAddress> cellRangeAddresses = new ArrayList<>();
        Element tr;
        List<Element> tdList;
        Element td;
        int trsize = trList.size();
        int tdsize;
        int colspan;
        int rowspan;
        CellRangeAddress cellRangeAddress;
        for (int row = 0; row < trsize; row++) {
            tr = trList.get(row);
            tdList = tr.getChildren("td");
            tdsize = tdList.size();
            for (int col = 0; col < tdsize; col++) {
                td = tdList.get(col);
                colspan = Integer.valueOf(null == td.getAttributeValue("colspan") ? "0" : td.getAttributeValue("colspan"));
                if (colspan == 0) {
                    colspan = 1;
                }


                rowspan = Integer.valueOf(null == td.getAttributeValue("rowspan") ? "0" : td.getAttributeValue("rowspan"));
                if (rowspan == 0) {
                    rowspan = 1;
                }
                if (rowspan > 1) {
                    cellRangeAddress = new CellRangeAddress(row, row + rowspan - 1, col, col);
                    cellRangeAddresses.add(cellRangeAddress);
                }

                if (colspan > 1) {
                    cellRangeAddress = new CellRangeAddress(row, row, col, col + colspan - 1);
                    cellRangeAddresses.add(cellRangeAddress);
                }
            }
        }
        return cellRangeAddresses;
    }

    /**
     * -
     * 设置表头样式
     *
     * @param wb
     * @return
     */
    private static CellStyle createHeadStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    /**
     * -
     * 设置表单记录样式
     *
     * @param wb
     * @return
     */
    private static CellStyle createBodyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);

        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    private static String getInnerText(Element td) {
        String txt = "";
        if (td.getText() == null || "".equals(td.getText())) {
            if (null != td.getChildren()) {
                for (int i = 0; i < td.getChildren().size(); i++) {
                    Element e = (Element) td.getChildren().get(i);
                    txt += getInnerText(e);
                }
            }
        } else {
            txt = td.getText();
        }
        return txt;
    }


}
