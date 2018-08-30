package com.campus.chatbuy.filter;

import org.guzz.api.taglib.TagSupportUtil;
import org.guzz.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 对xss 非法字符进行统一转义,controller不需要做处理
 */
public class FilterHttpServletRequest extends HttpServletRequestWrapper {
 
    public FilterHttpServletRequest(HttpServletRequest request) {
        super(request);
    }
 
    @Override
    public String getParameter(String name) {
        String value = super.getParameter((name));
        if (StringUtil.isEmpty(value)) {
            return value;
        }
//        return TagSupportUtil.escapeXml(value);
        return value;
    }

 
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if(values != null){
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (StringUtil.isEmpty(value)) {
                    continue;
                }
//                values[i] = TagSupportUtil.escapeXml(value);
                values[i] = value;
            }
        }
        return values;
    }
 
}