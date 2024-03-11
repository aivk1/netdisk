package com.disk.result;

import com.disk.constant.ResultConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(){
        Result<T> result = new Result<T>();
        result.code = ResultConstant.success;
        return result;
    }
    public static <T> Result<T> success(T object){
        Result<T> result = new Result<T>();
        result.code = ResultConstant.success;
        result.data = object;
        return result;
    }
    public static <T> Result<T> error(T object){
        Result<T> result = new Result<T>();
        result.code = ResultConstant.error;
        result.data = object;
        return result;
    }


}
