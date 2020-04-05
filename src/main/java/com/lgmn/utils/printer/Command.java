package com.lgmn.utils.printer;

import com.lgmn.utils.BufferedImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

public class Command {
    Vector<Byte> command = null;

//    private static class Holder{
//
//        private static final Command com = new Command();
//    }
//
//    public static Command getInstance(){
//        return Holder.com;
//    }

//    private static final ThreadLocal<Command> threadLocalInstance = new ThreadLocal<Command>() {
//        @Override
//        protected Command initialValue() {
//            return new Command();
//        }
//    };

    public Command() {
        command = new Vector<Byte>();
    }

//    public static Command getInstance() {
//        Command command =  threadLocalInstance.get();
//        command.clrCommand();
//        return command;
//    }

    public void clrCommand() {
        command.clear();
    }

    public Command buildCpclCommand(BufferedImage bi, PrintProps printProps) {
        int x = printProps.getX(), y = printProps.getY(), width = printProps.getWidth(), height = printProps.getHeight(), gap_m = printProps.getGap_m(), gap_n = printProps.getGap_n();
        addInitializePrinter(width, height, gap_m, gap_n);
        addCGraphics(bi, x, y, width);
        addPrint();
        return this;
    }

    public Command buildTeminalCpclCommand(BufferedImage bi, PrintProps printProps) {
        int x = printProps.getX(), y = printProps.getY(), width = printProps.getWidth(), height = printProps.getHeight(), gap_m = printProps.getGap_m(), gap_n = printProps.getGap_n();
        addInitializeTeminalPrinter(width, height, gap_m, gap_n);
        addCGraphics(bi, x, y, width);
        addPrint();
        return this;
    }

    /**
     * 标签初始化指令
     */
    public void addInitializePrinter(int width, int height, int gap_m, int gap_n) {
        String str = "SIZE " + width + " mm," + height + " mm\r\n" +
                "GAP " + gap_m + " mm," + gap_n + " mm\r\n" +
                "CLS\r\n" +
                "DENSITY 15\r\n" +
                "REFERENCE 0,0\r\n";
        addStrToCommand(str);
    }

    public void addInitializeTeminalPrinter(int width, int height, int gap_m, int gap_n) {
        String str ="PT:\r\n" +
                "SIZE " + width + " mm," + height + " mm\r\n" +
                "GAP " + gap_m + " mm," + gap_n + " mm\r\n" +
                "CLS\r\n" +
                "DENSITY 15\r\n" +
                "REFERENCE 0,0\r\n";
        addStrToCommand(str);
    }

    /**
     * 打印标签指令
     */
    public void addPrint() {
        String str = "PRINT 1,1\r\n";
        addStrToCommand(str);
    }

