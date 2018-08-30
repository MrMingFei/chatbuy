package com.campus.chatbuy.util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

	private static final Logger log = Logger.getLogger(ConfigUtil.class);

	// 非特殊情况不要公开变量
	private static Properties props;

	static {
		InputStream fis = null;
		try {
			// 初始化路径
			String fileName = "config.properties";
			String filePath = getConfigFolderPath() + fileName;
			log.info("Init ConfigUtil, The Properties Path Is = " + filePath);
			// 读取属性文件
			props = new Properties();
			fis = new FileInputStream(filePath);
			props.load(fis);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private ConfigUtil() {
	}

	/**
	 * 判断当前运行环境是否为线上
	 * @return
	 */
	public static boolean isProd(){
		return "prod".equals(getValue("run.env.mode"));
	}

	/**
	 * 获取配置文件夹的路径
	 *
	 * @return
	 */
	public static String getConfigFolderPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClassPath());
		sb.append("configs").append(File.separator);
		return sb.toString();
	}

	/**
	 * 根据给定的key获取:
	 *
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		return props.getProperty(key).trim();
	}

	/**
	 * 获取Int值
	 *
	 * @param key
	 * @return
	 */
	public static int getIntValue(String key) {
		return Integer.parseInt(getValue(key));
	}

	/**
	 * 获取Boolean类型的值
	 *
	 * @param key
	 * @return
	 */
	public static boolean getBooleanValue(String key) {
		return Boolean.parseBoolean(getValue(key));
	}

	/**
	 * 获取classes文件夹的路径
	 *
	 * @return
	 */
	public static String getClassPath() {
		return ConfigUtil.class.getResource("/").getPath();
	}

	/**
	 * 获取WEB-INF文件夹的路径
	 *
	 * @return
	 */
	public static String getWebInfPath() {
		String webPath = getClassPath();
		webPath = webPath.substring(0, webPath.length() - "/classes".length());
		return webPath;
	}
}