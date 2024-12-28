package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMonitorDetail;
import com.seebon.rpa.entity.robot.vo.design.RobotOperationMonitorDetailVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 运维监控相关接口
 * @date 2023/4/12 17:39
 */
public interface OperationMonitorService {

    /**
     * 分页查询运维监控列表页面
     * @param map
     * @return
     */
    IgGridDefaultPage<OperationMonitorVo> listPage(Map<String, Object> map);

    /**
     * 分页查询运维监控详情列表页面
     * @param map
     * @return
     */
    IgGridDefaultPage<RobotOperationMonitorDetailVO> queryDetailPage(Map<String, Object> map);

    /**
     * 异常办结信息处理
     *  @param handleMessageVo
     *  @return
     */
    int updateExceptionMessage(HandleMessageVo handleMessageVo);

    /**
     * 异常信息录入
     * @param list
     */
    void insertOperationDetail(List<OperationRequestVo> list, String warnCode);

    /**
     * 运维监控页面数量统计
     * @return
     */
    Map<String,Integer> operationCount();

    /**
     * 解析社保公积金地区参数
     */
    Map<String,String> analysisAppArgs(String appArgs);

    /**
     * 设置客户名称
     */
    List<OperationRequestVo> setCustomerName( List<OperationRequestVo> list);

    /**
     * 下载异常信息
     * @param vo
     */
    void downloadExcel(HttpServletResponse response,RobotOperationExcelVo vo);

    IgGridDefaultPage<RobotOperationMonitorDetailVO> operationPage(Map<String,Object> map);

    void export(HttpServletResponse response, Map<String,Object> map);

    int finish(HandleMessageDTO dto);

    int updateWarnType(String warnType, RobotOperationMonitorDetailVO detailVO);

    List<CustomerBase> operationListCustomer();

}