    /**
     * 将字符串根据字体转为byte数组添加到Command中
     *
     * @param str 字符串(命令或其他)
     */
    private void addStrToCommand(String str) {
        byte[] bs = null;
        if (!str.equals("")) {
            try {
                bs = str.getBytes("GB2312");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for (int i = 0; i < bs.length; i++) {
                command.add(bs[i]);
            }
        }
    }

    public void addCGraphics(BufferedImage bi, int x, int y, int printWidth) {
        int finalWidth = printWidth * 12;
        BufferedImage tag = BufferedImageUtils.bigorsmall(bi, finalWidth);

        int[] wandh = GetWandH(tag);

        byte[] datas1 = BufferedImageUtils.bufferedImageToBytes(tag);
        int width = wandh[0];
        byte[][] datas2 = getbyte2(datas1, wandh[0], wandh[1]);
        byte[][] datas3 = getbytev(datas2, wandh[0], wandh[1]);
        width = calcWidth(width);
        String str = "BITMAP " + x + "," + y + "," + (width / 8) + "," + wandh[1] + ",1,";
        addStrToCommand(str);
        for (int j = 0; j < wandh[1]; j++) {
            for (int k = 0; k < (width / 8); k++) {
                command.add(datas3[k][j]);
            }
        }

        addStrToCommand("\r\n");
    }

    private int calcWidth(int width) {
        if (width % 8 != 0) {
            width = width + 8 - width % 8;
        }
        if ((width / 8) % 4 != 0) {
            width = width + 32 - (((width / 8) % 4) * 8);
        }
        return width;
    }

    int[] GetWandH(String path) {
        int[] i = new int[2];
        File file = new File(path);
        FileInputStream fis;
        BufferedImage bufferedImg = null;
        try {
            fis = new FileInputStream(file);

            bufferedImg = ImageIO.read(fis);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        i[0] = bufferedImg.getWidth();
        i[1] = bufferedImg.getHeight();
        return i;

    }

    int[] GetWandH(BufferedImage bufferedImg) {
        int[] i = new int[2];

        i[0] = bufferedImg.getWidth();
        i[1] = bufferedImg.getHeight();
        return i;

    }

    /**
     * 将一维数字转为二维数组
     *
     * @param b
     * @param w 宽
     * @param h 高 单位px
     * @return
     */
    byte[][] getbyte2(byte[] b, int w, int h) {
        int out = 0;
        if (w % 8 != 0) {
            //多出的bit默认是不打印的 即为 0  所以在原数组进行打印即出多的bit值为1，处理后~
            out = 8 - w % 8;
            w = w + 8 - w % 8;
        }
        if ((w / 8) % 4 != 0) {
            out = 32 - (((w / 8) % 4) * 8);
            w = w + 32 - (((w / 8) % 4) * 8);
        }
        int width = w / 8;
        int height = h;
        int num = b.length - (width * height);
        byte[][] datas;
        if (width % 4 == 0) {
            datas = new byte[width][h];
            // 行
            for (int n = 0; n < h; n++) {
                // 列
                for (int m = 0; m < width; m++) {
                    byte c = b[num + n * width + m];
                    if (out > 0 && m == width - 1) {
                        c = outto1(c, out);

                    }
                    datas[m][n] = c;
                }
            }
        } else {
            int bwidth = width + 4 - width % 4;
            //水平单数byte则添加一个全部为0byte
            datas = new byte[bwidth][h];
            // 行
            for (int n = 0; n < h; n++) {
                // 列
                for (int m = 0; m < bwidth; m++) {
                    byte c = b[62 + n * (bwidth) + m];
                    if (out > 0 && m == width - 1) {
                        c = outto1(c, out);
                    }
                    if (m >= width) {
                        //每行最后一个byte值为255
                        c = -1;
                    }
                    datas[m][n] = c;
                }
            }
        }

        return datas;

    }

    /**
     * 二维数组垂直翻转
     *
     * @param datas
     * @param w
     * @param h
     * @return
     */
    byte[][] getbytev(byte[][] datas, int w, int h) {
        w = calcWidth(w);
        int width = w / 8;
        int height = h;
        byte[][] data;
        if (width % 4 == 0) {
            data = new byte[width][height];
            loopData(datas, h, width, data);
        } else {
            int bwidth = width + 4 - width % 4;
            data = new byte[bwidth][height];
            loopData(datas, h, bwidth, data);

        }
        return data;

    }

    private void loopData(byte[][] datas, int h, int width, byte[][] data) {
        for (int n = 0; n < h / 2; n++) {
            for (int m = 0; m < width; m++) {
                byte b = datas[m][n];
                byte c = datas[m][h - n - 1];
                data[m][n] = (byte) ~c;
                data[m][h - n - 1] = (byte) ~b;
            }
            if (h % 2 != 0 && n == (h / 2) - 1) {
                for (int m = 0; m < width; m++) {
                    byte b = datas[m][(h / 2)];
                    data[m][(h / 2)] = (byte) ~b;
                }
            }
        }
    }

    byte outto1(byte b, int out) {
        byte c = 0;
        if (out == 7) {
            b = (byte) (b + 127);
        } else if (out == 6) {
            b = (byte) (b + 63);
        } else if (out == 5) {
            b = (byte) (b + 31);
        } else if (out == 4) {
            b = (byte) (b + 15);
        } else if (out == 3) {
            b = (byte) (b + 7);
        } else if (out == 2) {
            b = (byte) (b + 3);
        } else if (out == 1) {
            b = (byte) (b + 1);
        }
        return b;

    }

    /**
     * 将BufferedImage转换为InputStream
     *
     * @param image
     * @return
     */
    public InputStream bufferedImageToInputStream(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "bmp", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vector<Byte> getCommand() {
        return command;
    }
}