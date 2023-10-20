package com.zy.base.exception;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/20 8:55
 */
public class StudyOnlineException extends RuntimeException{
    private String errorMessage;

    public StudyOnlineException() {
        super();
    }

    public StudyOnlineException(String message) {
        super(message);
        this.errorMessage=message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    public static void cast(CommonError commonError){
        throw new StudyOnlineException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new StudyOnlineException(errMessage);
    }
}
