package com.campus.chatbuy.bean;

import com.campus.chatbuy.model.*;

import java.util.Date;
import java.util.List;

/**
 * Created by jinku on 7/10/17.
 */
public class GoodsInfoBean {

	private String goodsId;//商品主键ID
	private String userId;//用户Id
	private String goodsName;//商品名称
	private String goodsDesc;//商品描述
	private String categoryId;//分类Id
	private String originPrice;//原价
	private String price;//现价
	private String source;//来源
	private Integer payWay;//支付方式：1=线下支付
	private Integer status;//商品状态信息(0=未发布，1=已发布，2=已下架，3=已出售)
	private Long readNum;//阅读量
	private String updateTime;
	private String createTime;

	private String categoryName;//分类名称
	private List<PicInfoBean> picList;
	private List<TagBean> tagList;
	private UserInfoBean userInfo;

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(String originPrice) {
		this.originPrice = originPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getPayWay() {
		return payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getReadNum() {
		return readNum;
	}

	public void setReadNum(Long readNum) {
		this.readNum = readNum;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<PicInfoBean> getPicList() {
		return picList;
	}

	public void setPicList(List<PicInfoBean> picList) {
		this.picList = picList;
	}

	public List<TagBean> getTagList() {
		return tagList;
	}

	public void setTagList(List<TagBean> tagList) {
		this.tagList = tagList;
	}

	public UserInfoBean getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfoBean userInfo) {
		this.userInfo = userInfo;
	}
}
