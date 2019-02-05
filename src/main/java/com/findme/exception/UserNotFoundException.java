package com.findme.exception;

public class UserNotFoundException extends Exception {
    private String userId;

    public static UserNotFoundException createWith(String userId){
        return new UserNotFoundException(userId);
    }

    public UserNotFoundException(String userId){
        this.userId = userId;
    }

    @Override
    public String getMessage(){
        return "Page with ID: '" + userId + "' not found.";
    }
}
