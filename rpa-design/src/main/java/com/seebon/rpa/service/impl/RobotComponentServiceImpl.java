package com.seebon.rpa.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.config.WorkPathConfig;
import com.seebon.rpa.entity.robot.RobotComponent;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.repository.mysql.RobotComponentDao;
import com.seebon.rpa.service.CustomerCommandService;
import com.seebon.rpa.service.RobotComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("versionService")
@Slf4j
public class RobotComponentServiceImpl implements RobotComponentService {

    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String ctx;

    @Resource
    private RobotComponentDao robotComponentDao;
    @Autowired
    private CustomerCommandService customerCommandService;


    /**
     * 列表
     *
     * @return
     */
    @Override
    public List<RobotComponentVO> getByRecord() {
        List<RobotComponentVO> robotComponentVOS = new ArrayList<>();
        List<RobotComponent> robotComponents = robotComponentDao.selectByAll();
        robotComponents.forEach(robotComponent -> {
            RobotComponentVO robotComponentVO = new RobotComponentVO();
            StringBuffer sb = new StringBuffer();
            String component = robotComponent.getComponent();//组件名
            String version = robotComponent.getVersion();//版本号
            String fileName = robotComponent.getFileName();//文件名
            sb.delete(0, sb.length());
            String path = sb
                    .append(component).append("/")
                    .append(version).append("/")
                    .append(fileName)
                    .toString();
            //http://172.172.4.8:9527/api/download/release/rpa-console-web/abc.jar
            String url = ctx + "/download/jar/" + path;
            robotComponentVO.setFilePath(url);
            BeanUtils.copyProperties(robotComponent, robotComponentVO);
            robotComponentVOS.add(robotComponentVO);
        });
        return robotComponentVOS;
    }

    /**
     * 添加组件
     *
     * @param component
     * @param comment
     * @param installJar
     * @return
     */
    @Override
    public int addComponents(String component, String comment, MultipartFile installJar) throws IOException {
        //判断名称是否重复
        Example example = new Example(RobotComponent.class);
        example.createCriteria()
                .andEqualTo("component", component);
        List<RobotComponent> robotComponents = robotComponentDao.selectByExample(example);
        if (robotComponents.size() > 0) {
            throw new BusinessException("已存相同名称的组件");
        }
        StringBuffer sb = new StringBuffer();
        String fileName = installJar.getOriginalFilename();//文件名
        log.info("上传后的文件名：" + fileName);
        //文件路径： /download/comName/releaseVersion/fileName
        String path = sb.append(WorkPathConfig.getWorkJar()).append(File.separator)
                .append(component).append(File.separator)
                .append("v0.0.1").append(File.separator)
                .append(fileName).toString();
        log.info("上传的文件路径：" + path);
        log.info(WorkPathConfig.getWorkJar());
        File file = new File(WorkPathConfig.getWorkJar() + "/" + component + "/v0.0.1/", fileName);
        log.info(file.getAbsolutePath());
        if (!file.exists()) {//不存在
            file.getParentFile().mkdirs();//创建
        }
        installJar.transferTo(file);

        User user = SecurityContext.currentUser();
        RobotComponentVO robotComponentVO = new RobotComponentVO();
        robotComponentVO.setComponent(component);
        robotComponentVO.setVersion("v0.0.1");
        robotComponentVO.setComment(comment);
        robotComponentVO.setStatus(1);
        robotComponentVO.setReleaseTime(new Date());
        robotComponentVO.setFileName(fileName);//文件名称
        robotComponentVO.setFilePath(path);//文件路径
        robotComponentVO.setCreateId((int) user.getId());
        robotComponentVO.setCreateName(user.getName());

        JSONObject jsonObjects = new JSONObject();
        jsonObjects.put("component", component);
        jsonObjects.put("type", "addComponent");
        int count= robotComponentDao.addComponent(robotComponentVO);
//        customerCommandService.addCustomerCommand("component", JSON.toJSONString(jsonObjects), "");
        return count;
    }

    /**
     * 历史
     *
     * @param component
     * @return
     */
    @Override
    public List<RobotComponentVO> getByHistory(String component) {
        List<RobotComponentVO> robotComponentVOS = new ArrayList<>();
        List<RobotComponent> robotComponents = robotComponentDao.selectByHistory(component);
        robotComponents.forEach(robotComponent -> {
            RobotComponentVO robotComponentVO = new RobotComponentVO();
            StringBuffer sb = new StringBuffer();
            sb.delete(0, sb.length());
            String path = sb
                    .append(component).append("/")
                    .append(robotComponent.getVersion()).append("/")
                    .append(robotComponent.getFileName())
                    .toString();
            //http://172.172.4.8:9527/api/download/release/rpa-console-web/abc.jar
            String url = ctx + "/download/jar/" + path;
            BeanUtils.copyProperties(robotComponent, robotComponentVO);
            robotComponentVO.setFilePath(url);
            robotComponentVOS.add(robotComponentVO);
        });
        return robotComponentVOS;
    }

