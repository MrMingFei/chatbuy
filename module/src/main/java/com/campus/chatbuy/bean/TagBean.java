package com.campus.chatbuy.bean;

import com.campus.chatbuy.model.GoodsTag;

public class TagBean {
	private String tagId;
	private String tagName;

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public static TagBean convert2Bean(GoodsTag goodsTag) {
		TagBean tagBean = new TagBean();
		tagBean.tagId = String.valueOf(goodsTag.getId());
		tagBean.tagName = goodsTag.getTagName();
		return tagBean;
	}
}