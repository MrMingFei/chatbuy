package com.campus.chatbuy.util;

import org.apache.log4j.Logger;
import org.guzz.api.taglib.TagSupportUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	private static Logger logger = Logger.getLogger(StringUtil.class);

	public static final BigDecimal HUNDRED = new BigDecimal(100);

	/**
	 * 判断一个字符串是否全部为空白字符，空白字符指由空格' '、制表符'\t'、回车符'\r'、换行符'\n'组成的字符串
	 * wangshuo
	 *
	 * @param input 输入字符集
	 * @return boolean
	 */
	public static boolean isBlank(CharSequence input) {
		if (input == null || "".equals(input)) {
			return true;
		}
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			// 只要有一个字符不是制表 空格 换行 回车中的一个,就不是空字符
			if (c != '\t' && c != ' ' && c != '\n' && c != '\r') {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String... strs) {
		for (String str : strs) {
			if (isEmpty(str)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotEmpty(String... strs) {
		return !isEmpty(strs);
	}

	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static int parseInt(String str, int defaultValue) {
		int number = defaultValue;
		try {
			number = Integer.parseInt(str);
		} catch (Exception e) {
			number = defaultValue;
		}
		return number;
	}

	public static String toUpperCnCaser(String str) {
		Number o = Integer.parseInt(str);

		String s = new DecimalFormat("#.00").format(o);
		s = s.replaceAll("\\.", "");
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };
		String unit = "仟佰拾兆仟佰拾亿仟佰拾万仟佰拾元角分";
		int l = unit.length();
		StringBuffer sb = new StringBuffer(unit);
		for (int i = s.length() - 1; i >= 0; i--) {
			sb = sb.insert(l - s.length() + i, digit[(s.charAt(i) - 0x30)]);
		}
		s = sb.substring(l - s.length(), l + s.length());
		s = s.replaceAll("零[拾佰仟]", "零").replaceAll("零{2,}", "零").replaceAll("零([兆万元])", "$1").replaceAll("零[角分]", "");
		return s;
	}

	public static String convertCent2Yuan(long cent) {
		return convertCent2Yuan(String.valueOf(cent));
	}

	public static String convertCent2Yuan(String centStr) {
		if (centStr == null || centStr.trim().equals(""))
			return "0.00";
		BigDecimal cent = new BigDecimal(centStr);
		if (cent.equals(BigDecimal.ZERO))
			return "0.00";
		return cent.divide(HUNDRED).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public static BigDecimal convertYuan2Cent(BigDecimal yuan) {
		if (yuan == null)
			return BigDecimal.ZERO;
		return yuan.multiply(HUNDRED);
	}
	/**
	 * 元转成分,例如 2.00 --> 200
	 *
	 * @return
	 */
	public static long convertYuan2Cent(String yuan) throws Exception {
		if (org.guzz.util.StringUtil.isEmpty(yuan)) {
			return 0L;
		}

		if(!yuan.contains(".")) {
			return Long.parseLong(yuan) * 100;
		}

		int dotIndex = yuan.indexOf(".");
		String yuanPrefix = yuan.substring(0, dotIndex);
		String yuanPoxfix = yuan.substring(dotIndex + 1, yuan.length());

		return Long.parseLong(yuanPrefix) * 100 + Long.parseLong(yuanPoxfix);

	}

	public static boolean isPhone(String str) {
		if (isBlank(str)) return false;
		Pattern pattern = Pattern.compile("^[1][0-9][0-9]{9}$");
		return pattern.matcher(str).matches();
	}

	public static void main(String[] args) {
		System.out.println(toUpperCnCaser("100"));

		String str = TagSupportUtil.escapeXml("<444444l");
		System.out.println(str);
		boolean result = isPhone("18713506432");
		System.out.println(result);
	}

}
