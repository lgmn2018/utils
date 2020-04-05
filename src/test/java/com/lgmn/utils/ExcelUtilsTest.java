package com.lgmn.utils;

import cn.afterturn.easypoi.entity.ImageEntity;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class ExcelUtilsTest {

    @Test
    public void exportFileByTemplate() {
        String tempPath="C:\\Users\\Lonel\\Desktop\\exportTest\\test.xlsx";
        String exportPath="C:\\Users\\Lonel\\Desktop\\exportTest";
        Map<String,Object> data = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Map<String,Object> listObject = new HashMap<>();
            listObject.put("name","name");
            listObject.put("age",i);
            File file =new File("C:\\Users\\Lonel\\Desktop\\exportTest\\"+i+".png");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] byt = new byte[fis.available()];
                fis.read(byt);
                listObject.put("avatar",new ImageEntity(byt,300,200));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            list.add(listObject);
        }
        data.put("maplist",list);
        ExcelUtils.exportFileByTemplate(tempPath,exportPath,data);
    }

    @Test
    public void exportInputStreamByTemplate() {
    }
}