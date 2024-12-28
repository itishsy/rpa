package com.seebon.rpa.runner;

import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.*;
import com.seebon.rpa.context.enums.ActionGroup;
import com.seebon.rpa.context.runtime.RobotConfigException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 初始化
 */
@Slf4j
@Component
public class RobotInitializer {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${developer.upset-actions:off}")
    private String upset;

    private JdbcTemplate jdbcTemplate;
    private Map<String, Object> actions = null;
    private Map<String, Object> targets = null;
    private Map<String, Object> enums = null;

    @PostConstruct
    public void initRobot() throws IllegalAccessException {
        if ("on".equals(upset) || "true".equals(upset)) {
            enums = new HashMap<>();
            actions = new HashMap<>(applicationContext.getBeansWithAnnotation(RobotAction.class));
            targets = new HashMap<>(applicationContext.getBeansWithAnnotation(ActionTarget.class));
            checkActionConfig();
            this.jdbcTemplate = getJdbcTemplate();
            if (this.jdbcTemplate != null) {
                initActions();
                initActionArgs();
                initTargetArgs();
            }
            actions = null;
            targets = null;
            enums = null;
        }
    }

    private JdbcTemplate getJdbcTemplate() {
        if (applicationContext.containsBean("mysqlDataSource")) {
            return new JdbcTemplate(applicationContext.getBean("mysqlDataSource", DataSource.class));
        }
        return null;
    }

    private void checkActionConfig() throws IllegalAccessException {
        Map<String, Object> beans = new HashMap<>(actions);
        beans.putAll(targets);
        for (String name : beans.keySet()) {
            Object action = beans.get(name);
            Field[] fields = action.getClass().getDeclaredFields();
            for (Field field : fields) {
                ActionArgs actionArgs = field.getAnnotation(ActionArgs.class);
                if (actionArgs != null) {
                    if (actionArgs.dict().isEnum() && !isSelect(actionArgs.style())) {
                        throw new RobotConfigException("選擇框必須有候選項: bean=" + name + ",field=" + field.getName());
                    }

                    field.setAccessible(true);
                    Object defaultValue = field.get(action);
                    if (defaultValue != null) {
                        throw new RobotConfigException("ActionArgs字段不允许有默认值");
                    }
                }
            }
        }
    }

    private boolean isSelect(DynamicFieldType dynamicFieldType) {
        return dynamicFieldType.equals(DynamicFieldType.undefined) ||
                dynamicFieldType.equals(DynamicFieldType.multiple) ||
                dynamicFieldType.equals(DynamicFieldType.single) ||
                dynamicFieldType.equals(DynamicFieldType.multipleDropList) ||
                dynamicFieldType.equals(DynamicFieldType.singleDropList);
    }

