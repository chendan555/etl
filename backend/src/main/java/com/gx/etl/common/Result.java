package com.gx.etl.common;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private int code;
    private T data;
    private String msg;
    private boolean success;

    public Result() {}

    public Result(int code, T data, String msg, boolean success) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = success;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, data, "success", true);
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, null, "success", true);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(-1, null, msg, false);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, null, msg, false);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    // 兼容前端读取 data.result
    public String getResult() { return msg; }
    public void setResult(String result) { this.msg = result; }
}
