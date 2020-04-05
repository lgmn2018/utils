package com.lgmn.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParttenUtils {

    public static String getMatchStr(String text, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher m = pattern.matcher(text);
        String str = "";
        if (m.find()) {
            str = m.group(1);
        }
        return str;
    }

    public static boolean matchs(String text, String patternStr){
        Pattern r = Pattern.compile(patternStr);
        Matcher matcher = r.matcher(text);
        return matcher.matches();
    }
}