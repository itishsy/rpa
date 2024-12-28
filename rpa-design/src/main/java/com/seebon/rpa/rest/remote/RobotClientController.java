package com.seebon.rpa.rest.remote;

import com.seebon.rpa.entity.robot.CustomerCommand;
import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotClientPriority;
import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.dto.RobotStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.repository.mysql.RobotClientDao;
import com.seebon.rpa.service.CustomerCommandService;
import com.seebon.rpa.service.RobotClientPriorityService;
import com.seebon.rpa.service.RobotClientService;
import com.seebon.rpa.service.RobotUsbKeyService;
import com.seebon.rpa.service.impl.component.OcrComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Api(value = "机器人客户端同步", tags = "通讯中心")
@RestController
@RequestMapping("/client")
public class RobotClientController {
    @Autowired
    private RobotClientService clientService;
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private OcrComponent ocrComponent;
    @Autowired
    private CustomerCommandService customerCommandService;
    @Autowired
    private RobotClientPriorityService clientPriorityService;
    @Autowired
    private RobotUsbKeyService robotUsbKeyService;

    @Value("${work.path}")
    private String workPath;

    @Value("${work.client.dirs}")
    private String workDirs;

    @ApiOperation(value = "机器人监控-创建机器人客户端")
    @PostMapping("/add")
    @Deprecated
    public int addClient(String machineCode, String workPath, String ip, Integer port) {
        return 1;
    }

    @ApiOperation(value = "机器人监控-客户端核对")
    @PostMapping("/check")
    public int clientCheck(String machineCode) {
        return clientService.check(machineCode);
    }

    @ApiOperation(value = "机器人监控-心跳检测")
    @PostMapping("/heartbeat")
    public String heartbeat(String machineCode, Integer status) {
        return clientService.heartbeat(machineCode, status);
    }

    @ApiOperation(value = "机器人监控-接收日志文件")
    @PostMapping("/log")
    public void receiveLog(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(workPath + "\\" + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "机器人安装-目录")
    @PostMapping("/install/dirs")
    public String workDirs() {
        return workDirs;
    }

    @ApiOperation(value = "获取客户机器认证凭证")
    @PostMapping("/find/token")
    public String findToken(String machineCode) {
        RobotClient robotClient = robotClientDao.selectClient(machineCode);
        if (robotClient != null && robotClient.getToken() != null) {
            return robotClient.getToken();
        }
        return "";
    }

    @ApiOperation(value = "机器人监控-获取最新版本号")
    @PostMapping("/latest/{component}")
    public RobotComponentVO checkComponent(@PathVariable("component") String component) {
        return clientService.checkComponentVersion(component);
    }

    @ApiOperation(value = "ocr识别")
    @PostMapping("/ocrText")
    public String ocrText(@RequestBody OcrReqVO reqVO) {
        return ocrComponent.ocrText(reqVO);
    }
    @ApiOperation(value = "自有百度ocr识别")
    @PostMapping("/ocrTextBySelfBaidu")
    public String ocrTextBySelfBaidu(@RequestBody OcrReqVO reqVO) {
        return ocrComponent.ocrTextBySelfBaidu(reqVO);
    }
    @ApiOperation(value = "百度ocr识别")
    @PostMapping("/ocrTextByBaidu")
    public String ocrTextByBaidu(@RequestBody OcrReqVO reqVO) {
        return ocrComponent.ocrTextByBaidu(reqVO);
    }

    @ApiOperation(value = "ocr识别验证码")
    @PostMapping("/ocrCaptcha")
    public String ocrCaptcha(@RequestBody OcrReqVO reqVO) {
        return ocrComponent.ocrCaptcha(reqVO);
    }

    @ApiOperation(value = "ocr识别验证码-新")
    @PostMapping("/ocrCaptchaNew")
    public String ocrCaptchaNew(@RequestBody OcrReqVO reqVO) {
        return ocrComponent.ocrCaptchaNew(reqVO);
    }

    @ApiOperation(value = "运营后台-盒子和申报账户统计信息")
    @PostMapping("/robotStatistics")
    public RobotStatisticsDTO robotStatistics() {
        return clientService.robotStatistics();
    }

    @PostMapping("/addCustomerCommand")
    public void addCustomerCommand(@RequestBody CustomerCommand command) {
        customerCommandService.addCustomerCommand(command.getClientId(), command.getCommand(), command.getArgs(), command.getRemark());
    }

    @ApiOperation(value = "获取执行优先级")
    @PostMapping("/listPriority/{machineCode}")
    public List<RobotClientPriority> listPriority(@PathVariable(value = "machineCode") String machineCode) {
        return clientPriorityService.list(machineCode);
    }

    @ApiOperation(value = "回调执行任务")
    @PostMapping("/callbackPriority/{machineCode}")
    public void callbackPriority(@PathVariable(value = "machineCode") String machineCode) {
        clientPriorityService.callbackPriority(machineCode);
    }

    @ApiOperation(value = "回调执行任务")
    @PostMapping("/getUsbKey")
    public UsbKeyResponse getUsbKey(@RequestBody RobotUsbKey usbKey) {
        return robotUsbKeyService.getUsbKey(usbKey);
    }
}
