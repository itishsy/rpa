package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.auth.po.SysButton;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:12:11
 */
public interface SysButtonDao extends Mapper<SysButton>, MySqlMapper<SysButton> {

    int selectCountByParams(Map<String,Object> params);

    List<Integer> selectButtonIdsByNotModuleCodes(@Param("moduleCodes") List<String> moduleCodes);
}
