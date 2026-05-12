package cn.xm.xy.common.result;

import lombok.Data;
import java.io.Serializable;

@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    public R() {}

    public static <T> R<T> ok() {
        return restResult(null, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> R<T> failed() {
        return restResult(null, ResultCode.FAIL.getCode(), ResultCode.FAIL.getMsg());
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, ResultCode.FAIL.getCode(), msg);
    }

    public static <T> R<T> failed(ResultCode resultCode) {
        return restResult(null, resultCode.getCode(), resultCode.getMsg());
    }

    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
