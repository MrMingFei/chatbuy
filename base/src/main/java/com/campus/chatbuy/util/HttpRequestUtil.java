package com.campus.chatbuy.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.guzz.util.FileUtil;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by jinku on 16/4/21.
 */
public class HttpRequestUtil {

	private static final Logger logger = Logger.getLogger(HttpRequestUtil.class);

	private static final String User_Agent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; SV1; .NET CLR 1.1"
			+ ".4322; CIBA; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";

	private static final int Timeout_In_Mills = 6 * 1000;

	public static String postUrl(String url, Map<String, String> params) throws Exception {
		String result = httpPostUrl(url, params, null);
		return result;
	}

	public static String getUrl(String url, Map<String, String> params) throws Exception {
		String result = httpGetUrl(url, params, null);
		return result;
	}

	/**
	 * 解决ajax跨域所引起的问题
	 *
	 * @param response
	 */
	public static void resolveAjaxCrossDomain(HttpServletResponse response, String domain) {
		response.setHeader("Access-Control-Allow-Origin", domain);
		response.setHeader("Access-Control-Allow-Methods", "POST");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
	}

	/**
	 * https post请求
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpsPostUrl(String url, Map<String, String> params) throws Exception {
		return httpsPostUrl(url, params, null, "UTF-8");
	}

	public static String httpsPostUrl(String url, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		return httpsPostUrl(url, params, headers, "UTF-8");
	}

	private static String httpGetUrl(String url, Map<String, String> paramsMap, Map<String, String> headers)
			throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(User_Agent).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout_In_Mills)
				.setSocketTimeout(Timeout_In_Mills).build();
		if (paramsMap != null && paramsMap.size() > 0) {
			ArrayList paramList = new ArrayList();
			Iterator iterator = paramsMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry entity = (Map.Entry) iterator.next();
				paramList.add(new BasicNameValuePair((String) entity.getKey(), (String) entity.getValue()));
			}
			if (!url.endsWith("?")) {
				url = url + "?";
			}
			String params = URLEncodedUtils.format(paramList, "UTF-8");
			url = url + params;
		}
		HttpGet httpGet = new HttpGet(url);
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				httpGet.addHeader(e.getKey(), e.getValue());
			}
		}
		HttpEntity httpEntity = null;
		CloseableHttpResponse response = null;
		String content = null;
		try {
			httpGet.setConfig(requestConfig);
			response = httpclient.execute(httpGet);
			httpEntity = response.getEntity();
			content = FileUtil.readText(httpEntity.getContent(), "UTF-8");
			httpGet.abort();
		} finally {
			if (httpEntity != null) {
				EntityUtils.consume(httpEntity);
			}
			if (response != null) {
				response.close();
			}
			if (httpclient != null) {
				httpclient.close();
			}
		}
		return content;
	}

	public static String httpPostJson(String url, String json, Map<String, String> headers)
			throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(User_Agent).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout_In_Mills)
				.setSocketTimeout(Timeout_In_Mills).build();

		CloseableHttpResponse response = null;
		HttpEntity httpEntity = null;
		String content = null;
		HttpPost httpPost = new HttpPost(url);

		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				httpPost.addHeader(e.getKey(), e.getValue());
			}
		}
		try {
			StringEntity stringEntity = new StringEntity(json.toString());
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			httpPost.setConfig(requestConfig);
			response = httpclient.execute(httpPost);
			httpEntity = response.getEntity();
			content = EntityUtils.toString(httpEntity, "UTF-8");
			httpPost.abort();
		} finally {
			if (httpEntity != null) {
				EntityUtils.consume(httpEntity);
			}
			if (response != null) {
				response.close();
			}
			if (httpclient != null) {
				httpclient.close();
			}
		}
		return content;
	}

	public static String httpPostUrl(String url, Map<String, String> paramsMap, Map<String, String> headers)
			throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(User_Agent).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout_In_Mills)
				.setSocketTimeout(Timeout_In_Mills).build();

		CloseableHttpResponse response = null;
		HttpEntity httpEntity = null;
		String content = null;
		HttpPost httpPost = new HttpPost(url);
		ArrayList nvps = new ArrayList();
		if (paramsMap != null && paramsMap.size() > 0) {
			Iterator en = paramsMap.entrySet().iterator();
			while (en.hasNext()) {
				Map.Entry entry = (Map.Entry) en.next();
				nvps.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
			}
		}
		if (headers != null && headers.size() > 0) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				httpPost.addHeader(e.getKey(), e.getValue());
			}
		}
		try {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
			httpPost.setEntity(formEntity);
			httpPost.setConfig(requestConfig);
			response = httpclient.execute(httpPost);
			httpEntity = response.getEntity();
			content = EntityUtils.toString(httpEntity, "UTF-8");
			httpPost.abort();
		} finally {
			if (httpEntity != null) {
				EntityUtils.consume(httpEntity);
			}
			if (response != null) {
				response.close();
			}
			if (httpclient != null) {
				httpclient.close();
			}
		}
		return content;
	}

	/**
	 * https post请求
	 *
	 * @param url
	 * @param paramsMap
	 * @param encode
	 * @return
	 * @throws Exception
	 */
	public static String httpsPostUrl(String url, Map<String, String> paramsMap, Map<String, String> headers,
			String encode) throws Exception {
		String content = null;
		HttpEntity httpEntity = null;
		CloseableHttpResponse response = null;
		SSLContext build = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
			@Override public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();
		SSLConnectionSocketFactory item = new SSLConnectionSocketFactory(build, NoopHostnameVerifier.INSTANCE);
		Registry<ConnectionSocketFactory> builder = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", item).build();

		PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(builder);
		CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(User_Agent).setConnectionManager(pool)
				.setRedirectStrategy(new LaxRedirectStrategy()).build();
		try {
			HttpPost http = new HttpPost(url);
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> e : headers.entrySet()) {
					http.addHeader(e.getKey(), e.getValue());
				}
			}
			List<NameValuePair> nvps = new ArrayList<>();
			if (paramsMap != null && paramsMap.size() > 0) {
				for (Map.Entry<String, String> e : paramsMap.entrySet()) {
					nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
				}
			}
			UrlEncodedFormEntity en = new UrlEncodedFormEntity(nvps, encode);
			http.setEntity(en);
			response = httpclient.execute(http);
			httpEntity = response.getEntity();
			content = EntityUtils.toString(httpEntity, "UTF-8");
			http.abort();
		} finally {
			if (httpEntity != null) {
				EntityUtils.consume(httpEntity);
			}
			if (response != null) {
				response.close();
			}
			if (httpclient != null) {
				httpclient.close();
			}
			if (pool != null) {
				pool.close();
			}
		}
		return content;
	}

}
