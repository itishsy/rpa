package com.seebon.rpa.service.impl;

import cn.hutool.core.io.FileUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotAppEnv;
import com.seebon.rpa.entity.robot.dto.design.RobotAppEnvVO;
import com.seebon.rpa.repository.mysql.RobotAppDao;
import com.seebon.rpa.repository.mysql.RobotAppEnvDao;
import com.seebon.rpa.service.RobotAppEnvService;
import com.seebon.rpa.service.RobotAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RobotAppEnvServiceImpl implements RobotAppEnvService {
    @Autowired
    private RobotAppEnvDao appEnvDao;
    @Autowired
    private RobotAppDao robotAppDao;

    @Autowired
    private RobotAppService robotAppService;
    @Override
    public IgGridDefaultPage<RobotAppEnv> listPage(Map<String, Object> params) {
        if (!params.containsKey("isPage")) {
            throw new BusinessException("非查询参数");
        }
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer size = Integer.parseInt(params.get("size").toString());
        PageHelper.startPage(page, size);
        Page<RobotAppEnv> list = (Page<RobotAppEnv>) appEnvDao.selectByParams(params);
        return new IgGridDefaultPage<RobotAppEnv>(list, (int) list.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addRobotAppEnv(RobotAppEnv robotAppEnv) {
        if (StringUtils.isBlank(robotAppEnv.getAppCode())) {
            throw new BusinessException("应用必填" + robotAppEnv.getAppCode());
        }
        if (robotAppEnv.getId() != null) {
            robotAppEnv.setUpdateTime(new Date());
            appEnvDao.updateByPrimaryKeySelective(robotAppEnv);
        } else {
            robotAppEnv.setCreateTime(new Date());
            robotAppEnv.setUpdateTime(new Date());
            appEnvDao.insertSelective(robotAppEnv);
        }
        //设置appCode状态为未发布
        robotAppDao.updateStatus(robotAppEnv.getAppCode());
        return 1;
    }

    @Override
    public RobotAppEnv getById(Integer id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        List<RobotAppEnv> list = appEnvDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Integer deleteById(Integer id) {
        //设置appCode状态为未发布
        RobotAppEnv robotAppEnv = appEnvDao.selectByPrimaryKey(id);
        robotAppDao.updateStatus(robotAppEnv.getAppCode());
        return appEnvDao.deleteByPrimaryKey(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addRobotApps(RobotAppEnvVO robotAppEnvVO) {
        if (robotAppEnvVO.getPaths() == null || robotAppEnvVO.getPaths().length == 0){
            throw new BusinessException("路径错误，路径不能为空");
        }
        String[] path = robotAppEnvVO.getPaths();
        for (String s : path) {

            RobotAppEnv robotAppEnv = new RobotAppEnv();
            BeanUtils.copyProperties(robotAppEnvVO,robotAppEnv);
            if (StringUtils.isEmpty(robotAppEnv.getAppName())){
                throw new BusinessException("应用必填"+robotAppEnv.getAppCode());
            }
            robotAppEnv.setAppCode(robotAppService.queryAppCode(robotAppEnv.getAppName()));
            robotAppEnv.setPath(s);
            addRobotAppEnv(robotAppEnv);
        }
        return path.length;
    }
}
