package com.lgmn.utils.printer;

import com.lgmn.utils.image.Svg;

import java.awt.image.BufferedImage;
import java.util.Vector;

public class GpDeviceManager {

    private static GpDevice device;
    //打印机ip固定值
    private String ip = "192.168.199.122";
    private int port = 9100;
//    private static GpDeviceManager instance;

//    private GpDeviceManager() {
//        super();
//    }
//
//    public static GpDeviceManager getInstance() {
//        if (instance == null) {
//            synchronized (GpDeviceManager.class) {
//                if (instance == null) {
//                    instance = new GpDeviceManager();
//                }
//            }
//        }
//        return instance;
//    }

    public GpDevice getDevice() {
        if (null == device) {
            device = new GpDevice();
        }
        return device;
    }

    public void setEthernetPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public GpCom.ERROR_CODE openPort() {
        return getDevice().openEthernetPort(ip, port);
    }

    public GpCom.ERROR_CODE closePort() {
        if (device != null) {
            return device.closePort();
        } else {
            return GpCom.ERROR_CODE.FAILED;
        }
    }

    public GpCom.ERROR_CODE sendGraphics(BufferedImage bi, PrintProps printProps) {
        Command cpcl = new Command().buildCpclCommand(bi, printProps);

        Vector<Byte> datas = cpcl.getCommand();
        //发送命令
        return getDevice().sendDataImmediately(datas);
    }

    public GpCom.ERROR_CODE sendData(Vector<Byte> data){
        return getDevice().sendDataImmediately(data);
    }
}