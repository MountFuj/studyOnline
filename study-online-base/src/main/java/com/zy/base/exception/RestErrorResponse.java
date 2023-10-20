package com.zy.base.exception;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/10/20 8:56
 */
public class RestErrorResponse {
    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
