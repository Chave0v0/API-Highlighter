package com.chave.handler;

import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.Color;
import com.chave.config.UserConfig;
import com.chave.service.APIMatchService;
import com.chave.service.SensitiveInfoMatchService;
import com.chave.utils.Util;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class APIHighLighterHandler implements HttpHandler {
    private Logging log;
    private APIMatchService apiMatchService;
    private SensitiveInfoMatchService sensitiveInfoMatchService;
    private ArrayList<Integer> messageIdList;

    public APIHighLighterHandler() {
        this.log = Main.API.logging();
        this.apiMatchService = new APIMatchService();
        this.sensitiveInfoMatchService = new SensitiveInfoMatchService();
        this.messageIdList = new ArrayList<>();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        try {
            Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequest.class);
            if ((Boolean) matchMethod.invoke(apiMatchService, requestToBeSent)) {
                // 添加到arraylist中 为了检查对应response
                messageIdList.add(requestToBeSent.messageId());

                // 匹配到进行高亮处理
                Util.setHighlightColor(requestToBeSent, Color.YELLOW);

                // 只对匹配到的接口进行敏感信息检查
                HashMap result = sensitiveInfoMatchService.sensitiveInfoMatch(requestToBeSent);
                if (!result.isEmpty()) {
                    // 对history进行红色高亮处理
                    Util.setHighlightColor(requestToBeSent, Color.ORANGE);

                    if (APIMatchService.MATCHED_ITEM.getResult() != null && !APIMatchService.MATCHED_ITEM.getResult().contains("敏感信息")) {
                        APIMatchService.MATCHED_ITEM.setResult(APIMatchService.MATCHED_ITEM.getResult() + "/存在敏感信息");
                    } else {
                        APIMatchService.MATCHED_ITEM.setResult("存在敏感信息");
                    }

                    // 刷新列表
                    Util.flushAPIList(Main.UI.getHighlighterMainUI().getApiTable());

                }
            }

        } catch (Exception e) {
            log.logToError(e);
        }
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        try {
            if (messageIdList.contains(responseReceived.messageId())) {
                HashMap result = sensitiveInfoMatchService.sensitiveInfoMatch(responseReceived);
                if (!result.isEmpty()) {
                    // 对history进行红色高亮处理
                    Util.setHighlightColor(responseReceived, Color.ORANGE);

                    if (APIMatchService.MATCHED_ITEM.getResult() != null && !APIMatchService.MATCHED_ITEM.getResult().contains("存在敏感信息")) {
                        APIMatchService.MATCHED_ITEM.setResult(APIMatchService.MATCHED_ITEM.getResult() + "/存在敏感信息");
                    } else {
                        APIMatchService.MATCHED_ITEM.setResult("存在敏感信息");
                    }

                    // 刷新列表
                    Util.flushAPIList(Main.UI.getHighlighterMainUI().getApiTable());

                }
            }
        } catch (Exception e) {
            log.logToError(e);
        }

        return null;
    }
}
