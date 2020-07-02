package com.cmcc.algo.config;

import org.springframework.beans.factory.annotation.Value;

public class CommonConfig {
    public static String filePath;
    public static Integer interval;

    @Value("${file-path}")
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }



}
