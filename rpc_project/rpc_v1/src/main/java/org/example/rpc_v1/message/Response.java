package org.example.rpc_v1.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Response implements Serializable {
    private int code;
    private String message;
    private Object data;

    public static Response success(Object data){
        return Response.builder().code(200).message("调用成功").data(data).build();
    }

    public static Response fail(String message){
        return Response.builder().code(500).message(message).build();
    }

}
