package com.seebon.rpa;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ExcelUtilTest {
    private static final String filePath = "D:\\seebon\\rpa\\cache\\合肥\\社保\\108124\\人员列表.xls";

    public static void main(String[] args) {
        try {
            InputStream inputStream = new FileInputStream(filePath);
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            HSSFSheet shee2 = wb.getSheetAt(0);
            System.out.println(shee2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream2 = new FileInputStream(filePath);
            XSSFWorkbook wb2 = new XSSFWorkbook(inputStream2);
            XSSFSheet shee2 = wb2.getSheetAt(0);
            System.out.println(shee2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 创建文件输入流
            FileInputStream fis = new FileInputStream(filePath);
            OPCPackage open = OPCPackage.open(fis);
            Workbook workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
