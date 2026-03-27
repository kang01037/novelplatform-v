package org.example.novelplatformv.util;

import org.springframework.http.HttpStatus;

public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;

    public ResponseMessage(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //接口请求成功
    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<T>(HttpStatus.OK.value(), "success", data);
    }

    //接口请求失败
    public static <T> ResponseMessage<T> error(String message) {
        return new ResponseMessage<T>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