    private void initActions() {
        Map<String, String> actionDict = new HashMap<>();
        int order = 1;
        for (ActionGroup actionGroup : ActionGroup.values()) {
            DataDictKey dataDictKey = actionGroup.getClass().getAnnotation(DataDictKey.class);
            try {
                Field field = actionGroup.getClass().getDeclaredField("name");
                field.setAccessible(true);
                String dictKey = dataDictKey.value();
                String dictCode = actionGroup.name();
                String dictName = field.get(actionGroup).toString();
                actionDict.put(dictCode, dictName);
                upsetDict(order, dictKey, dictCode, dictName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            order++;
        }


        jdbcTemplate.execute("truncate table `robot_action`;");
        for (String actionName : actions.keySet()) {
            Object action = actions.get(actionName);
            String[] names = action.getClass().getName().split("\\.");
            String groupName = actionDict.get(names[names.length - 2]);// convertGroupName(action.getClass().getName());
            RobotAction robotAction = action.getClass().getAnnotation(RobotAction.class);
            jdbcTemplate.execute(String.format("INSERT INTO `robot_action` " +
                            "(`action_code`,`action_name`,`group_name`,`comment`,`target_type`,`display_order`)" +
                            "VALUES('%s','%s','%s','%s','%s',%d);",
                    actionName,
                    robotAction.name(),
                    groupName,
                    robotAction.comment(),
                    robotAction.targetType().getSimpleName(),
                    robotAction.order()
            ));
        }
    }

    private void upsetDict(int order, String dictKey, String dictCode, String dictName) {
        try {
            String query = "SELECT id FROM robot_data_dict WHERE dict_key='"
                    + dictKey + "' and dict_code = '" + dictCode + "'";
            Integer id = jdbcTemplate.queryForObject(query, Integer.class);
            jdbcTemplate.update("UPDATE `robot_data_dict` " +
                    "set `dict_name`=?,`display_order`=? where `id`=?", dictName, order, id);
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO `robot_data_dict` " +
                            "(`dict_key`,`dict_code`,`dict_name`,`display_order`)" +
                            "VALUES(?,?,?,?);",
                    dictKey, dictCode, dictName, order);
        }
    }

    private void initActionArgs() {
        String inAction = "";
        for (String name : actions.keySet()) {
            Object action = actions.get(name);
            Field[] fields = action.getClass().getDeclaredFields();
            if (fields.length == 0) {
                continue;
            }
            int display_order = 0;
            String in = "";
            for (Field field : fields) {
                upsetArgs("action", name, field, display_order++);
                upsetDictArgs(field);
                in += ",'" + field.getName() + "'";
            }
            jdbcTemplate.update("delete from robot_args_define WHERE scope='action' and args_code = '"
                    + name + "' and field_key not in(" + in.substring(1) + ")");
            inAction += ",'" + name + "'";

        }
        jdbcTemplate.update("delete from robot_args_define WHERE scope='action' and args_code not in(" + inAction.substring(1) + ")");
    }

    private void initTargetArgs() throws IllegalAccessException {
        String inTarget = "";
        for (String name : targets.keySet()) {
            Object target = targets.get(name);
            Field[] fields = target.getClass().getDeclaredFields();
            if (fields.length == 0) {
                continue;
            }
            int display_order = 0;
            String in = "";
            for (Field field : fields) {
                upsetArgs("target", name, field, display_order++);
                upsetDictArgs(field);
                in += ",'" + field.getName() + "'";
            }
            jdbcTemplate.update("delete from robot_args_define WHERE scope='target' and args_code = '"
                    + name + "' and field_key not in(" + in.substring(1) + ")");
            inTarget += ",'" + name + "'";
        }
        jdbcTemplate.update("delete from robot_args_define WHERE scope='target' and args_code not in(" + inTarget.substring(1) + ")");
    }

    private void upsetArgs(String scope, String argsCode, Field field, int display_order) {
        ActionArgs actionArgs = field.getAnnotation(ActionArgs.class);
        if (actionArgs == null) {
            return;
        }
        String field_key = field.getName();
        String field_name = actionArgs.value();
        String comment = actionArgs.comment();
        String field_type = convertFieldType(field);
        DataDictKey dataDictKey = null;
        if (actionArgs.dict().isEnum()) {
            dataDictKey = actionArgs.dict().getAnnotation(DataDictKey.class);
            if (!enums.containsKey(dataDictKey.value())) {
                enums.put(dataDictKey.value(), actionArgs.dict());
            } else {
                if (!enums.get(dataDictKey.value()).equals(actionArgs.dict())) {
                    throw new RobotConfigException("配置错误。字典编码重复: dict=" + dataDictKey.value());
                }
            }

            try {
                String query = "SELECT id FROM robot_data_key WHERE dict_key=" + dataDictKey.value();
                Integer id = jdbcTemplate.queryForObject(query, Integer.class);
                jdbcTemplate.update("UPDATE `robot_data_key` " +
                                "set `dict_key`=?,`dict_value`=?,`status`=1 where `id`=?",
                        dataDictKey.value(), dataDictKey.name(), id);
            } catch (Exception e) {
                jdbcTemplate.update("INSERT INTO `robot_data_key` " +
                                "(`dict_key`,`dict_value`,`status`,`comment`)" +
                                "VALUES(?,?,?,?);",
                        dataDictKey.value(), dataDictKey.name(), 1, "");
            }
        }
        String dictKey = "";
        if (!actionArgs.style().equals(DynamicFieldType.undefined)) {
            field_type = actionArgs.style().name();
        } else if (dataDictKey != null) {
            field_type = DynamicFieldType.singleDropList.name();
            dictKey = dataDictKey.value();
        }

        String cond = "";
        Conditions conditions = field.getAnnotation(Conditions.class);
        if (conditions != null) {
            StringBuilder sb = new StringBuilder();
            for (String str : conditions.value()) {
                sb.append(",");
                sb.append("\"" + str.split(":")[0] + "\"");
                sb.append(":");
                sb.append("\"" + str.split(":")[1] + "\"");
            }
            if (sb.length() > 0) {
                cond = "{" + sb.toString().substring(1) + "}";
            }
        }

        int required = actionArgs.required() ? 1 : 0;
        try {
            String query = "SELECT id FROM robot_args_define WHERE scope='"
                    + scope + "' and args_code = '" + argsCode + "' and field_key='" + field_key + "'";
            Integer id = jdbcTemplate.queryForObject(query, Integer.class);
            jdbcTemplate.update("UPDATE `robot_args_define` " +
                            "set `field_name`=?,`field_type`=?,`field_name`=?,`dict_key`=?,`display_order`=?,`required`=?,`cond`=? where `id`=?",
                    field_name, field_type, field_name, dictKey, display_order, required, cond, id);
        } catch (EmptyResultDataAccessException e) {
            jdbcTemplate.update("INSERT INTO `robot_args_define` " +
                            "(`scope`,`args_code`,`field_key`,`field_name`,`field_type`,`dict_key`,`display_order`,`required`,`cond`,`comment`)" +
                            "VALUES(?,?,?,?,?,?,?,?,?,?);",
                    scope, argsCode, field_key, field_name,
                    field_type, dictKey, display_order, required, cond, comment);
        }
    }

    private String convertFieldType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isEnum()) {
            return "singleDropList";
        }
        if (fieldType.equals(Boolean.class)) {
            return "single";
        }
        if (fieldType.equals(Date.class)) {
            return "date";
        }
        if (fieldType.equals(Integer.class) || fieldType.equals(int.class)
                || fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return "number";
        }
        return "text";
    }

    private void upsetDictArgs(Field field) {
        ActionArgs actionArgs = field.getAnnotation(ActionArgs.class);
        if (actionArgs == null) {
            return;
        }
        if (actionArgs.dict().isEnum()) {
            int order = 1;
            String inStr = "";
            DataDictKey dataDictKey = actionArgs.dict().getAnnotation(DataDictKey.class);
            String dictKey = dataDictKey.value();
            for (Object o : actionArgs.dict().getEnumConstants()) {
                String dictCode = ((Enum) o).name();
                String dictName = "";
                try {
                    Field field1 = o.getClass().getDeclaredField("name");
                    field1.setAccessible(true);
                    dictName = field1.get(o).toString();

                    upsetDict(order, dictKey, dictCode, dictName);
                    order++;
                    inStr += ",'" + dictCode + "'";
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            jdbcTemplate.update("delete from robot_data_dict WHERE dict_key='" + dictKey + "' and dict_code not in(" + inStr.substring(1) + ")");
        }
    }
}
