package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotComponent;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public interface RobotComponentService {

    List<RobotComponentVO> getByRecord();

    int addComponents(String component, String comment, MultipartFile installJar) throws IOException;

    List<RobotComponentVO> getByHistory(String component);

    RobotComponentVO upgrade(MultipartFile installJar, String component, String version, String comment) throws IOException;

    int activate(String component, String version) throws IOException;

    int enableStop(Integer id);

    List<RobotComponent> queryByComment(String comment);
}
