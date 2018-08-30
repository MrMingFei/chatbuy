package com.campus.chatbuy.model;

import org.guzz.annotations.GenericGenerator;
import org.guzz.annotations.Parameter;
import org.guzz.annotations.Table;

import javax.persistence.GeneratedValue;
import java.util.Date;

/**
 * Created by jinku on 17-7-8.
 *
 * 商品信息实体表
 */

@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName = "goods")
@Table(name = "goods_info")
public class GoodsInfo {

    public final static int Status_Published = 1;//在售
    public final static int Status_UnPublished = 2;//已下架
    public final static int Status_Sold = 3;//已出售
    public final static int Status_Deleted = 4;//已删除

    public final static int Pay_Way_offline = 1;

    public final static String Source_ChatBuy = "chatbuy";
    public final static String Source_HuoDong = "huodong";

    @javax.persistence.Id
    @GenericGenerator(name = "idGenerator", strategy = "assigned")
    @GeneratedValue(generator = "idGenerator")
    private Long goodsId;//商品主键ID
    private Long userId;//用户Id
    private String goodsName;//商品名称
    private String goodsDesc;//商品描述
    private Integer categoryId;//分类Id
    private Long originPrice;//原价
    private Long price;//现价
    private String source;//来源
    private Integer payWay;//支付方式：1=线下支付
    private Integer status;//商品状态信息(0=未发布，1=已发布，2=已下架，3=已出售)
    private Long readNum;//阅读量
    private Date updateTime;
    private Date createTime;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Long getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(Long originPrice) {
        this.originPrice = originPrice;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getPayWay() {
        return payWay;
    }

    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getReadNum() {
        return readNum;
    }

    public void setReadNum(Long readNum) {
        this.readNum = readNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
