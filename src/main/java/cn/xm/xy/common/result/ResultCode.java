package cn.xm.xy.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 业务码 1xxx
    PARAM_MISSING(1001, "参数缺失"),
    PARAM_INVALID(1002, "参数格式错误"),
    SERVICE_UNAVAILABLE(1003, "远程服务不可用"),
    REMOTE_CALL_ERROR(1004, "远程调用失败"),
    TOKEN_INVALID(1005, "平台令牌无效或未获取");

    private final int code;
    private final String msg;
}
