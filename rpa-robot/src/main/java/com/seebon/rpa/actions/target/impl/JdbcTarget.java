package com.seebon.rpa.actions.target.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


@Slf4j
@ActionTarget
public class JdbcTarget extends AbstractTarget<JdbcTemplate> {

    @Conditions("mysql:url,username,password")
    @ActionArgs(value = "数据库类型", required = true, dict = DatabaseType.class)
    private String dbType;

    @ActionArgs("URL")
    private String url;

    @ActionArgs("账号")
    private String username;

    @ActionArgs("密码")
    private String password;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate fetchTarget() {
        if (ctx.contains(dbType)) {
            return ctx.getVariable(dbType);
        }

        switch (DatabaseType.valueOf(dbType)) {
            case h2: {
                return jdbcTemplate;
            }
            case mysql: {
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setUrl(url);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                ctx.setVariable(dbType, jdbcTemplate);
                return jdbcTemplate;
            }
            case mongo:
            case redis:
            default: {
                return null;
            }
        }
    }
}
