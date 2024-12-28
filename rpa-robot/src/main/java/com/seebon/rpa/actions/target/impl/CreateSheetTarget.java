package com.seebon.rpa.actions.target.impl;

import cn.hutool.core.io.FileUtil;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.utils.Convert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @Author created By charles
 * @Description:
 * @Date 2023/2/15 15:07
 * @Modifide By:
 */
@Slf4j
@ActionTarget
public class CreateSheetTarget extends AbstractTarget<Sheet> {
    @ActionArgs("模板文件路径")
    private String tplPath;

    @ActionArgs("文件路径")
    private String filePath;

    @ActionArgs("表格索引")
    private Integer sheetIndex;

    private Workbook workbook = null;

    private CellStyle style = null;

    @Setter
    private boolean isWrite = false;

    @Override
    public Sheet fetchTarget() {
        File file = null;
        try {
            if (StringUtils.isNotBlank(tplPath)) {
                file = new File(tplPath);
                workbook = WorkbookFactory.create(file);
            } else {
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet();
                for (int i = 0; i < 10; i++) {
                    sheet.createRow(i);
                }
            }
            style = workbook.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            return workbook.getSheetAt(sheetIndex);
        } catch (Exception e) {
            log.error("【Exception】", e);
            if (e.getMessage() != null && file != null && e.getMessage().contains("unsupported file type")) {
                System.gc();
                FileUtil.rename(file, "error_file_" + System.currentTimeMillis(), true);
            }
            return null;
        }
    }

    public CellStyle getStyle() {
        return style;
    }

    @Override
    public void release() {
        if (workbook != null) {
            try {
                if (isWrite) {
                    log.info("被改写了，保存workbook。");
                    byte[] bytes = Convert.workbook2Byte(workbook);
                    //写入文件
                    Convert.writeFile(bytes, filePath);
                }
            } catch (Exception e) {
                log.error("生成Excel文件异常." + e.getMessage(), e);
            } finally {
                System.gc();
                isWrite = false;
                workbook = null;
            }
        }
        super.release();
    }
}
