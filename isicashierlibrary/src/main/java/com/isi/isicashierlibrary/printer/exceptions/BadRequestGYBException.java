package com.isi.isicashierlibrary.printer.exceptions;

public class BadRequestGYBException extends Exception {

    public BadRequestGYBException() {
        super("The Access_token is expired");
    }


}
