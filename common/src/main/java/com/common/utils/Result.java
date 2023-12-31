package com.common.utils;

import com.alibaba.fastjson2.JSON;
import com.common.enums.ResultType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 封装统一响应结果
 *
 * @author youzhengjie
 * @date 2023-06-15 22:49:18
 */
@JsonInclude(JsonInclude.Include.NON_NULL) //为null的字段不进行序列化
public class Result<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应状态码对应的信息提示
     */
    private String msg;

    /**
     * 返回给前端的数据
     */
    private T data;

    public Result() {

    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg=msg;
        this.data = data;
    }

    /**
     * 构建ResponseResult对象
     *
     * @param code 代码
     * @param msg  味精
     * @param data 数据
     * @return {@link Result}<{@link D}>
     */
    public static<D> Result<D> build(Integer code, String msg, D data){

        return new Result<D>()
                .setCode(code)
                .setMsg(msg)
                .setData(data);
    }
    public static<D> Result<D> build(ResultType resultType){

        return new Result<D>()
                .setCode(resultType.getCode())
                .setMsg(resultType.getMessage())
                .setData(null);
    }
    public static<D> Result<D> build(ResultType resultType, D data){

        return new Result<D>()
                .setCode(resultType.getCode())
                .setMsg(resultType.getMessage())
                .setData(data);
    }
    public static<D> Result<D> ok(){

        return new Result<D>()
                .setCode(ResultType.SUCCESS.getCode())
                .setMsg(ResultType.SUCCESS.getMessage())
                .setData(null);
    }
    public static<D> Result<D> ok(D data){

        return new Result<D>()
                .setCode(ResultType.SUCCESS.getCode())
                .setMsg(ResultType.SUCCESS.getMessage())
                .setData(data);
    }

    public static<D> Result<D> ok(String msg, D data){

        return new Result<D>()
                .setCode(ResultType.SUCCESS.getCode())
                .setMsg(msg)
                .setData(data);
    }


    public static<D> Result<D> fail(D data){

        return new Result<D>()
                .setCode(ResultType.ERROR.getCode())
                .setMsg(ResultType.ERROR.getMessage())
                .setData(data);
    }

    public static<D> Result<D> fail(String msg, D data){

        return new Result<D>()
                .setCode(ResultType.ERROR.getCode())
                .setMsg(msg)
                .setData(data);
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 把当前对象序列化成JSON
     *
     * @return {@link String}
     */
    public String toJSONString(){
        return JSON.toJSONString(this);
    }

}
