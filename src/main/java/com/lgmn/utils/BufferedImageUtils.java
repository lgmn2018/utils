package com.lgmn.utils;

import com.lgmn.utils.image.code.BarCode;
import com.lgmn.utils.image.code.Code;
import com.lgmn.utils.image.code.CodeEntity;
import com.lgmn.utils.image.code.QRCode;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BufferedImageUtils {

    public static void mergeBufferedImages(BufferedImage base, List<CodeEntity> codeEntitys) {
        for (CodeEntity codeEntity : codeEntitys) {
            BufferedImage merge = genBarcode(codeEntity);
            mergeBufferedImage(base, merge, codeEntity);
        }
    }
    /**
     * 合并BufferedImage
     *
     * @param base
     * @param merge
     */
    public static void mergeBufferedImage(BufferedImage base, BufferedImage merge, CodeEntity codeEntity) {
        Graphics g = base.getGraphics();
        g.drawImage(merge, codeEntity.getX(), codeEntity.getY(), codeEntity.getWidth(), codeEntity.getHeight(), null);
        g.dispose();
    }

    public static BufferedImage genBarcode(CodeEntity codeEntity) {
        CodeEntity.Type type = codeEntity.getType();
        BufferedImage bi = null;
        Code code = null;
        switch (type) {
            case BARCODE:
                code =new BarCode();
                bi = code.getBufferedImage(codeEntity.getValue(), codeEntity.getWidth(), codeEntity.getHeight());
                bi = convertBgToWhite(bi);
                break;
            case QRCODE:
                code =new QRCode();
                bi = code.getBufferedImage(codeEntity.getValue(), codeEntity.getWidth(), codeEntity.getHeight());
                bi = convertBgToWhite(bi);
                break;
            default:
                System.out.println("default");
                break;
        }
        return bi;
    }



    // document 转 bufferedImage
    public static BufferedImage renderToImage(Document doc) throws IOException {

        final BufferedImage[] imagePointer = new BufferedImage[1];

        // Rendering hints can't be set programatically, so
        // we override defaults with a temporary stylesheet.
        // These defaults emphasize quality and precision, and
        // are more similar to the defaults of other SVG viewers.
        // SVG documents can still override these defaults.
        String css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}";
        File cssFile = File.createTempFile("batik-default-override-", ".css");
        FileUtils.writeStringToFile(cssFile, css);

        TranscodingHints transcoderHints = new TranscodingHints();
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
                SVGDOMImplementation.getDOMImplementation());
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                SVGConstants.SVG_NAMESPACE_URI);
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());

        try {

            TranscoderInput input = new TranscoderInput(doc);

            ImageTranscoder t = new ImageTranscoder() {

                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out)
                        throws TranscoderException {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        } catch (TranscoderException ex) {
            // Requires Java 6
            ex.printStackTrace();
            throw new IOException("Couldn't convert ");
        } finally {
            cssFile.delete();
        }

        return imagePointer[0];
    }

    // document 转 bufferedImage
    public static BufferedImage documentToBufferedImage(Document doc) {
        BufferedImage newBufferedImage = null;
        try {
            BufferedImage bi = renderToImage(doc);
            newBufferedImage = convertBgToWhite(bi);
//            newBufferedImage = bi;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newBufferedImage;
    }

    // 把底色变成白色
    public static BufferedImage convertBgToWhite(BufferedImage bi) {
        BufferedImage newBufferedImage = new BufferedImage(bi.getWidth(),
                bi.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        newBufferedImage.createGraphics().drawImage(bi, 0, 0, Color.WHITE, null);
        antiColor(newBufferedImage);
        return newBufferedImage;
    }

    // 反色转换
    public static void antiColor(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = img.getRGB(x, y);

                int red = (r >> 16) & 0x0ff;
                int green = (r >> 8) & 0x0ff;
                int blue = r & 0x0ff;

                red = 255 - red;
                green = 255 - green;
                blue = 255 - blue;

                r = (red << 16) | (green << 8) | blue;
                img.setRGB(x, y, r);
            }
        }
    }

    public static byte[] bufferedImageToBytes(BufferedImage bImage) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "bmp", out);
        } catch (IOException e) {
            //log.error(e.getMessage());
        }
        return out.toByteArray();
    }

    public static BufferedImage bigorsmall(BufferedImage bi, int mWidth) {
        BufferedImage tag = null;
        BufferedImage srcImg = bi;
        //假设要缩小到600点像素
        int width = mWidth;
        //按比例，将高度缩减
        int height = srcImg.getHeight(null) * width / srcImg.getWidth(null);
        //缩小
        Image smallImg = srcImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        tag = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = tag.getGraphics();
        // 绘制缩小后的图
        g.drawImage(smallImg, 0, 0, null);
        g.dispose();
        return tag;

    }
}