package org.kurento.tutorial.one2manycall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Result<T> {
  private int code;
  private T data;
  private String msg = "ok";

  private Result() {
  }

  public Result(int code, T data, String msg) {
    this.code = code;
    this.data = data;
    this.msg = msg;
  }
  public static <E> Result<E> result(int code, E data, String msg) {
    return new Result(code, data, msg);
  }

  public static <E> Result<E> success(E data) {
    return new Result(0, data, "ok");
  }

  public static Result<Boolean> success() {
    return new Result(0, true, "ok");
  }

  public static <E> Result<E> success(E data, String msg) {
    return new Result(0, data, msg);
  }

  public static <E> Result<E> fail(int code, String msg) {
    return new Result(code, (Object)null, msg != null && !msg.isEmpty() ? msg : "系统繁忙，请稍候再试");
  }

  public static <E> Result<E> fail(String msg) {
    return fail(-10, msg);
  }

  public static <E> Result<E> fail(Throwable throwable) {
    return fail(-1, throwable != null ? throwable.getMessage() : "系统繁忙，请稍候再试");
  }

  public static <E> Result<E> validFail(String msg) {
    return new Result(-9, (Object)null, msg != null && !msg.isEmpty() ? msg : "系统繁忙，请稍候再试");
  }


  public static <E> Result<E> timeout() {
    return fail(-2, "请求超时，请稍候再试");
  }

  public int getCode() {
    return this.code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public T getData() {
    return this.data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getMsg() {
    return this.msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @JsonIgnore
  public boolean isSuccess() {
    return this.code == 0 || this.code == 200;
  }

  public Boolean getIsSuccess() {
    return this.isSuccess();
  }
}
