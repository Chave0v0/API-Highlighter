package com.chave.service;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.APIConfig;
import com.chave.config.UserConfig;
import com.chave.pojo.APIItem;
import com.chave.utils.Util;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIMatchService {
    private Logging log;


    public APIMatchService() {
        this.log = Main.API.logging();
    }

    // 精确匹配（只匹配path）
    // 不支持启用URL编码
    // 不支持检查完整数据包
    // 支持检查Method
    public HashMap<String, Object> exactMatch(HttpRequest request) {
        APIItem matchedItem = null;
        boolean isMatched = false;
        // 获取单纯的path
        String path = request.pathWithoutQuery();

        for (APIItem apiItem : APIConfig.TARGET_API) {

            // 如果测试状态是true(已测试状态)  则不再进行匹配
            if (apiItem.getState()) {
                continue;
            }

            // 处理没有 PathVariable 的情况
            if (isMatched = apiItem.getPath().equalsIgnoreCase(path)) {
                if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                    if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                        matchedItem = apiItem;
                        break;
                    }
                } else {
                    matchedItem = apiItem;
                    break;
                }
            } else {
                // 处理有 PathVariable 的情况
                Pattern pattern = Pattern.compile("^" + Util.convertPathToRegex(apiItem.getPath()) + "$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(path);
                if (isMatched = matcher.matches()) {
                    if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                        if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {
                            matchedItem = apiItem;
                            break;
                        }
                    } else {
                        matchedItem = apiItem;
                        break;
                    }
                }
            }
        }

        HashMap<String, Object> result = new HashMap();
        result.put("api", matchedItem);
        result.put("isMatched", isMatched);

        return result;
    }


    // 半模糊匹配（只匹配path）
    // 不支持启用URL编码
    // 不支持检查完整数据包
    // 支持检查Method
    public HashMap<String, Object> semiFuzzMatch(HttpRequest request) {
        boolean isMatched = false;
        Pattern pattern = null;
        APIItem matchedItem = null;

        // 获取单纯的path
        String path = request.pathWithoutQuery();

        for (APIItem apiItem : APIConfig.TARGET_API) {

            // 如果测试状态是true(已测试状态)  则不再进行匹配
            if (apiItem.getState()) {
                continue;
            }

            try {
                // 声明没有 PathVariable 情况下的正则
                pattern = Pattern.compile("^(/[^/]+)?" + apiItem.getPath() + "(/.*)?$", Pattern.CASE_INSENSITIVE);
            } catch (Exception e) {
                // 如果正则编译捕获异常,并且有"{",认为是有PathVariable的情况，重新生成正则。
                if (apiItem.getPath().contains("{")) {
                    pattern = Pattern.compile("^(/[^/]+)?" + Util.convertPathToRegex(apiItem.getPath()) + "(/.*)?$", Pattern.CASE_INSENSITIVE);
                } else {
                    // 其他情况认为是预期外的异常，直接输出log
                    log.logToError("半精确匹配出现异常" + e.getCause());
                }
            }

            Matcher matcher = pattern.matcher(path);
            if (isMatched = matcher.matches()) {
                if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                    if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                        matchedItem = apiItem;
                        break;
                    }
                } else {
                    matchedItem = apiItem;
                    break;
                }
            }
        }


        HashMap<String, Object> result = new HashMap();
        result.put("api", matchedItem);
        result.put("isMatched", isMatched);

        return result;
    }

    // 模糊匹配（默认匹配path+参数）
    // 支持启用URL编码
    // 支持检查完整数据包
    // 支持检查Method
    public HashMap<String, Object> fuzzMatch(HttpRequest request) {
        boolean isMatched = false;
        Pattern pattern = null;
        APIItem matchedItem = null;

        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 每次循环先赋值 便于找到匹配到的item位置

            // 如果测试状态是true(已测试状态)  则不再进行匹配
            if (apiItem.getState()) {
                continue;
            }

            if (UserConfig.IS_ANALYZE_PATHVARIABLE) {
                pattern = Pattern.compile(".*" + Util.convertPathToRegex(apiItem.getPath()) + ".*", Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(".*" + Pattern.quote(apiItem.getPath()) + ".*", Pattern.CASE_INSENSITIVE);
            }


            if (UserConfig.IS_CHECK_ENTIRE_REQUEST) {
                // 检查完整数据包匹配逻辑
                String requestData = request.toString();

                // 如果开启url编码 先对request进行解码
                if (UserConfig.IS_URL_DECODE) {
                    requestData = Util.urlDecode(requestData);
                }

                // 去除换行 否则正则匹配异常
                requestData = requestData.replaceAll("\r\n", "");

                Matcher matcher = pattern.matcher(requestData);

                if (isMatched = matcher.matches()) {
                    // 如果需要匹配method 则再次匹配method
                    if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                        if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                            matchedItem = apiItem;
                            break;
                        }
                    } else {
                        matchedItem = apiItem;
                        break;
                    }
                }
            } else {
                // 默认情况下匹配path+参数  模糊匹配包含匹配参数 无需处理
                String path = request.path();

                if (UserConfig.IS_URL_DECODE) {
                    // 处理开启url编码情况  先对path进行url解码
                    path = Util.urlDecode(path);
                }

                Matcher matcher = pattern.matcher(path);

                if (isMatched = matcher.matches()) {
                    if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                        // 如果需要检查method 并且apiItem的method不为空
                        if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                            matchedItem = apiItem;
                            break;
                        }
                    } else {
                        matchedItem = apiItem;
                        break;
                    }
                }
            }
        }

        HashMap<String, Object> result = new HashMap();
        result.put("api", matchedItem);
        result.put("isMatched", isMatched);
        return result;
    }
}
