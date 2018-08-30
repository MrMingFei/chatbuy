package com.campus.chatbuy.bean;

/**
 * Created by jinku on 2017/7/21.
 */
public class PicRequestInfo {
	private String picId;//图片Id
	private String access_url;//生成的文件CDN下载url
	private String source_url;//生成的文件COS源站url
	private String url;//操作文件的url
	private String resource_path;//资源路径. 格式:/appid/bucket/xxx

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getAccess_url() {
		return access_url;
	}

	public void setAccess_url(String access_url) {
		this.access_url = access_url;
	}

	public String getSource_url() {
		return source_url;
	}

	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getResource_path() {
		return resource_path;
	}

	public void setResource_path(String resource_path) {
		this.resource_path = resource_path;
	}

	@Override
	public String toString() {
		return "PicRequestInfo{" +
				"picId=" + picId +
				", access_url='" + access_url + '\'' +
				", source_url='" + source_url + '\'' +
				", url='" + url + '\'' +
				", resource_path='" + resource_path + '\'' +
				'}';
	}
}
