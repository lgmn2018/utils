package com.lgmn.utils.printer;

import com.lgmn.utils.SvgUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @Author: TJM
 * @Date: 2020/3/27 14:18
 */
@Slf4j
public class Printer {

//    private static final ThreadLocal<Printer> threadLocalInstance;
//
//    static {
//        threadLocalInstance = ThreadLocal.withInitial(() -> new Printer());
//    }

    GpDeviceManager instance = new GpDeviceManager();

    Command command = new Command();

    SvgUtils svgUtils = new SvgUtils();

//    private Printer(){
//
//        instance = new GpDeviceManager();
//    }
//
//    public static final Printer getInstance(){
//        return LazyHolder.print;
//    }
//
//    private static class LazyHolder{
//        private static final Printer print = new Printer();
//    }

//    public static Printer getInstance(){
//        return threadLocalInstance.get();
//    }

    int time = 5;

    public Printer() {
    }

    String _ip;

    int _port;

    @Setter
    private String svgPath;

    @Setter
    private Map<String,String> dataMap;

    @Getter
    PrintProps printProps;

    public Printer(String ip, int port, PrintProps printProps) {
        instance.setEthernetPort(ip, port);
        System.out.println("GpDeviceManager:" + instance);
        this._ip = ip;
        this._port = port;
        this.printProps = printProps;
    }

    private Vector<Byte> genPrintData(){
        BufferedImage bufferedImage = svgUtils.exportBufferedImage(svgPath, dataMap);
        command.clrCommand();
        command.buildCpclCommand(bufferedImage, printProps);
        Vector<Byte> datas = command.getCommand();
        return datas;
    }

    public void print(){
        print(svgPath,dataMap);
    }

    public void print(String svgPath, Map<String, String> dataMap) {
        print(_ip,_port,svgPath,printProps,dataMap);
    }

    public void print(Vector<Byte> data) {
        instance.setEthernetPort(_ip, _port);
        GpCom.ERROR_CODE openErr;
        int openTime = 1;
        do {
            openTime = openTime + 1;
            openErr = instance.openPort();
        } while (!openErr.equals(GpCom.ERROR_CODE.SUCCESS) && openTime <= time);

        //方式2
        if (openErr == GpCom.ERROR_CODE.SUCCESS) {
            GpCom.ERROR_CODE sendErr;
            int sendTime = 1;
            do {
                sendTime = sendTime + 1;
                sendErr = instance.sendData(data);
            } while (!sendErr.equals(GpCom.ERROR_CODE.SUCCESS) && sendTime <= time);

            if (sendErr == GpCom.ERROR_CODE.FAILED) {
                log.error("打印出现错误，打印机ip：" + _ip);
            }
        } else {
            log.error("请重启打印机：" + _ip);
        }
        close(_ip);
    }

    public void print(String ip, int port, String svgPath, PrintProps printProps, Map<String, String> dataMap) {
        instance.setEthernetPort(ip, port);
        GpCom.ERROR_CODE openErr;
        int openTime = 1;
        do {
            openTime = openTime + 1;
            openErr = instance.openPort();
        } while (!openErr.equals(GpCom.ERROR_CODE.SUCCESS) && openTime <= time);

        // 方式1
//        BufferedImage bufferedImage = SvgUtils.exportBufferedImage(svgPath, dataMap);
//        GpCom.ERROR_CODE err = instance.sendGraphics(bufferedImage, printProps);

        //方式2
        if (openErr == GpCom.ERROR_CODE.SUCCESS) {
            Vector<Byte> data = genPrintData();
            GpCom.ERROR_CODE sendErr;
            int sendTime = 1;

            do {
                sendTime = sendTime + 1;
                sendErr = instance.sendData(data);
            } while (!sendErr.equals(GpCom.ERROR_CODE.SUCCESS) && sendTime <= time);

            if (sendErr == GpCom.ERROR_CODE.FAILED) {
                log.error("打印出现错误，打印机ip：" + ip);
            }
        } else {
            log.error("请重启打印机：" + ip);
        }
        close(ip);
    }




    public void batchPrint(String ip, int port, String svgPath, PrintProps printProps, List<Map<String, String>> dataMaps) {
        instance.setEthernetPort(ip, port);
        GpCom.ERROR_CODE err = instance.openPort();
        if (err == GpCom.ERROR_CODE.SUCCESS) {
            err = sendData(svgPath, printProps, dataMaps, 0, dataMaps.size());
            if (err == GpCom.ERROR_CODE.FAILED) {
                log.error("批量打印出现错误，打印机ip：" + ip);
            }
            instance.closePort();
        } else {
            log.error("请重启打印机：" + ip);
        }
    }

    private GpCom.ERROR_CODE sendData(String svgPath, PrintProps printProps, List<Map<String, String>> dataMaps, int index, int size) {
        GpCom.ERROR_CODE err;
        if (index < size) {
            Vector<Byte> data = svgUtils.getPrintData(svgPath, printProps, dataMaps.get(index));
            err = instance.sendData(data);
            if (err == GpCom.ERROR_CODE.SUCCESS) {
                index = index + 1;
                err = sendData(svgPath, printProps, dataMaps, index, size);
            }
        } else {
            err = GpCom.ERROR_CODE.SUCCESS;
        }
        return err;
    }


    public void close(String ip) {
        GpCom.ERROR_CODE close;
        if (instance != null) {
            int closeTime = 1;
            log.info(ip + " close time:" + closeTime);
            do {
                closeTime = closeTime + 1;
                close = instance.closePort();
            }
            while (!close.equals(GpCom.ERROR_CODE.SUCCESS) && closeTime <= time);
        }
    }

    public void close() {
        GpCom.ERROR_CODE close;
        if (instance != null) {
            int closeTime = 1;
            log.info(" close time:" + closeTime);
            do {
                closeTime = closeTime + 1;
                close = instance.closePort();
            }
            while (!close.equals(GpCom.ERROR_CODE.SUCCESS) && closeTime <= time);
        }
    }

    @PreDestroy
    public void destory() {
        close();
    }

//    public Vector<Byte> getPrintData(String svgPath, int x, int y) {
//        Svg svg = new Svg();
//
//        BufferedImage bufferedImage = svg.exportBufferedImage(svgPath);
//
//        Command cpcl = Command.getInstance();
//        cpcl.addInitializePrinter();
//        cpcl.addCGraphics(bufferedImage, x, y);
//        cpcl.addPrint();
//
//        Vector<Byte> datas = cpcl.getCommand();
//        return datas;
//    }
//
//    public Vector<Byte> getPrintData(String svgPath) {
//        int x = 0, y = 0;
//        Vector<Byte> datas = getPrintData(svgPath, x, y);
//        return datas;
//    }
}