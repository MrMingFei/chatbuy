package com.campus.chatbuy.manager;

import com.campus.chatbuy.bean.GoodsSessionBean;
import com.campus.chatbuy.model.UserSession;

import java.util.List;

/**
 * Created by jinku on 2017/8/10.
 */
public interface IGoodsSessionManager {

	// 获取当前的会话列表
	List<GoodsSessionBean> goodsSessionList(Integer userType) throws Exception;

	List<UserSession> sessionList();

	List<UserSession> sessionList(Integer userType);

	// 获取指定会话信息
	GoodsSessionBean goodsSession(Long userId, Long otherUserId, Long goodsId) throws Exception;

	void updateSessionAccessTime(Long sessionId);

	void delete(Long otherUserId, Long goodsId);
}
