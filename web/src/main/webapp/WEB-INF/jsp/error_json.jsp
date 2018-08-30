<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import ="com.campus.chatbuy.bean.ResultBean" %>
<%@ page import ="com.campus.chatbuy.util.JsonConverUtil" %>
<%
    String exception = (String) request.getAttribute("exception");
    String errorJson = JsonConverUtil.bean2Json(ResultBean.failure(exception));
    out.write(errorJson);
    out.flush();
%>
