package com.chave.config;

import com.chave.pojo.APIItem;
import java.util.ArrayList;
import java.util.HashMap;

public class APIConfig {
    public static ArrayList<APIItem> TARGET_API = new ArrayList<>();

    public static final HashMap ITEM_FIELD = new HashMap();

    static {
        ITEM_FIELD.put(0, "method");
        ITEM_FIELD.put(1, "path");
        ITEM_FIELD.put(2, "result");
        ITEM_FIELD.put(3, "state");
        ITEM_FIELD.put(4, "note");
        ITEM_FIELD.put(5, "isFound");
    }

}
