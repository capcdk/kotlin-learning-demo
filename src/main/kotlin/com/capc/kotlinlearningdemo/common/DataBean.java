package com.capc.kotlinlearningdemo.common;

/**
 * @author chendk on 2021/8/9
 */
public class DataBean<T> {

    private String code;
    private String error;
    private T data;

    public static <T> DataBean<T> ok(T data) {
        DataBean<T> dataBean = new DataBean<>();
        dataBean.code = "200";
        dataBean.data = data;
        return dataBean;
    }

    public static <T> DataBean<T> fail(String error) {
        DataBean<T> dataBean = new DataBean<>();
        dataBean.code = "500";
        dataBean.error = error;
        return dataBean;
    }

    private DataBean() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "code='" + code + '\'' +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}
