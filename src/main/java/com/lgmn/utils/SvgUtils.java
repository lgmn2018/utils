package com.lgmn.utils;

import com.lgmn.utils.image.Svg;
import com.lgmn.utils.image.code.CodeEntity;
import com.lgmn.utils.printer.Command;
import com.lgmn.utils.printer.PrintProps;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SvgUtils {
    // 导出bmp
    public boolean exportBMP(String svgPath, String exportPath, Map<String, String> dataMap) {
        boolean success = false;
        BufferedImage bi = exportBufferedImage(svgPath, dataMap);
        BufferedImage bufferedImage = BufferedImageUtils.convertBgToWhite(bi);
        success = bufferedImageSaveFile(exportPath, bufferedImage);
        return success;
    }

    public boolean bufferedImageSaveFile(String exportPath, BufferedImage bi) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(exportPath);
            success = ImageIO.write(bi, "bmp", fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return success;
        }
    }

    public BufferedImage exportBufferedImage(String svgPath, Map<String, String> dataMap) {
        Svg svg = new Svg();
        BufferedImage bi = null;
        List<CodeEntity> codeEntitys = new ArrayList<>();
        Document doc = svg.loadSvgFile(svgPath);
        Element rootElement = doc.getDocumentElement();
        try {
            svg.replaceTemplate(rootElement, codeEntitys, dataMap);
            bi = BufferedImageUtils.documentToBufferedImage(doc);
            BufferedImageUtils.mergeBufferedImages(bi, codeEntitys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return bi;
        }
    }

    public Vector<Byte> getPrintData(String svgPath, PrintProps printProps, Map<String, String> dataMap) {
        BufferedImage bufferedImage = exportBufferedImage(svgPath, dataMap);
//        Command cpcl = Command.getInstance().buildCpclCommand(bufferedImage, printProps);
        Command cpcl = new Command().buildCpclCommand(bufferedImage,printProps);
        Vector<Byte> datas = cpcl.getCommand();
        return datas;
    }

    public Vector<Byte> getTeminalPrintData(String svgPath, PrintProps printProps, Map<String, String> dataMap) {
        BufferedImage bufferedImage = exportBufferedImage(svgPath, dataMap);
//        Command cpcl = Command.getInstance().buildTeminalCpclCommand(bufferedImage, printProps);
        Command cpcl = new Command().buildCpclCommand(bufferedImage,printProps);
        Vector<Byte> datas = cpcl.getCommand();
        return datas;
    }

    public byte[] getPrintDataByteArray(String svgPath, PrintProps printProps, Map<String, String> dataMap) {
        Vector<Byte> datas = getPrintData(svgPath, printProps, dataMap);
        String printHead = "PT:\r\n";
        byte[] headBytes = printHead.getBytes();
        int headLength=headBytes.length;
        byte[] byteArray = new byte[headLength+datas.size()];

        for (int i = 0; i < headBytes.length; i++) {
            byteArray[i]=headBytes[i];
        }

        for (int i = headLength; i < datas.size(); i++) {
            byteArray[i] = datas.get(i).byteValue();
        }
        return byteArray;
    }
}