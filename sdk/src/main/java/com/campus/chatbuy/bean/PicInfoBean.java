package com.campus.chatbuy.bean;

/**
 * Created by jinku on 2017/7/18.
 */
public class PicInfoBean {
	private String picId;//图片Id
	private String originUrl;//原图地址
	private String bigUrl;//大图地址
	private String smallUrl;//小图地址
	private int width;//原图宽度
	private int height;//原图高度

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getOriginUrl() {
		return originUrl;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	public String getBigUrl() {
		return bigUrl;
	}

	public void setBigUrl(String bigUrl) {
		this.bigUrl = bigUrl;
	}

	public String getSmallUrl() {
		return smallUrl;
	}

	public void setSmallUrl(String smallUrl) {
		this.smallUrl = smallUrl;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override public String toString() {
		return "PicInfo{" +
				"picId='" + picId + '\'' +
				", originUrl='" + originUrl + '\'' +
				", bigUrl='" + bigUrl + '\'' +
				", smallUrl='" + smallUrl + '\'' +
				", width=" + width +
				", height=" + height +
				'}';
	}
}
