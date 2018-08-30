package com.campus.chatbuy.bean;

/**
 * Created by jinku on 2017/10/16.
 */
public class UserGoodsStat {

    private long selloutCount;//已售数量
    private long sellingCount;//在售数量

    public long getSelloutCount() {
        return selloutCount;
    }

    public void setSelloutCount(long selloutCount) {
        this.selloutCount = selloutCount;
    }

    public long getSellingCount() {
        return sellingCount;
    }

    public void setSellingCount(long sellingCount) {
        this.sellingCount = sellingCount;
    }
}
