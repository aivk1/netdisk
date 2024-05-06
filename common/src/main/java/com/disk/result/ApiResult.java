package com.disk.result;

import com.disk.constant.MessageConstant;
import com.disk.constant.ResultConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class ApiResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    private static final String CODE_KEY = "code";
    private static final String MSG_KEY = "msg";
    private static final String DATA_KEY = "data";
    public ApiResult(Integer code, String msg) {
        super.put(CODE_KEY, code);
        super.put(MSG_KEY, msg);
    }
    public ApiResult(Integer code, String msg, Object data) {
        super.put(CODE_KEY, code);
        super.put(MSG_KEY, msg);
        if(data != null) {
            super.put(DATA_KEY, data);
        }
    }
    public static ApiResult success() {
        return new ApiResult(ResultConstant.success, "success");
    }
    public static ApiResult success(Object data) {
        return new ApiResult(ResultConstant.success, "success", data);
    }
    public static ApiResult error(String msg) {
        return new ApiResult(ResultConstant.error, msg);
    }
}
