package com.github.doodler.common.utils;

import java.io.File;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: JavaFileUtils
 * @Author: Fred Feng
 * @Date: 16/01/2025
 * @Version 1.0.0
 */
@UtilityClass
public class JavaFileUtils {

    public String byteCountToDisplaySize(File file) {
        return org.apache.commons.io.FileUtils.byteCountToDisplaySize(file.length());
    }

    public File getUserDir() {
        return new File(System.getProperty("user.dir"));
    }

    public File getFile(File directory, String... names) {
        if (directory == null) {
            throw new NullPointerException("directorydirectory must not be null");
        }
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }

    public File getFile(String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = null;
        for (String name : names) {
            if (file == null) {
                file = new File(name);
            } else {
                file = new File(file, name);
            }
        }
        return file;
    }

}
