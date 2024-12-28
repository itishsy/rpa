package com.seebon.rpa.actions.target.impl;

import com.seebon.rpa.SpringBeanHolder;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.actions.target.Target;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.enums.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Slf4j
@ActionTarget
public class TargetsTarget extends AbstractTarget<Target> {

    @Setter
    @Conditions({"driverTarget:type,optionMap",
            "elementTarget:attrType,attrValue",
            "sheetTarget:sheetIndex",
            "fileTarget:filePath",
            "responseTarget:url,method,params",
            "jdbcTarget:dbType,username,password",
            "objectTarget:text"})
    @ActionArgs(value = "操作对象", dict = TargetName.class)
    private String targetName;

    /*driverTarget*/
    @ActionArgs(value = "浏览器", dict = BrowserType.class, comment = "当前浏览器")
    private String type;
    @ActionArgs("驱动参数")
    private Map<String, Object> optionMap;

    /*elementTarget*/
    @ActionArgs(value = "属性", dict = AttributeType.class)
    private String attrType;
    @ActionArgs(value = "属性值")
    private String attrValue;

    /*fileTarget、sheetTarget*/
    @ActionArgs("文件路径")
    private String filePath;

    /*sheetTarget*/
    @ActionArgs("表格索引")
    private Integer sheetIndex;

    /*responseTarget、jdbcTarget*/
    @ActionArgs("URL")
    private String url;
    @ActionArgs(value = "请求方法", required = true, dict = HttpMethod.class)
    private String method;
    @ActionArgs(value = "请求参数")
    private Object params;

    /*jdbcTarget*/
    @ActionArgs(value = "数据库类型", dict = DatabaseType.class)
    private String dbType;
    @ActionArgs("账号")
    private String username;
    @ActionArgs("密码")
    private String password;

    /*objectTarget*/
    @ActionArgs(value = "操作对象", parsed = false, comment = "变量名、Json、表达式")
    private String text;

    @Override
    public Target fetchTarget() {
        if (StringUtils.isBlank(targetName)) {
            return SpringBeanHolder.getBean(NoneTarget.class);
        } else {
            return SpringBeanHolder.getBean(targetName);
        }
    }
}
