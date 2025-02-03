package com.chave.ui;

import burp.api.montoya.logging.Logging;
import com.chave.Main;
import lombok.Getter;
import javax.swing.*;

@Getter
public class MainUI {
    private JTabbedPane mainTabbedPane = new JTabbedPane();
    private Logging log;
    private HighlighterMainUI highlighterMainUI;
    private SensitiveInfoMainUI sensitiveInfoMainUI;

    public MainUI() {
        this.log = Main.API.logging();
        initUI();
    }

    private void initUI() {
        // 创建各个组件ui实例
        highlighterMainUI = new HighlighterMainUI();
        sensitiveInfoMainUI = new SensitiveInfoMainUI();

        mainTabbedPane.add("Highlighter", highlighterMainUI.getHighlighterPanel());  // 添加高亮功能ui
        mainTabbedPane.add("Sensitive Info", sensitiveInfoMainUI.getSensitiveInfoPanel());  // 添加敏感信息检查功能ui
    }
}
