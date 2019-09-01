package com.study.server.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class RpcResponse {

    private String requestId;

    private Throwable error;

    private Object result;

    private void setJsonObject(Object object){
        result = JSONObject.toJSONString(object);
    }

    private Object parseJsonObject(){
       return JSONObject.toJSON(result);
    }

}
