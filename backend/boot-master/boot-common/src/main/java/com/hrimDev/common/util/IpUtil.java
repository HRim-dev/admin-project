package com.hrimDev.common.util;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class IpUtil {

    public static String getIp(){
        String ip;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            log.debug("ip 추출 불가");
            ip = "";
        }
        return ip;
    }

    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // IPv6 루프백 주소인 경우 IPv4로 변환
                ipAddress = "127.0.0.1";
            }
        }

        // 다중 프록시 환경에서 첫 번째 IP만 추출
        int firstCommaIndex = ipAddress.indexOf(',');
        if (firstCommaIndex != -1) {
            ipAddress = ipAddress.substring(0, firstCommaIndex);
        }

        return ipAddress;
    }

    public static String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unable to determine server IP";
        }
    }
}
