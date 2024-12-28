package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.dto.design.OperationMonitorVo;
import com.seebon.rpa.entity.robot.dto.design.RobotOperationExcelVo;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMonitorDetail;
import com.seebon.rpa.entity.robot.vo.design.RobotOperationMonitorDetailVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023/4/12 17:46
 */
@Component
public interface OperationMonitorDao extends Mapper<RobotOperationMonitorDetail>, MySqlMapper<RobotOperationMonitorDetail>{

    /**
     * 分页查询监控列表
     *
     * @param map
     * @return
     */
    List<OperationMonitorVo> findByOperation(Map<String, Object> map);

    /**
     * 分页查询运维监控详情列表
     *
     * @param map
     * @return
     */
    List<RobotOperationMonitorDetailVO> findDetailByOperation(Map<String, Object> map);

    /**
     * 办结信息处理
     * @param ids
     * @param handleType
     * @param handleRemark
     * @param handleLink
     * @return
     */
    int updateExceptionMessage(@Param("ids")List<Long> ids,@Param("handleType") String handleType,
                               @Param("handleRemark") String handleRemark, @Param("handleLink") String handleLink);

    /**
     * 运维监控页面记录数统计
     * @param today
     * @return
     */
    Integer selectOperationCount(@Param("today") String today);

    /**
     * 批量插入监控报表数据
     * @param operationMonitorDetailVoList
     */
    void insertOperationDetail( List<RobotOperationMonitorDetail> operationMonitorDetailVoList);

    /**
     * 查询错误信息详情
     */
    List<RobotOperationExcelVo> errorInfoList(RobotOperationExcelVo vo);

    Integer selectCountByParams(Map<String,Object> map);

    List<RobotOperationMonitorDetailVO> selectPageListByParams(Map<String,Object> map);

    int updateFinish(@Param("detailVO") RobotOperationMonitorDetailVO detailVO,
                     @Param("handleStatus") Integer handleStatus,
                     @Param("handleType") String handleType,
                     @Param("handleRemark") String handleRemark,
                     @Param("handleLink") String handleLink);

    int updateWarnType(@Param("warnType")String warnType, @Param("detailVO")RobotOperationMonitorDetailVO detailVO);

}
