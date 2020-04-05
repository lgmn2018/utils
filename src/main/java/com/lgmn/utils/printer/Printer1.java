package com.lgmn.utils.printer;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import java.io.*;

/**
 * @Author: TJM
 * @Date: 2020/3/30 16:56
 */
public class Printer1 {

    public static OrientationRequested PORTRAIT = OrientationRequested.PORTRAIT;

    public static void main(String[] args) {
        File file = new File("E:\\项目资料\\优码\\公司便签.bmp");
        try {
            PDFPrint(file);
        } catch (PrintException e) {
            e.printStackTrace();
        }
    }

    public static void PDFPrint(File file) throws PrintException {
        if (file == null) {
            System.err.println("缺少打印文件");
        }
        InputStream fis = null;
        try {
            // 构建打印请求属性集
            HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(PORTRAIT);
            // 设置打印格式，因为未确定类型，所以选择autosense
            DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
            // 打印参数
            DocAttributeSet aset = new HashDocAttributeSet();
//             aset.add(new Copies(1)); //份数
//             aset.add(MediaSize.NA.NA_10X15_ENVELOPE); //纸张
            // aset.add(Finishings.STAPLE);//装订
            // 定位默认的打印服务
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();

            fis = new FileInputStream(file); // 构造待打印的文件流

            Doc doc = new SimpleDoc(fis, flavor, aset);
            DocPrintJob job = defaultService.createPrintJob(); // 创建打印作业
            job.addPrintJobListener(new PrintJobListener() {

                @Override
                public void printDataTransferCompleted(PrintJobEvent arg0) {
                    System.out.println("printDataTransferCompleted");

                }

                @Override
                public void printJobCanceled(PrintJobEvent arg0) {
                    System.out.println("printJobCanceled");

                }

                @Override
                public void printJobCompleted(PrintJobEvent arg0) {
                    System.out.println("printJobCompleted");

                }

                @Override
                public void printJobFailed(PrintJobEvent arg0) {
                    System.out.println("printJobFailed");

                }

                @Override
                public void printJobNoMoreEvents(PrintJobEvent arg0) {
                    System.out.println("printJobNoMoreEvents");

                }

                @Override
                public void printJobRequiresAttention(PrintJobEvent arg0) {
                    System.out.println("printJobRequiresAttention");

                }

            });
            job.print(doc, pras);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}