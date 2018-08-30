package com.campus.chatbuy.enums;

public enum FileType {

    PDF(1, "pdf"), DOC(2, "doc"), ZIP(3, "zip"), EXCEL(4, "xls"), HTML(5, "html"), PNG(6, "png"), JPG(7, "jpg"),
    JPEG(8, "jpeg");

    private int index;
    private String name;

    FileType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static FileType getFileTypeByStr(String type) {
        if ("pdf".equalsIgnoreCase(type)) {
            return PDF;
        }
        if ("doc".equalsIgnoreCase(type)) {
            return DOC;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return ZIP;
        }
        if ("xls".equalsIgnoreCase(type)) {
            return EXCEL;
        }
        if ("html".equalsIgnoreCase(type)) {
            return HTML;
        }
        if ("png".equalsIgnoreCase(type)) {
            return PNG;
        }
        if ("jpg".equalsIgnoreCase(type)) {
            return JPG;
        }
        if ("jpeg".equalsIgnoreCase(type)) {
            return JPEG;
        }
        return null;
    }

    @Override
    public String toString() {
        return "FileType{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}