package com.chave.handler;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.UserConfig;
import com.chave.service.APIMatchService;
import com.chave.utils.Util;

import java.lang.reflect.Method;

public class APIHighLighterHandler implements HttpHandler {
    private Logging log;
    private APIMatchService apiMatchService;

    public APIHighLighterHandler() {
        this.log = Main.API.logging();
        this.apiMatchService = new APIMatchService();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        try {
            Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequest.class);
            if ((Boolean) matchMethod.invoke(apiMatchService, requestToBeSent)) {
                // 处理匹配到的逻辑
                log.logToOutput("匹配到接口：" + requestToBeSent.path());

                // 匹配到进行高亮处理
                Util.setHighlightColor(requestToBeSent, com.chave.config.Color.YELLOW);


                // 后续这里可以添加HaE匹配规则

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
