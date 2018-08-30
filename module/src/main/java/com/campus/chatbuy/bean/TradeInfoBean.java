package com.campus.chatbuy.bean;

import java.util.Date;

/**
 * Created by jinku on 2017/9/8.
 */
public class TradeInfoBean {

    private Long goodsId; // 商品id
    private String tradePrice; // 交易价格
    private String tradeAddress; // 交易地址
    private String tradeTime;//交易时间

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(String tradePrice) {
        this.tradePrice = tradePrice;
    }

    public String getTradeAddress() {
        return tradeAddress;
    }

    public void setTradeAddress(String tradeAddress) {
        this.tradeAddress = tradeAddress;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    @Override
    public String toString() {
        return "TradeInfoBean{" +
                "goodsId=" + goodsId +
                ", tradePrice='" + tradePrice + '\'' +
                ", tradeAddress='" + tradeAddress + '\'' +
                ", tradeTime='" + tradeTime + '\'' +
                '}';
    }
}
