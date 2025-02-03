package com.chave.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import lombok.Data;
import lombok.Getter;

import javax.swing.*;

@Getter
public class MainUI {
    private JTabbedPane mainTabbedPane = new JTabbedPane();
    private Logging log;
    private HighlighterUI highlighterUI;
    private SensitiveInfoUI sensitiveInfoUI;

    public MainUI() {
        this.log = Main.API.logging();
        initUI();
    }

    private void initUI() {
        // 创建各个组件ui实例
        highlighterUI = new HighlighterUI();
        sensitiveInfoUI = new SensitiveInfoUI();

        mainTabbedPane.add("Highlighter", highlighterUI.getHighlighterPanel());  // 添加高亮功能ui
        mainTabbedPane.add("Sensitive Info", sensitiveInfoUI.getSensitiveInfoPanel());  // 添加敏感信息检查功能ui
    }
}
