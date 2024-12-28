package com.seebon.rpa.repository.config;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface MybatisMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
