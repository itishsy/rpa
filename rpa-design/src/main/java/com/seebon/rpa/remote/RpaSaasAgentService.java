package com.seebon.rpa.remote;

import com.seebon.rpa.entity.agent.dto.DeclareValidateQueryDTO;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareOfferExportParamsDTO;
import com.seebon.rpa.entity.agent.vo.declare.employee.EmployeeAccfundDeclareChangeeProcessVO;
import com.seebon.rpa.entity.agent.vo.declare.employee.EmployeeSocialDeclareChangeeProcessVO;
import com.seebon.rpa.entity.robot.dto.monitor.NoDeclareDTO;
import com.seebon.rpa.entity.agent.dto.openApi.DeclareCounterOfferDTO;
import com.seebon.rpa.entity.robot.vo.CheckDeclareDataExVO;
import com.seebon.rpa.entity.robot.vo.CheckDeclareDataVO;
import com.seebon.rpa.entity.saas.vo.CustomerInsuredRegisterExVO;
import com.seebon.rpa.entity.saas.vo.DevAddrMsgVO;
import com.seebon.rpa.http.HttpService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sun.management.resources.agent;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@HttpService("${rpa.agent.server}")
public interface RpaSaasAgentService {

    /**
     * 调saas服务获取待申报人数
     *
     * @param businessType
     * @return
     */
    @PostMapping("/agent/declareChange/getAwaitDeclareNumber")
    Integer awaitDeclareNumber(String businessType, String accountNumber, Integer addrId);

    @PostMapping("/agent/worktable/getLastCompletedCount")
    Integer getLastCompletedCount(@RequestParam("custId") Integer custId,
                                  @RequestParam("addrId") Integer addrId,
                                  @RequestParam("businessType") Integer businessType,
                                  @RequestParam("accountNumber") String accountNumber);
    @PostMapping("/agent/declareChange/httpTest")
    int httpTest();

    @PostMapping("/agent/worktable/getMonitorCountByDeclareStatus")
    Map<String,Integer> getMonitorCountByDeclareStatus();

    @PostMapping("/agent/declareChange/getDeclareStatistics")
    Map<String, Integer> getDeclareStatistics(@RequestParam("addrId")Integer addrId,
                                              @RequestParam("appType")Integer appType);

    @PostMapping("/agent/declareChange/selectDeclareCount")
    Map<String, Integer> selectDeclareCount(@RequestParam("businessType")Integer businessType,
                                            @RequestParam("addrId")Integer addrId,
                                            @RequestParam("accountNumber")String accountNumber,
                                            @RequestParam("changeType")String changeType);

    @PostMapping("/agent/declareChange/noDeclareCount")
    List<NoDeclareDTO> noDeclareCount(@RequestParam("businessType")Integer businessType,
                                      @RequestParam("custId")Integer custId,
                                      @RequestParam("addrId")Integer addrId,
                                      @RequestParam("accountNumber")String accountNumber);

    @GetMapping("/agent/dev/msg/listAddrMsg")
    List<DevAddrMsgVO> listAddrMsg(@RequestParam("devUserName")String devUserName,
                                   @RequestParam("testUserName")String testUserName,
                                   @RequestParam("ywUserName")String ywUserName);

    @PostMapping("/agent/declareChange/countDistinctByParams")
    Integer countDistinctByParams(@RequestBody DeclareValidateQueryDTO dto);

    @PostMapping("/agent/employeeDeclare/declareCounterOffer")
    List<DeclareCounterOfferDTO> declareCounterOffer(@RequestBody List<DeclareCounterOfferDTO> dtos);

    @PostMapping("/agent/declareChange/getMaxProcessTime")
    CustomerInsuredRegisterExVO getMaxProcessTime(@RequestBody CustomerInsuredRegisterExVO registerVO);

    @PostMapping("/agent/declareChange/selectDeclareCountByParams")
    Integer selectDeclareCountByParams(@RequestBody Map<String,Object> params);

    @PostMapping("/agent/paidIn/selectTotalFee")
    String selectTotalFee(@RequestBody Map<String,Object> map);

    @PostMapping("/agent/employeeDeclare/getUnDeclareData")
    Map<String, Object> getUnDeclareData(@RequestBody CheckDeclareDataVO dataVO);

    @PostMapping("/agent/employeeDeclare/getUnCheckData")
    Map<String, Object> getUnCheckData(@RequestBody CheckDeclareDataVO dataVO);

    @PostMapping("/agent/employeeDeclare/checkOfferExport")
    Map<String, Object> checkOfferExport(@RequestBody DeclareOfferExportParamsDTO params);

    @PostMapping(value = "/agent/declareChange/listSocialProcess")
    List<EmployeeSocialDeclareChangeeProcessVO> listSocialProcess(@RequestParam("uuid") String uuid, @RequestParam("type") Integer type);

    @PostMapping("/agent/declareChange/listAccfundProcess")
    List<EmployeeAccfundDeclareChangeeProcessVO> listAccfundProcess(@RequestParam("uuid") String uuid, @RequestParam("type") Integer type);

    @PostMapping(value = "/agent/declareChange/findSocialProcess")
    EmployeeSocialDeclareChangeeProcessVO findSocialProcess(@RequestParam("uuid") String uuid, @RequestParam("addrId") Integer addrId, @RequestParam("customerId") Integer customerId);

    @PostMapping("/agent/declareChange/findAccfundProcess")
    EmployeeAccfundDeclareChangeeProcessVO findAccfundProcess(@RequestParam("uuid") String uuid, @RequestParam("addrId") Integer addrId, @RequestParam("customerId") Integer customerId);
}
