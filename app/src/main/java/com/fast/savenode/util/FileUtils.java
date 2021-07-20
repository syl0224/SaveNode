package com.fast.savenode.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class FileUtils {
    public static final String ZHConfig = "/data/local/tmp/ZHConfig";
    public static final String ClickableFilter = "ClickableFilter";
    public static final String AreaFilter = "AreaFilter";

    public static String getProperty(String key) {
        Properties properties = new Properties();
        String value = "on";
        try {
            File file = new File(ZHConfig);
            if (!file.exists()) {
                return value;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            value = properties.getProperty(key);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
