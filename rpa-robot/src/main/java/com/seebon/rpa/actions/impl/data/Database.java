package com.seebon.rpa.actions.impl.data;

import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.JdbcTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.DbExeType;
import com.seebon.rpa.utils.ELParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "数据库操作", targetType = JdbcTarget.class, order = 99)
public class Database extends AbstractAction {

    @Conditions({
            "selectList:sql,dataKey",
            "checkList:sql,dataKey",
            "returnList:sql,dataKey",
            "singleRow:sql,dataKey",
            "update:sql,dataKey",
            "batchUpdate:sql,dataList,columnList",
            "batchAdd:sql,dataList"
    })
    @ActionArgs(value = "执行类型", required = true, dict = DbExeType.class)
    private String exeType;

    @ActionArgs(value = "执行语句", required = true)
    private String sql;

    @ActionArgs(value = "结果变量")
    private String dataKey;

    @ActionArgs(value = "数据", required = true)
    private List<Map<String, Object>> dataList;

    @ActionArgs(value = "字段编码")
    private String columnList;

    @Override
    public void run() {
        JdbcTemplate jdbcTemplate = getTarget();
        RowMapper<Map<String, Object>> rowMapper = new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                Map<String, Object> map = new HashMap<String, Object>();
                ResultSetMetaData metaData;
                try {
                    metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int j = 1; j <= columnCount; j++) {
                        String columnName = metaData.getColumnLabel(j);
                        Object columnValue = resultSet.getObject(columnName);
                        map.put(columnName, columnValue);
                    }
                } catch (SQLException e) {
                    log.error("【Exception】", e);
                }
                return map;
            }
        };
        switch (DbExeType.valueOf(exeType)) {
            case selectList: {
                this.query(jdbcTemplate, rowMapper);
                break;
            }
            case checkList: {
                this.query(jdbcTemplate, rowMapper);
                break;
            }
            case returnList: {
                this.query(jdbcTemplate, rowMapper);
                break;
            }
            case singleRow: {
                Map<String, Object> map = jdbcTemplate.queryForObject(sql, rowMapper);
                ctx.addVariable(dataKey, map);
                break;
            }
            case batchUpdate:
                List<Map<String, String>> jsonArray = (List<Map<String, String>>) JSONObject.parseObject(columnList, List.class);
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public int getBatchSize() {
                        return dataList.size();
                    }

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Map<String, Object> json = dataList.get(i);
                        for (Map<String, String> map : jsonArray) {
                            String index = map.get("index");
                            String code = map.get("code");
                            String type = map.get("type");
                            Object value = map.get("value");
                            if (value != null && StringUtils.isBlank(value.toString())) {
                                value = json.get(code);
                            }
                            if ("int".equals(type)) {
                                ps.setInt(Integer.parseInt(index), Integer.parseInt(value.toString()));
                            } else if ("string".equals(type)) {
                                ps.setString(Integer.parseInt(index), value.toString());
                            }
                        }
                    }
                });
                break;
            case batchAdd:
                for (Map<String, Object> map : dataList) {
                    ctx.setVariable("item", map);
                    String sqlParse = ELParser.parse(sql, ctx.getVariables(), String.class);
                    log.info("批量新增：sql=" + sqlParse);
                    jdbcTemplate.update(sqlParse);
                }
                ctx.getVariables().remove("item");
                break;
            case update:
            default:
                int res = jdbcTemplate.update(sql);
                if (StringUtils.isNotBlank(dataKey)) {
                    ctx.setVariable(dataKey, res);
                }
                break;
        }
    }

    private void query(JdbcTemplate jdbcTemplate, RowMapper<Map<String, Object>> rowMapper) {
        List<Map<String, Object>> list = jdbcTemplate.query(sql, new Object[]{}, rowMapper);
        ctx.setVariable(dataKey, list);
    }
}
