package com.seebon.rpa.rest;

import com.seebon.rpa.entity.robot.RobotDataDict;
import com.seebon.rpa.service.RobotDataDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "数据字典", tags = "系统管理")
@RestController
@RequestMapping("/data")
public class DataDictController {

    @Autowired
    private RobotDataDictService dataDictService;

    @ApiOperation(value = "数据字典-所有字典Key")
    @PostMapping("/dict/allKey")
    public List<String> findAllKey() {
        return dataDictService.findAllKey();
    }

    @ApiOperation(value = "数据字典-根据Key查询")
    @PostMapping("/dict/{key}")
    public List<RobotDataDict> findByKey(@PathVariable("key") String key) {
        return dataDictService.findList(key);
    }

    @ApiOperation(value = "数据字典-根据parentKey查询")
    @PostMapping("/dict/keys")
    public List<String> findKeys(@RequestParam("parentKey") String parentKey) {
        return dataDictService.findKeys(parentKey);
    }

    @PostMapping("/getDictByCode")
    @ApiOperation("通过dictCode获取字典集合")
    public RobotDataDict getDictByCode(@RequestParam("dictCode") String dictCode){
        RobotDataDict dict = dataDictService.getDictByCode(dictCode);
        return dict;
    }
}
