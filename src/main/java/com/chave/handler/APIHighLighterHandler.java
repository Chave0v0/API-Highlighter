package com.chave.handler;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.APIConfig;
import com.chave.config.Color;
import com.chave.config.SensitiveInfoConfig;
import com.chave.pojo.APIItem;
import com.chave.service.SensitiveInfoMatchService;
import com.chave.utils.Util;
import java.util.HashMap;

public class APIHighLighterHandler implements HttpHandler {
    private Logging log;
    private SensitiveInfoMatchService sensitiveInfoMatchService;
    private HashMap<Integer, HttpRequest> messageIdList;

    public APIHighLighterHandler() {
        this.log = Main.API.logging();
        this.sensitiveInfoMatchService = new SensitiveInfoMatchService();
        this.messageIdList = new HashMap<>();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        try {
            HashMap apiMatchResult = Util.getAPIMatchResult(requestToBeSent);
            boolean isMatched = (boolean) apiMatchResult.get("isMatched");
            APIItem matchedItem = (APIItem) apiMatchResult.get("api");

            if (isMatched) {
                // 添加到arraylist中 为了检查对应response
                if (messageIdList.get(requestToBeSent.messageId()) == null) {
                    messageIdList.put(requestToBeSent.messageId(), requestToBeSent);
                }

                // 对匹配到的接口进行标记
                Util.setAPIFound(matchedItem.getPath(), requestToBeSent);

                // 匹配到进行高亮处理
                Util.setHighlightColor(requestToBeSent, Color.YELLOW);

                if (SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO) {
                    // 只对匹配到的接口进行敏感信息检查
                    HashMap sensitiveInfoMatchResult = sensitiveInfoMatchService.sensitiveInfoMatch(requestToBeSent);
                    if (!sensitiveInfoMatchResult.isEmpty()) {
                        // 对history进行红色高亮处理
                        Util.setHighlightColor(requestToBeSent, Color.ORANGE);

                        // 标记result 存在敏感信息
                        Util.setAPIResult(APIConfig.SENSITIVE_INFO_RESULT, matchedItem.getPath(), requestToBeSent);

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
                        HashMap apiMatchResult = Util.getAPIMatchResult(request);
                        APIItem matchedItem = (APIItem) apiMatchResult.get("api");

                        // 标记result 存在敏感信息
                        Util.setAPIResult(APIConfig.SENSITIVE_INFO_RESULT, matchedItem.getPath(), request);

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
