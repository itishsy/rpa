package com.seebon.rpa.runner;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.utils.Convert;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sql脚本升级
 *
 * @author: xfz
 */
@Slf4j
@Component
public class RobotUpgradeSql {
    private static final String sqlFile = "db/upgrade-h2.sql";

    @Autowired(required = false)
    private HikariDataSource dataSource;

    @Autowired(required = false)
    private DruidDataSource druidDataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initRobot() {
        if (druidDataSource == null) {
            return;
        }
        if (dataSource != null && dataSource.getJdbcUrl().contains("jdbc:mysql")) {
            log.info("已配置开发者模式,直连mysql,取消更新.");
            return;
        }
        this.jdbcTemplate = new JdbcTemplate(druidDataSource);

        //初始化表字段
        Map<String, List<Map<String, Object>>> tableMap = this.initTableColumn();

        //升级sql
        this.upgradeSql(tableMap);
    }

    private Map<String, List<Map<String, Object>>> initTableColumn() {
        String table_sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'";
        List<Map<String, Object>> tables = jdbcTemplate.query(table_sql, new Object[]{}, Convert.newRowMapper());
        if (CollectionUtils.isEmpty(tables)) {
            return Maps.newHashMap();
        }
        Map<String, List<Map<String, Object>>> tableMap = Maps.newHashMap();
        for (Map<String, Object> map : tables) {
            Object table_name = map.get("TABLE_NAME");
            String column_sql = "SELECT COLUMN_NAME,DATA_TYPE, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='" + table_name + "'";
            List<Map<String, Object>> columns = jdbcTemplate.query(column_sql, new Object[]{}, Convert.newRowMapper());
            if (CollectionUtils.isEmpty(columns)) {
                continue;
            }
            tableMap.put(table_name.toString(), columns);
        }
        return tableMap;
    }

    private void upgradeSql(Map<String, List<Map<String, Object>>> tableMap) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(sqlFile);

        List<String> sqls = IoUtil.readUtf8Lines(is, Lists.newArrayList());
        for (String sql : sqls) {
            if (StringUtils.isBlank(sql) || this.hasKeyword(sql, Lists.newArrayList("add", "index"))) {
                //sql为空或者索引先不执行
                continue;
            }
            if (this.hasKeyword(sql, Lists.newArrayList("modify", "column"))) {
                this.executeSql(sql);
                continue;
            }
            String tableName = sql.toLowerCase().split("table")[1].split("add")[0].trim();
            String columnName = this.getColumnName(sql);
            List<Map<String, Object>> columnList = tableMap.get(tableName);
            if (CollectionUtils.isEmpty(columnList)) {
                continue;
            }
            log.info("表：" + tableName + ",现有字段：" + this.getColumnList(columnList));
            boolean hasColumn = false;
            for (Map<String, Object> column : columnList) {
                if (column.get("COLUMN_NAME").toString().equalsIgnoreCase(columnName)) {
                    hasColumn = true;
                    log.info("字段：" + columnName + " 已存在.");
                    break;
                }
            }
            if (!hasColumn) {
                this.executeSql(sql);
            }
        }
    }

    private String getColumnName(String sql) {
        String[] items = sql.toLowerCase().split(" ");
        Integer addIndex = null;
        for (int i = 0; i < items.length; i++) {
            if (StringUtils.isBlank(items[i])) {
                continue;
            }
            if ("add".equals(items[i].trim())) {
                addIndex = i;
            }
        }
        for (int j = addIndex + 1; j < items.length; j++) {
            if (StringUtils.isBlank(items[j]) || "column".equals(items[j])) {
                continue;
            }
            return items[j];
        }
        return null;
    }

    private boolean hasKeyword(String sql, List<String> keywords) {
        List<String> items = Lists.newArrayList(sql.toLowerCase().split(" "));
        return CollectionUtil.containsAll(items, keywords);
    }

    private String getColumnList(List<Map<String, Object>> columns) {
        return columns.stream().map(vo -> vo.get("COLUMN_NAME").toString()).collect(Collectors.joining(","));
    }

    private void executeSql(String sql) {
        log.info("执行 sql:" + sql);
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.error("执行 sql失败:" + sql, e);
        }
    }
}
