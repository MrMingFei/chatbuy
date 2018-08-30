package com.campus.chatbuy.bean;

import java.util.Date;

/**
 * Created by jinku on 2017/9/15.
 */
public class TradeInfoResultBean {

    private String tradeId; //流水号
    private String goodsId; // 商品id
    private Long tradePrice; // 交易价格
    private String buyerId; // 买家id
    private String sellerId; // 卖家Id
    private String tradeTime; // 交易时间
    private String tradeAddress; // 交易地址
    private Integer tradeState; // 交易状态

    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Long getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Long tradePrice) {
        this.tradePrice = tradePrice;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getTradeAddress() {
        return tradeAddress;
    }

    public void setTradeAddress(String tradeAddress) {
        this.tradeAddress = tradeAddress;
    }

    public Integer getTradeState() {
        return tradeState;
    }

    public void setTradeState(Integer tradeState) {
        this.tradeState = tradeState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
