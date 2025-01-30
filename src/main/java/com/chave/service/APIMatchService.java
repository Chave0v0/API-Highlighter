package com.chave.service;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.APIConfig;
import com.chave.config.UserConfig;
import com.chave.pojo.APIItem;
import com.chave.utils.Util;
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
    public boolean exactMatch(HttpRequest request) {
        boolean isMatched = false;
        String path = request.path();
        // 当有get参数时 去除参数部分
        if (path.contains("?")) {
            path = path.split("\\?", 2)[0];
        }

        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 如果测试状态是true(已测试状态)  则不再进行匹配
            if (apiItem.getState()) {
                continue;
            }

            // 处理没有 PathVariable 的情况
            if (isMatched = apiItem.getPath().equalsIgnoreCase(path)) {
                if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                    if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                        break;
                    }
                } else {
                    break;
                }
            } else {
                // 处理有 PathVariable 的情况
                Pattern pattern = Pattern.compile("^" + Util.convertPathToRegex(apiItem.getPath()) + "$");
                Matcher matcher = pattern.matcher(path);
                if (isMatched = matcher.matches()) {
                    if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                        if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return isMatched;
    }


    // 半模糊匹配（只匹配path）
    // 不支持启用URL编码
    // 不支持检查完整数据包
    // 支持检查Method
    public boolean semiFuzzMatch(HttpRequest request) {
        boolean isMatched = false;
        Pattern pattern = null;
        String path = request.path();
        // 当有get参数时 去除参数部分
        if (path.contains("?")) {
            path = path.split("\\?", 2)[0];
        }
        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 如果测试状态是true(已测试状态)  则不再进行匹配
            if (apiItem.getState()) {
                continue;
            }

            try {
                // 声明没有 PathVariable 情况下的正则
                pattern = Pattern.compile("^(/[^/]+)?" + apiItem.getPath() + "(/.*)?$");
            } catch (Exception e) {
                // 如果正则编译捕获异常,并且有"{",认为是有PathVariable的情况，重新生成正则。
                if (apiItem.getPath().contains("{")) {
                    pattern = Pattern.compile("^(/[^/]+)?" + Util.convertPathToRegex(apiItem.getPath()) + "(/.*)?$");
                } else {
                    // 其他情况认为是预期外的异常，直接输出log
                    log.logToError(e);
                }
            }

            Matcher matcher = pattern.matcher(path);
            if (isMatched = matcher.matches()) {
                if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                    if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return isMatched;
    }

    // 模糊匹配（默认匹配path+参数）
    // 支持启用URL编码
    // 支持检查完整数据包
    // 支持检查Method
    public boolean fuzzMatch(HttpRequest request) {
        boolean isMatched = false;
        Pattern pattern = null;

        for (APIItem apiItem : APIConfig.TARGET_API) {
            // 如果测试状态是true(已测试状态)  则不再进行匹配
            if (apiItem.getState()) {
                continue;
            }

            if (UserConfig.IS_ANALYZE_PATHVARIABLE) {
                pattern = Pattern.compile(".*" + Util.convertPathToRegex(apiItem.getPath()) + ".*");
            } else {
                pattern = Pattern.compile(".*" + Pattern.quote(apiItem.getPath()) + ".*");
            }


            if (UserConfig.IS_CHECK_ENTIRE_REQUEST) {
                // 检查完整数据包匹配逻辑
                String requestData = request.toString();

                // 如果开启url编码 先对request进行解码
                if (UserConfig.IS_URL_ENCODE) {
                    requestData = Util.urlDecode(requestData);
                }

                // 去除换行 否则正则匹配异常
                requestData = requestData.replaceAll("\r\n", "");

                Matcher matcher = pattern.matcher(requestData);

                if (isMatched = matcher.matches()) {
                    // 如果需要匹配method 则再次匹配method
                    if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                        if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } else {
                // 默认情况下匹配path+参数  模糊匹配包含匹配参数 无需处理
                String path = request.path();

                if (UserConfig.IS_URL_ENCODE) {
                    // 处理开启url编码情况  先对path进行url解码
                    path = Util.urlDecode(path);
                }

                Matcher matcher = pattern.matcher(path);

                if (isMatched = matcher.matches()) {
                    if (UserConfig.IS_CHECK_HTTP_METHOD && apiItem.getMethod() != null) {
                        // 如果需要检查method 并且apiItem的method不为空
                        if (isMatched = (apiItem.getMethod().equalsIgnoreCase(request.method()))) {  // 比较http method
                            break;  // method匹配上直接break
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return isMatched;
    }
}
