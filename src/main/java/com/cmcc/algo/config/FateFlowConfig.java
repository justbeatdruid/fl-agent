package com.cmcc.algo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FateFlowConfig {
    public static String fateFlowHost;
    public static String fateFlowPort;

    @Value("${fate-flow.host}")
    public void setFateFlowHost(String fateFlowHost) {
        this.fateFlowHost = fateFlowHost;
    }

    @Value("${fate-flow.port}")
    public void setFateFlowPort(String fateFlowPort) {
        this.fateFlowPort = fateFlowPort;
    }
}
