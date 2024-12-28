package com.seebon.rpa.service.impl;

import com.seebon.rpa.BusinessException;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;
import com.seebon.rpa.repository.mysql.RobotArgsDefineDao;
import com.seebon.rpa.repository.mysql.RobotDao;
import com.seebon.rpa.repository.mysql.RobotDataDictDao;
import com.seebon.rpa.service.RobotArgsDefineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class RobotArgsDefineServiceImpl implements RobotArgsDefineService {
    @Autowired
    private RobotArgsDefineDao robotArgsDefineDao;
    @Autowired
    private RobotDao robotDao;
    @Autowired
    private RobotDataDictDao robotDataDictDao;

    @Override
    public int saveAppArgs(RobotArgsDefine argsDefine) {
        Example exampleArgs = new Example(RobotArgsDefine.class);
        exampleArgs.createCriteria().andEqualTo("groupName", argsDefine.getGroupName()).andEqualTo("argsCode", argsDefine.getArgsCode());
        List<RobotArgsDefine> robotAppArgs = robotArgsDefineDao.selectByExample(exampleArgs);
        //根据字段ID、字段名称查询是否有相同
        List<RobotArgsDefine> robotAppArgs1 = robotArgsDefineDao.selectArgs(argsDefine.getFieldKey(), argsDefine.getFieldName(), argsDefine.getArgsCode());
        if (robotAppArgs1.size() > 0) {
            throw new BusinessException("字段ID、字段名称不能重复添加!");
        }
        RobotArgsDefine robotAppArg = new RobotArgsDefine();
        BeanUtils.copyProperties(argsDefine, robotAppArg);
        robotAppArg.setArgsCode(argsDefine.getArgsCode());
        robotAppArg.setScope("app");
        robotAppArg.setGroupName(argsDefine.getGroupName());
        robotAppArg.setDisplayOrder(robotAppArgs.size() + 1);
        return robotArgsDefineDao.insert(robotAppArg);
    }


    @Override
    public List<RobotArgsDefine> findArgs(String scope, String argsCode) {
        Example example = new Example(RobotArgsDefine.class);
        example.createCriteria().andEqualTo("scope", scope).andEqualTo("argsCode", argsCode);
        example.setOrderByClause("display_order");
        return robotArgsDefineDao.selectByExample(example);
    }


    @Override
    public int deleteArgsDefine(String fieldKey, String appCode) {
        //查询出被删除的那条arg
        Example exampleArg = new Example(RobotArgsDefine.class);
        exampleArg.createCriteria().andEqualTo("fieldKey", fieldKey).andEqualTo("argsCode", appCode);
        RobotArgsDefine robotAppArg = robotArgsDefineDao.selectOneByExample(exampleArg);
        //查询此登录方式有多少条arg
        Example exampleArgs = new Example(RobotArgsDefine.class);
        exampleArgs.createCriteria().andEqualTo("groupName", robotAppArg.getGroupName());
        List<RobotArgsDefine> robotAppArgs = robotArgsDefineDao.selectByExample(exampleArgs);
        //如果删除的arg不是最后一个
        if (robotAppArg.getDisplayOrder() != robotAppArgs.size()) {
            //重新设置显示顺序
            robotArgsDefineDao.updateExecOrder(robotAppArg.getGroupName(), robotAppArg.getDisplayOrder());
        }
        return robotArgsDefineDao.deleteByExample(exampleArg);
    }


    @Override
    public int updateArgsDefine(RobotAppArgsVO argsVO, String fieldKey) {
        //根据字段ID、字段名称查询是否有相同除去本身
        List<RobotArgsDefine> argsDefines = robotArgsDefineDao.selectArgsById(argsVO.getFieldKey(), argsVO.getFieldName(), argsVO.getArgsCode(), argsVO.getId());
        if (CollectionUtils.isNotEmpty(argsDefines)) {
            throw new BusinessException("字段ID、字段名称不能重复添加!");
        }
        Example example = new Example(RobotArgsDefine.class);
        example.createCriteria().andEqualTo("fieldKey", fieldKey).andEqualTo("argsCode", argsVO.getComment());
        RobotArgsDefine robotAppArgs = new RobotArgsDefine();
        BeanUtils.copyProperties(argsVO, robotAppArgs);
        robotAppArgs.setArgsCode(argsVO.getComment());
        return robotArgsDefineDao.updateByExample(robotAppArgs, example);
    }

    @Override
    public List<RobotArgsDefine> listRobotArgsDefine(String appCode) {
        Example example = new Example(RobotArgsDefine.class);
        example.createCriteria().andEqualTo("scope", "robot").andEqualTo("argsCode", appCode);
        List<RobotArgsDefine> robotArgsDefines = robotArgsDefineDao.selectByExample(example);
        return robotArgsDefines;
    }
}
