package com.chave.ui;

import burp.api.montoya.MontoyaApi;
import lombok.Data;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

@Data
public class MainUI {
    private JPanel mainPanel;

    public MainUI(MontoyaApi api) {
        initUI();
    }

    private void initUI() {
        // 定义各种组件
        JPanel userInputPanel;
        JPanel userConfigPanel;
        JPanel apiTablePanel;
        JPanel userOperationPanel;
        JPanel apiSearchPanel;
        JButton inputAPIButton;
        JButton deleteAPIButton;
        JButton changeStateButton;
        JButton changeVulnTypeButton;
        JButton moveToTopButton;
        JButton addToScopeButton;
        JButton checkHistoryButton;
        JButton apiSearchButton;
        JComboBox<String> matchModComboBox;
        JCheckBox urlencodeCheckBox;
        JTable apiTalbe;
        JTextField apiSearchTextField;



        // 创建主面板并设置 BoxLayout 垂直排列
        mainPanel = new JPanel();
        BoxLayout mainLayOut = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(mainLayOut);



        // 创建api导入面板
        userInputPanel = new JPanel();
        BoxLayout userInputLayOut = new BoxLayout(userInputPanel, BoxLayout.Y_AXIS);
        userInputPanel.setLayout(userInputLayOut);
        userInputPanel.setMaximumSize(new Dimension(2000, 400));
        userInputPanel.setMaximumSize(new Dimension(2000, 400));
        // 创建 JTextArea
        JTextArea userInputTextArea = new JTextArea();
        TitledBorder titledBorder = BorderFactory.createTitledBorder("批量导入API");
        userInputTextArea.setBorder(BorderFactory.createCompoundBorder(userInputTextArea.getBorder(), titledBorder));
        JScrollPane scrollPane = new JScrollPane(userInputTextArea);
        // 添加文本框
        userInputPanel.add(scrollPane);



        // 设置用户配置面板
        userConfigPanel = new JPanel();
        BoxLayout userConfigLayOut = new BoxLayout(userConfigPanel, BoxLayout.X_AXIS);
        userConfigPanel.setLayout(userConfigLayOut);
        userConfigPanel.setMaximumSize(new Dimension(1000, 24));
        userConfigPanel.setMinimumSize(new Dimension(1000, 24));
        userConfigPanel.setPreferredSize(new Dimension(1000, 24));
        // 创建导入API按钮
        inputAPIButton = new JButton("导入API");
        inputAPIButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputAPIButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        // 创建匹配模式下拉框
        String[] matchOptions = {"精确匹配", "半模糊匹配", "模糊匹配"};
        matchModComboBox = new JComboBox<>(matchOptions);
        matchModComboBox.setMaximumSize(new Dimension(100, 20));
        matchModComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        matchModComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        // 创建复选框启用URL编码
        urlencodeCheckBox = new JCheckBox("启用URL编码");
        // 创建检查History按钮
        checkHistoryButton = new JButton("检查history");
        // 向用户配置面板添加元素
        userConfigPanel.add(Box.createHorizontalGlue());
        userConfigPanel.add(inputAPIButton);
        userConfigPanel.add(Box.createHorizontalStrut(20));
        userConfigPanel.add(matchModComboBox);
        userConfigPanel.add(Box.createHorizontalStrut(20));
        userConfigPanel.add(urlencodeCheckBox);
        userConfigPanel.add(Box.createHorizontalStrut(20));
        userConfigPanel.add(checkHistoryButton);
        userConfigPanel.add(Box.createHorizontalGlue());

        // 创建表格面板
        apiTablePanel = new JPanel();
        // 创建api列表
        String[] columnName = {"Method", "Path", "Result", "State", "Note", "Domain"};
        DefaultTableModel model = new DefaultTableModel(columnName, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        apiTalbe = new JTable(model);
        apiTalbe.setPreferredScrollableViewportSize(new Dimension(apiTablePanel.getWidth(), apiTablePanel.getHeight()));
        // 设置表头背景
        JTableHeader apiTableHeader = apiTalbe.getTableHeader();
        apiTableHeader.setBackground(new Color(215, 215, 215));
        // 创建表格滚动面板
        JScrollPane apiTableScrollPane = new JScrollPane(apiTalbe);
        apiTableScrollPane.setMaximumSize(new Dimension(2000, 850));



        // 创建用户操作面板
        userOperationPanel = new JPanel();
        userOperationPanel.setMaximumSize(new Dimension(2000, 74));
        // 创建用户操作按钮
        deleteAPIButton = new JButton("删除API");
        deleteAPIButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteAPIButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        deleteAPIButton.setPreferredSize(new Dimension(200,70));
        changeStateButton = new JButton("切换测试状态");
        changeStateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeStateButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        changeStateButton.setPreferredSize(new Dimension(200,70));
        changeVulnTypeButton = new JButton("切换漏洞类型");
        changeVulnTypeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeVulnTypeButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        changeVulnTypeButton.setPreferredSize(new Dimension(200,70));
        moveToTopButton = new JButton("将已测试的接口移至顶端");
        moveToTopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        moveToTopButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        moveToTopButton.setPreferredSize(new Dimension(200,70));
        addToScopeButton = new JButton("添加到Scope");
        addToScopeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToScopeButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        addToScopeButton.setPreferredSize(new Dimension(200,70));
        // 向操作面板中添加按钮
        userOperationPanel.add(deleteAPIButton);
        userOperationPanel.add(changeStateButton);
        userOperationPanel.add(changeVulnTypeButton);
        userOperationPanel.add(moveToTopButton);
        userOperationPanel.add(addToScopeButton);



        // 创建查找api面板
        apiSearchPanel = new JPanel();
        BoxLayout apiSearchLayOut = new BoxLayout(apiSearchPanel, BoxLayout.X_AXIS);
        apiSearchPanel.setLayout(apiSearchLayOut);
        apiSearchPanel.setMaximumSize(new Dimension(2000, 24));
        // 创建搜索框
        apiSearchTextField = new JFormattedTextField();
        // 创建搜索按钮
        apiSearchButton = new JButton("查找API");
        // 将元素添加到面板
        apiSearchPanel.add(Box.createHorizontalStrut(5));
        apiSearchPanel.add(apiSearchTextField);
        apiSearchPanel.add(Box.createHorizontalStrut(5));
        apiSearchPanel.add(apiSearchButton);
        apiSearchPanel.add(Box.createHorizontalStrut(5));



        // 添加各个面板
        mainPanel.add(userInputPanel);  // 添加导入api面板
        mainPanel.add(userConfigPanel);  // 添加用户配置面板
        mainPanel.add(apiTableScrollPane);  // 添加api列表
        mainPanel.add(userOperationPanel);  // 添加api列表面板
        mainPanel.add(apiSearchPanel);  // 添加api查找面板

    }
}
