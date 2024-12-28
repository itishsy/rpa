package com.seebon.rpa.rest.design;

import com.seebon.rpa.BusinessException;
import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigConditionVO2;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigConditionVO5;
import com.seebon.rpa.entity.robot.dto.design.RobotAppConfigGroupVO;
import com.seebon.rpa.entity.robot.dto.design.RobotCopyArgsVo;
import com.seebon.rpa.entity.robot.vo.design.RobotAppConfigGroupItemVO;
import com.seebon.rpa.entity.robot.vo.design.RobotAppFormVO;
import com.seebon.rpa.service.RobotAppConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "信息配置", tags = "信息配置")
@RestController
@RequestMapping("/appConfig")
public class RobotAppConfigController {
    @Autowired
    private RobotAppConfigService robotAppConfigService;

    @ApiOperation(value = "维护表单（更新也用这个）")
    @PostMapping("/addInput")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-维护表单", operation = "维护表单")
    public Integer addInput(@RequestBody RobotAppConfigForm configForm) {
        return robotAppConfigService.addInput(configForm);
    }

    @ApiOperation(value = "获取表单名称表单ID")
    @PostMapping("/getInputInfo")
    public List<RobotAppConfigForm> getInputInfo(@RequestBody RobotAppConfigForm configInput) {
        if (null == configInput) {
            return null;
        }
        return robotAppConfigService.getInputInfoByType(configInput);
    }

    @ApiOperation(value = "添加分组")
    @PostMapping("/addGroup")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-添加分组", operation = "添加分组")
    public void addGroup(@RequestBody RobotAppConfigGroupVO configGroup) {
        robotAppConfigService.addGroup(configGroup);
    }

    @ApiOperation(value = "获取分组信息")
    @PostMapping("/getGroupInfo")
    public List<RobotAppConfigGroup> getGroupInfo(@RequestParam(value = "robotType") String robotType) {
        if (StringUtils.isBlank(robotType)) {
            return null;
        }
        return robotAppConfigService.getGroupInfo(robotType);
    }

    @ApiOperation(value = "改分组名")
    @PostMapping("/editGroupInfo")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-修改分组", operation = "修改分组")
    public void editGroupInfo(@ApiParam("id必填(分组id,getGroupInfo接口可获取)") @RequestBody RobotAppConfigGroupVO configGroup) {
        try {
            if (null == configGroup) {
                throw new BusinessException("参数异常");
            }
            robotAppConfigService.editGroupInfo(configGroup);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }

    @ApiOperation(value = "逻辑删除分组")
    @PostMapping("/deleteGroupInfo")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-删除分组", operation = "删除分组")
    public Integer logicDeleteGroupInfo(@RequestBody RobotAppFormGroup group) {
        return robotAppConfigService.deleteGroupInfo(group);
    }

    @ApiOperation(value = "添加分组明细")
    @PostMapping("/addGroupItem")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-添加分组明细", operation = "添加分组明细")
    public Integer addGroupItem(@RequestBody @Validated({SaveGroup.class}) RobotAppConfigGroupItemVO configGroupItem, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new BusinessException(bindingResult.getFieldError().getDefaultMessage());
            }
            return robotAppConfigService.addGroupItem(configGroupItem);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @ApiOperation(value = "编辑分组明细")
    @PostMapping("/editGroupItem")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-编辑分组明细", operation = "编辑分组明细")
    public Integer editGroupItem(@RequestBody RobotAppConfigGroupItemVO configGroupItem) {
        return robotAppConfigService.editGroupItem(configGroupItem);
    }

    @ApiOperation(value = "编辑分组明细-排序")
    @PostMapping("/editGroupItemOrder")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-编辑分组明细-排序", operation = "编辑分组明细-排序")
    public Integer editGroupItemOrder(@RequestBody RobotAppConfigGroupItemVO configGroupItem) {
        return robotAppConfigService.editGroupItemOrder(configGroupItem);
    }

    @ApiOperation(value = "逻辑删除分组字段(就是删除字段)")
    @PostMapping("/deleteGroupItemById/{argsDefineId}")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-删除分组字段", operation = "删除分组字段")
    public Integer deleteGroupItemById(@PathVariable(value = "argsDefineId") Integer argsDefineId) {
        try {

            return robotAppConfigService.logicDeleteGroupItemById(argsDefineId);
        } catch (Exception e) {
            log.error("deleteGroupItemById()->" + e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }

    @ApiOperation(value = "检查添加分组明细校验字段是否重复(返回true重复;false不重复)")
    @PostMapping("/checkGroupItem")
    public Boolean checkGroupItem(@RequestBody RobotArgsDefine argsDefine) {
        if (null == argsDefine) {
            throw new BusinessException("参数错误");
        }
        Integer count = robotAppConfigService.checkGroupItem(argsDefine);
        return count == null || count > 0;
    }

    @ApiOperation(value = "添加条件")
    @PostMapping("/addCondition")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-添加分组字段条件", operation = "添加分组字段条件")
    public Integer addCondition(@RequestBody RobotAppConfigConditionVO2 configConditions) {
        return robotAppConfigService.addCondition(configConditions);
    }

    @ApiOperation(value = "获取条件(点击添加条件时使用)")
    @PostMapping("/getCondition/{id}")
    public RobotAppConfigConditionVO5 getCondition(@ApiParam("分组明细id") @PathVariable Integer id) {
        return robotAppConfigService.getConditionByGroupItemId(id);
    }

    @ApiOperation(value = "将分组跟换到其他表单")
    @PostMapping("/changeGroupToOtherForm")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-切换分组表单", operation = "切换分组表单")
    public Integer changeForm(@RequestBody RobotAppConfigGroupVO groupVO, @RequestParam(value = "formId") Integer formId) {
        return robotAppConfigService.changeForm(groupVO, formId);
    }

    @ApiOperation(value = "初始化界面")
    @PostMapping("/getRobotConfigInfoById/{createRobotAppId}")
    public List<RobotAppFormVO> getRobotConfigInfoById(@PathVariable String createRobotAppId) {
        return robotAppConfigService.getRobotConfigInfoById(createRobotAppId);
    }

    @ApiOperation(value = "复制参数")
    @PostMapping("/copyArgs")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-复制参数", operation = "复制参数")
    public void getRobotConfigInfoById(@RequestBody RobotCopyArgsVo robotCopyArgsVo) {
        robotAppConfigService.copyArgs(robotCopyArgsVo);
    }

    @ApiOperation(value = "获取通用参数")
    @PostMapping("/getGeneralArgs")
    public List<RobotAppFormVO> getGeneralArgs(Integer businessType) {
        return robotAppConfigService.getGeneralArgs(businessType);
    }
    @ApiOperation(value = "新增通用参数")
    @PostMapping("/addGeneralArgs")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-新增通用参数", operation = "新增通用参数")
    public void addGeneralArgs(@RequestBody List<RobotAppFormVO> robotCopyArgsVo,String appCode ) {
        robotAppConfigService.addGeneralArgs(robotCopyArgsVo,appCode);
    }

    @ApiOperation(value = "获取字段名称列表")
    @PostMapping("/getFiledNameList")
    public List<RobotArgsDefine> getFiledNameList (@RequestParam String keyWord) {
        return robotAppConfigService.getFiledNameList(keyWord);
    }
}
