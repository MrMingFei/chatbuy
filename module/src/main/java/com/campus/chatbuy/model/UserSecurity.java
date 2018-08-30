package com.campus.chatbuy.model;

import org.guzz.annotations.GenericGenerator;
import org.guzz.annotations.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Author: baoxuebin
 * Date: 2017/7/11
 */
@javax.persistence.Entity
@org.guzz.annotations.Entity(businessName = "userSecurity")
@Table(name = "user_security")
public class UserSecurity {

    public final static int ACCOUNT_STATE_INACTIVE = 0;
    public final static int ACCOUNT_STATE_NORMAL = 1;
    public final static int ACCOUNT_STATE_LOCK = 2;

    public final static int Role_Type_Common = 1;
    public final static int Role_Type_Admin = 2;

    // 用户id
    @javax.persistence.Id
    @GenericGenerator(name = "idGenerator", strategy = "assigned")
    @GeneratedValue(generator = "idGenerator")
    private Long userId;
    private String mobile;
    // 采用 bcrypt 加密
    private String password;
    private Integer roleType;//用户类型:1=普通用户;2=管理员
    private Integer status;// 账户状态信息，0=未激活，1=正常，2=锁定
    private Date updateTime;
    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
