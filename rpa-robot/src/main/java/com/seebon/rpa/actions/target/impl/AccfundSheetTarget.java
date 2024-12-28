package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.OfferLabelType;
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

@Slf4j
@ActionTarget
public class AccfundSheetTarget extends AbstractTarget<Sheet> {

    @ActionArgs("模板文件路径")
    private String tplPath;

    private Workbook workbook = null;

    private CellStyle style = null;

    @Setter
    private boolean isWrite = false;

    @Override
    public Sheet fetchTarget() {
        try {
            if (StringUtils.isNotBlank(tplPath)) {
                File file = new File(tplPath);
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
            return workbook.getSheetAt(0);
        } catch (Exception e) {
            log.error("写入报盘文件异常." + e.getMessage(), e);
        }
        return null;
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

                    OfferLabelType businessNode = ctx.get("businessNode");
                    String filePath = null;
                    if (OfferLabelType.changeInto.equals(businessNode)) {
                        filePath = ctx.get(RobotConstant.ZR_PATH);
                    }
                    if (OfferLabelType.accountUnsealed.equals(businessNode)) {
                        filePath = ctx.get(RobotConstant.QF_PATH);
                    }
                    //写入文件
                    Convert.writeFile(bytes, filePath);
                }
            } catch (Exception e) {
                log.error("写入报盘文件异常." + e.getMessage(), e);
            } finally {
                isWrite = false;
                workbook = null;
            }
        }
        super.release();
    }
}
