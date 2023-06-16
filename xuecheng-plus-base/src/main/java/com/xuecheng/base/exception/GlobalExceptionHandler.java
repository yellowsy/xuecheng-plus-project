package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description TODO
 */
@Slf4j
@ControllerAdvice//控制器增强
//@RestControllerAdvice
public class GlobalExceptionHandler {
    //对项目的自定义异常类型进行处理
   @ResponseBody
   @ExceptionHandler(XueChengPlusException.class)  //自定义异常类
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public RestErrorResponse customException(XueChengPlusException e){
        //记录异常
        log.error("系统异常{}",e.getErrMessage(),e);

        //解析出异常信息
        String errMessage = e.getErrMessage();
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
   }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class) //JRS303异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        //存储错误信息
        List<String> errors=new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item->{
            errors.add(item.getDefaultMessage());
        });
        //将异常信息拼接为字符串
        String errMassage = StringUtils.join(errors, ",");
        //记录异常
        log.error("系统异常{}",e.getMessage(),errMassage);

        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMassage);
        return restErrorResponse;
    }

   @ResponseBody
   @ExceptionHandler(Exception.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public RestErrorResponse exception(Exception e){
        //记录异常
        log.error("系统异常{}",e.getMessage(),e);

        //解析出异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
   }
}
