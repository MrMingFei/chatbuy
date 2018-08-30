package com.campus.chatbuy.bean;

import java.util.List;

public class DonateQueryBean {
    private List<Integer> donateStatus;
    private Integer pageNo;
    private Integer pageSize;

    public void setDonateStatus(List<Integer> donateStatus) {
        this.donateStatus = donateStatus;
    }

    public List<Integer> getDonateStatus() {
        return donateStatus;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}
