package com.cmcc.algo.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommonConfig {
    public static String filePath;
    public static String pythonPath;
    public static String cliPyPath;

    @Value("${file-path}")
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    @Value("${python-path}")
    public void setPythonPath(String pythonPath){
        this.pythonPath = pythonPath;
    }

    @Value("${client-py-path}")
    public void setCliPyPath(String cliPyPath){
        this.cliPyPath = cliPyPath;
    }
    @Value("${partyId}")
    private Integer partyId;

}
