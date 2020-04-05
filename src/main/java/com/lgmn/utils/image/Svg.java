package com.lgmn.utils.image;

import com.lgmn.utils.BufferedImageUtils;
import com.lgmn.utils.exception.KeyNotFoundException;
import com.lgmn.utils.exception.NoDataException;
import com.lgmn.utils.exception.SvgPlaceholderKeyUndefinedException;
import com.lgmn.utils.image.code.CodeEntity;
import com.lgmn.utils.ParttenUtils;
import com.lgmn.utils.printer.Command;
import com.lgmn.utils.printer.PrintProps;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Svg {
    /**
     * todo
     * 1、载入svg文件
     * 2、读取svg节点
     * 3、修改svg占位内容
     * 4、转换byte[]数据
     */


    // 加载svg文件
    public Document loadSvgFile(String path) {
        Document doc = null;
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String uri = new File(path).toURI().toString();
            doc = f.createDocument(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    // 替换svg模板内容
    public void replaceTemplate(Node currnode, List<CodeEntity> codeEntitys, Map<String, String> dataMap) throws Exception {
        if (currnode.hasChildNodes()) {
            NodeList nodeList = currnode.getChildNodes();
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++) {
                Node node = nodeList.item(i);
                replaceTemplate(node, codeEntitys, dataMap);
            }
        }

        loopAttr(currnode);
        String nodeValue = currnode.getNodeValue();
        if (null != nodeValue) {
            String pattern = "^\\{\\{.*\\}\\}$";
            Pattern r = Pattern.compile(pattern);
            String qrcodePrefix = "qrcode";
            String barcodePrefix = "barcode";
            if (ParttenUtils.matchs(nodeValue, pattern)) {
                String codeKeyPattern = "\\{\\{(.*):.*\\}\\}";
                String codeValuePattern = "\\{\\{.*:(.*)\\}\\}";
                String keyPattern = "\\{\\{(.*)\\}\\}";
                String codeKey = ParttenUtils.getMatchStr(nodeValue, codeKeyPattern);
                String codeValue = ParttenUtils.getMatchStr(nodeValue, codeValuePattern);
                if (qrcodePrefix.equals(codeKey)) {
                    String dataMapValue = getDataMapValue(dataMap, codeValue);
                    try {
                        codeEntitys.add(readCodeEntity(currnode.getParentNode(), CodeEntity.Type.QRCODE, dataMapValue));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (barcodePrefix.equals(codeKey)) {
                    String dataMapValue = getDataMapValue(dataMap, codeValue);
                    try {
                        codeEntitys.add(readCodeEntity(currnode.getParentNode(), CodeEntity.Type.BARCODE, dataMapValue));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (StringUtils.isEmpty(codeKey)) {
                    String textKey = ParttenUtils.getMatchStr(nodeValue, keyPattern);
                    String textValue = getDataMapValue(dataMap, textKey);
                    currnode.setNodeValue(textValue);
                } else {
                    throw new SvgPlaceholderKeyUndefinedException("Svg 占位符键未定义:" + codeKey);
                }
            }
        }
    }

    private String getDataMapValue(Map<String, String> dataMap, String key) throws Exception {
        if (dataMap.size() == 0) {
            throw new NoDataException("DataMap没有数据，请检查");
        }
        String dataMapValue = "";
        if (dataMap.containsKey(key)) {
            dataMapValue = dataMap.get(key);
        } else {
            throw new KeyNotFoundException("DataMap找不到key为" + key + "的数据");
        }
        return dataMapValue;
    }

    public CodeEntity readCodeEntity(Node node, CodeEntity.Type type, String value) throws Exception {
        CodeEntity codeEntity = new CodeEntity();
        codeEntity.setType(type);
        NamedNodeMap namedNodeMap = node.getAttributes();
        String x = "";
        int x_int = 0;
        String y = "";
        int y_int = 0;
        String width = "";
        int width_int = 0;
        String height = "";
        int height_int = 0;

        if (null != namedNodeMap) {
            x = namedNodeMap.getNamedItem("x").getNodeValue();
            if (StringUtils.isNotEmpty(x)) {
                x_int = Integer.parseInt(x);
            } else {
                throw new Exception(type.toString() + " 缺少属性：x");
            }
            y = namedNodeMap.getNamedItem("y").getNodeValue();
            if (StringUtils.isNotEmpty(y)) {
                y_int = Integer.parseInt(y);
            } else {
                throw new Exception(type.toString() + " 缺少属性：y");
            }
            width = namedNodeMap.getNamedItem("width").getNodeValue();
            if (StringUtils.isNotEmpty(width)) {
                width_int = Integer.parseInt(width);
            } else {
                throw new Exception(type.toString() + " 缺少属性：width");
            }
            height = namedNodeMap.getNamedItem("height").getNodeValue();
            if (StringUtils.isNotEmpty(height)) {
                height_int = Integer.parseInt(height);
            } else {
                throw new Exception(type.toString() + " 缺少属性：height");
            }
            /**
             * todo
             * 根据实际数据对value赋值
             */
            codeEntity.setX(x_int);
            codeEntity.setY(y_int);
            codeEntity.setWidth(width_int);
            codeEntity.setHeight(height_int);
            codeEntity.setValue(value);
        }
        return codeEntity;
    }

    public void loopAttr(Node currnode) {
        NamedNodeMap namedNodeMap = currnode.getAttributes();
        if (null != namedNodeMap) {
            int length = namedNodeMap.getLength();
            for (int i = 0; i < length; i++) {
                Node node = namedNodeMap.item(i);
                Short nodeType = node.getNodeType();
                String value = "";
                switch (nodeType) {
                    case Node.ATTRIBUTE_NODE:
                        value = node.getNodeValue();
                        break;
                }
//                System.out.println(node.getNodeName() + ":" + value);
            }
        }

//        System.out.println("width:"+width+"; height:"+height+"; x:"+x+"; y:"+y+";");

    }
}