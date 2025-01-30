package com.chave.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.internal.ObjectFactoryLocator;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import com.chave.Main;
import com.chave.config.APIConfig;
import com.chave.config.UserConfig;
import com.chave.pojo.APIItem;
import com.chave.pojo.MatchMod;
import com.chave.service.APIMatchService;
import com.chave.utils.Util;
import jdk.jshell.Snippet;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.lang.module.FindException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

@Data
public class MainUI {
    private JPanel mainPanel;
    private Logging log;

    public MainUI(MontoyaApi api) {
        this.log = api.logging();
        initUI();
    }

    private void initUI() {
        // 定义各种组件
        JPanel userOperationPanel;
        JPanel apiTablePanel;
        JPanel apiSearchPanel;
        JButton importAPIButton;
        JButton changeStateButton;
        JButton deleteItemButton;
        JButton checkHistoryButton;
        JButton apiSearchButton;
        JComboBox<String> matchModComboBox;
        JCheckBox urlencodeCheckBox;
        JCheckBox checkEntireReqCheckBox;
        JCheckBox checkMethodCheckBox;
        JTable apiTable;
        JTextField apiSearchTextField;


        // 创建主面板并设置 BoxLayout 垂直排列
        mainPanel = new JPanel();
        BoxLayout mainLayOut = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(mainLayOut);


        // 创建 JTextArea
        JTextArea userInputTextArea = new JTextArea();
        TitledBorder titledBorder = BorderFactory.createTitledBorder("批量导入API");
        userInputTextArea.setBorder(BorderFactory.createCompoundBorder(userInputTextArea.getBorder(), titledBorder));
        JScrollPane scrollPane = new JScrollPane(userInputTextArea);
        scrollPane.setMaximumSize(new Dimension(2000, 260));
        scrollPane.setMaximumSize(new Dimension(2000, 260));
        scrollPane.setPreferredSize(new Dimension(2000, 260));


        // 设置用户操作面板
        userOperationPanel = new JPanel();
        BoxLayout userConfigLayOut = new BoxLayout(userOperationPanel, BoxLayout.X_AXIS);
        userOperationPanel.setLayout(userConfigLayOut);
        userOperationPanel.setMaximumSize(new Dimension(1000, 24));
        userOperationPanel.setMinimumSize(new Dimension(1000, 24));
        userOperationPanel.setPreferredSize(new Dimension(1000, 24));
        // 创建导入API按钮
        importAPIButton = new JButton("导入API");
        importAPIButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        importAPIButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        // 创建匹配模式下拉框
        String[] matchOptions = {"精确匹配", "半模糊匹配", "模糊匹配"};
        matchModComboBox = new JComboBox<>(matchOptions);
        matchModComboBox.setMaximumSize(new Dimension(100, 20));
        matchModComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        matchModComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
        matchModComboBox.setSelectedIndex(0);  // 默认选择精确匹配
        // 创建复选框启用URL编码
        urlencodeCheckBox = new JCheckBox("启用URL编码");
        urlencodeCheckBox.setEnabled(false);  // 默认是精确匹配，初始化禁用
        // 创建复选框检查完整数据包
        checkEntireReqCheckBox = new JCheckBox("检查完整数据包");
        checkEntireReqCheckBox.setEnabled(false); // 默认是精确匹配，初始化禁用
        // 创建复选框检查Method
        checkMethodCheckBox = new JCheckBox("检查Method");
        // 创建检查History按钮
        checkHistoryButton = new JButton("检查history");
        // 创建切换漏洞状态按钮
        changeStateButton = new JButton("切换测试状态");
        // 创建切换漏洞状态按钮
        deleteItemButton = new JButton("删除API");
        // 向用户配置面板添加元素
        userOperationPanel.add(Box.createHorizontalGlue());
        userOperationPanel.add(importAPIButton);
        userOperationPanel.add(Box.createHorizontalStrut(20));
        userOperationPanel.add(matchModComboBox);
        userOperationPanel.add(Box.createHorizontalStrut(20));
        userOperationPanel.add(urlencodeCheckBox);
        userOperationPanel.add(Box.createHorizontalStrut(20));
        userOperationPanel.add(checkEntireReqCheckBox);
        userOperationPanel.add(Box.createHorizontalStrut(20));
        userOperationPanel.add(checkMethodCheckBox);
        userOperationPanel.add(Box.createHorizontalStrut(20));
        userOperationPanel.add(checkHistoryButton);
        userOperationPanel.add(Box.createHorizontalStrut(20));
        userOperationPanel.add(changeStateButton);
        userOperationPanel.add(Box.createHorizontalGlue());



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
        apiTable = new JTable(model);
        apiTable.setPreferredScrollableViewportSize(new Dimension(apiTablePanel.getWidth(), apiTablePanel.getHeight()));
        apiTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);  // 支持不连续的多行选择
        // 设置表头背景
        JTableHeader apiTableHeader = apiTable.getTableHeader();
        apiTableHeader.setBackground(new Color(215, 215, 215));
        // 创建表格滚动面板
        JScrollPane apiTableScrollPane = new JScrollPane(apiTable);
        apiTableScrollPane.setMaximumSize(new Dimension(2000, 850));



