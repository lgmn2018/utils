package com.lgmn.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class ExcelUtils {
    public static String exportFileByTemplate(String templatePath, String exportPath, Map<String,Object> data){
        Workbook workbook = getWrokbook(templatePath, data);

        String savePath=exportPath;
        File savefile = new File(savePath);
        if (!savefile.exists()) {
            boolean result = savefile.mkdirs();
            System.out.println("目录不存在,进行创建,创建" + (result ? "成功!" : "失败！"));
        }

        String extens = FilenameUtils.getExtension(templatePath);

        UUID uuid = UUID.randomUUID();

        String filePath = savePath  +"\\"+uuid +"."+extens;

        filePath = FilenameUtils.separatorsToSystem(filePath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    public static InputStream exportInputStreamByTemplate(String templatePath, Map<String,Object> data) {
        Workbook workbook = getWrokbook(templatePath, data);

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ByteArrayInputStream bais = null;
        try {
            workbook.write(baos);
            bais=new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                baos.close();
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bais;
    }

    private static Workbook getWrokbook(String templatePath, Map<String, Object> data) {
        File templateFile=new File(templatePath);
        if(!templateFile.exists()){
            System.out.println("找不到模板文件");
            try {
                throw new FileNotFoundException("找不到模板文件");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        // 加载模板
        TemplateExportParams params = new TemplateExportParams(templatePath);
        // 生成workbook 并导出
        Workbook workbook = null;
        workbook = ExcelExportUtil.exportExcel(params, data);
        return workbook;
    }
}