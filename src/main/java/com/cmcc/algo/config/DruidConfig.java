package com.cmcc.algo.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;

@Configuration
public class DruidConfig {
    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String  dbPassword;

    @Value("${spring.datasource.max-idle}")
    private Integer maxActive;

    @Value("${spring.datasource.min-idle}")
    private Integer minIdle;

    @Value("${spring.datasource.initial-size}")
    private Integer initialSize;

    @Bean(name = "druidDataSource")
    @Primary
    public DruidDataSource initDruidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(dbDriver);
        druidDataSource.setUrl(dbUrl);
        druidDataSource.setUsername(dbUser);
        druidDataSource.setPassword(dbPassword);
        // 最大连接池数量
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setInitialSize(initialSize);
        // 初始化，如果不加，默认会在第一次调用数据库连接数初始化
        druidDataSource.init();
        return druidDataSource;
    }
}