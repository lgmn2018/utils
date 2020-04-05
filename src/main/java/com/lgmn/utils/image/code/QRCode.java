package com.lgmn.utils.image.code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.lgmn.utils.BitMatricUtils;

import java.awt.image.BufferedImage;
import java.io.*;

public class QRCode extends Code {
    @Override
    public BufferedImage getBufferedImage(String context, int width, int height) {
        BitMatrix bc = this.generate(context, BarcodeFormat.QR_CODE, width, height);
        return BitMatricUtils.writeBitMatricToBufferedImage(bc);
    }

    @Override
    public String getFilePath(String context, int width, int height, String path) {
        File out = new File(path + "/" + context+"."+ format);

        BitMatrix bc = this.generate(context, BarcodeFormat.QR_CODE, width, height);

        BitMatricUtils.writeBitMatricToFile(bc, format, out);

        return out.getAbsolutePath();
    }
}