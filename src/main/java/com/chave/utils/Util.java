package com.chave.utils;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.internal.ObjectFactoryLocator;
import com.chave.Main;
import com.chave.config.APIConfig;
import com.chave.pojo.APIItem;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {
    public static String convertPathToRegex(String path) {
        // 使用正则表达式替换 {.*} 为 [^/]+
        return path.replaceAll("\\{[^/]+\\}", "[^/]+");
    }

    public static boolean checkAPIItemExist(APIItem o) {
        boolean isExist = false;

        if (APIConfig.TARGET_API.isEmpty()) {
            return isExist;
        }

        for (APIItem apiItem : APIConfig.TARGET_API) {
            if (apiItem.equals(o)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }


    public static void setHighlightColor(Object obj, String color) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> requestClass = obj.getClass();
        Method annotationsMethod = requestClass.getMethod("annotations");
        Annotations annotations = (Annotations) annotationsMethod.invoke(obj, null);

        annotations.setHighlightColor(ObjectFactoryLocator.FACTORY.highlightColor(color));
    }

    public static void flushAPIList(DefaultTableModel model) {
        // 修改之后刷新列表  防止数据不一致
        model.setRowCount(0);
        for (APIItem apiItem : APIConfig.TARGET_API) {
            model.addRow(new Object[]{apiItem.getMethod(), apiItem.getPath(), apiItem.getResult(), apiItem.getState(), apiItem.getNote(), apiItem.getDomain()});
        }
    }

    public static String urlDecode(String input) {
        return Main.API.utilities().urlUtils().decode(input);
    }
}
