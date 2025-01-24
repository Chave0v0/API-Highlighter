package com.chave.service;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import com.chave.config.APIConfig;

public class APIMatchService {
    public boolean exactMatch(HttpRequestToBeSent requestToBeSent) {
        // 处理不涉及 PathVariable 的情况
        if (APIConfig.TARGET_API.get(requestToBeSent.path()) != null) {
            return true;
        }

        // 处理 PathVariable 的情况
        // ......

        return false;
    }


    // 半模糊匹配
    public boolean semiFuzzMatch(HttpRequestToBeSent requestToBeSent) {
        return false;
    }

    // 模糊匹配
    public boolean fuzzMatch(HttpRequestToBeSent requestToBeSent) {
        return false;
    }
}
