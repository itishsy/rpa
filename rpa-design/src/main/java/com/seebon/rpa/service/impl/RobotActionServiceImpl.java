package com.seebon.rpa.service.impl;

import com.seebon.rpa.entity.robot.RobotAction;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.RobotDataDict;
import com.seebon.rpa.entity.robot.vo.DataDictVO;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;
import com.seebon.rpa.repository.mysql.RobotActionDao;
import com.seebon.rpa.service.RobotActionService;
import com.seebon.rpa.service.RobotArgsDefineService;
import com.seebon.rpa.service.RobotDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RobotActionServiceImpl implements RobotActionService {


    @Autowired
    private RobotActionDao robotActionDao;

    @Autowired
    private RobotArgsDefineService argsDefineService;

    @Autowired
    private RobotDataDictService dataDictService;

    /**
     * 所有操作指令
     *
     * @return
     */
    @Override
    public List<RobotAction> findAll() {
        return robotActionDao.findAll();
    }

    @Override
    public String findTargetType(String actionCode) {
        Example example1 = new Example(RobotAction.class);
        example1.createCriteria().andEqualTo("actionCode", actionCode);
        RobotAction robotAction = robotActionDao.selectOneByExample(example1);
        if (robotAction != null && robotAction.getTargetType() != null) {
            return robotAction.getTargetType();
        }
        return "customized";
    }

    /**
     * 动态字段查询
     * @param argsCode
     * @return
     */
    @Override
    public List<RobotAppArgsVO> findActionArgs(String argsCode) {
        List<RobotArgsDefine> argsDefines = argsDefineService.findArgs("action",argsCode);
        return convertToVO(argsDefines);
    }

    @Override
    public List<RobotAppArgsVO> findTargetArgs(String argsCode) {
        String targetCode = findTargetType(argsCode);
        List<RobotArgsDefine> argsDefines = argsDefineService.findArgs("target",targetCode);
        return convertToVO(argsDefines);
    }

    private List<RobotAppArgsVO> convertToVO(List<RobotArgsDefine> argsDefines){
        List<RobotAppArgsVO> robotAppArgsVOS = new ArrayList<>();
        for(RobotArgsDefine argsDefine : argsDefines){
            RobotAppArgsVO appArgsVO = new RobotAppArgsVO();
            BeanUtils.copyProperties(argsDefine,appArgsVO);
            String dictKey = argsDefine.getDictKey();
            if (StringUtils.isNotBlank(dictKey) && !"0".equals(dictKey)) {
                List<RobotDataDict> dicts = dataDictService.findList(dictKey);
                List<DataDictVO> dictVOS = new ArrayList<>();
                for (RobotDataDict dict : dicts) {
                    dictVOS.add(new DataDictVO(dict.getDictCode(), dict.getDictName()));
                }
                if (dictVOS.size() > 0) {
                    appArgsVO.setRobotDataDicts(dictVOS);
                }
            }
            robotAppArgsVOS.add(appArgsVO);
        }
        return robotAppArgsVOS;
    }
}
