package com.chave.service;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.SensitiveInfoConfig;
import com.chave.pojo.RuleItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveInfoMatchService {
    private Logging log;
    private HashMap<String, ArrayList> requestResult;
    private HashMap<String, ArrayList> responseResult;

    public SensitiveInfoMatchService() {
        this.log = Main.API.logging();
    }

    public HashMap sensitiveInfoMatch(HttpRequest request) {
        try {
            // 每次调用初始化一个result
            requestResult = new HashMap<>();

            for (RuleItem ruleItem : SensitiveInfoConfig.RULE_LIST) {
                // 如果规则未启用 直接跳过
                if (!ruleItem.getLoaded()) {
                    continue;
                }

                if (ruleItem.getScope().equals("response")) {
                    // 如果规则只检查response 在request这里直接跳过
                    continue;
                }

                ArrayList<String> matchedData = new ArrayList<>();

                // 由于是敏感信息检查 替换掉\r\n方便匹配
                String requestData = request.toString().replaceAll("\r\n", ",");

                Pattern pattern = Pattern.compile(ruleItem.getRegex());
                Matcher matcher = pattern.matcher(requestData);

                while (matcher.find()) {
                    matchedData.add(matcher.group());
                }

                // 匹配到了才往result中加数据
                if (matchedData.size() > 0) {
                    requestResult.put(ruleItem.getName(), matchedData);
                }
            }
        } catch (NullPointerException nullPointerException) {
            // 空指针会在未开启敏感信息检查时出现 预期内异常 暂不做处理
        }


        return requestResult;
    }


    public HashMap sensitiveInfoMatch(HttpResponse response) {
        try {
            // 每次调用初始化一个result
            responseResult = new HashMap<>();

            for (RuleItem ruleItem : SensitiveInfoConfig.RULE_LIST) {
                // 如果规则未启用 直接跳过
                if (!ruleItem.getLoaded()) {
                    continue;
                }

                if (ruleItem.getScope().equals("request")) {
                    // 如果规则只检查request 在response这里直接跳过
                    continue;
                }

                ArrayList<String> matchedData = new ArrayList<>();

                // 由于是敏感信息检查 替换掉\r\n方便匹配
                String responseData = response.toString().replaceAll("\r\n", ",");

                Pattern pattern = Pattern.compile(ruleItem.getRegex());
                Matcher matcher = pattern.matcher(responseData);

                while (matcher.find()) {
                    matchedData.add(matcher.group());
                }

                // 匹配到了才往result中加数据
                if (matchedData.size() > 0) {
                    responseResult.put(ruleItem.getName(), matchedData);
                }
            }
        } catch (NullPointerException nullPointerException) {
            // 空指针会在未开启敏感信息检查时出现 预期内异常 暂不做处理
        }

        return responseResult;
    }

}
