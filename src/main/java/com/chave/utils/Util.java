package com.chave.utils;

public class Util {
    public static String convertPathToRegex(String path) {
        // 使用正则表达式替换 {.*} 为 [^/]+
        return path.replaceAll("\\{[^/]+\\}", "[^/]+");
    }
}
