package com.campus.chatbuy.model;

import org.guzz.annotations.GenericGenerator;
import org.guzz.annotations.Table;

import javax.persistence.GeneratedValue;
import java.util.Date;

/**
 * Created by jinku on 2017/9/7.
 *
 * 交易详情:交易地点信息
 */
@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName = "tradeInfo")
@Table(name = "trade_info")
public class TradeInfo {

    public final static int Trade_Status_Init = 0;//待确认
    public final static int Trade_Status_Confirmed = 1;//已确认

    @javax.persistence.Id
    @GenericGenerator(name = "idGenerator", strategy = "assigned")
    @GeneratedValue(generator = "idGenerator")
    private Long tradeId; //流水号
    private Long goodsId; // 商品id
    private Long tradePrice; // 交易价格
    private Long buyerId; // 买家id
    private Long sellerId; // 卖家Id
    private String tradeTime; // 交易时间
    private String tradeAddress; // 交易地址
    private Integer tradeState; // 交易状态

    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Long tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
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
