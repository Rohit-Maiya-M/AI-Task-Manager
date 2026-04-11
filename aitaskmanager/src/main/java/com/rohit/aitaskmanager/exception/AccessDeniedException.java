package com.rohit.aitaskmanager.exception;

public class AccessDeniedException extends RuntimeException{
    public AccessDeniedException(String msg){
        super(msg);
    }
}
