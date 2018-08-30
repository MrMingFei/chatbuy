package com.campus.chatbuy.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.campus.chatbuy.util.IPUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.guzz.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinku on 8/19/17.
 */
public class LogFilter implements Filter {

    private Logger logger = Logger.getLogger(LogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 请求开始时间
        long startTime = System.currentTimeMillis();
        MyResponseWrapper responseWrapper = new MyResponseWrapper(response);
//        response.setHeader("server-from", MachineUtil.getLocalHostName());

        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            calculateRequestCosts(request, responseWrapper, startTime);
        }
    }

    private void calculateRequestCosts(HttpServletRequest request, MyResponseWrapper responseWrapper, long startTime) {
        // 计算请求耗时
        long endTime = System.currentTimeMillis();
        long period = endTime - startTime;

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("url", request.getRequestURI());
        logMap.put("costTime", String.valueOf(period));
        logMap.put("ip", IPUtil.getRequestIp(request));

        // 不记录文件上传参数，不记录登录参数
        String contentType = StringUtil.dealNull(request.getContentType()).toLowerCase();
        if (!contentType.contains("multipart/form-data") && !request.getRequestURI().contains("/user/login")
                && !request.getRequestURI().contains("/user/register")) {
            logMap.put("params", JSON.toJSON(getRequestMap(request)));
        }

        String content = responseWrapper.getContent();
        if (content != null && !content.contains("<html>")) {
            try {
                JSONObject jsonObject = JSON.parseObject(content);
                logMap.put("response", jsonObject);
            } catch (Exception e) {
//                logMap.put("response", content);
            }
        }

        logger.info(JSON.toJSONString(logMap));
        if (period > 3000) { // 超过500ms报警
            logger.warn(String.format("请求时间过长:%d ms --> params=%s", period, request.getRequestURI()));
        }
    }

    private Map<String, String> getRequestMap(HttpServletRequest request) {
        Map<String, String> requestMap = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            requestMap.put(key, request.getParameter(key));
        }
        return requestMap;
    }

    @Override
    public void destroy() {

    }

}

class MyResponseWrapper extends HttpServletResponseWrapper {
    private MyWriter myWriter;

    private MyServletOutputStream stream;

    public MyResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (myWriter == null) {
            myWriter = new MyWriter(super.getWriter());
        }

        return myWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // "Content-Disposition", "attachment;filename=
        String disposition = super.getHeader("Content-Disposition");
        // 二进制附件不进行拦截
        if (disposition != null && disposition.toLowerCase().contains("attachment;")) {
            return super.getOutputStream();
        }

        if (stream == null) {
            stream = new MyServletOutputStream(super.getOutputStream());
        }

        return stream;
    }

    public String getContent() {
        if (myWriter != null) {
            return myWriter.getContent();
        }

        if (stream != null) {
            byte[] bs = stream.getBytes();

            return new String(bs);
        }

        return null;
    }
}

class MyServletOutputStream extends ServletOutputStream {
    final ServletOutputStream outputStream;

    ByteArrayOutputStream boos = new ByteArrayOutputStream();

    public MyServletOutputStream(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.outputStream.write(b);

        boos.write(b);
    }

    @Override
    public boolean isReady() {
        return outputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        outputStream.setWriteListener(writeListener);
    }

    @Override
    public void write(int b) throws IOException {
        this.outputStream.write(b);
        boos.write(b);
    }

    public byte[] getBytes() {
        return boos.toByteArray();
    }

    public void close() throws IOException {
        boos.close();
        super.close();
        outputStream.close();
    }

}

class MyWriter extends PrintWriter {
    private StringBuilder buffer;

    public MyWriter(Writer out) {
        super(out);
        buffer = new StringBuilder();
    }

    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
        buffer.append(buf, off, len);
    }

    @Override
    public void write(char[] buf) {
        super.write(buf);

        buffer.append(buf);
    }

    @Override
    public void write(int c) {
        super.write(c);
        buffer.append(c);
    }

    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        buffer.append(s);
    }

    @Override
    public void write(String s) {
        super.write(s);
        buffer.append(s);
    }

    public String getContent() {
        return buffer.toString();
    }

}
