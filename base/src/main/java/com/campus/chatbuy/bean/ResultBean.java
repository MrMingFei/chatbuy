package com.campus.chatbuy.bean;

import org.apache.log4j.MDC;

public class ResultBean {

	public static final int RETURN_SUCCESS_CODE = 0;// 操作成功返回码
	public static final int RETURN_ERROR_CODE = 1;// 操作失败返回码

	public static final String RETURN_SUCCESS_MESSAGE = "操作成功";// 操作成功提示语
	public static final String RETURN_ERROR_MESSAGE = "操作失败";// 操作失败提示语

	private Error error = new Error();
	private Object data;
	private String logId;

	public ResultBean() {
		this.logId = (String) MDC.get("traceId");
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public static class Error {

		public int returnCode = RETURN_SUCCESS_CODE;
		public String returnMessage = RETURN_SUCCESS_MESSAGE;
		public String returnUserMessage = RETURN_SUCCESS_MESSAGE;

		public static final Error NORMAL_ERROR = new Error();

		static {
			NORMAL_ERROR.returnCode = RETURN_ERROR_CODE;
			NORMAL_ERROR.returnMessage = RETURN_ERROR_MESSAGE;
			NORMAL_ERROR.returnUserMessage = RETURN_ERROR_MESSAGE;
		}

		@Override public String toString() {
			return "Error [returnCode=" + returnCode + ", returnMessage=" + returnMessage + ", returnUserMessage="
					+ returnUserMessage + "]";
		}
	}

	public static ResultBean success(Object data) {
		ResultBean resultBean = new ResultBean();
		resultBean.setError(new Error());
		resultBean.setData(data);

		return resultBean;
	}

	public static ResultBean failure(int errorCode) {
		ResultBean resultBean = new ResultBean();
		Error error = Error.NORMAL_ERROR;
		error.returnCode = errorCode;
		resultBean.setError(error);
		return resultBean;
	}

	public static ResultBean failure(int errorCode, String errorMsg, String errorUserMsg) {
		ResultBean resultBean = new ResultBean();
		Error error = Error.NORMAL_ERROR;
		error.returnCode = errorCode;
		error.returnMessage = errorMsg;
		error.returnUserMessage = errorUserMsg;
		resultBean.setError(error);
		return resultBean;
	}

	public static ResultBean failure(String errorMsg, String errorUserMsg) {
		ResultBean resultBean = new ResultBean();
		Error error = Error.NORMAL_ERROR;
		error.returnMessage = errorMsg;
		error.returnUserMessage = errorUserMsg;
		resultBean.setError(error);
		return resultBean;
	}

	public static ResultBean failure(String errorMsg) {
		ResultBean resultBean = new ResultBean();
		Error error = Error.NORMAL_ERROR;
		error.returnMessage = errorMsg;
		error.returnUserMessage = errorMsg;
		resultBean.setError(error);
		return resultBean;
	}
}
