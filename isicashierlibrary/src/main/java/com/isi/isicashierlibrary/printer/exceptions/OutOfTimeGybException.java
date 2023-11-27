package com.isi.isicashierlibrary.printer.exceptions;

public class OutOfTimeGybException extends Exception {

    public OutOfTimeGybException() {
        super("The Access_token is expired");
    }
}
