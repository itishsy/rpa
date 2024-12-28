package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotLoginAuth;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.EmployeeChangeTrackDTO;
import com.seebon.rpa.entity.robot.dto.GenerateQueueDTO;
import com.seebon.rpa.entity.robot.dto.RobotLoginAuthDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskQueueGenerate;
import com.seebon.rpa.entity.robot.vo.GenerateQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotLoginAuthVO;
import com.seebon.rpa.entity.robot.vo.RobotTrackVO;

import java.util.List;
import java.util.Map;


public interface RobotLoginAuthService {

    RobotLoginAuth addLoginAuthOrAddQueue(RobotLoginAuthDTO robotLoginAuthDTO);

    IgGridDefaultPage<RobotLoginAuthVO> pageList(Map<String, Object> map);

    Boolean startLogin(Integer id);

    Integer curUserNoLoginCount(Map<String, Object> map);

    Boolean updateLoginStatus(ChangeLoginStatusDTO changeLoginStatusDTO);

    Boolean updateLoginStatus(String taskCode, String declareSystem,Integer clientId, Integer sessionKeepStatus);

    Map<String, Object> checkLoginAuthInfo(String declareSystem, String accountNumber, String orgName, String phoneNumber);

    List<RobotLoginAuthVO> queryList(Map<String, Object> map);

    // 入数据处理并返回登录或执行预估时间信息
    GenerateQueueVO generateQueueOrLoginAuth(List<GenerateQueueDTO> generateQueueDTOList);

    // 获取执行轨迹状态
    RobotTrackVO queryEmployeeChangeTrackStatus(EmployeeChangeTrackDTO employeeChangeTrackDTO);

    // 获取执行轨迹信息
    RobotTrackVO queryEmployeeChangeTrackInfo(EmployeeChangeTrackDTO employeeChangeTrackDTO);

    // 查找队列生成记录
    List<RobotTaskQueueGenerate> findTaskQueueGenerate(String queryStr, Integer limit, String orderBy, String orderDirection);
}
