package com.lgmn.utils.printer;

import com.lgmn.utils.SvgUtils;
import com.lgmn.utils.image.Svg;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class PrinterTest {

    @Test
    public void getInstance() {
        Printer printer = new Printer();
    }

    @Test
    public void print() {
        String ip = "192.168.192.200";
        String ip2 = "192.168.192.201";
        String ip3 = "192.168.192.202";
        String ip4 = "192.168.192.203";
        String ip5 = "192.168.192.204";

        int port = 9100;
//        String svgPath = "C:\\Users\\Lonel\\Desktop\\qd\\barcode.svg";
        String svgPath = "E:\\项目资料\\优码\\盈俊\\盈俊3.svg";
        String exportPath = "E:\\项目资料\\优码\\盈俊\\yj3.bmp";
        Printer printer = new Printer();
        Printer printer1 = new Printer();
        Printer printer2 = new Printer();
        Printer printer3 = new Printer();
        Printer printer4 = new Printer();
        PrintProps printProps = new PrintProps();
        printProps.setWidth(100);
        printProps.setHeight(150);
//        printProps.setHeight(50);

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("modelName","打印测试。。。");
        dataMap.put("width","80mm");
        dataMap.put("color","全灰");
        dataMap.put("perPackQuantity","50");
        dataMap.put("specs","2.5斤");
        dataMap.put("prodUser","0001");
        dataMap.put("prodDate","2020-03-01");
        dataMap.put("packNum","50");
        dataMap.put("jitai","01");
        dataMap.put("labelNum","120033397020001");

//        dataMap.put("color","1230");
//        dataMap.put("banci","甲-5");
//        dataMap.put("date","2020.03.07");
//        dataMap.put("grossWeight","26.00");
//        dataMap.put("fineness","240D");
//        dataMap.put("factPerBagNumber","6");
//        dataMap.put("netWeight","25.00");
//        dataMap.put("labelNumber","11907297070030");


        printer.print(ip, port, svgPath,printProps,dataMap);
//        printer1.print(ip2, port, svgPath,printProps,dataMap);
//        printer2.print(ip3, port, svgPath,printProps,dataMap);
//        printer3.print(ip4, port, svgPath,printProps,dataMap);
//        printer4.print(ip5, port, svgPath,printProps,dataMap);
//        SvgUtils.exportBMP(svgPath,exportPath,dataMap);
    }

    @Test
    public void batchPrint(){
        String ip = "192.168.192.201";
        int port = 9100;
//        String svgPath = "C:\\Users\\Lonel\\Desktop\\qd\\barcode.svg";
        String svgPath = "E:\\项目资料\\优码\\清远\\清远.svg";
        String exportPath = "E:\\项目资料\\优码\\清远\\清远.bmp";
        Printer printer = new Printer();
        PrintProps printProps = new PrintProps();
        printProps.setWidth(60);
        printProps.setHeight(40);
//        printProps.setHeight(50);

        List<Map<String,String>> dataMaps = new ArrayList<>();
        Map<String, String> dataMap1 = new HashMap<>();
        dataMap1.put("color","1230");
        dataMap1.put("banci","甲-5");
        dataMap1.put("date","2020.03.07");
        dataMap1.put("grossWeight","26.00");
        dataMap1.put("fineness","240D");
        dataMap1.put("factPerBagNumber","6");
        dataMap1.put("netWeight","25.00");
        dataMap1.put("labelNumber","11907297070030");

        dataMaps.add(dataMap1);

        Map<String, String> dataMap2 = new HashMap<>();

        dataMap2.put("color","1231");
        dataMap2.put("banci","甲-6");
        dataMap2.put("date","2020.03.07");
        dataMap2.put("grossWeight","27.00");
        dataMap2.put("fineness","250D");
        dataMap2.put("factPerBagNumber","6");
        dataMap2.put("netWeight","25.00");
        dataMap2.put("labelNumber","119072970700323");
        dataMaps.add(dataMap2);

//        Map<String, String> dataMap3 = new HashMap<>();
//
//        dataMap3.put("color","1233");
//        dataMap3.put("banci","甲-6");
//        dataMap3.put("date","2020.03.07");
//        dataMap3.put("grossWeight","27.00");
//        dataMap3.put("fineness","250D");
//        dataMap3.put("factPerBagNumber","6");
//        dataMap3.put("netWeight","25.00");
//        dataMap3.put("labelNumber","119072970700324");
//        dataMaps.add(dataMap3);

        printer.batchPrint(ip, port, svgPath,printProps,dataMaps);
    }

    @Test
    public void test(){
//        BigDecimal finalWidth = new BigDecimal(80.0/100*1180);
//        System.out.println(Integer.parseInt(finalWidth.toString()));


        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dstr = sdf.format(calendar.getTime());
        System.out.println(dstr);
    }

    @Test
    public void exportBmp(){
//        String svgPath = "C:\\Users\\Lonel\\Desktop\\qd\\barcode.svg";
        String svgPath = "E:\\项目资料\\优码\\公司便签.svg";
//        String bmpPath = "C:\\Users\\Lonel\\Desktop\\qd\\barcode60.bmp";
        String bmpPath = "E:\\项目资料\\优码\\公司便签.bmp";
        PrintProps printProps = new PrintProps();
        printProps.setWidth(100);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("userName","陈岩");
        dataMap.put("phone","13288316779");
        dataMap.put("address","佛山市禅城区古大路16号A座A511-A单元");

        SvgUtils svgUtils = new SvgUtils();
        svgUtils.exportBMP(svgPath,bmpPath,dataMap);
    }
}