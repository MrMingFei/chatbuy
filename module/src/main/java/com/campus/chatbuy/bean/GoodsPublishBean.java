package com.campus.chatbuy.bean;

import java.util.List;

/**
 * Created by jinku on 2017/7/11.
 */
public class GoodsPublishBean {

	private Long goodsId;
	private String goodsName;
	private String goodsDesc;
	private String categoryId;
	private String originPrice;
	private String price;
	private List<Long> tagList;
	private List<Long> picList;

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
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

	public List<Long> getTagList() {
		return tagList;
	}

	public void setTagList(List<Long> tagList) {
		this.tagList = tagList;
	}

	public List<Long> getPicList() {
		return picList;
	}

	public void setPicList(List<Long> picList) {
		this.picList = picList;
	}

	@Override
	public String toString() {
		return "GoodsPublishBean{" +
				"goodsId=" + goodsId +
				", goodsName='" + goodsName + '\'' +
				", goodsDesc='" + goodsDesc + '\'' +
				", categoryId='" + categoryId + '\'' +
				", originPrice=" + originPrice +
				", price=" + price +
				", tagList=" + tagList +
				", picList=" + picList +
				'}';
	}
}
