package com.campus.chatbuy.util;

import org.apache.commons.lang.StringUtils;
import org.guzz.util.DateUtil;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils extends DateUtil {

	private static SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sformatChn = new SimpleDateFormat("yyyy年MM月dd日");

	public static Date str2Date(String str, SimpleDateFormat sdf) throws ParseException {
		if (str != null) {
			return sdf.parse(str);
		}
		return null;
	}

	public static Date str2Date(String dateStr, String pattern) throws ParseException {
		if (dateStr != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.parse(dateStr);
		}
		return null;
	}

	public static String format(Date date) {
		if (date == null) {
			return null;
		}
		return sformat.format(date);
	}

	public static String format(long date) {
		if (date == 0) {
			return null;
		}
		return sformat.format(date);
	}

	/**
	 * 时间搓类型转成日期格式
	 * 
	 * @param date
	 * @return
	 */
	public static String format(String date) {
		if (date == null) {
			return null;
		}
		return sformat.format(Long.parseLong(date));
	}

	/**
	 * 时间搓类型转成日期格式
	 * 
	 * @param date
	 * @return
	 */
	public static String format(String date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(Long.parseLong(date));
	}

	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String formatChn(Date dt) {
		return sformatChn.format(dt);
	}

	public static String formatChn(String dt) throws ParseException {
		if (StringUtils.isBlank(dt)) {
			return "";
		}
		Date date = sf.parse(dt);
		return sformatChn.format(date);
	}

	public static int diffDayNum(Date preDate, Date nextDate) {

		long time1 = getZeroTimeInMillis(preDate);
		long time2 = getZeroTimeInMillis(nextDate);
		long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(betweenDays));

	}

	public static int diffDayNum(long preDate, long nextDate) {
		long betweenDays = (nextDate - preDate) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(betweenDays));
	}

	/*
	 * 获取传入日期零时的时间毫秒数
	 */
	public static long getZeroTimeInMillis(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 给date添加(days,hours,minutes , seconds)时间偏移
	 * 
	 * @param date
	 * @param days
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return Date
	 */
	public static Date Add(Date date, int days, int hours, int minutes, int seconds) {
		Date dt = date;
		if (dt != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);

			if (days != 0) {
				calendar.add(Calendar.DATE, days);
			}
			if (hours != 0) {
				calendar.add(Calendar.HOUR, hours);
			}
			if (minutes != 0) {
				calendar.add(Calendar.MINUTE, minutes);
			}
			if (seconds != 0) {
				calendar.add(Calendar.SECOND, seconds);
			}
			dt = calendar.getTime();
		}
		return dt;
	}

	/**
	 * 格式化传入的日期:获取日期粒度
	 *
	 * @return 20171111
     */
	public static String getDayStr(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = fillZero(String.valueOf(calendar.get(Calendar.MONTH) + 1), 2);
		String day = fillZero(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2);

		return year + month + day;
	}

	/**
	 * 格式化传入的日期:获取小时粒度
	 *
	 * @return 2017111116
	 */
	public static String getHourStr(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = fillZero(String.valueOf(calendar.get(Calendar.MONTH) + 1), 2);
		String day = fillZero(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2);
		String hour = fillZero(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), 2);

		return year + month + day + hour;
	}

	/**
	 * 格式化传入的日期:获取分钟粒度
	 *
	 * @return 201711111631
	 */
	public static String getMinuteStr(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = fillZero(String.valueOf(calendar.get(Calendar.MONTH) + 1), 2);
		String day = fillZero(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2);
		String hour = fillZero(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), 2);
		String minute = fillZero(String.valueOf(calendar.get(Calendar.MINUTE)), 2);

		return year + month + day + hour + minute;
	}

	/**
	 * 位数不够,填充0
	 *
	 * @param origin : 原始字符串
	 * @param digit : 位数
     * @return
     */
	private static String fillZero(String origin, int digit) {
		for(int i = 0; i < digit - origin.length(); i++) {
			origin = "0" + origin;
		}
		return origin;
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(sformat.format(new Date()));
		System.out.println(getDayStr(new Date()));
		System.out.println(getHourStr(new Date()));
		System.out.println(getMinuteStr(new Date()));
		System.out.println(fillZero("1", 2));
	}
}