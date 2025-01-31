package com.chave;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.chave.handler.APIHighLighterHandler;
import com.chave.ui.MainUI;

public class Main implements BurpExtension {

    public static MontoyaApi API;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        API = montoyaApi;
        Logging log = API.logging();

        API.extension().setName("API Highlighter");
        log.logToOutput("API Highlighter v1.0.0\n\n" +
                "Rebuild: Chave\n" +
                "GitHub: https://github.com/Chave0v0/API-Highlighter");

        // 初始化ui
        MainUI ui = new MainUI(montoyaApi);
        API.userInterface().registerSuiteTab("API Highlighter", ui.getMainPanel());
        API.http().registerHttpHandler(new APIHighLighterHandler());
    }
}
