package com.example.seatchoice.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectUtil {

	static final Logger log = LoggerFactory.getLogger(ConnectUtil.class);

	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		log.info("X-FORWARDED-FOR : " + ip);

		if (ip == null) {
			ip = request.getHeader("Proxy-Client-IP");
			log.info("Proxy-Client-IP : " + ip);
		}
		if (ip == null) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			log.info("WL-Proxy-Client-IP : " + ip);
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			log.info("HTTP_CLIENT_IP : " + ip);
		}
		if (ip == null) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			log.info("HTTP_X_FORWARDED_FOR : " + ip);
		}
		if (ip == null) {
			ip = request.getRemoteAddr();
			log.info("getRemoteAddr : "+ip);
		}
		log.info("Result : IP Address : "+ip);

		return ip;
	}

	public static String getBrowser(HttpServletRequest request) {
		// 에이전트
		String agent = request.getHeader("User-Agent");
		// 브라우져 구분
		String browser = null;
		if (agent != null) {
			if (agent.indexOf("Trident") > -1) {
				browser = "MSIE";
			} else if (agent.indexOf("Chrome") > -1) {
				browser = "Chrome";
			} else if (agent.indexOf("Opera") > -1) {
				browser = "Opera";
			} else if (agent.indexOf("iPhone") > -1 && agent.indexOf("Mobile") > -1) {
				browser = "iPhone";
			} else if (agent.indexOf("Android") > -1 && agent.indexOf("Mobile") > -1) {
				browser = "Android";
			}
		}
		return browser;
	}

	public static String getOs(HttpServletRequest request) {
		// 에이전트
		String agent = request.getHeader("User-Agent");
		String os = null;
		if(agent.indexOf("NT 6.0") != -1) os = "Windows Vista/Server 2008";
		else if(agent.indexOf("NT 5.2") != -1) os = "Windows Server 2003";
		else if(agent.indexOf("NT 5.1") != -1) os = "Windows XP";
		else if(agent.indexOf("NT 5.0") != -1) os = "Windows 2000";
		else if(agent.indexOf("NT") != -1) os = "Windows NT";
		else if(agent.indexOf("9x 4.90") != -1) os = "Windows Me";
		else if(agent.indexOf("98") != -1) os = "Windows 98";
		else if(agent.indexOf("95") != -1) os = "Windows 95";
		else if(agent.indexOf("Win16") != -1) os = "Windows 3.x";
		else if(agent.indexOf("Windows") != -1) os = "Windows";
		else if(agent.indexOf("Linux") != -1) os = "Linux";
		else if(agent.indexOf("Macintosh") != -1) os = "Macintosh";
		else os = "";
		return os;
	}

	public static String getWebType(HttpServletRequest request) {
		String filter = "iphone|ipod|android|windows ce|blackberry|symbian|windows phone|webos|opera mini|opera mobi|polaris|iemobile|lgtelecom|nokia|sonyericsson|lg|samsung";
		String filters[] = filter.split("\\|");
		String webType = "";

		for(String tmp : filters){
			if ( request.getHeader("User-Agent").toLowerCase().indexOf(tmp) != -1) {
				webType = "MOBILE";
				break;
			} else {
				webType = "PC";
			}
		}
		return webType;

	}

}