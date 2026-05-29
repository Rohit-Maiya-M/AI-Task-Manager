package com.rohit.aitaskmanager.exception;

public class UserNotInGroupException extends RuntimeException{
    public UserNotInGroupException(String msg){
        super(msg);
    }
}
