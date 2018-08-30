package com.campus.chatbuy.bean;

import java.util.List;

/**
 * Created by jinku on 2017/7/7.
 */
public class GoodsQueryBean {

	private String query;// 查询语句

	private List<Integer> tagIdList;// 标签Id
	private Integer majorId;// 专业Id
	private Integer universityId;// 大学Id
	private List<Integer> categoryId;//分类Id
	private Integer goodsStatus;//商品状态

	private Integer pageNo;
	private Integer pageSize;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<Integer> getTagIdList() {
		return tagIdList;
	}

	public void setTagIdList(List<Integer> tagIdList) {
		this.tagIdList = tagIdList;
	}

	public Integer getMajorId() {
		return majorId;
	}

	public void setMajorId(Integer majorId) {
		this.majorId = majorId;
	}

	public Integer getUniversityId() {
		return universityId;
	}

	public void setUniversityId(Integer universityId) {
		this.universityId = universityId;
	}

	public List<Integer> getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(List<Integer> categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getGoodsStatus() {
		return goodsStatus;
	}

	public void setGoodsStatus(Integer goodsStatus) {
		this.goodsStatus = goodsStatus;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "GoodsQueryBean{" +
				"query='" + query + '\'' +
				", tagIdList=" + tagIdList +
				", majorId=" + majorId +
				", universityId=" + universityId +
				", categoryId=" + categoryId +
				", goodsStatus=" + goodsStatus +
				", pageNo=" + pageNo +
				", pageSize=" + pageSize +
				'}';
	}
}
