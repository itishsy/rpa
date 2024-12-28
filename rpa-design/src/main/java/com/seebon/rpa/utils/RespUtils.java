package com.seebon.rpa.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

@Slf4j
public final class RespUtils{

    /**
     * 设置响应
     *
     * @param fileName
     * @param resp
     */
    public static void export(String fileName, byte[] bytes, HttpServletResponse resp) {
        RespUtils.reset(RespUtils.getFileName(fileName), resp);
        RespUtils.out(bytes, resp);
    }

    /**
     * 输
     *出文件流
     * @param bytes
     * @param resp
     */
    private static void out(byte[] bytes, HttpServletResponse resp) {
        byte[] buf = new byte[1024];
        ServletOutputStream os = null;
        BufferedInputStream is = null;
        try {
            os = resp.getOutputStream();
            is = new BufferedInputStream(new ByteArrayInputStream(bytes));
            IOUtils.copy(is, os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * 清空输出流
     *
     * @param fileName
     * @param resp
     */
    private static void reset(String fileName, HttpServletResponse resp) {
        resp.reset();
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/vnd.ms-excel");
        resp.setBufferSize(1024);
        resp.setHeader("Content-disposition", "attachment; filename=" + fileName);
        resp.setHeader("FileName", fileName);
    }

    /**
     * 获取导出文件名称
     *
     * @param fileName
     * @return
     */
    private static String getFileName(String fileName) {
        try {
            return new String(URLEncoder.encode(fileName, "UTF-8").getBytes("UTF-8"), "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void write(HttpServletResponse resp, String fName, Class<?> clz, List<?> list) {
        try {
            Calendar calendar = Calendar.getInstance();
            String fileName = URLEncoder.encode(fName + calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH)+1) + calendar.get(Calendar.DAY_OF_MONTH), "UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "GBK") +  ".xlsx");
            resp.setHeader("FileName", fileName + ".xlsx");
            resp.setHeader("Access-Control-Expose-Headers", "FileName");
            resp.setContentType("application/vnd.ms-excel");
            resp.setCharacterEncoding("utf-8");

            EasyExcel.write(resp.getOutputStream(), clz).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(fName).doWrite(list);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("RespUtils.write ex:{}",e);
        }

    }
}
