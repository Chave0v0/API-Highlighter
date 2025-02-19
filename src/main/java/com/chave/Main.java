package com.chave;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.chave.config.SensitiveInfoConfig;
import com.chave.editor.RequestEditor;
import com.chave.editor.ResponseEditor;
import com.chave.handler.APIHighLighterHandler;
import com.chave.ui.MainUI;
import java.io.*;

public class Main implements BurpExtension {

    public static MontoyaApi API;
    public static MainUI UI;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        API = montoyaApi;
        Logging log = API.logging();

        API.extension().setName("API Highlighter");
        log.logToOutput("API Highlighter v2.1.2\n\n" +
                "Rebuild: Chave\n" +
                "GitHub: https://github.com/Chave0v0/API-Highlighter\n");

        // 初始化ui
        UI = new MainUI();
        API.userInterface().registerSuiteTab("API Highlighter", UI.getMainTabbedPane());
        API.http().registerHttpHandler(new APIHighLighterHandler());
        // 添加敏感信息展示Tab
        API.userInterface().registerHttpRequestEditorProvider(new RequestEditor());
        API.userInterface().registerHttpResponseEditorProvider(new ResponseEditor());

        // 检查存放配置文件的目录是否存在，若不存在则创建目录
        File rule_dir = new File(SensitiveInfoConfig.RULE_CONFIG_DIR);
        if (!rule_dir.exists()) {
            log.logToOutput("配置文件目录不存在, 即将自动创建...");
            boolean created = rule_dir.mkdirs();  // 创建目录及其父目录
            if (created) {
                log.logToOutput("[+] 配置文件目录创建成功: " + rule_dir.getAbsolutePath());
            } else {
                log.logToOutput("[-] 配置文件目录创建失败.");
            }
        } else {
            log.logToOutput("[+] 配置文件目录已存在, 无需创建.");
        }
    }
}
