package com.campus.chatbuy.bean;

import java.util.Date;

/**
 * Created by jinku on 2017/8/10.
 */
public class GoodsSessionBean {

	public String sessionId;
	public String buyUserId;
	public String buyUserName;
	public String buyUserAvatar;
	public String sellUserId;
	public String sellUserName;
	public String sellUserAvatar;
	public Integer userType;
	public String goodsId;
	public String goodsName;
	public String goodsPic;
	public String goodsPrice;
	public Integer goodsStatus;
	public String goodsOriginPrice;
	public int unreadNum;
	public MessageBean latestMessage;
	public Integer isInBlack;//是否屏蔽对方

	public String createTime;
	public String updateTime;
}
