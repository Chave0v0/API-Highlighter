package com.chave.service;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.logging.Logging;
import com.chave.config.APIConfig;
import com.chave.pojo.APIItem;
import com.chave.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIMatchService {
    private Logging log;

    public APIMatchService(MontoyaApi api) {
        this.log = api.logging();
    }

    // 精确匹配
    public boolean exactMatch(HttpRequestToBeSent requestToBeSent) {
        boolean isMatched = false;
        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 处理没有 PathVariable 的情况
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
        boolean isMatched = false;
        Pattern patternNoPathVariable = null;
        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 声明没有 PathVariable 情况下的正则
            try {
                patternNoPathVariable = Pattern.compile("^(/[^/]+)?" + apiItem.getPath() + "(/.*)?$");
            } catch (Exception e) {
                // 如果抛出异常,并且有"{",认为是有PathVariable的情况，重新生成正则。
                if (apiItem.getPath().contains("{")) {
                    patternNoPathVariable = Pattern.compile("^(/[^/]+)?" + Util.convertPathToRegex(apiItem.getPath()) + "(/.*)?$");
                } else {
                    log.logToError(e);
                }
            }

            Matcher matcherNoPathVariable = patternNoPathVariable.matcher(requestToBeSent.path());
            if (isMatched = matcherNoPathVariable.matches()) {
                break;
            }
        }

        return isMatched;
    }

    // 模糊匹配
    public boolean fuzzMatch(HttpRequestToBeSent requestToBeSent) {
        return false;
    }
}
