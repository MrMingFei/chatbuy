package com.campus.chatbuy;

import com.campus.chatbuy.util.HttpRequestUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinku on 2017/7/24.
 */
public class GoodsActionTest {

	@Test
	public void testPublishGoods() throws Exception{
		String url = "http://localhost:8080/chatbuy/web/goods/publishGoods";
		Map<String, String> params = new HashMap<>();
		params.put("picList", "1233,443355,5667");
		String result = HttpRequestUtil.postUrl(url, params);
		System.out.print(result);
	}
}
