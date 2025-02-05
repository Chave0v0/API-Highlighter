package com.chave.handler;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.Color;
import com.chave.config.SensitiveInfoConfig;
import com.chave.config.UserConfig;
import com.chave.pojo.APIItem;
import com.chave.service.APIMatchService;
import com.chave.service.SensitiveInfoMatchService;
import com.chave.utils.Util;
import java.lang.reflect.Method;
import java.util.HashMap;

public class APIHighLighterHandler implements HttpHandler {
    private Logging log;
    private APIMatchService apiMatchService;
    private SensitiveInfoMatchService sensitiveInfoMatchService;
    private HashMap<Integer, HttpRequest> messageIdList;

    public APIHighLighterHandler() {
        this.log = Main.API.logging();
        this.apiMatchService = new APIMatchService();
        this.sensitiveInfoMatchService = new SensitiveInfoMatchService();
        this.messageIdList = new HashMap<>();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        try {
            Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequest.class);
            HashMap apiMatchResult = (HashMap) matchMethod.invoke(apiMatchService, requestToBeSent);
            boolean isMatched = (boolean) apiMatchResult.get("isMatched");
            APIItem matchedItem = (APIItem) apiMatchResult.get("api");

            if (isMatched) {
                // 添加到arraylist中 为了检查对应response
                if (messageIdList.get(requestToBeSent.messageId()) == null) {
                    messageIdList.put(requestToBeSent.messageId(), requestToBeSent);
                }

                // 对匹配到的接口进行标记
                matchedItem.setIsFound("find");

                // 匹配到进行高亮处理
                Util.setHighlightColor(requestToBeSent, Color.YELLOW);

                if (SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO) {
                    // 只对匹配到的接口进行敏感信息检查
                    HashMap sensitiveInfoMatchResult = sensitiveInfoMatchService.sensitiveInfoMatch(requestToBeSent);
                    if (!sensitiveInfoMatchResult.isEmpty()) {
                        // 对history进行红色高亮处理
                        Util.setHighlightColor(requestToBeSent, Color.ORANGE);

                        if (matchedItem.getResult() != null && !matchedItem.getResult().contains("敏感信息")) {
                            matchedItem.setResult(matchedItem.getResult() + "/存在敏感信息");
                        } else {
                            matchedItem.setResult("存在敏感信息");
                        }

                    }
                }


                // 刷新列表
                Util.flushAPIList(Main.UI.getHighlighterMainUI().getApiTable());
            }

        } catch (Exception e) {
            log.logToError("request handler异常");
        }
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        try {
            if (SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO) {
                // 查询当前response对应的request是否被匹配
                HttpRequest request = messageIdList.get(responseReceived.messageId());
                if (request != null) {
                    HashMap sensitiveInfoMatch = sensitiveInfoMatchService.sensitiveInfoMatch(responseReceived);

                    if (!sensitiveInfoMatch.isEmpty()) {
                        // 对history进行红色高亮处理
                        Util.setHighlightColor(responseReceived, Color.ORANGE);

                        // 重新匹配一次 找到对应的apiItem
                        Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequest.class);
                        HashMap apiMatchResult = (HashMap) matchMethod.invoke(apiMatchService, request);
                        APIItem matchedItem = (APIItem) apiMatchResult.get("api");

                        // 标记result
                        if (matchedItem.getResult() != null && !matchedItem.getResult().contains("存在敏感信息")) {
                            matchedItem.setResult(matchedItem.getResult() + "/存在敏感信息");
                        } else {
                            matchedItem.setResult("存在敏感信息");
                        }

                        // 刷新列表
                        Util.flushAPIList(Main.UI.getHighlighterMainUI().getApiTable());

                    }
                }
            }
        } catch (Exception e) {
            log.logToError("response handler异常");
        }

        return null;
    }
}
