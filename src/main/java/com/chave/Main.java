package com.chave;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.chave.http.HTTP;
import com.chave.ui.UIMain;

public class Main implements BurpExtension {

    public static MontoyaApi API;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        API = montoyaApi;
        Logging log = API.logging();

        API.extension().setName("API Highlighter");
        log.logToOutput("Hello World");

        // 初始化ui
        UIMain ui = new UIMain(montoyaApi);
        API.userInterface().registerSuiteTab("API Highlighter", ui.getRoot());
        API.http().registerHttpHandler(new HTTP(montoyaApi));
    }
}
