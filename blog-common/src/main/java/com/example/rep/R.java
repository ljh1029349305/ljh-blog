package com.example.rep;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class R {
    private Integer code;
    private String msg;
    private Object data;



    public static R ok() {
        return new R().setCode(200).setMsg("success");
    }

    public static R ok(String msg) {
        return new R().setCode(200).setMsg(msg);
    }

    public static R error(String msg) {
        return new R().setCode(500).setMsg(msg);
    }
}