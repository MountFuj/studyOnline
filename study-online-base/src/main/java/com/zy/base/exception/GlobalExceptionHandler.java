package com.zy.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description 异常统一处理
 * @date 2023/10/20 9:14
 */
@ControllerAdvice
@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(StudyOnlineException.class)
    public RestErrorResponse customException(StudyOnlineException e){
        String errorMessage = e.getErrorMessage();
        log.error("系统异常{}",e.getErrorMessage(),e);
        return new RestErrorResponse(errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {

        log.error("【系统异常】{}",e.getMessage(),e);

        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());

    }
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse doValidException(MethodArgumentNotValidException argumentNotValidException) {

        BindingResult bindingResult = argumentNotValidException.getBindingResult();
        StringBuffer errMsg = new StringBuffer();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        fieldErrors.forEach(error -> {
            errMsg.append(error.getDefaultMessage()).append(",");
        });
        log.error(errMsg.toString());
        return new RestErrorResponse(errMsg.toString());
    }
}
