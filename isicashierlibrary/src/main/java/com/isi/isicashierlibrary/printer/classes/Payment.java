package com.isi.isicashierlibrary.printer.classes;

public class Payment {
    private String code;
    private String subcode;
    private String description;

    public Payment(String code, String subcode, String description) {
        this.code = code;
        this.description = description;
        this.subcode = subcode;
    }

    public String getSubcode() {
        return this.subcode;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }
}
