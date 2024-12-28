package com.seebon.rpa.remote;

import com.seebon.rpa.entity.robot.dto.DeclareAccountBaseDTO;
import com.seebon.rpa.entity.robot.dto.design.DeclareAccountBaseVo;
import com.seebon.rpa.entity.robot.vo.FileStoreVO;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.saas.BusinessSceneWarnVO;
import com.seebon.rpa.entity.saas.DevUserAddr;
import com.seebon.rpa.entity.saas.PolicyDeclareOperationTypeDictVO;
import com.seebon.rpa.entity.saas.dto.AddrBusinessChangeProcessDTO;
import com.seebon.rpa.entity.saas.dto.register.CityRegisterDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterParamsDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterRecentlyQueryDTO;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddr;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddrDeadlineSetting;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddrDeclareSetting;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerAddrGroup;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.po.message.MessageList;
import com.seebon.rpa.entity.saas.po.message.MessageListFile;
import com.seebon.rpa.entity.saas.po.message.MessageUserReceiveSetting;
import com.seebon.rpa.entity.saas.vo.CustomerInsuredRegisterExVO;
import com.seebon.rpa.entity.saas.vo.PolicyAddrDeadlineSettingVO;
import com.seebon.rpa.entity.saas.vo.declare.customer.CustomerMapInfoVO;
import com.seebon.rpa.entity.saas.vo.message.MessageRuleConfigVO;
import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import com.seebon.rpa.entity.sms.vo.ForwardConfigVO;
import com.seebon.rpa.entity.sms.vo.GetSmsCodeVO;
import com.seebon.rpa.http.HttpService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 18:27:05
 */
@HttpService("${rpa.saas.server}")
public interface RpaSaasService {

    @PostMapping("/policy/employeeDeclare/httpTest")
    int httpTest();

    /**
     * 调saas服务查询字典表获取机器人业务类型
     *
     * @param dictCode
     * @return
     */
    @PostMapping("/policy/sys/dict/getDictByCode")
    RobotAppVO getRobotDeclareType(String dictCode);

    /**
     * 调saas服务获取待申报人数
     *
     * @param businessType
     * @return
     */
    @PostMapping("/policy/declareChange/getAwaitDeclareNumber")
    Integer awaitDeclareNumber(String businessType, String accountNumber, Integer addrId);

    @PostMapping("/policy/sys/customer/getCustomerById/{id}")
    CustomerBase getCustomer(@PathVariable("id") Integer id);

    @PostMapping("/policy/sys/customer/listAllCustomer")
    List<CustomerBase> listCustomer(@RequestParam("filter") Boolean filter, @RequestParam("status")String status);

    @PostMapping("/policy/declareAccount/saveDeclareAccount")
    Integer saveDeclareAccount(@RequestBody DeclareAccountBaseDTO dto);

    @PostMapping("/policy/declareAccount/editDeclareAccount")
    Integer editDeclareAccount(@RequestBody DeclareAccountBaseDTO dto);

    /**
     * 文件上传
     *
     * @return
     */
    @PostMapping("/policy/sys/file/fileUploadForServer")
    Map<String, Object> fileUploadForServer(@RequestBody String params);

    @PostMapping("/policy/sys/file/fileDelete/{fileId}")
    void fileDelete(@PathVariable(value = "fileId") Integer fileId,@RequestParam("indexName")String indexName);

    @PostMapping("/policy/file/getByFileIds/{fileIds}")
    List<FileStoreVO> getByFileIds(@PathVariable(value = "fileIds") String fileIds);

    @PostMapping("/policy/sms/saveForwardConfig")
    void saveForwardConfig(@RequestBody ForwardConfigVO config);

    @PostMapping("/policy/robot/getSmsCode")
    String getSmsCode(@RequestBody GetSmsCodeVO smsCodeVO);

    /*@PostMapping("/policy/agent/getSysUser/{userId}")
    SysUser getSysUser(@PathVariable(value = "userId") Integer userId);*/

    @PostMapping("/policy/agent/getUserDeclareAccountList")
    List<SysUserDeclareAccountVO> getUserDeclareAccountList(@RequestParam("userId") Integer userId, @RequestParam("businessType") Integer businessType);

    @PostMapping("/policy/agent/getRegisteredPerson")
    Integer getRegisteredPerson();

    @PostMapping("/policy/agent/getCityList")
    List<PolicyAddr> getCityList();

