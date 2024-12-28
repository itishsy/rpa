package com.seebon.rpa.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IpUtils {
    //获取本机ip
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }

    //获取客户端ip
    public String getIpAddr(HttpServletRequest request) throws Exception {
        if (request == null) {
            throw new Exception("getIpAddr method HttpServletRequest Object is null");
        }
        String ipStr = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getRemoteAddr();
        }

        final String arr[] = ipStr.split(",");
        for (String s : arr) {
            if (!"unknown".equalsIgnoreCase(s)) {
                ipStr = s;
                break;
            }
        }

        return ipStr.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ipStr;
    }

    public static String getIpAddr2(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
