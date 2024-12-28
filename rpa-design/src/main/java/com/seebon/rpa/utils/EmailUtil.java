package com.seebon.rpa.utils;

import com.google.common.collect.Lists;
import com.seebon.common.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2021-08-11 19:13:13
 */

@Slf4j
public final class EmailUtil {

    private static final String mess_mail_user = "seebot@nfstone.com";

    private static final String mess_mail_service_host = "smtp.seebon.com";

    private static final String mess_mail_passwd = "rpa@@##xx996";

    public static void sendEmailAttachRetiree(Map<String, Object> map) throws Exception {
        EmailUtils.MessageInfo messageInfo = EmailUtils.getMessageInfo(mess_mail_service_host, mess_mail_user, mess_mail_passwd);
        messageInfo.setSubject((String) map.get("subject"));
        String tplName = (String) map.get("tplName");
        if (!StringUtils.isEmpty(tplName)) {
            messageInfo.setTplName(tplName);//freemark模板
        } else {
            messageInfo.setContent((String) map.get("mail_content"));
        }
        messageInfo.setParams(map);
        messageInfo.setFromAlias("广州南方仕通网络科技有限公司");
        if (map.get("receiverEmail") instanceof String[]) {
            messageInfo.setReceiver((String[]) map.get("receiverEmail"));
        } else {
            messageInfo.setReceiver((String) map.get("receiverEmail"));
        }
        List<File> files = Lists.newArrayList();
        List<Map<String, Object>> filestoreItems = (List<Map<String, Object>>) map.get("files");
        try {
            if (CollectionUtils.isNotEmpty(filestoreItems)) {
                for (Map<String, Object> item : filestoreItems) {
                    String clientFileName = (String) item.get("fileName");
                    String filePath = (String) item.get("filePath");
                    int lastIndexOf = clientFileName.lastIndexOf(".");
                    //获取文件的后缀名
                    String suffix = clientFileName.substring(lastIndexOf);
                    File file = File.createTempFile(clientFileName.replace(suffix, ""), suffix);

                    InputStream in = new FileInputStream(filePath);
                    byte[] bytes = toByteArray(in);

                    BufferedOutputStream bos = null;
                    try {
                        //缓冲流
                        bos = new BufferedOutputStream(new FileOutputStream(file));
                        //将字节数组写出
                        bos.write(bytes);
                    } catch (Exception e) {
                        log.error("【Exception】", e);
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                        if (bos != null) {
                            bos.close();
                        }
                    }
                    files.add(file);
                }
            }
            messageInfo.setAdjunct(files);
            //发送邮件
            boolean isSuccess = EmailUtils.sendEmail(messageInfo);
            if (!isSuccess) {
                throw new RuntimeException(messageInfo.getFailureReason());
            }
        } finally {
            files.stream().forEach(file -> file.delete());
        }
    }

    public static void sendEmailAttachRetireeBytes(Map<String, Object> map) throws Exception {
        EmailUtils.MessageInfo messageInfo = EmailUtils.getMessageInfo(mess_mail_service_host, mess_mail_user, mess_mail_passwd);
        messageInfo.setSubject((String) map.get("subject"));
        String tplName = (String) map.get("tplName");
        if (!StringUtils.isEmpty(tplName)) {
            messageInfo.setTplName(tplName);//freemark模板
        } else {
            messageInfo.setContent((String) map.get("mail_content"));
        }
        messageInfo.setParams(map);
        messageInfo.setFromAlias("广州南方仕通网络科技有限公司");
        if (map.get("receiverEmail") instanceof String[]) {
            messageInfo.setReceiver((String[]) map.get("receiverEmail"));
        } else {
            messageInfo.setReceiver((String) map.get("receiverEmail"));
        }
        List<File> files = Lists.newArrayList();
        List<Map<String, Object>> filestoreItems = (List<Map<String, Object>>) map.get("files");
        try {
            if (CollectionUtils.isNotEmpty(filestoreItems)) {
                for (Map<String, Object> item : filestoreItems) {
                    String clientFileName = (String) item.get("fileName");
                    int lastIndexOf = clientFileName.lastIndexOf(".");
                    //获取文件的后缀名
                    String suffix = clientFileName.substring(lastIndexOf);
                    File file = File.createTempFile(clientFileName.replace(suffix, ""), suffix);

                    byte[] bytes = (byte[])item.get("bytes");

                    BufferedOutputStream bos = null;
                    try {
                        //缓冲流
                        bos = new BufferedOutputStream(new FileOutputStream(file));
                        //将字节数组写出
                        bos.write(bytes);
                    } catch (Exception e) {
                        log.error("【Exception】", e);
                    } finally {
                        if (bos != null) {
                            bos.close();
                        }
                    }
                    files.add(file);
                }
            }
            messageInfo.setAdjunct(files);
            //发送邮件
            boolean isSuccess = EmailUtils.sendEmail(messageInfo);
            if (!isSuccess) {
                throw new RuntimeException(messageInfo.getFailureReason());
            }
        } finally {
            files.stream().forEach(file -> file.delete());
        }
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}
