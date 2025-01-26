package com.chave.handler;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.logging.Logging;
import com.chave.config.UserConfig;
import com.chave.service.APIMatchService;

import java.lang.reflect.Method;

public class APIHighLighterHandler implements HttpHandler {
    private MontoyaApi api;
    private Logging log;
    private APIMatchService apiMatchService;

    public APIHighLighterHandler(MontoyaApi api) {
        this.api = api;
        this.log = api.logging();
        this.apiMatchService = new APIMatchService();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        try {
            Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequestToBeSent.class);
            if ((Boolean) matchMethod.invoke(apiMatchService, requestToBeSent)) {
                // 处理匹配到的逻辑
                log.logToOutput("匹配到接口：" + requestToBeSent.path());
            }
        } catch (Exception e) {
            log.logToError(e);
        }
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }
}