    @PostMapping("/policy/declareAddr/selectPolicyDeclareSetList")
    List<PolicyAddrDeclareSetting> selectPolicyDeclareSetList();

    @PostMapping("/policy/declareAccount/principalList")
    List<DeclareAccountBaseVo> principalList();

    @PostMapping("/policy/declareAccount/getOrganizeList")
    List<DeclareAccountBaseVo> getOrganizeList(@RequestParam("addressName") String addressName);

    @PostMapping("/policy/declareAccount/getDeclareCount")
    Map<String,Integer> getDeclareCount();

    @PostMapping("/policy/declareAddr/getLastCostDate")
    String getLastCostDate(Integer addrId);

    @PostMapping("/policy/declareAccount/getAccountCount")
    int getAccountCount(String accountNumber,Integer addrId,Integer businessType);

    @PostMapping("/policy/declareAccount/getAllAccount")
    List<DeclareAccountBaseDTO>  getAllAccount(String accountNumber,Integer businessType,Integer createId,Integer addrId);

    @PostMapping("/policy/businessWarn/getDeclareResultList")
    List<BusinessSceneWarnVO> getDeclareResultList(@RequestBody BusinessSceneWarnVO sceneWarn);

    @PostMapping("/policy/offerSettings/getAllOperationTypes")
    List<PolicyDeclareOperationTypeDictVO> getAllOperationTypes();

    @PostMapping("/policy/agent/getPolicyAddrDeadlineSettings/{addrId}/{businessType}")
    List<PolicyAddrDeadlineSettingVO> getPolicyAddrDeadlineSettings(@PathVariable(value = "addrId") Integer addrId,
                                                                   @PathVariable(value = "businessType") Integer businessType);

    @PostMapping("/policy/agent/getPolicyAddrDeadlineSetting/{addrId}/{businessType}/{declareType}")
    PolicyAddrDeadlineSetting getPolicyAddrDeadlineSetting(@PathVariable(value = "addrId") Integer addrId,
                                                                   @PathVariable(value = "businessType") Integer businessType,
                                                                   @PathVariable(value = "declareType") Integer declareType
    );

    @PostMapping("/policy/agent/listUserAddr")
    List<DevUserAddr> listUserAddr(@RequestBody DevUserAddr userAddr);

    @PostMapping("/policy/sys/dict/getLqDay")
    Integer getLqDay();

    @GetMapping("/policy/policyAddr/getPopularAddr")
    List<PolicyAddr> getPopularAddr();



    @PostMapping("/policy/message/rule/getMessageRule/{messageType}/{warnType}")
    MessageRuleConfigVO getMessageRule(@PathVariable("messageType") String messageType, @PathVariable("warnType") String warnType);

    @PostMapping("/policy/message/list/saveMessage")
    int saveMessage(@RequestBody MessageList messageList);

    @PostMapping("/policy/message/list/saveMessageFile")
    int saveMessageFile(@RequestBody MessageListFile messageListFile);

    @PostMapping("/policy/message/list/getUserReceiveSetting/{userId}/{messageType}/{warnType}")
    MessageUserReceiveSetting getUserReceiveSetting(@PathVariable("userId") Integer userId,
                                                    @PathVariable("messageType") String messageType,
                                                    @PathVariable("warnType") String warnType);

    @PostMapping("/policy/cust/insured/findRegister")
    CustomerInsuredRegisterExVO findRegister(@RequestBody CustomerInsuredRegisterExVO register);

    @PostMapping("/policy/cust/insured/selectRegisterNumber")
    Integer selectRegisterNumber(@RequestBody RegisterParamsDTO params);

    @PostMapping("/policy/sys/customer/addrGroups")
    List<CustomerAddrGroup> addrGroups();

    @PostMapping("/policy/cust/insured/selectCityRegister")
    List<CityRegisterDTO> selectCityRegister();

    @PostMapping("/policy/sys/customer/getCustomerMapInfo")
    CustomerMapInfoVO getCustomerMapInfo();

    @PostMapping("/policy/agent/saveAddrBusinessProcess")
    void saveAddrBusinessProcess(@RequestBody AddrBusinessChangeProcessDTO dto);

    @PostMapping("/policy/declareAccount/getAccountNumber")
    Integer getAccountNumber(@RequestBody Map<String,Object> params);

    @PostMapping("/policy/cust/insured/isAccountRegisterRecently")
    boolean isAccountRegisterRecently(@RequestBody RegisterRecentlyQueryDTO registerRecentlyQueryDTO);

}
