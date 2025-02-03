package com.chave.config;

import com.chave.pojo.MatchMod;

public class UserConfig {
    public static MatchMod MATCH_MOD = MatchMod.exactMatch;
    public static Boolean IS_URL_DECODE = Boolean.FALSE;
    public static Boolean IS_CHECK_ENTIRE_REQUEST = Boolean.FALSE;
    public static Boolean IS_CHECK_HTTP_METHOD = Boolean.FALSE;
    public static Boolean IS_ANALYZE_PATHVARIABLE = Boolean.FALSE;

}
