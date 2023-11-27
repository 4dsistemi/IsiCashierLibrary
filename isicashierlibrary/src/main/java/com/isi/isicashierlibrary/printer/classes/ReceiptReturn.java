package com.isi.isicashierlibrary.printer.classes;

import com.isi.isicashierlibrary.printer.brand.ERROR_TYPE;

public class ReceiptReturn {
    private ERROR_TYPE error_type;
    private Integer closureNumber;
    private Integer documentNumber;

    public ReceiptReturn(ERROR_TYPE error_type, int closureNumber, int documentNumber) {
        this.error_type = error_type;
        this.closureNumber = closureNumber;
        this.documentNumber = documentNumber;
    }

    public ERROR_TYPE getError_type() {
        return this.error_type;
    }

    public void setError_type(ERROR_TYPE error_type) {
        this.error_type = error_type;
    }

    public Integer getClosureNumber() {
        return this.closureNumber;
    }

    public void setClosureNumber(Integer closureNumber) {
        this.closureNumber = closureNumber;
    }

    public Integer getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }
}

