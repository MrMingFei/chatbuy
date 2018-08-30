package com.campus.chatbuy.qim.sign;

import com.tls.sigcheck.tls_sigcheck;
import org.apache.log4j.Logger;
import org.guzz.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SigCheckUtil {

	private static final Logger log = Logger.getLogger(SigCheckUtil.class);

	private final static String privateKeyPath = getClassPath() + "qim/private_key";
	private final static String publicKeyPath = getClassPath() + "qim/public_key";

	public static String sign(String sdkAppId, String userId) throws Exception {
		tls_sigcheck signCheck = new tls_sigcheck();
		String priKey = readKey(privateKeyPath);
		int ret = signCheck.tls_gen_signature_ex2(sdkAppId, userId, priKey);
		log.info("SigCheck sign result is [" + signCheck + "]");
		Assert.assertEquals(ret, 0, "generate sign failed");
		String sign = signCheck.getSig();
		Assert.assertNotEmpty(sign, "generate sign failed");
		return sign;
	}

	public static boolean check(String sdkAppId, String userId, String sign) throws Exception {
		tls_sigcheck signCheck = new tls_sigcheck();
		String pubKey = readKey(publicKeyPath);
		int ret = signCheck.tls_check_signature_ex2(sign, pubKey, sdkAppId, userId);
		log.info("SigCheck check result is [" + signCheck + "]");
		if (0 == ret) {
			return true;
		}
		return false;
	}

	private static String readKey(String keyPath) throws Exception {
		File pubKeyFile = new File(keyPath);
		BufferedReader br = null;
		StringBuilder strBuilder = new StringBuilder();
		String s = "";
		try {
			br = new BufferedReader(new FileReader(pubKeyFile));
			while ((s = br.readLine()) != null) {
				strBuilder.append(s + '\n');
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return strBuilder.toString();
	}

	private static String getClassPath() {
		return SigCheckUtil.class.getResource("/").getPath();
	}

	public static void main(String args[]) throws Exception {
		String sdkAppId = "1400035427";
		String userId = "zhangsan";

		SigCheckUtil sigCheck = new SigCheckUtil();
		String sign = sigCheck.sign(sdkAppId, userId);
		System.out.println(sign);
		sigCheck.check(sdkAppId, userId, sign);
	}
}
