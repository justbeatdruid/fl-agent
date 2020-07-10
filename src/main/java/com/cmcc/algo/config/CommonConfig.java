package com.cmcc.algo.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommonConfig {
    @Value("${file-path}")
    public String filePath;

    @Value("${python-path}")
    public String pythonPath;

    @Value("${client-py-path}")
    public String cliPyPath;

    @Value("${partyId}")
    private Integer partyId;

}
