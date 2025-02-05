package com.chave.editor;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.extension.*;
import com.chave.Main;
import com.chave.config.SensitiveInfoConfig;
import com.chave.config.UserConfig;
import com.chave.service.APIMatchService;
import com.chave.service.SensitiveInfoMatchService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ResponseEditor implements HttpResponseEditorProvider {

    @Override
    public ExtensionProvidedHttpResponseEditor provideHttpResponseEditor(EditorCreationContext creationContext) {
        return new Editor();
    }

    private static class Editor implements ExtensionProvidedHttpResponseEditor {
        private HttpRequestResponse requestResponse;
        private final JTabbedPane jTabbedPane = new JTabbedPane();
        private APIMatchService apiMatchService = new APIMatchService();
        private SensitiveInfoMatchService sensitiveInfoMatchService = new SensitiveInfoMatchService();

        public Editor() {
        }

        @Override
        public HttpResponse getResponse() {
            return requestResponse.response();
        }

        @Override
        public void setRequestResponse(HttpRequestResponse requestResponse) {
            this.requestResponse = requestResponse;
        }

        @Override
        public boolean isEnabledFor(HttpRequestResponse requestResponse) {
            HttpResponse response = requestResponse.response();
            HttpRequest request = requestResponse.request();
            try {
                Method matchMethod = APIMatchService.class.getMethod(UserConfig.MATCH_MOD.name(), HttpRequest.class);
                HashMap apiMatchResult = (HashMap) matchMethod.invoke(apiMatchService, request);
                boolean isMatched = (boolean) apiMatchResult.get("isMatched");
                if (isMatched && SensitiveInfoConfig.IS_CHECK_SENSITIVE_INFO) {
                    HashMap result = sensitiveInfoMatchService.sensitiveInfoMatch(response);
                    if (!result.isEmpty()) {
                        genreateEditorUI(result);
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                Main.API.logging().logToError(e);
                return false;
            }
            return false;
        }

        @Override
        public String caption() {
            return "MarkInfo";
        }

        @Override
        public Component uiComponent() {
            return jTabbedPane;
        }

        @Override
        public Selection selectedData() {
            return null;
        }

        @Override
        public boolean isModified() {
            return false;
        }

        private void genreateEditorUI(HashMap result) {
            Set<String> keys = result.keySet();

            // 每次均清空原有标签进行刷新
            jTabbedPane.removeAll();

            for (String key : keys) {
                // 计数器
                int index = 0;
                // 构建表格数据
                ArrayList<String> dataList = (ArrayList<String>) result.get(key);
                Object[][] data = new Object[dataList.size()][2];
                for (String dataItem : dataList) {
                    data[index][0] = index + 1;
                    data[index][1] = dataItem;
                    index++;
                }
                // 创建表格
                String[] columnName = {"#", "Information"};
                DefaultTableModel model = new DefaultTableModel(data, columnName);
                JTable table = new JTable(model);
                // 设置列宽
                table.getColumnModel().getColumn(0).setMaxWidth(75);
                // 设置第一行居中
                TableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                ((DefaultTableCellRenderer) centerRenderer).setHorizontalAlignment(SwingConstants.CENTER);
                table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
                // 设置表头背景
                JTableHeader tableHeader = table.getTableHeader();
                tableHeader.setBackground(new Color(215, 215, 215));
                // 创建表格滚动面板
                JScrollPane tableScrollPane = new JScrollPane(table);

                // 创建标签页
                jTabbedPane.addTab(key, tableScrollPane);
            }
        }
    }
}
