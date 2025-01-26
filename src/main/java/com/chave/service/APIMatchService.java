package com.chave.service;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import com.chave.config.APIConfig;
import com.chave.pojo.APIItem;
import com.chave.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIMatchService {
    // 精确匹配
    public boolean exactMatch(HttpRequestToBeSent requestToBeSent) {
        boolean isMatched = false;
        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 处理不没有 PathVariable 的情况
            if (apiItem.getPath() == requestToBeSent.path()) {
                isMatched = true;
            } else {
                // 处理有 PathVariable 的情况
                Pattern pattern = Pattern.compile("^" + Util.convertPathToRegex(apiItem.getPath()) + "$");
                Matcher matcher = pattern.matcher(requestToBeSent.path());
                if (isMatched = matcher.matches()) {
                    break;
                }
            }
        }

        return isMatched;
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
