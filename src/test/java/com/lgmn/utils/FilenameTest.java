package com.lgmn.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

public class FilenameTest {
    @Test
    public void getExtension(){
        String path="C:\\Users\\Lonel\\Desktop\\test.xlsx";
        String extension = FilenameUtils.getExtension(path);
        System.out.println(extension);
        String prefix = FilenameUtils.getPrefix(path);
        System.out.println(prefix);

        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
    }
}