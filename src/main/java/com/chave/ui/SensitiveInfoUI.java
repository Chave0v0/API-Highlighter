package com.chave.ui;

import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.SensitiveInfoConfig;
import com.chave.pojo.RuleItem;
import com.chave.utils.Util;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.EventObject;

@Getter
public class SensitiveInfoUI {
    private Logging log;

    // 创建相关组件
    private JPanel sensitiveInfoPanel;
    private JPanel userConfigPanel;
    private JPanel ruleListMainPanel;
    private JPanel userOperationPanel;
    private JButton addRuleButton;
    private JButton editRuleButton;
    private JButton removeRuleButton;
    private JButton changeRuleLoadStateButton;
    private JTextField userConfigTextField;
    private JCheckBox isCheckSensitiveInfoCheckBox;
    private JLabel ruleFileLabel;
    private JTable ruleTable;

    public SensitiveInfoUI() {
        this.log = Main.API.logging();
        initSensitiveInfoUI();
    }

    private void initSensitiveInfoUI() {
        // 创建主面板并设置 BoxLayout 垂直排列
        sensitiveInfoPanel = new JPanel();
        BoxLayout mainLayOut = new BoxLayout(sensitiveInfoPanel, BoxLayout.Y_AXIS);
        sensitiveInfoPanel.setLayout(mainLayOut);


        // 绘制用户配置界面
        userConfigPanel = new JPanel();
        BoxLayout userConfigLayout = new BoxLayout(userConfigPanel, BoxLayout.X_AXIS);
        userConfigPanel.setLayout(userConfigLayout);
        userConfigPanel.setMaximumSize(new Dimension(2000, 25));
        userConfigPanel.setMinimumSize(new Dimension(2000, 25));
        // 创建配置文件输入框
        userConfigTextField = new JTextField();
        userConfigTextField.setText(SensitiveInfoConfig.RULE_CONFIG_FILE);
        // 创建开启敏感信息检查复选框
        isCheckSensitiveInfoCheckBox = new JCheckBox("开启敏感信息检查");
        // 创建规则文件输入提示标签
        ruleFileLabel = new JLabel("规则配置文件:");
        // 将组件添加到面板
        userConfigPanel.add(Box.createHorizontalStrut(10));
        userConfigPanel.add(ruleFileLabel);
        userConfigPanel.add(Box.createHorizontalStrut(13));
        userConfigPanel.add(userConfigTextField);
        userConfigPanel.add(Box.createHorizontalStrut(20));
        userConfigPanel.add(isCheckSensitiveInfoCheckBox);
        userConfigPanel.add(Box.createHorizontalStrut(10));


        // 绘制规则展示、操作面板
        ruleListMainPanel = new JPanel();  // 主面板
        BoxLayout ruleListMainLayout = new BoxLayout(ruleListMainPanel, BoxLayout.X_AXIS);
        ruleListMainPanel.setLayout(ruleListMainLayout);
        userOperationPanel = new JPanel();  // 用户操作面板
        BoxLayout userOperationLayout = new BoxLayout(userOperationPanel, BoxLayout.Y_AXIS);
        userOperationPanel.setLayout(userOperationLayout);
        userOperationPanel.setMaximumSize(new Dimension(80, 10000));
        userOperationPanel.setMaximumSize(new Dimension(80, 10000));
        // 创建列表操作按钮
        addRuleButton = new JButton("add");
        addRuleButton.setMaximumSize(new Dimension(100, 28));
        addRuleButton.setMinimumSize(new Dimension(100, 28));
        editRuleButton = new JButton("edit");
        editRuleButton.setMaximumSize(new Dimension(100, 28));
        editRuleButton.setMinimumSize(new Dimension(100, 28));
        removeRuleButton = new JButton("remove");
        removeRuleButton.setMaximumSize(new Dimension(100, 28));
        removeRuleButton.setMinimumSize(new Dimension(100, 28));
        changeRuleLoadStateButton = new JButton("load/unload");
        changeRuleLoadStateButton.setMaximumSize(new Dimension(100, 28));
        changeRuleLoadStateButton.setMinimumSize(new Dimension(100, 28));
        // 将用户操作按钮添加到操作面板
        userOperationPanel.add(addRuleButton);
        userOperationPanel.add(Box.createVerticalStrut(20));
        userOperationPanel.add(editRuleButton);
        userOperationPanel.add(Box.createVerticalStrut(20));
        userOperationPanel.add(removeRuleButton);
        userOperationPanel.add(Box.createVerticalStrut(20));
        userOperationPanel.add(changeRuleLoadStateButton);


        // 创建规则展示表格
        String[] columnName = {"Loaded", "Name", "Regex", "Scope"};
        DefaultTableModel model = new DefaultTableModel(columnName, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        ruleTable = new JTable(model);
        ruleTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);  // 支持不连续的多行选择
        // 设置表头背景
        JTableHeader ruleTableHeader = ruleTable.getTableHeader();
        ruleTableHeader.setBackground(new Color(215, 215, 215));
        // 创建表格滚动面板
        JScrollPane ruleTableScrollPane = new JScrollPane(ruleTable);
        // 设置表格默认宽度
        ruleTable.getColumnModel().getColumn(0).setMaxWidth(100);
        ruleTable.getColumnModel().getColumn(0).setMinWidth(100);
        ruleTable.getColumnModel().getColumn(1).setMaxWidth(200);
        ruleTable.getColumnModel().getColumn(1).setMinWidth(200);
        ruleTable.getColumnModel().getColumn(3).setMaxWidth(150);
        ruleTable.getColumnModel().getColumn(3).setMinWidth(150);
        // 设置第一列（索引0）和第四列（索引3）数据居中对齐
        TableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        ((DefaultTableCellRenderer) centerRenderer).setHorizontalAlignment(SwingConstants.CENTER);
        ruleTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        ruleTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        // 禁止直接表格编辑
        DefaultCellEditor disabledEditor = new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean isCellEditable(EventObject anEvent) {
                return false;
            }
        };
        ruleTable.getColumnModel().getColumn(0).setCellEditor(disabledEditor);
        ruleTable.getColumnModel().getColumn(1).setCellEditor(disabledEditor);
        ruleTable.getColumnModel().getColumn(2).setCellEditor(disabledEditor);
        ruleTable.getColumnModel().getColumn(3).setCellEditor(disabledEditor);
        // 将各个组件添加到展示面板
        ruleListMainPanel.add(Box.createHorizontalStrut(10));
        ruleListMainPanel.add(userOperationPanel);
        ruleListMainPanel.add(Box.createHorizontalStrut(10));
        ruleListMainPanel.add(ruleTableScrollPane);
        ruleListMainPanel.add(Box.createHorizontalStrut(10));


