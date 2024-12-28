package com.seebon.rpa.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 发送手机短信
 */
public final class SmsUtil {

    public static final String XML_RESOURCE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soap:Body>" +
            "<SendDepartmentSMS xmlns=\"http://www.seebon.com/webservices/\">" +
            "<MobilePhone>phone</MobilePhone>" +
            "<Message>verification</Message>" +
            "<Department>rpa</Department>" +
            "</SendDepartmentSMS>" +
            "</soap:Body>" +
            "</soap:Envelope>";

    private static final String SOAP_URL = "http://192.168.0.61:8088/spfc/SpfCoreService.asmx";
    private static final String SOAP_URL_OPS = "http://192.168.0.61:8088/spfc/SpfCoreService.asmx?op=SendDepartmentSMS";

    public static void send(String phoneNumber,String content){
        String xmlFile = XML_RESOURCE.replace("phone", phoneNumber).replace("verification", content);
        OutputStream out = null;
        InputStream in = null;
        HttpURLConnection httpConn = null;
        try{
            URL url = new URL(SOAP_URL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("Content-Length", String.valueOf(xmlFile.length()));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("soapActionString", SOAP_URL_OPS);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            out = httpConn.getOutputStream();
            out.write(xmlFile.getBytes());
            in = httpConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            reader.readLine();
            in.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpConn.disconnect();
        }

    }
}
