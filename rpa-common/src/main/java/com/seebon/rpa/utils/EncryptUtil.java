package com.seebon.rpa.utils;

import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-24 19:16:07
 */
public class EncryptUtil {

    private static final String KEY = "1a2C9b8gh5ltvt0m";
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding" ;


    public static String base64Encode(byte[] bytes) {
        return encodeBase64String(bytes);
    }

    public static byte[] base64Decode(String base64Code) throws Exception {
        return new BASE64Decoder().decodeBuffer(base64Code);
    }

    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * 加密
     * @param content
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String content) throws Exception {
        return base64Encode(aesEncryptToBytes(content, KEY));
    }

    public static String aesDecryptByBytes (byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    /**
     * 解密
     * @param encryptStr
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr) throws Exception {
        return aesDecryptByBytes(base64Decode(encryptStr), KEY);
    }

    public static void main(String[] args) throws Exception{
        String s1 = aesEncrypt("");
        System.out.println(s1);
        String s = aesDecrypt("biqdGaDDpYYupgRRo6laNg==");
        System.out.println(s);
    }
}
