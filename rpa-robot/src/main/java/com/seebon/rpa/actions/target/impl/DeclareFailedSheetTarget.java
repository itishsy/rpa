package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.utils.Convert;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@Slf4j
@ActionTarget
public class DeclareFailedSheetTarget extends AbstractTarget<Sheet> {

    @Getter
    @ActionArgs("表格索引")
    private Integer sheetIndex;

    private Workbook workbook = null;

    @Setter
    private boolean isWrite = false;

    @Override
    protected Sheet fetchTarget() {
        String filePath = ctx.get(RobotConstant.FILE_PATH);
        if (sheetIndex == null) {
            sheetIndex = ctx.get(RobotConstant.SHEET_INDEX);
        }
        try {
            File file = new File(filePath);
            workbook = WorkbookFactory.create(file);
            return workbook.getSheetAt(sheetIndex == null ? 0 : sheetIndex);
        } catch (Exception e) {
            log.error("报盘失败处理异常." + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void release() {
        String filePath = ctx.get(RobotConstant.FILE_PATH);
        if (workbook != null) {
            try {
                if (isWrite) {
                    log.info("被改写了，保存workbook。");
                    byte[] bytes = Convert.workbook2Byte(workbook);
                    //先关闭
                    workbook.close();
                    //写入文件
                    Convert.writeFile(bytes, filePath);
                } else {
                    log.info("workbook.close()");
                    workbook.close();
                }
            } catch (Exception e) {
                log.error("报盘失败处理异常." + e.getMessage(), e);
            } finally {
                System.gc();
                isWrite = false;
                workbook = null;
            }
        }
        super.release();
    }
}
