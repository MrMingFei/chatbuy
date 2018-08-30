package com.campus.chatbuy.util;

import com.campus.chatbuy.enums.FileType;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

/**
 * Created by jinku on 2017/12/19.
 */
public class DownloadUtil {

    private static final Logger log = Logger.getLogger(DownloadUtil.class);

    public static final int BYTE_TMP_SIZE = 256 * 1024;

    public static void printFileData(String fileName, FileType fileType, byte[] fileData,
                                     HttpServletResponse response, String errorInfo) {
        OutputStream out = null;
        try {
            if (fileName.indexOf(".") == -1) {
                fileName = fileName + "." + fileType.getName();
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(fileData);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType(getContentTypeByFileType(fileType) + ";charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes(), "iso-8859-1"));
            out = response.getOutputStream();

            byte[] b = new byte[BYTE_TMP_SIZE];
            int n = 0;
            while ((n = bais.read(b)) != -1) {
                out.write(b, 0, n);
            }
            out.flush();
        } catch (Exception e) {
            log.error("printFileData exception", e);
            response.reset();
            response.setContentType("text/html;charset=utf-8");
            try {
                out = response.getOutputStream();
                out.write(errorInfo.getBytes());
                out.flush();
            } catch (Exception ex) {
                log.error("printFileData exception", ex);
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                log.error("printFileData close exception", e);
            }
        }
    }

    public static void printErrorInfo(HttpServletResponse response, String errorInfo) {
        response.setContentType("text/html;charset=utf-8");
        try {
            OutputStream out = response.getOutputStream();
            out.write(errorInfo.getBytes());
            out.flush();
        } catch (Exception ex) {
            log.error("printErrorInfo exception", ex);
        }
    }

    public static String getContentTypeByFileType(FileType fileType) throws Exception {
        String contentType = null;
        if (fileType == FileType.PDF) {
            contentType += "application/pdf";
        } else if (fileType == FileType.EXCEL) {
            contentType += "application/vnd.ms-excel";
        } else if (fileType == FileType.ZIP) {
            contentType += "application/zip";
        } else if (fileType == FileType.DOC) {
            contentType += "application/msword";
        } else if (fileType == FileType.HTML) {
            contentType += "text/html";
        } else if (fileType == FileType.JPG) {
            contentType += "image/jpg";
        } else if (fileType == FileType.PNG) {
            contentType += "image/png";
        } else if (fileType == FileType.JPEG) {
            contentType += "image/jpeg";
        } else {
            throw new Exception(
                    "getContentTypeByFileType the fileType is not supported; fileType is [" + fileType + "]");
        }

        return contentType;
    }

    public static void printData(HttpServletResponse response, String contentType, byte[] data) throws Exception {
        OutputStream out = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            response.setContentType(contentType);
            out = response.getOutputStream();

            byte[] b = new byte[8 * 1024];
            int n = 0;
            while ((n = bais.read(b)) != -1) {
                out.write(b, 0, n);
            }
            out.flush();
        } finally {
            out.close();
        }
    }
}
