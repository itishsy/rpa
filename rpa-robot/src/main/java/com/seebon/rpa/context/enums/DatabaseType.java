package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1017", name = "数据库类型")
public enum DatabaseType {
    mysql("MYSQL"),
    h2("H2"),
    redis("Redis"),
    mongo("MongoDB");

    String name;
    DatabaseType(String name){
        this.name = name;
    }
}
