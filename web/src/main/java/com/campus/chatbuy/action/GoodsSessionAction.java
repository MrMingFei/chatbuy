package com.campus.chatbuy.action;

import com.campus.chatbuy.bean.GoodsSessionBean;
import com.campus.chatbuy.bean.ResultBean;
import com.campus.chatbuy.manager.IGoodsSessionManager;
import com.campus.chatbuy.util.ThreadLocalUtil;
import org.guzz.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by jinku on 2017/8/10.
 */

@Controller
@RequestMapping(value = "/chatbuy/web/goodsSession")
public class GoodsSessionAction {

	@Autowired
	private IGoodsSessionManager goodsSessionManager;

	// 获取当前的会话列表
	@RequestMapping(value = "/sessionList", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean sessionList(Integer userType) throws Exception {
		// 获取指定会话信息
		List<GoodsSessionBean> sessionBeanList = goodsSessionManager.goodsSessionList(userType);
		return ResultBean.success(sessionBeanList);
	}

	// 获取指定会话,没有则创建
	@RequestMapping(value = "/goodsSession", method = RequestMethod.GET)
	@ResponseBody
	public ResultBean goodsSession(Long otherUserId, Long goodsId) throws Exception {
		Assert.assertNotNull(otherUserId, "sellUserId is null");
		Assert.assertNotNull(goodsId, "goodsId is null");
		long userId = ThreadLocalUtil.getUserId();
		Assert.assertTrue(userId != otherUserId, "当前用户Id与对方用户Id相同");

		// 获取指定会话信息
		GoodsSessionBean sessionBean = goodsSessionManager.goodsSession(userId, otherUserId, goodsId);
		return ResultBean.success(sessionBean);
	}

	/**
	 * 删除用户指定会话
	 *
	 * @param otherUserId
	 * @param goodsId
	 * @return
	 * @throws Exception
     */
	@RequestMapping(value = "/deleteSession", method = RequestMethod.POST)
	@ResponseBody
	public ResultBean deleteSession(Long otherUserId, Long goodsId) throws Exception {
		goodsSessionManager.delete(otherUserId, goodsId);
		return ResultBean.success(null);
	}

}
