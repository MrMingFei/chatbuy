package com.campus.chatbuy.bean;

import com.campus.chatbuy.model.UserInfo;
import com.campus.chatbuy.util.StringUtil;

/**
 * Author: baoxuebin
 * Date: 2017/7/16
 */
public class RegisterBean {

    // 手机号码
    private String mobile;
    // 昵称
    private String userName;
    // 性别
    private Integer sex;
    // 大学
    private Integer universityId;
    // 专业
    private Integer majorId;
    // 大学
    private String universityName;
    // 专业
    private String majorName;
    // 入学年份
    private String entranceYear;
    // 头像图片Id
    private String picId;
    // 密码
    private String password;
    private String confirmPwd;

    public String verify() {
        if (!StringUtil.isPhone(mobile)) { // 验证电话号码
            return "无效的电话号码";
        } else if (StringUtil.isBlank(userName)) {
            return "昵称不能为空";
        } else if (UserInfo.Sex_Male != sex && UserInfo.Sex_Female != sex) {
            return "无效的性别";
        } else if (universityId == null || universityId <= 0 || StringUtil.isEmpty(universityName)) {
            return "无效的大学信息";
        } else if (majorId == null || majorId <= 0 || StringUtil.isEmpty(majorName)) {
            return "无效的专业信息";
        } else if (StringUtil.isEmpty(entranceYear)) {
            return "无效的入学年份";
        } else if (StringUtil.isBlank(password) || StringUtil.isBlank(confirmPwd) || !password.equals(confirmPwd)) {
            return "无效的密码";
        }
        return null;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Integer universityId) {
        this.universityId = universityId;
    }

    public Integer getMajorId() {
        return majorId;
    }

    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public String getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(String entranceYear) {
        this.entranceYear = entranceYear;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }

    @Override
    public String toString() {
        return "RegisterBean{" +
                "mobile='" + mobile + '\'' +
                ", userName='" + userName + '\'' +
                ", sex=" + sex +
                ", universityId='" + universityId + '\'' +
                ", majorId='" + majorId + '\'' +
                ", universityName='" + universityName + '\'' +
                ", majorName='" + majorName + '\'' +
                ", entranceYear='" + entranceYear + '\'' +
                ", picId='" + picId + '\'' +
                '}';
    }
}
