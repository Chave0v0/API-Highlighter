package com.chave.config;

import com.chave.pojo.APIItem;

import java.util.ArrayList;
import java.util.HashMap;

public class APIConfig {
    public static HashMap<String, APIItem> TARGET_API = new HashMap<>();
    public static ArrayList<String> USER_INPUT_API = new ArrayList<>();

    // 初始化一些测试数据
    static {
        TARGET_API.put("/test", new APIItem("GET", "/test"));
    }
}
