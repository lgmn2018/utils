package com.lgmn.utils.image.code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Code {
    public String format = "png";

    /**
     * @param content 要生成的内容 （注：条码不能包含中文字符）
     * @param format 条码/二维码格式
     * @param width 宽度
     * @param height 高度
     * @param hints
     * @return BitMatrix
     */
    BitMatrix generate(String content, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) {
        try {
            if (null == content || content.equals("")) {
                System.out.println("Image.class-->generate()-->content is null");
            }
            // 二维码的格式是BarcodeFormat.QR_CODE qr类型二维码 BarcodeFormat.DATA_MATRIX dm码
            BitMatrix qrc = new MultiFormatWriter().encode(content, format, width, height, hints);
            return qrc;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    BitMatrix generate(String content, BarcodeFormat format, int width, int height) {
        HashMap<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        return generate(content,format,width,height,hints);
    }

    /**
     * 将内容生成的条码/二维码转换成bmp格式的InputStream
     * @param context 要生成的内容 （注：条码不能包含中文字符）
     * @param width 宽度
     * @param height 高度
     * @return
     */
    public InputStream getInputStream(String context, int width, int height){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage image = getBufferedImage(context,width,height);
            ImageIO.write(image, "bmp", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将内容生成的条码/二维码转换成bmp格式的byte[]
     * @param context
     * @param width
     * @param height
     * @return
     */
    public byte[] getBytes(String context, int width, int height){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage image = getBufferedImage(context,width,height);
            ImageIO.write(image, "bmp", os);
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将内容生成的条码/二维码转换BufferedImage
     * @param context 要生成的内容 （注：条码不能包含中文字符）
     * @param width 宽度
     * @param height 高度
     * @return
     */
    public abstract BufferedImage getBufferedImage(String context, int width, int height);

    /**
     * 生成内容对应的条码/二维码图片文件后返回图片路径
     * @param context 要生成的内容 （注：条码不能包含中文字符）
     * @param width 宽度
     * @param height 高度
     * @param path 图片保存目录
     * @return
     */
    public abstract String getFilePath(String context, int width, int height, String path);
}
