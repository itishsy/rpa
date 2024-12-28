package com.seebon.rpa.actions.target.impl;

import cn.hutool.core.io.FileUtil;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.utils.Convert;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Slf4j
@ActionTarget
public class SheetTarget extends AbstractTarget<Sheet> {

    @Getter
    @ActionArgs("文件路径")
    private String filePath;

    @Getter
    @ActionArgs("表格索引")
    private Integer sheetIndex;

    private Workbook workbook = null;

    @Setter
    private boolean isWrite = false;

    @Override
    public Sheet fetchTarget() {
        File file = null;
        try {
            file = new File(this.getNewFilePath());
            workbook = WorkbookFactory.create(file);
            return workbook.getSheetAt(sheetIndex == null ? 0 : sheetIndex);
        } catch (Exception e) {
            log.error("【Exception】", e);
            if (e.getMessage() != null && file != null && e.getMessage().contains("unsupported file type")) {
                System.gc();
                FileUtil.rename(file, "error_file_" + System.currentTimeMillis(), true);
            }
            return null;
        } finally {
            ctx.setVariable(RobotConstant.CACHE_DOWNLOAD_FILE_NAME, null);
        }
    }

    @Override
    public void release() {
        if (workbook != null) {
            try {
                if (isWrite) {
                    log.info("被改写了，保存workbook。");
                    byte[] bytes = workbook2Byte(workbook);
                    //先关闭
                    workbook.close();

                    BufferedOutputStream bos = null;
                    try {
                        File file = new File(filePath);
                        bos = new BufferedOutputStream(new FileOutputStream(file));
                        bos.write(bytes);
                        bos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(bos);
                    }
                } else {
                    log.info("workbook.close()");
                    workbook.close();
                }
            } catch (Exception e) {
                log.error("【Exception】", e);
            } finally {
                System.gc();
                isWrite = false;
                workbook = null;
            }
        }
        super.release();
    }

    private byte[] workbook2Byte(Workbook workbook) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        baos.flush();
        return baos.toByteArray();
    }

    private String getNewFilePath() {
        //如果配置文件路径，则优先读取配置.
        if (StringUtils.isNotBlank(filePath)) {
            return filePath;
        }
        String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
        if (StringUtils.isBlank(downloadPath)) {
            throw new RuntimeException("浏览器不支持自动下载,请先配置文件路径.");
        }
        //缓存文件列表
        List<String> fileNames = ctx.get(RobotConstant.CACHE_DOWNLOAD_FILE_NAME);
        //新的文件路径
        String newFilePath = Convert.getNewPath(downloadPath, fileNames);
        if (newFilePath.contains(".crdownload")) {//下载中的文件名
            Convert.sleep(5);//休眠5秒
            return Convert.getNewPath(downloadPath, fileNames);
        }
        return newFilePath;
    }
}
