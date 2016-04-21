package ru.cdecl.pub.iota.exceptions;

public class InitializationException extends RuntimeException {

    public InitializationException() {
        super();
    }

    public InitializationException(Exception e) {
        super(e);
    }

}
