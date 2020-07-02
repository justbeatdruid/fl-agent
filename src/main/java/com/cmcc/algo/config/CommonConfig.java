package com.cmcc.algo.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommonConfig {
    public static String filePath;
    public static Integer interval;

    @Value("${file-path}")
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    @Value("${partyId}")
    private Integer partyId;

}
