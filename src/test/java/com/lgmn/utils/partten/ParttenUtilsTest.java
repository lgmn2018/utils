package com.lgmn.utils.partten;

import com.lgmn.utils.ParttenUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParttenUtilsTest {

    @Test
    public void getMatchStr() {
        String text="{{qrcode:wewr}}";
        String keyPattern="\\{\\{(.*):.*\\}\\}";
        String valuePattern="\\{\\{.*:(.*)\\}\\}";
        String pattern="\\{\\{(.*)\\}\\}";
        String str= ParttenUtils.getMatchStr(text,keyPattern);
        System.out.println(str);
        assertEquals("qrcode", str);
        String value =  ParttenUtils.getMatchStr(text,valuePattern);
        System.out.println(value);

        String value2 =  ParttenUtils.getMatchStr(text,pattern);
        System.out.println(value2);
    }
}