    /**
     * 升级
     *
     * @param installJar
     * @param component
     * @param version
     * @param comment
     * @return
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RobotComponentVO upgrade(MultipartFile installJar, String component, String version, String comment) throws IOException {
        RobotComponentVO robotComponentVO = new RobotComponentVO();
        StringBuffer sb = new StringBuffer();
        String fileName = installJar.getOriginalFilename();
        log.info("上传后的文件名：" + fileName);
        sb.delete(0, sb.length());
        //文件路径： /download/comName/releaseVersion/fileName
        String path = sb.append(WorkPathConfig.getWorkJar()).append(File.separator)
                .append(component).append(File.separator)
                .append(version).append(File.separator)
                .append(fileName).toString();
        log.info("上传的文件路径：" + path);
        //如果原组件为运行中，则修改为历史版本，如果是停止，则还是停止
        robotComponentDao.updateStatus(component);
      //  robotComponentDao.updateStatusIsStop(component);
        int i = Integer.valueOf(version.substring(5));//截取旧版本号
        String newVersion = new DecimalFormat().format(i + 1); //新版本号
        robotComponentVO.setVersion("v0.0." + newVersion);
        robotComponentVO.setComponent(component);
        robotComponentVO.setComment(comment);
        robotComponentVO.setFileName(fileName);
        robotComponentVO.setFilePath(path);
        robotComponentVO.setReleaseTime(new Date());
        robotComponentVO.setStatus(1);
        User user = SecurityContext.currentUser();
        robotComponentVO.setCreateName(user.getName());
        File file = new File(WorkPathConfig.getWorkJar() + "/" + component + "/" + robotComponentVO.getVersion() + "/", fileName);
        log.info(file.getAbsolutePath());
        if (!file.exists()) {//不存在
            file.getParentFile().mkdir();//创建
        }
        installJar.transferTo(file);

        robotComponentDao.addComponent(robotComponentVO);
        JSONObject jsonObjects = new JSONObject();
        jsonObjects.put("component", component);
        jsonObjects.put("type", "upgrade");
        customerCommandService.addCustomerCommand("component", JSON.toJSONString(jsonObjects), "");
        return robotComponentVO;

    }

    /**
     * 使用此版本
     *
     * @param component
     * @param version
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int activate(String component, String version) throws IOException {
        Example example = new Example(RobotComponent.class);
        example.createCriteria()
                .andEqualTo("component", component)
                .andEqualTo("version", version);
        RobotComponent robotComponent = robotComponentDao.selectOneByExample(example);

        //查找当前组件最新的版本
        List<RobotComponentVO> robotComponentVOS = robotComponentDao.selectNewVersion(component);
        RobotComponentVO robotComponentVO = robotComponentVOS.get(0);
        if (robotComponentVO == null){
            throw new BusinessException("未发现当前组件对应的版本");
        }
        //修改当前组件状态为历史版本
        robotComponentDao.updateStatus(component);

        StringBuffer sb = new StringBuffer();
        String fileName = robotComponent.getFileName();
        sb.delete(0, sb.length());
        //文件路径： /download/comName/releaseVersion/fileName
        String path = sb.append(WorkPathConfig.getWorkJar()).append(File.separator)
                .append(component).append(File.separator)
                .append(version).append(File.separator)
                .append(fileName)
                .toString();
        RobotComponentVO robotComponentVO1 = new RobotComponentVO();

        int i = Integer.valueOf(robotComponentVO.getVersion().substring(5));//截取旧版本号
        String newVersion = new DecimalFormat().format(i + 1); //新版本号
        robotComponentVO1.setComponent(component);
        robotComponentVO1.setVersion("v0.0." + newVersion);
        robotComponentVO1.setComment("引用【"+version+"】进行发布");
        robotComponentVO1.setStatus(1);
        robotComponentVO1.setReleaseTime(new Date());
        robotComponentVO1.setFilePath(path);
        robotComponentVO1.setFileName(fileName);
        File file = new File(WorkPathConfig.getWorkJar() + "/" + component + "/" + robotComponentVO1.getVersion(), fileName);
        log.info(file.getAbsolutePath());
        if (!file.exists()) {//不存在
            file.getParentFile().mkdirs();//创建
        }


        //复制使用版本的jar文件至最新版本下
        File source = new File(WorkPathConfig.getWorkJar() + "/" + component + "/" + version + "/" + fileName);//使用版本路径
        File dest = new File(WorkPathConfig.getWorkJar() + "/" + component + "/" + robotComponentVO1.getVersion() + "/" + fileName);//最新版本路径
        Files.copy(source.toPath(), dest.toPath());
        return robotComponentDao.addComponent(robotComponentVO1);
    }

    /**
     * 启用/停止
     *
     * @param id
     * @return
     */
    @Override
    public int enableStop(Integer id) {
        RobotComponent robotComponent = robotComponentDao.selectByPrimaryKey(id);
        if (robotComponent.getStatus() == 0) {
            return robotComponentDao.enableStatus(robotComponent.getId());
        }
        return robotComponentDao.stopStatus(robotComponent.getId());
    }

    @Override
    public List<RobotComponent> queryByComment(String comment) {
        return robotComponentDao.selectByComment(comment);
    }


}
