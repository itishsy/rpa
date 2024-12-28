package com.seebon.rpa.actions.target.impl;

import cn.hutool.core.io.FileUtil;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

/**
 * @author ZhenShijun
 * @dateTime 2023-03-21 15:47:05
 */
@Slf4j
@ActionTarget
public class DocTarget extends AbstractTarget<XWPFDocument> {

    @ActionArgs("文件路径")
    private String filePath;

    private XWPFDocument doc = null;

    @Setter
    private boolean isWrite = false;

    @Override
    protected XWPFDocument fetchTarget() {
        File file = null;
        try {
            file = new File(filePath);
            doc = new XWPFDocument(new FileInputStream(file));
            return doc;
        } catch (Exception e) {
            log.error("【Exception】", e);
            if (e.getMessage() != null && file != null && e.getMessage().contains("unsupported file type")) {
                System.gc();
                FileUtil.rename(file, "error_file_" + System.currentTimeMillis(), true);
            }
            return null;
        }
    }

    @Override
    public void release() {
        if (doc != null) {
            try {
                if (isWrite) {
                    log.info("被改写了，保存word。");
                    byte[] bytes = doc2Byte(doc);
                    //先关闭
                    doc.close();

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
                    doc.close();
                }
            } catch (Exception e) {
                log.error("【Exception】", e);
            } finally {
                System.gc();
                isWrite = false;
                doc = null;
            }
        }
        super.release();
    }

    private byte[] doc2Byte(XWPFDocument doc) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.write(baos);
        baos.flush();
        return baos.toByteArray();
    }

}
