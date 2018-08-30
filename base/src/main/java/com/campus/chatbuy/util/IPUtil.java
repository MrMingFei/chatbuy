package com.campus.chatbuy.util;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IPUtil {
	private static Logger logger = Logger.getLogger(IPUtil.class);
	private static final String[] HEADERS_TO_TRY = { "X-Real-IP", "x-forwarded-for", "remote_addr" };

	public static String getRequestIp(HttpServletRequest request) {
		String ip = null;
		for (String header : HEADERS_TO_TRY) {
			ip = request.getHeader(header);
			if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		ip = request.getRemoteAddr();
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = IPUtil.getLocalIP();
		}
		return ip;
	}

	/**
	 * 获得本地IP
	 *
	 * @return
	 */
	public static String getLocalIP() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
				Enumeration<InetAddress> nias = ni.getInetAddresses();
				while (nias.hasMoreElements()) {
					InetAddress ia = (InetAddress) nias.nextElement();
					if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia instanceof Inet4Address) {
						//logger.info("get Local Host Address IP:"+ia.getHostAddress());
						return ia.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			logger.error("get Local Host Address Exception!", e);
		}
		return null;
	}

	public static void main(String[] args) {
	}

}