        // 将各个组件添加到主面板
        sensitiveInfoPanel.add(Box.createVerticalStrut(20));
        sensitiveInfoPanel.add(userConfigPanel);
        sensitiveInfoPanel.add(Box.createVerticalStrut(20));
        sensitiveInfoPanel.add(ruleListMainPanel);
        sensitiveInfoPanel.add(Box.createVerticalStrut(10));



        isCheckSensitiveInfoCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO == Boolean.FALSE) {
                    try {
                        String filePath = userConfigTextField.getText().trim();
                        File ruleYamlFile = new File(filePath);
                        if (ruleYamlFile.exists()) {
                            Util.loadRuleYamlConfig(filePath);
                            SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO = Boolean.TRUE;
                            isCheckSensitiveInfoCheckBox.setSelected(true);
                        } else {
                            showCreateDefaultConfigDialog(isCheckSensitiveInfoCheckBox);
                        }

                        // 如果成功加载yaml配置 则展示规则列表
                        if (SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO) {
                            Util.flushRuleList(ruleTable);
                        }
                    } catch (Exception exception) {
                        isCheckSensitiveInfoCheckBox.setSelected(false);
                        log.logToError("Yaml配置文件加载异常.");
                    }

                } else if (e.getStateChange() == ItemEvent.DESELECTED && SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO == Boolean.TRUE) {
                    // 清空表格
                    model.setRowCount(0);
                    // 直接清空list
                    SensitiveInfoConfig.RULE_LIST = null;
                    // 关闭检查敏感信息
                    SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO = Boolean.FALSE;
                }
            }
        });


        changeRuleLoadStateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operateRuleDialog("changeState");
            }
        });


        addRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operateRuleDialog("add");
            }
        });

        editRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operateRuleDialog("edit");
            }
        });

        removeRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operateRuleDialog("remove");
            }
        });

    }

    // 添加规则
    private void operateRuleDialog(String operation) {
        try {
            // 创建弹窗ui需要组件
            JPanel operateRuleMainPanel = new JPanel();
            JPanel namePanel = new JPanel();
            JPanel regexPanel = new JPanel();
            JPanel scopePanel = new JPanel();
            JLabel nameLabel = new JLabel("Name: ");
            JLabel regexLabel = new JLabel("Regex: ");
            JLabel scopeLabel = new JLabel("Scope: ");
            JLabel removeTipLabel = new JLabel("是否删除所有选中规则?");
            JTextField nameTextField = new JTextField();
            JTextField regexTextField = new JTextField();
            String[] scopeOptions = {"any", "request", "response"};
            JComboBox scopeComboBox = new JComboBox(scopeOptions);

            // 调整组件大小
            nameTextField.setMinimumSize(new Dimension(300, 25));
            nameTextField.setMaximumSize(new Dimension(300, 25));
            regexTextField.setMinimumSize(new Dimension(300, 25));
            regexTextField.setMaximumSize(new Dimension(300, 25));
            scopeComboBox.setMaximumSize(new Dimension(300, 25));
            scopeComboBox.setMaximumSize(new Dimension(300, 25));

            // 设置布局
            BoxLayout addRuleMainLayout = new BoxLayout(operateRuleMainPanel, BoxLayout.Y_AXIS);
            BoxLayout nameLayout = new BoxLayout(namePanel, BoxLayout.X_AXIS);
            BoxLayout regexLayout =  new BoxLayout(regexPanel, BoxLayout.X_AXIS);
            BoxLayout scopeLayout = new BoxLayout(scopePanel, BoxLayout.X_AXIS);
            operateRuleMainPanel.setLayout(addRuleMainLayout);
            namePanel.setLayout(nameLayout);
            regexPanel.setLayout(regexLayout);
            scopePanel.setLayout(scopeLayout);

            // 添加各个组件
            namePanel.add(nameLabel);
            namePanel.add(Box.createHorizontalStrut(3));
            namePanel.add(nameTextField);
            regexPanel.add(regexLabel);
            regexPanel.add(regexTextField);
            scopePanel.add(scopeLabel);
            scopePanel.add(scopeComboBox);
            operateRuleMainPanel.add(namePanel);
            operateRuleMainPanel.add(Box.createVerticalStrut(10));
            operateRuleMainPanel.add(regexPanel);
            operateRuleMainPanel.add(Box.createVerticalStrut(10));
            operateRuleMainPanel.add(scopePanel);

            int option = 0;
            if (operation.equals("add")) {
                option = JOptionPane.showConfirmDialog(null, operateRuleMainPanel, "添加规则", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            } else if (operation.equals("edit")) {
                int[] selectedRows = ruleTable.getSelectedRows();
                if (!(selectedRows.length > 0)) {
                    return;  // 如果没选中 直接退出 防止异常
                }
                int selectedRow = selectedRows[0];

                // 如果是编辑 先展示当前数据
                nameTextField.setText(SensitiveInfoConfig.RULE_LIST.get(selectedRow).getName());
                regexTextField.setText(SensitiveInfoConfig.RULE_LIST.get(selectedRow).getRegex());
                scopeComboBox.setSelectedItem(SensitiveInfoConfig.RULE_LIST.get(selectedRow).getScope());

                option = JOptionPane.showConfirmDialog(null, operateRuleMainPanel, "编辑规则", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            } else if (operation.equals("remove")) {
                int[] selectedRows = ruleTable.getSelectedRows();
                if (!(selectedRows.length > 0)) {
                    return;  // 如果没选中 直接退出 防止异常
                }
                option = JOptionPane.showConfirmDialog(null, removeTipLabel, "删除规则", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            } else if (operation.equals("changeState")) {
                int[] selectedRows = ruleTable.getSelectedRows();
                if (!(selectedRows.length > 0)) {
                    return;  // 如果没选中 直接退出 防止异常
                }
            }


            // 只有选择"是" 进行操作  其他情况不做处理
            if (option == JOptionPane.YES_OPTION) {
                if (operation.equals("add")) {
                    RuleItem ruleItem = new RuleItem(nameTextField.getText(), Boolean.TRUE, regexTextField.getText(), (String) scopeComboBox.getSelectedItem());
                    SensitiveInfoConfig.RULE_LIST.add(ruleItem);
                } else if (operation.equals("edit")) {
                    int selectedRow = ruleTable.getSelectedRows()[0];  // 如果同时选择多行 只取第一个

                    // 修改rule_list中的值
                    RuleItem ruleItem = SensitiveInfoConfig.RULE_LIST.get(selectedRow);
                    ruleItem.setName(nameTextField.getText());
                    ruleItem.setRegex(regexTextField.getText());
                    ruleItem.setScope((String) scopeComboBox.getSelectedItem());
                } else if (operation.equals("remove")) {
                    int selectedRow = ruleTable.getSelectedRows()[0];  // 如果同时选择多行 只取第一个

                    // 删除规则
                    SensitiveInfoConfig.RULE_LIST.remove(selectedRow);
                } else if (operation.equals("changeState")) {
                    // 支持多行同时修改
                    int[] selectedRows = ruleTable.getSelectedRows();

                    for (int selectedRow : selectedRows) {
                        if (SensitiveInfoConfig.RULE_LIST.get(selectedRow).getLoaded() == Boolean.FALSE) {
                            SensitiveInfoConfig.RULE_LIST.get(selectedRow).setLoaded(Boolean.TRUE);
                        } else {
                            SensitiveInfoConfig.RULE_LIST.get(selectedRow).setLoaded(Boolean.FALSE);
                        }
                    }
                }

                // 刷新列表  直接导出新的数据覆盖原文件
                Util.flushRuleList(ruleTable);
                Util.exportToYaml();
            }
        } catch (Exception e) {
            log.logToError(e);
        }
    }



    private void showCreateDefaultConfigDialog(JCheckBox jCheckBox) throws IOException, ClassNotFoundException {
        JLabel jLabel = new JLabel("是否使用默认规则?");
        // 创建确认对话框
        int option = JOptionPane.showConfirmDialog(null, jLabel, "目标文件不存在", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        // 检测用户选择
        if (option == JOptionPane.YES_OPTION) {
            // 用户选择了 "是"
            ByteArrayInputStream bais = new ByteArrayInputStream(Util.base64ToByteCode(SensitiveInfoConfig.DEFAULT_RULE_LIST_DATA));
            ObjectInputStream ois = new ObjectInputStream(bais);
            SensitiveInfoConfig.RULE_LIST = (ArrayList) ois.readObject();

            // 切换敏感信息检查状态
            SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO = Boolean.TRUE;
            // 将默认规则导出到文件
            Util.exportToYaml();
            jCheckBox.setSelected(true);
        } else if (option == JOptionPane.NO_OPTION) {
            // 用户选择了 "否"，保持未勾选状态
            jCheckBox.setSelected(false);
        } else if (option == JOptionPane.CLOSED_OPTION) {
            // 用户直接关闭了对话框，保持未勾选状态
            jCheckBox.setSelected(false);
        }
    }

}


