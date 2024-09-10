//package com.sakeman.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Profile;
//import com.zaxxer.hikari.HikariDataSource;
//import javax.sql.DataSource;
//
//@Primary
//@Configuration
//@Profile("!local")  // ローカル以外で動作させる
//public class DataSourceConfig {
//
////    @Value("${cloud.aws.rds.instances[0].dbInstanceIdentifier}")
//    private String dbInstanceIdentifier;
//
////    @Value("${cloud.aws.rds.instances[0].username}")
//    private String username;
//
////    @Value("${cloud.aws.rds.instances[0].password}")
//    private String password;
//
//    @Bean
//    public DataSource dataSource() {
//        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://" + dbInstanceIdentifier + ":3306/sakemandb");
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//
//        // その他の接続プール設定
//        dataSource.setMaximumPoolSize(15);
//        dataSource.setMinimumIdle(5);
//        dataSource.setIdleTimeout(600000);
//        dataSource.setMaxLifetime(1800000);
//        dataSource.setConnectionTimeout(30000);
//        dataSource.setValidationTimeout(5000);
//        dataSource.setConnectionTestQuery("SELECT 1");
//
//        return dataSource;
//    }
//}
//
