package com.campus.chatbuy.bean;

import com.campus.chatbuy.model.PicInfo;
import com.campus.chatbuy.model.UserInfo;

import java.util.Date;

/**
 * Created by jinku on 2017/7/24.
 */
public class UserInfoBean {

	private String userId;
	private String mobile;
	private String token;
	private String userName;
	private String avatarPicId;
	private Integer sex;
	private Integer universityId;
	private String universityName;
	private Integer majorId;
	private String majorName;
	private String entranceYear;
	private PicInfoBean avatarPicInfo;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarPicId() {
		return avatarPicId;
	}

	public void setAvatarPicId(String avatarPicId) {
		this.avatarPicId = avatarPicId;
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

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	public Integer getMajorId() {
		return majorId;
	}

	public void setMajorId(Integer majorId) {
		this.majorId = majorId;
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

	public PicInfoBean getAvatarPicInfo() {
		return avatarPicInfo;
	}

	public void setAvatarPicInfo(PicInfoBean avatarPicInfo) {
		this.avatarPicInfo = avatarPicInfo;
	}
}
