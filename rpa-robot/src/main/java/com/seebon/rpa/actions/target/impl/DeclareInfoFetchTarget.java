package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.WebExcelNumLine;
import com.seebon.rpa.utils.Convert;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Slf4j
@ActionTarget
public class DeclareInfoFetchTarget extends AbstractTarget<Sheet> {

    @Getter
    @ActionArgs(value = "更新文件路径", comment = "更新文件路径,不配置则更新报盘文件filePath", style = DynamicFieldType.text)
    private String filePath;

    @Getter
    @ActionArgs(value = "更新文件表头行号", comment = "更新文件表头行号，不配置则为报盘文件表头,从1开始")
    private Integer header;

    @Getter
    @ActionArgs(value = "更新文件身份证列", dict = WebExcelNumLine.class, comment = "更新文件身份证所在列，不配置则为报盘文件身份证所在列")
    private WebExcelNumLine idCardColIndex;

    @Getter
    @ActionArgs(value = "更新文件个人账号列", dict = WebExcelNumLine.class, comment = "更新文件个人账号所在列，不配置则为报盘文件个人账号所在列")
    private WebExcelNumLine numberColIndex;

    private Workbook workbook = null;

    private CellStyle style = null;

    @Setter
    private boolean isWrite = false;

    @Override
    public Sheet fetchTarget() {
        try {
            String updatePath = StringUtils.defaultIfBlank(filePath, ctx.get("filePath"));
            Integer sheetIndex = ctx.get(RobotConstant.SHEET_INDEX);
            if (sheetIndex == null) {
                sheetIndex = 0;
            }
            workbook = WorkbookFactory.create(new File(updatePath));
            style = workbook.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            return workbook.getSheetAt(sheetIndex);
        } catch (Exception e) {
            log.error("报盘信息补全异常." + e.getMessage(), e);
        }
        return null;
    }

    public CellStyle getStyle() {
        return style;
    }

    @Override
    public void release() {
        if (workbook == null) {
            return;
        }
        try {
            if (isWrite) {
                log.info("被改写了，保存workbook。");
                byte[] bytes = Convert.workbook2Byte(workbook);
                //先关闭
                workbook.close();

                String updatePath = StringUtils.defaultIfBlank(filePath, ctx.get("filePath"));

                //写入文件
                Convert.writeFile(bytes, updatePath);
            } else {
                log.info("workbook.close()");
                workbook.close();
            }
        } catch (Exception e) {
            log.error("报盘信息补全异常." + e.getMessage(), e);
        } finally {
            System.gc();
            isWrite = false;
            workbook = null;
        }
        super.release();
    }
}
