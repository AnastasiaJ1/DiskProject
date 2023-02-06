package com.example.disk1.exceptions;

public class InputFormatException extends Exception {
    String message;
    public InputFormatException(String str) {
        message = str;
    }
    public String toString() {
        return ("Exception Occurred : " + message);
    }
}
