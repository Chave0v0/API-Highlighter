package com.chave.config;

import com.chave.pojo.APIItem;

import java.util.ArrayList;
import java.util.HashMap;

public class APIConfig {
    public static ArrayList<APIItem> TARGET_API = new ArrayList<>();
    public static ArrayList<String> USER_INPUT_API = new ArrayList<>();

    // 初始化一些测试数据
    static {
        TARGET_API.add(new APIItem("GET", "/test"));
        TARGET_API.add(new APIItem("GET", "/api/v1/{id}/info"));
        TARGET_API.add(new APIItem("GET", "/api/v1/{id}/product/{name}"));
    }
}