        // 创建查找api面板
        apiSearchPanel = new JPanel();
        BoxLayout apiSearchLayOut = new BoxLayout(apiSearchPanel, BoxLayout.X_AXIS);
        apiSearchPanel.setLayout(apiSearchLayOut);
        apiSearchPanel.setMaximumSize(new Dimension(2000, 25));
        apiSearchPanel.setMinimumSize(new Dimension(2000, 25));
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
        mainPanel.add(scrollPane);  // 添加导入api面板
        mainPanel.add(userOperationPanel);  // 添加用户配置面板
        mainPanel.add(apiTableScrollPane);  // 添加api列表
        mainPanel.add(Box.createVerticalStrut(2));  // 添加一定间距
//        mainPanel.add(userOperationPanel);  // 添加用户操作面板
        mainPanel.add(apiSearchPanel);  // 添加api查找面板


        // 导入API按钮监听器
        importAPIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = userInputTextArea.getText().trim();
                if (userInput.length() == 0) {
                    return;
                }
                String[] lines = userInput.split("\n");
                for (String line : lines) {
                    // 如果包含","则认为需要创建带有method的对象
                    if (line.contains(",")) {
                        String[] split = line.split(",", 2);
                        String method = split[0].trim();
                        String path = split[1].trim();
                        // 处理method为空 输入不合法的情况
                        if (method.length() == 0
                                || (!method.equalsIgnoreCase("GET")
                                && !method.equalsIgnoreCase("POST")
                                && !method.equalsIgnoreCase("PUT")
                                && !method.equalsIgnoreCase("DELETE")
                                && !method.equalsIgnoreCase("OPTIONS"))) {
                            method = null;
                        }
                        if (path.length() == 0) {
                            continue;
                        }


                        try {
                            APIItem item;
                            if (method == null) {
                                item = new APIItem(path.toLowerCase());
                            } else {
                                item = new APIItem(method.toUpperCase(), path.toLowerCase());
                            }
                            if (!Util.checkAPIItemExist(item)) {
                                APIConfig.TARGET_API.add(item);
                            }
                        } catch (Exception exception) {
                            log.logToError(exception);
                            continue;
                        }


                    } else {
                        if (line.trim().length() == 0) {
                            continue;
                        }

                        try {
                            APIItem item = new APIItem(line.trim().toLowerCase());
                            if (!Util.checkAPIItemExist(item)) {
                                APIConfig.TARGET_API.add(item);
                            }
                        } catch (Exception exception) {
                            log.logToError(exception);
                            continue;
                        }


                    }
                }

