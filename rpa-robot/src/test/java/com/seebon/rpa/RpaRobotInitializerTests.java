package com.seebon.rpa;

import com.seebon.rpa.runner.RobotInitializer;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class RpaRobotInitializerTests {

    @Autowired
    private RobotInitializer initializer;

    private JdbcTemplate jdbcTemplate() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverClassName("com.mysql.jdbc.Driver");
        dataSourceProperties.setUrl("jdbc:mysql://192.168.0.81:3306/rpa_robot");
        dataSourceProperties.setUsername("dev");
        dataSourceProperties.setPassword("dev");
        return new JdbcTemplate(dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build());
    }

    @Test
    void initRobot(){
//        initializer.setJdbcTemplate(jdbcTemplate());
//        initializer.initArgs();
    }
}
