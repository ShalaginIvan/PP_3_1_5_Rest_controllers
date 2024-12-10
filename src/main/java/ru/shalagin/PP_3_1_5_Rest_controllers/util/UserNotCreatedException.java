package ru.shalagin.PP_3_1_5_Rest_controllers.util;

public class UserNotCreatedException extends RuntimeException{
    public UserNotCreatedException(String msg) {
        super(msg);
    }
}
