package com.campus.chatbuy.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinku on 2017/7/11.
 */
public class PageBean <T> {
	private long total;
	private List<T> rows = new ArrayList<>();

	public static PageBean getPageBean(long total, List rows) {
		PageBean pageBean = new PageBean();
		pageBean.total = total;
		if (rows != null) {
			pageBean.rows = rows;
		}
		return pageBean;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
