package com.chave.utils;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.internal.ObjectFactoryLocator;
import burp.api.montoya.logging.Logging;
import com.chave.Main;
import com.chave.config.APIConfig;
import com.chave.config.SensitiveInfoConfig;
import com.chave.pojo.APIItem;
import com.chave.pojo.RuleItem;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    public static void flushRuleList(JTable table) {
        // 修改之后刷新列表  防止数据不一致
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (RuleItem ruleItem : SensitiveInfoConfig.RULE_LIST) {
            model.addRow(new Object[]{ruleItem.getLoaded(), ruleItem.getName(), ruleItem.getRegex(), ruleItem.getScope()});
        }
    }

    public static String urlDecode(String input) {
        return Main.API.utilities().urlUtils().decode(input);
    }

    public static void loadRuleYamlConfig(String filePath) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filePath);
        SensitiveInfoConfig.RULE_CONFIG_FILE = filePath;

        LinkedHashMap linkedHashMap = new Yaml().load(fis);
        ArrayList<LinkedHashMap> arrayList = (ArrayList) linkedHashMap.get("rules");

        SensitiveInfoConfig.RULE_LIST = new ArrayList();
        for (LinkedHashMap hashMap : arrayList) {
            RuleItem ruleItem = new RuleItem((String) hashMap.get("name"), (Boolean) hashMap.get("loaded"), (String) hashMap.get("regex"), (String) hashMap.get("scope"));
            SensitiveInfoConfig.RULE_LIST.add(ruleItem);
        }
    }

    public static byte[] getSerializedData(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }

    public static String byteCodeToBase64(byte[] code) {
        return Base64.getEncoder().encodeToString(code);
    }

    public static byte[] base64ToByteCode(String bs) {
        byte[] value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", (Class[])null).invoke(base64, (Object[])null);
            value = (byte[])((byte[])decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs));
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[])((byte[])decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs));
            } catch (Exception var5) {
            }
        }

        return value;
    }

    public static void exportToYaml() throws IOException {
        Map<String, Object> yamlData = new LinkedHashMap<>();
        List<Map<String, Object>> ruleList = new ArrayList<>();

        for (RuleItem ruleItem : SensitiveInfoConfig.RULE_LIST) {
            Map<String, Object> ruleItemMap = new LinkedHashMap<>();
            ruleItemMap.put("name", ruleItem.getName());
            ruleItemMap.put("loaded", ruleItem.getLoaded());
            ruleItemMap.put("regex", ruleItem.getRegex());
            ruleItemMap.put("scope", ruleItem.getScope());
            ruleList.add(ruleItemMap);
        }

        yamlData.put("rules", ruleList);

        // 配置YAML选项（保持块格式）
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);

        // 写入文件
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(SensitiveInfoConfig.RULE_CONFIG_FILE)) {
            yaml.dump(yamlData, writer);
        }

    }
}