                try {
                    // 导入之后清空输入面板 清空当前列表刷新
                    userInputTextArea.setText("");
                    Util.flushAPIList(model);
                } catch (Exception exception) {
                    System.out.println(exception);
                }

            }
        });


        // 监听匹配模式监听器
        matchModComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户选择
                String selectedOption = (String) matchModComboBox.getSelectedItem();

                if ("精确匹配".equals(selectedOption)) {
                    UserConfig.MATCH_MOD = MatchMod.exactMatch;
                    // 不支持开启url编码
                    urlencodeCheckBox.setEnabled(false);
                    urlencodeCheckBox.setSelected(false);
                    // 不支持检查完整数据包
                    checkEntireReqCheckBox.setEnabled(false);
                    checkEntireReqCheckBox.setSelected(false);
                    // 每次切换匹配模式均初始化配置为false
                    UserConfig.IS_URL_ENCODE = Boolean.FALSE;
                    UserConfig.IS_CHECK_ENTIRE_REQUEST = Boolean.FALSE;
                    UserConfig.IS_CHECK_HTTP_METHOD = Boolean.FALSE;
                } else if ("半模糊匹配".equals(selectedOption)) {
                    UserConfig.MATCH_MOD = MatchMod.semiFuzzMatch;
                    // 不支持开启url编码
                    urlencodeCheckBox.setEnabled(false);
                    urlencodeCheckBox.setSelected(false);
                    // 不支持检查完整数据包
                    checkEntireReqCheckBox.setEnabled(false);
                    // 每次切换匹配模式均初始化配置为false
                    UserConfig.IS_URL_ENCODE = Boolean.FALSE;
                    UserConfig.IS_CHECK_ENTIRE_REQUEST = Boolean.FALSE;
                    UserConfig.IS_CHECK_HTTP_METHOD = Boolean.FALSE;
                } else if ("模糊匹配".equals(selectedOption)) {
                    UserConfig.MATCH_MOD = MatchMod.fuzzMatch;
                    // 不支持开启url编码
                    urlencodeCheckBox.setEnabled(true);
                    urlencodeCheckBox.setSelected(false);
                    // 不支持检查完整数据包
                    checkEntireReqCheckBox.setEnabled(true);
                    checkEntireReqCheckBox.setSelected(false);
                    // 每次切换匹配模式均初始化配置为false  是否解析路径参数除外
                    UserConfig.IS_URL_ENCODE = Boolean.FALSE;
                    UserConfig.IS_CHECK_ENTIRE_REQUEST = Boolean.FALSE;
                    UserConfig.IS_CHECK_HTTP_METHOD = Boolean.FALSE;

                    // 询问是否解析PathVariable
                    showPathVariableDialog();
                }
            }
        });


        // 监听列表修改  同步对象修改  同时刷新列表防止输入预期外字符导致列表与ArrayList不一致
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                try {
                    int eventType = e.getType();
                    int rowNumber = e.getLastRow();
                    int columnNumber = e.getColumn();
                    String value = (String) model.getValueAt(rowNumber, columnNumber);

                    // 同步修改ArrayList中的对象
                    if (eventType == TableModelEvent.UPDATE) {
                        APIItem item = APIConfig.TARGET_API.get(rowNumber);
                        Field field = APIItem.class.getDeclaredField((String) APIConfig.ITEM_FIELD.get(columnNumber));
                        field.setAccessible(true);

                        // 验证数据合法性  数据合法才进行修改
                        if (columnNumber == 0) {
                            if (value.equalsIgnoreCase("GET")
                                    || value.equalsIgnoreCase("POST")
                                    || value.equalsIgnoreCase("PUT")
                                    || value.equalsIgnoreCase("DELETE")
                                    || value.equalsIgnoreCase("OPTIONS")) {
                                field.set(item, value.toUpperCase());
                            } else if (value.equalsIgnoreCase("null")) {
                                field.set(item, null);
                            }
                        } else if (columnNumber == 3) {
                            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                field.set(item, Boolean.parseBoolean(value));
                            }
                        } else {
                            field.set(item, value);
                        }

                        // 修改之后刷新列表  防止数据不一致
                        Util.flushAPIList(model);

                    }
                } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    // 由于添加数据时也会触发该监听器，这里捕获添加多行数据时抛出的异常 不做处理
                } catch (Exception exception) {
                    // 捕获预期外的异常
                    log.logToError(exception);
                }

            }
        });


        // 添加url编码复选框监听器
        urlencodeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    UserConfig.IS_URL_ENCODE = Boolean.TRUE;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    UserConfig.IS_URL_ENCODE = Boolean.FALSE;
                }
            }
        });


        // 添加检查完整数据包监听器
        checkEntireReqCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    UserConfig.IS_CHECK_ENTIRE_REQUEST = Boolean.TRUE;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    UserConfig.IS_CHECK_ENTIRE_REQUEST = Boolean.FALSE;
                }
            }
        });

        // 添加检查method监听器
        checkMethodCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    UserConfig.IS_CHECK_HTTP_METHOD = Boolean.TRUE;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    UserConfig.IS_CHECK_HTTP_METHOD = Boolean.FALSE;
                }
            }
        });


        // 检查history监听器
        checkHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取匹配service用于检查
                APIMatchService apiMatchService = new APIMatchService();
                // 获取所有history
                List<ProxyHttpRequestResponse> historyList = Main.API.proxy().history();
                for (ProxyHttpRequestResponse proxyHttpRequestResponse : historyList) {
                    // 仅检查request
                    HttpRequest request = proxyHttpRequestResponse.request();
                    // 遍历检查
                    try {
                        Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequest.class);

                        // 匹配到进行高亮
                        if ((Boolean) matchMethod.invoke(apiMatchService, request)) {
                            // 匹配到进行高亮处理
                            Util.setHighlightColor(proxyHttpRequestResponse, com.chave.config.Color.YELLOW);

                            // 后续这里可以添加HaE规则

                        } else {
                            // 检查后如果没匹配到直接去除高亮
                            Util.setHighlightColor(proxyHttpRequestResponse, com.chave.config.Color.NONE);
                        }
                    } catch (Exception exception) {
                        log.logToError(exception);
                    }
                }
            }
        });


        // 添加表格键盘时间监听器  实现按退格键删除数据
        apiTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    // 支持多行同时删除
                    int[] selectedRows = apiTable.getSelectedRows();

                    // 如果有选中的行
                    if (selectedRows.length > 0) {
                        // 由于删除行时会影响索引，所以需要倒序删除
                        for (int i = selectedRows.length - 1; i >= 0; i--) {
                            int row = selectedRows[i];
                            // 删除选中的行
                            APIConfig.TARGET_API.remove(row);
                            model.removeRow(row);
                        }
                    }

                    // 修改之后刷新列表  防止数据不一致
                    Util.flushAPIList(model);
                }
            }
        });


        // 查找API监听器
        apiSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchAPI = apiSearchTextField.getText();
                // 先去除前后空格
                searchAPI = searchAPI.trim();
                // 先去除目前所有的选中状态
                apiTable.clearSelection();
                // 支持精确匹配

                if (searchAPI.length() != 0) {
                    if (searchAPI.contains("=")) {

                        // 精确匹配支持所有字段匹配
                        String[] split = searchAPI.split("=", 2);
                        String key = split[0].trim();
                        String value = split[1].trim();
                        int index = 0;

                        // 反射获取getter方法
                        String methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                        Method getterMethod = null;
                        try {
                            getterMethod = APIItem.class.getMethod(methodName, null);
                        } catch (Exception exception) {
                            log.logToError("查询key输入错误");
                            try {
                                // 当输入查询key错误时，默认查询path
                                getterMethod = APIItem.class.getMethod("getPath");
                            } catch (NoSuchMethodException noSuchMethodException) {
                                // 这里不会有异常 方法一定存在
                            }
                        }

                        for (APIItem apiItem : APIConfig.TARGET_API) {
                            try {
                                Object o = getterMethod.invoke(apiItem, null);
                                if (o.toString().equalsIgnoreCase(value)) {
                                    apiTable.addRowSelectionInterval(index, index);
                                    apiTable.requestFocusInWindow();  // 立即刷新状态 便于删除
                                }
                                index++;
                            } catch (NullPointerException nullPointerException) {
                                // 空指针为预期内异常
                                index++;
                                continue;
                            }  catch (Exception exception) {
                                index++;
                                log.logToError(exception);
                            }
                        }
                    } else {
                        // 快速搜索仅支持快速搜索method/path/method,path
                        if (searchAPI.contains(",")) {
                            String[] split = searchAPI.split(",", 2);
                            String method = split[0].trim();
                            String path = split[1].trim();
                            int index = 0;

                            for (APIItem apiItem : APIConfig.TARGET_API) {
                                if (apiItem.getMethod() == null) {
                                    index++;
                                    continue;
                                }

                                // 默认带","的情况就是匹配method的情况
                                if (apiItem.getMethod().equalsIgnoreCase(method) && apiItem.getPath().equalsIgnoreCase(path)) {
                                    apiTable.setRowSelectionInterval(index, index);
                                    apiTable.requestFocusInWindow();  // 立即刷新状态 便于删除
                                }
                                index++;
                            }
                        } else {
                            if (searchAPI.equals("*")) {
                                apiTable.setRowSelectionInterval(0, APIConfig.TARGET_API.size() - 1);
                                apiTable.requestFocusInWindow();  // 立即刷新状态 便于删除
                            } else {
                                String path = searchAPI;
                                int index = 0;
                                for (APIItem apiItem : APIConfig.TARGET_API) {
                                    if (apiItem.getPath().equalsIgnoreCase(path)) {
                                        apiTable.addRowSelectionInterval(index, index);
                                        apiTable.requestFocusInWindow();  // 立即刷新状态 便于删除
                                    }
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
        });


        // 切换测试状态按钮监听器
        changeStateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 支持多行同时修改
                int[] selectedRows = apiTable.getSelectedRows();

                for (int selectedRow : selectedRows) {
                    if (APIConfig.TARGET_API.get(selectedRow).getState() == Boolean.FALSE) {
                        APIConfig.TARGET_API.get(selectedRow).setState(Boolean.TRUE);
                    } else {
                        APIConfig.TARGET_API.get(selectedRow).setState(Boolean.FALSE);
                    }
                }

                // 刷新列表
                Util.flushAPIList(model);
            }
        });
    }


    private static void showPathVariableDialog() {
        // 根据 UserConfig.IS_ANALYZE_PATHVARIABLE 提示当前状态
        JLabel jLabel = new JLabel("当前状态：" + UserConfig.IS_ANALYZE_PATHVARIABLE);

        // 创建确认对话框
        int option = JOptionPane.showConfirmDialog(null, jLabel,
                "是否解析 PathVariable?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        // 检测用户选择
        if (option == JOptionPane.YES_OPTION) {
            // 用户选择了 "是"
            UserConfig.IS_ANALYZE_PATHVARIABLE = true;  // 更新配置
        } else if (option == JOptionPane.NO_OPTION) {
            // 用户选择了 "否"
            UserConfig.IS_ANALYZE_PATHVARIABLE = false;  // 更新配置
        } else if (option == JOptionPane.CLOSED_OPTION) {
            // 用户直接关闭了对话框，不做处理
        }
    }
}
