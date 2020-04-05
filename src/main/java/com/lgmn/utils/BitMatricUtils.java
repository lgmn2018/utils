package com.lgmn.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BitMatricUtils {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private static BufferedImage toBufferedImage(BitMatrix bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, bm.get(i, j) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static BufferedImage writeBitMatricToBufferedImage(BitMatrix bm) {
        BufferedImage image = toBufferedImage(bm);
        return image;
    }

    public static void writeBitMatricToFile(BitMatrix bm, String format, File file) {
        BufferedImage image = toBufferedImage(bm);
        try {
            if (!ImageIO.write(image, format, file)) {
                throw new RuntimeException("Can not write an image to file" + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void parseCode(File file) {
        try {
            MultiFormatReader formatReader = new MultiFormatReader();

            if (!file.exists()) {
                return;
            }

            BufferedImage image = ImageIO.read(file);

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            Result result = formatReader.decode(binaryBitmap, hints);

            System.out.println("解析结果 = " + result.toString());
            System.out.println("二维码格式类型 = " + result.getBarcodeFormat());
            System.out.println("二维码文本内容 = " + result.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}