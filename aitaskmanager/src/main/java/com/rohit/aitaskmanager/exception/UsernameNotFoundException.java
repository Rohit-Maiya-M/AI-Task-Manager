package com.rohit.aitaskmanager.exception;

public class UsernameNotFoundException extends RuntimeException{
    public UsernameNotFoundException(String msg){
        super(msg);
    }
}
