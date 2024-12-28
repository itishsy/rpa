package com.seebon.rpa.utils.sms;

import com.seebon.common.utils.EmailUtils;
import sun.misc.BASE64Decoder;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author MR. Li
 * @Description:
 * @Date: Created in 10:53 2021/8/13
 * @Modified By:
 */
public class SmsUtils {
    /**
     * 短信的长度
     */
    public final static Integer smsCodeLenth4 = 4;
    public final static Integer smsCodeLenth6 = 6;

    /**
     * 这种暂不考虑
     * Pattern p = Pattern.compile("【(\\d+)】");
     * Matcher m = p.matcher(str);
     * while(m.find()) {
     * System.out.println("匹配结果："+m.group());
     * System.out.println("提取组1:"+m.group(1));
     * }
     *
     * @param sms
     * @return 区短信内容最后匹配到的数字和字母组成的字符串
     */
    public static String getSmsCode(String sms) {
//        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+$");
        Pattern pattern = Pattern.compile("[A-Za-z0-9]{4,}(?![A-Za-z0-9])");
        Matcher matcher = pattern.matcher(sms);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static void setEmail(String content, String... mailTo) {
        String host = "smtp.seebon.com";
        String username = "spfcore@seebon.com";
        String password = "spf@1233";
        EmailUtils.MessageInfo messageInfo = EmailUtils.getMessageInfo(host, username, password);
        messageInfo.setSubject("申报机器人操作异常提醒");
        messageInfo.setFromAlias("广州仕邦人力资源有限公司");
        messageInfo.setContent(content);
        messageInfo.setReceiver(mailTo); // ,"liudandan@seebon.com"
        EmailUtils.sendEmail(messageInfo);
    }

    private static String getSmsNumCode(String sms, Integer codeLenth) {
        Pattern pattern = Pattern.compile("\\d{" + codeLenth + "}");
        Matcher matcher = pattern.matcher(sms);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }


    private static String getSmsLetterCode(String sms, Integer codeLenth) {
        Pattern pattern = Pattern.compile("[a-zA-Z]{6}");
        Matcher matcher = pattern.matcher(sms);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getStackMsg(StackTraceElement[] stackTraceElements) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement element = stackTraceElements[i];
            sb.append("<p>" + element.toString() + "</p>");
        }
        return sb.toString();
    }

    private static String getSmsLetterAndNumCode(String sms, Integer codeLenth) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]{6}");
        Matcher matcher = pattern.matcher(sms);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /*public static void main(String[] args) {

        setEmail("<a href='http://192.168.0.65:9091/jiuyan_web/robot/executeHistory?accountId=1'>深圳社保(深圳社保局-社保-减员)</a>执行连续多次失败:</br></p>" +
                "<p>上次失败时间： 2021-09-18 13:46:22</p>" +
                "<p>失败原因： failed 、</br> (2021-09-18 </br> 13:55:44)</p>" +
                "<p> 本次失败时间： 2021-09-18 14:10:08</p>" +
                "<p>失败原因： failed(2021-09-18 13:55:44)</p>" +
                "<a href='http://192.168.0.65:9091/jiuyan_web/robot/executeHistory?accountId=1'>查看明细</a></br>" +
                "<p>异常日志：</p>" +
                getStackMsg(new RuntimeException("fuck").getStackTrace()));

        //sms.replaceAll("\r|\n|\t", "")
        *//*String sms4  ="你的验证码为:1234";
        String sms6  ="你的验证码为:123487";
        String letterCode6  ="你的验证码为:abcdef3aaa";
        String letterAndNumCode = "你的验证码为:abf3aaa";
        String code = "+CMS:17665104688         010101再次测试1254dfffffaa\n" +
                "\u0000\u0000";
        System.out.println(getSmsNumCode(sms4,smsCodeLenth4));
        System.out.println(getSmsNumCode(sms6,smsCodeLenth6));
        System.out.println(getSmsLetterCode(letterCode6,smsCodeLenth6));
        System.out.println(getSmsLetterAndNumCode(letterAndNumCode,smsCodeLenth6));
        System.out.println(getSmsCode(code));*//*
    }*/

    public static void setImgEmail(String title, String content, String mailTo) {
        try {
            String host = "smtp.seebon.com";
            String username = "spfcore@seebon.com";
            String password = "spf@1233";
            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);// 设置smtp主机
            properties.put("mail.smtp.auth", "true");// 使用smtp身份验证

            MimeMessage message = new MimeMessage(Session.getInstance(properties,
                    new Authenticator() {
                        @Override
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(//设置发送帐号密码
                                    username, password);
                        }
                    }));
            message.setFrom(new InternetAddress(username, "申报机器人"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
            message.setSubject("机器人验证图片");

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setContent("<h3>" + title + "<h3/><img src=\"cid:image\">", "text/html;charset=gb2312");

            MimeMultipart multipart = new MimeMultipart("related");

            multipart.addBodyPart(messageBodyPart);

            BASE64Decoder decoder = new BASE64Decoder();

            //Base64解码
            byte[] bytes = decoder.decodeBuffer(content);
            // 处理数据
           /*for (int i = 0; i < b.length; ++i) {
               //调整异常数据
               if (b[i] < 0) {
                   b[i] += 256;
               }
           }
           ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
           BufferedImage bufferedImage = ImgUtil.toImage(content);
           ImageIO.write(bufferedImage, "jpg", outputStream);
           byte[] bytes = outputStream.toByteArray();*/
            DataHandler dh = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));//图片路径

            messageBodyPart = new MimeBodyPart();
//           Files.write(Paths.)
//            messageBodyPart.setDataHandler(new DataHandler(new FileDataSource("d:\\1.jpg")));
            messageBodyPart.setDataHandler(dh);
            messageBodyPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);// 发送邮件
            System.out.println("邮件发送成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String title, String content, String mailTo) {
        try {
            String host = "smtp.seebon.com";
            String username = "spfcore@seebon.com";
            String password = "spf@1233";
            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);// 设置smtp主机
            properties.put("mail.smtp.auth", "true");// 使用smtp身份验证

            MimeMessage message = new MimeMessage(Session.getInstance(properties,
                    new Authenticator() {
                        @Override
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(//设置发送帐号密码
                                    username, password);
                        }
                    }));
            message.setFrom(new InternetAddress(username, "申报机器人"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
            message.setSubject(title);

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setContent("<h3>" + title + "<h3/><img src=\"cid:image\">", "text/html;charset=gb2312");

            MimeMultipart multipart = new MimeMultipart("related");

            multipart.addBodyPart(messageBodyPart);

            BASE64Decoder decoder = new BASE64Decoder();

            //Base64解码
            byte[] bytes = decoder.decodeBuffer(content);
            DataHandler dh = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));//图片路径

            messageBodyPart = new MimeBodyPart();
//           Files.write(Paths.)
//            messageBodyPart.setDataHandler(new DataHandler(new FileDataSource("d:\\1.jpg")));
            messageBodyPart.setDataHandler(dh);
            messageBodyPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);// 发送邮件
            System.out.println("邮件发送成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
