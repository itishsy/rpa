package com.seebon.rpa.utils;

import com.google.common.collect.Lists;
import com.seebon.common.utils.EmailUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class EmailSendUtill {

    public static Boolean sendEmail(EmailUtils.MessageInfo messageInfo ){

        String username = messageInfo.getSender();
        String password = messageInfo.getPassword();
        String[] receiver = messageInfo.getReceiver();
        String fromAlias = messageInfo.getFromAlias();
        String subject = messageInfo.getSubject();
        String content = messageInfo.getContent();

        String serverHost = messageInfo.getServerHost();


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", serverHost);
        props.put("mail.smtp.port", "25");

        javax.mail.Session session = javax.mail.Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            String fronName = username;
            if (StringUtils.isNotBlank(serverHost) && serverHost.contains("vanke.com")) {
                fronName = username.concat("@vanke.com");
            }
            if (StringUtils.isNotBlank(fromAlias)) {
                message.setFrom(new InternetAddress(fronName, fromAlias));
            } else {
                message.setFrom(new InternetAddress(fronName));
            }
            Address[] tos = new InternetAddress[receiver.length];
            for (int i = 0; i < receiver.length; i++) {
                tos[i] = new InternetAddress(receiver[i]);
            }
            message.setRecipients(Message.RecipientType.TO, tos);

            if (StringUtils.isNotBlank(subject)) {
                message.setSubject(subject);
            }

            Multipart multipart = new MimeMultipart();

            // 设置邮件正文
            MimeBodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html; charset=UTF-8");
            multipart.addBodyPart(contentPart);


            List<File> adjunct = messageInfo.getAdjunct();
            for(File file : adjunct) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                FileDataSource dataSource = new FileDataSource(file);
                DataHandler dataHandler = new DataHandler(dataSource);
                attachmentPart.setDataHandler(dataHandler);
                attachmentPart.setFileName(MimeUtility.encodeText(file.getName()));
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email sent successfully");
            messageInfo.setSendStatus(true);
            return true;
        } catch (Exception e) {
            messageInfo.setSendStatus(false);
            messageInfo.setFailureReason(e.getMessage());
            return false;
        }

    }

}
