package com.campus.chatbuy.model;

import org.guzz.annotations.GenericGenerator;
import org.guzz.annotations.Table;

import javax.persistence.GeneratedValue;
import java.util.Date;

/**
 * Created by jinku on 2017/9/7.
 *
 * 交易记录(我的已购列表和我的已售列表)
 */
@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName = "tradeRecord")
@Table(name = "trade_record")
public class TradeRecord {

    public final static int Trade_Type_Buy = 1;
    public final static int Trade_Type_Sell = 2;

    @javax.persistence.Id
    @GenericGenerator(name = "idGenerator", strategy = "identity")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private Long userId;
    private Long otherUserId;
    private Integer tradeType;// 交易类型:1=购买;2=售卖
    private Long tradeId;
    private Long goodsId;

    private Date createTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

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
