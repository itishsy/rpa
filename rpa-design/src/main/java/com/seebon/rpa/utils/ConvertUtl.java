package com.seebon.rpa.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.agent.enums.BusinessTypeEnum;
import com.seebon.rpa.entity.agent.vo.declare.employee.EmployeeAccfundDeclareChangeeProcessVO;
import com.seebon.rpa.entity.agent.vo.declare.employee.EmployeeSocialDeclareChangeeProcessVO;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskQueueItem;
import com.seebon.rpa.entity.robot.dto.RobotTaskScheduleDTO;
import com.seebon.rpa.entity.robot.dto.design.DesignPositionVO;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesignCopyVO;
import com.seebon.rpa.entity.robot.enums.LoginTypeEnum;
import com.seebon.rpa.entity.robot.enums.QueueItemTypeEnum;
import com.seebon.rpa.entity.robot.enums.ServiceItemTypeEnum;
import com.seebon.rpa.entity.robot.vo.*;
import com.seebon.rpa.entity.saas.po.declare.PolicyAddrDeadlineSetting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class ConvertUtl {
    private static ThreadLocal<DesignPositionVO> position = ThreadLocal.withInitial(() -> new DesignPositionVO());

    public static String getVersionName(String version, String rule) {
        //去掉V
        String substring = version.substring(1, version.length());
        String[] split = substring.split("\\.");
        String lastNewVersion = null;
        switch (rule) {
            case "主版本号":
                Integer number = Integer.parseInt(split[0]) + 1;
                String s = String.valueOf(number);
                split[0] = s;
                String newVersion = String.join(".", split);
                lastNewVersion = "V" + newVersion;
                break;
            case "子版本号":
                number = Integer.parseInt(split[1]) + 1;
                s = String.valueOf(number);
                split[1] = s;
                newVersion = String.join(".", split);
                lastNewVersion = "V" + newVersion;
                break;
            case "补丁":
                number = Integer.parseInt(split[2]) + 1;
                s = String.valueOf(number);
                split[2] = s;
                newVersion = String.join(".", split);
                lastNewVersion = "V" + newVersion;
                break;
            default:
                break;
        }
        return lastNewVersion;
    }

    public static void checkFlowDesign(List<RobotFlowStepVO> steps) {
        //开始步骤
        List<RobotFlowStep> starts = steps.stream().filter(s -> "start".equals(s.getActionCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(starts) || starts.size() != 1) {
            throw new BusinessException("流程缺少开始步骤或者存在多个开始步骤");
        }
        //结束步骤
        List<RobotFlowStep> ends = steps.stream().filter(s -> "end".equals(s.getActionCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ends) || ends.size() != 1) {
            throw new BusinessException("流程缺少结束步骤或者存在多个结束步骤");
        }
    }

    public static void setEdge(JSONObject edge, String source_port, String source_cell, String target_port, String target_cell) {
        JSONObject source = edge.getJSONObject("source");
        source.put("port", source_port);
        source.put("cell", source_cell);

        JSONObject target = edge.getJSONObject("target");
        target.put("port", target_port);
        target.put("cell", target_cell);
    }

    public static void setData(RobotFlowStepVO stepVO, JSONObject json, Map<String, String> actionMap, RobotFlowDesignCopyVO exVO) {
        JSONObject data = json.getJSONObject("data");
        data.put("flowName", stepVO.getStepName());
        data.put("stepName", stepVO.getStepName());
        data.put("stepCode", stepVO.getStepCode());
        data.put("desc", stepVO.getStepName());
        data.put("comment", stepVO.getStepName());
        data.put("groupName", stepVO.getStepName());
        data.put("quote", false);
        data.put("nodeState", "0");
        data.put("type", "action");
        data.put("openEdit", 1);
        data.put("actionCode", stepVO.getActionCode());
        data.put("actionName", actionMap.get(stepVO.getActionCode()));
        data.put("parentFlowCode", exVO.getParentFlowCode());

        RobotFlowStep step = new RobotFlowStep();
        BeanUtils.copyProperties(stepVO, step);
        if ("subFlow".equals(step.getActionCode())) {
            data.put("type", "flow");
        }
        data.put("step", JSON.toJSONString(step));
    }

    public static JSONObject getSkipTo(List<JSONObject> cells, String skipTo) {
        for (JSONObject jsonObject : cells) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null) {
                continue;
            }
            String step = data.getString("step");
            if (StringUtils.isBlank(step)) {
                continue;
            }
            RobotFlowStep flowStep = JSON.parseObject(step, RobotFlowStep.class);
            if (flowStep.getStepName().equals(skipTo)) {
                return jsonObject;
            }
        }
        return null;
    }

    public static JSONObject getEdgeCell(List<JSONObject> cells, String sourceCell, String sourcePort) {
        for (JSONObject jsonObject : cells) {
            if (!"edge".equals(jsonObject.getString("shape"))) {
                continue;
            }
            JSONObject source = jsonObject.getJSONObject("source");
            if (source.getString("port").equals(sourcePort) && source.getString("cell").equals(sourceCell)) {
                return jsonObject;
            }
        }
        return null;
    }

    public static JSONObject getJson(String fileName, String id) {
        InputStream is = ConvertUtl.class.getClassLoader().getResourceAsStream("json/" + fileName);
        String jsonStr = IoUtil.readUtf8(is);
        JSONObject jsonObj = JSON.parseObject(jsonStr);
        jsonObj.put("id", StringUtils.defaultIfBlank(id, UUIDGenerator.uuidString()));
        if (fileName.contains("start.json") || fileName.contains("end.json") || fileName.contains("step.json")) {
            jsonObj.getJSONObject("ports").getJSONArray("items").addAll(ConvertUtl.getPorts(null));
        }
        if (fileName.contains("polygon.json")) {
            jsonObj.getJSONObject("ports").getJSONArray("items").addAll(ConvertUtl.getPorts(null));
        }
        setPosition(jsonObj, fileName);
        return jsonObj;
    }

    public static void setPosition(JSONObject jsonObj, String fileName) {
        if (fileName.contains("start.json")) {
            Integer x = jsonObj.getJSONObject("position").getInteger("x");
            Integer y = jsonObj.getJSONObject("position").getInteger("y");
            position.get().setX(x);
            position.get().setY(y);
        }
        if (fileName.contains("step.json") || fileName.contains("polygon.json") || fileName.contains("end.json")) {
            Integer x = position.get().getX();
            Integer y = position.get().getY() + 100;

            jsonObj.getJSONObject("position").put("x", x);
            jsonObj.getJSONObject("position").put("y", y);

            position.get().setX(x);
            position.get().setY(y);
        }
    }

    public static List<JSONObject> getPorts(List<String> ports) {
        List<JSONObject> list = Lists.newArrayList();
        if (ports == null || ports.contains("top")) {
            JSONObject top_port = new JSONObject();
            top_port.put("group", "top");
            top_port.put("id", UUIDGenerator.uuidString());
            list.add(top_port);
        }
        if (ports == null || ports.contains("bottom")) {
            JSONObject bottom_port = new JSONObject();
            bottom_port.put("group", "bottom");
            bottom_port.put("id", UUIDGenerator.uuidString());
            list.add(bottom_port);
        }
        if (ports == null || ports.contains("left")) {
            JSONObject left_port = new JSONObject();
            left_port.put("group", "left");
            left_port.put("id", UUIDGenerator.uuidString());
            list.add(left_port);
        }
        if (ports == null || ports.contains("right")) {
            JSONObject right_port = new JSONObject();
            right_port.put("group", "right");
            right_port.put("id", UUIDGenerator.uuidString());
            list.add(right_port);
        }
        return list;
    }

    public static String getPortId(JSONObject step, String port) {
        JSONArray items = step.getJSONObject("ports").getJSONArray("items");
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            String group = item.getString("group");
            if (group.equals(port)) {
                return item.getString("id");
            }
        }
        return null;
    }

    public static void setFlowStep(RobotFlowStep step) {
        Session session = SecurityContext.currentUser();
        step.setId(null);
        step.setCreateId((int) session.getId());
        step.setCreateTime(new Date());
        step.setUpdateId((int) session.getId());
        step.setUpdateTime(new Date());
    }

    public static RobotOfferRuleItem getRuleItem(List<RobotOfferRuleVO> ruleList, String failReason) {
        for (RobotOfferRuleVO rule : ruleList) {
            for (RobotOfferRuleItem itemVo : rule.getRuleItemList()) {
                Integer matchRule = itemVo.getMatchRule();
                String content = itemVo.getContent();
                Boolean isMatch = ConvertUtl.matchContent(matchRule, content, failReason);
                if (isMatch) {
                    log.info("失败原因：" + failReason + ",匹配的规则：" + JSON.toJSONString(itemVo));
                    return itemVo;
                }
            }
        }
        log.info("失败原因：" + failReason + ",没有匹配的规则");
        return null;
    }

    /**
     * 匹配规则：1-equals 2-contains  3-startsWith 4-endsWith
     */
    public static boolean matchContent(Integer matchRule, String content, String failReason) {
        Boolean isMatch = false;
        if (1 == matchRule) {
            if (failReason.equals(content)) {
                isMatch = true;
            }
        }
        if (2 == matchRule) {
            if (failReason.contains(content)) {
                isMatch = true;
            }
        }
        if (3 == matchRule) {
            if (failReason.startsWith(content)) {
                isMatch = true;
            }
        }
        if (4 == matchRule) {
            if (failReason.endsWith(content)) {
                isMatch = true;
            }
        }
        return isMatch;
    }

    public static String getBusinessType(String businessType) {
        if ("1001001".equals(businessType)) {
            return "1";
        }
        if ("1001002".equals(businessType)) {
            return "2";
        }
        return null;
    }

    public static Integer getQueueItem(String serviceItemName) {
        if ("增员".equals(serviceItemName) || "减员".equals(serviceItemName) || "调基".equals(serviceItemName) || "补缴".equals(serviceItemName)) {
            return 1;
        }
        return QueueItemTypeEnum.getByCodeName(serviceItemName);
    }

    public static Integer null2Zero(List<RobotTaskQueueVO> value) {
        if (CollectionUtils.isEmpty(value)) {
            return 0;
        }
        return value.size();
    }

    public static boolean checkSchedule(RobotFlowVO flow, Date nowDate) {
        String execCycle = flow.getExecCycle();
        String execAreaTime = flow.getExecAreaTime();
        Integer execStyle = flow.getExecStyle();
        if (!ScheduleUtils.matchSchedule(execCycle, execAreaTime, execStyle, nowDate)) {
            return false;
        }
        return true;
    }

    public static boolean hasDeclareData(RobotFlowVO flow, Map<String, Object> declareMap) {
        if (MapUtils.isEmpty(declareMap)) {
            return false;
        }
        if (hasData(1, flow, declareMap, "addCount")) {
            return true;
        }
        if (hasData(2, flow, declareMap, "stopCount")) {
            return true;
        }
        if (hasData(3, flow, declareMap, "adjustCount")) {
            return true;
        }
        if (hasData(5, flow, declareMap, "suppCount")) {
            return true;
        }
        return false;
    }

    public static boolean hasData(Integer serviceItem, RobotFlowVO flow, Map<String, Object> declareMap, String keyName) {
        if (MapUtils.isNotEmpty(declareMap) && flow.getServiceItem().equals(serviceItem) && declareMap.get(keyName) != null && Integer.parseInt(declareMap.get(keyName).toString()) > 0) {
            return true;
        }
        return false;
    }

    public static Map<String, Object> getDeclareData(RobotTaskVO taskVO, Map<String, Object> declareDataMap) {
        if (MapUtils.isEmpty(declareDataMap)) {
            return null;
        }
        JSONObject appArgs = JSONObject.parseObject(taskVO.getAppArgs());
        Integer businessType = BusinessTypeEnum.getCodeByKey(appArgs.getString("businessType"));
        String addrName = appArgs.getString("addrName");
        List<Map<String, Object>> list = (List<Map<String, Object>>) declareDataMap.get(businessType.toString());
        for (Map<String, Object> dataVO : list) {
            if (dataVO.get("custId").toString().equals(taskVO.getClientId().toString()) && dataVO.get("addrName").toString().equals(addrName) &&
                    dataVO.get("accountNumber").toString().equals(taskVO.getAccountNumber())) {
                return dataVO;
            }
        }
        return null;
    }

    public static CheckDeclareDataVO getCheckDeclareDataVO(List<RobotTaskVO> list) {
        List<Integer> clientIds = list.stream().map(vo -> vo.getClientId()).distinct().collect(Collectors.toList());
        List<String> socialAccountList = Lists.newArrayList();
        List<String> accfundAccountList = Lists.newArrayList();
        for (RobotTaskVO task : list) {
            JSONObject appArgs = JSONObject.parseObject(task.getAppArgs());
            if ("1001001".equals(appArgs.getString("businessType"))) {
                socialAccountList.add(appArgs.getString("addrName") + "-" + task.getAccountNumber());
            }
            if ("1001002".equals(appArgs.getString("businessType"))) {
                accfundAccountList.add(appArgs.getString("addrName") + "-" + task.getAccountNumber());
            }
        }
        CheckDeclareDataVO dataVO = new CheckDeclareDataVO();
        dataVO.setClientIds(clientIds);
        dataVO.setSocialAccountList(socialAccountList);
        dataVO.setAccfundAccountList(accfundAccountList);
        return dataVO;
    }

    public static boolean isH5Login(String declareSystem, List<RobotTaskArgs> taskArgsList) {
        String loginType = getLoginType(declareSystem, taskArgsList);
        if (StringUtils.isNotBlank(loginType)) {
            boolean isH5Login = StringUtils.endsWithAny(loginType, "扫码登录", "短信登录", "验证码登录");
            log.info("登录方式：loginType=" + loginType + ",是否H5登录 isH5Login=" + isH5Login);
            return isH5Login;
        }
        return false;
    }

    public static String getLoginType(String declareSystem, List<RobotTaskArgs> taskArgsList) {
        if (CollectionUtils.isEmpty(taskArgsList)) {
            return null;
        }
        String loginTypeValue = LoginTypeEnum.getValueByCode(declareSystem);
        Optional<RobotTaskArgs> first = taskArgsList.stream().filter(vo -> vo.getArgsKey().equals(loginTypeValue)).findFirst();
        if (first.isPresent()) {
            return first.get().getArgsValue();
        } else if (LoginTypeEnum.dgsLoginType.getCode().equals(declareSystem)) {
            // 单工伤如果没有单独配置登录方式，默认使用社保系统登录方式
            return taskArgsList.stream().filter(vo -> vo.getArgsKey().equals(LoginTypeEnum.socialLoginType.getValue())).findFirst().map(RobotTaskArgs::getArgsValue).orElse(null);
        }
        return null;
    }

    public static boolean hasTaskItem(List<RobotTaskQueueItem> queueItemList, RobotTaskQueueItem queueItem) {
        if (CollectionUtils.isEmpty(queueItemList)) {
            return false;
        }
        List<RobotTaskQueueItem> queueItems = queueItemList.stream().filter(vo -> vo.getServiceItem().equals(queueItem.getServiceItem())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(queueItems)) {
            return false;
        }
        return true;
    }

    public static void log(RobotTaskVO task, RobotFlowVO flow, String msg) {
        if (flow != null) {
            log.info(task.getOrgName() + " " + task.getAccountNumber() + " " + task.getAppName() + " " + flow.getDeclareSystem() + " " + flow.getFlowName() + " " + msg);
        } else {
            log.info(task.getOrgName() + " " + task.getAccountNumber() + " " + task.getAppName() + " " + msg);
        }
    }

    public static Integer getSortRule(String loginType) {
        if (StringUtils.isBlank(loginType)) {
            return 4;
        }
        boolean isH5Login = StringUtils.endsWithAny(loginType, "扫码登录", "短信登录", "验证码登录");
        if (isH5Login) {
            return 1;
        }
        boolean ukey = StringUtils.endsWithAny(loginType, "CA证书登录");
        if (ukey) {
            return 3;
        }
        boolean account = StringUtils.endsWithAny(loginType, "账号密码登录", "接收器验证");
        if (account) {
            return 4;
        }
        return 4;
    }

    public static RobotTaskQueueItem newTaskQueueItem(RobotFlowVO flow, Dict dict) {
        RobotTaskQueueItem taskQueueItem = new RobotTaskQueueItem();
        taskQueueItem.setServiceItem("10018008".equals(flow.getTagCode()) ? 11 : flow.getServiceItem());
        if (dict != null && dict.containsKey("fileSuffix")) {
            taskQueueItem.setItemArgs(JSON.toJSONString(Dict.create().set("fileSuffix", dict.getStr("fileSuffix"))));
        }
        taskQueueItem.setCreateTime(new Date());
        taskQueueItem.setUpdateTime(new Date());
        return taskQueueItem;
    }

    public static String getServiceItemNames(List<RobotTaskQueueItem> taskQueueItemList) {
        if (CollectionUtils.isEmpty(taskQueueItemList)) {
            return "";
        }
        List<String> serviceItemNames = Lists.newArrayList();
        for (RobotTaskQueueItem item : taskQueueItemList) {
            serviceItemNames.add(ServiceItemTypeEnum.getNameByCode(item.getServiceItem()));
        }
        return String.join(",", serviceItemNames);
    }

    public static Long getLastPreTime(List<RobotTaskQueueVO> list, Integer sort) {
        if (CollectionUtils.isEmpty(list) || sort == null || sort == 1) {
            return 0L;
        }
        return list.stream().filter(vo -> vo.getSort() <= (sort - 1)).mapToLong(vo -> vo.getPreTime()).sum();
    }

    public static String getMonthStr(Integer month) {
        if (month == -1) {
            return DateUtil.format(DateUtil.lastMonth(), "yyyyMM");//上月
        } else if (month == 0) {
            return DateUtil.format(new Date(), "yyyyMM");//当月
        } else if (month == 1) {
            return DateUtil.format(DateUtil.nextMonth(), "yyyyMM");//下月
        }
        return "";
    }

    public static String getMonthStr2(Integer month) {
        if (month == -1) {
            return "上月";
        } else if (month == 0) {
            return "当月";
        } else if (month == 1) {
            return "下月";
        }
        return "";
    }

    public static Dict checkAppSchedule(List<RobotTaskScheduleDTO> scheduleList) {
        Dict dict = Dict.create().set("deadLineFlag", false);
        if (CollectionUtils.isEmpty(scheduleList)) {
            return dict;
        }
        try {
            scheduleList.sort((o1, o2) -> o1.getExecOrder() - o2.getExecOrder());
        } catch (Exception e) {
            log.error("e========>{}", e.getMessage(), e);
        }
        List<String> days = Lists.newArrayList();
        List<String> hours = Lists.newArrayList();
        scheduleList.stream().filter(item -> item.getExecStyle() == 1 || item.getExecStyle() == 2).forEach(item -> {
            days.addAll(Lists.newArrayList(item.getExecCycle().split("-")));
            String execAreaTime = item.getExecAreaTime();
            String[] split = null;
            if (item.getExecStyle() == 1) {
                split = execAreaTime.split("-");
            } else {
                split = execAreaTime.split(",");
            }
            for (String hour : split) {
                hours.add(hour.split(":")[0]);
            }
        });
        String declareCycle = null;
        if (CollectionUtils.isNotEmpty(days) && CollectionUtils.isNotEmpty(hours)) {
            days.sort((o1, o2) -> Integer.valueOf(o1) - Integer.valueOf(o2));
            hours.sort((o1, o2) -> Integer.valueOf(o1) - Integer.valueOf(o2));
            String minDay = days.get(0);
            String minHour = hours.get(0);
            String maxDay = days.get(days.size() - 1);
            String maxHour = hours.get(hours.size() - 1);
            declareCycle = "当月".concat(minDay).concat("号-").concat(maxDay).concat("号：").concat(minHour).concat(":00-").concat(maxHour).concat(":00");
        }
        boolean flag = false;
        for (RobotTaskScheduleDTO item : scheduleList) {
            if (ScheduleUtils.matchSchedule(item.getExecCycle(), item.getExecAreaTime(), item.getExecStyle(), new Date())) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            dict.put("deadLineFlag", true);
            dict.put("declareCycle", declareCycle);
        }
        return dict;
    }

    public static String getSocialExecutionCode(List<EmployeeSocialDeclareChangeeProcessVO> processList, String trackStatus) {
        List<EmployeeSocialDeclareChangeeProcessVO> success = processList.stream().filter(vo -> StringUtils.isNotBlank(vo.getRobotExecCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(success)) {
            return null;
        }
        Collections.reverse(success);
        for (EmployeeSocialDeclareChangeeProcessVO vo : success) {
            if ("6".equals(trackStatus)) {
                if (vo.getProcessComment().contains("变更为申报成功") || vo.getProcessComment().contains("变更为申报失败")) {
                    return vo.getRobotExecCode();
                }
            } else if ("5".equals(trackStatus)) {
                if (StringUtils.isNotBlank(vo.getNodeComment()) && vo.getNodeComment().contains("待网站审核")) {
                    return vo.getRobotExecCode();
                }
            }
        }
        return success.get(0).getRobotExecCode();
    }

    public static String getAccfundExecutionCodes(List<EmployeeAccfundDeclareChangeeProcessVO> processList, String trackStatus) {
        List<EmployeeAccfundDeclareChangeeProcessVO> success = processList.stream().filter(vo -> StringUtils.isNotBlank(vo.getRobotExecCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(success)) {
            return null;
        }
        Collections.reverse(success);
        for (EmployeeAccfundDeclareChangeeProcessVO vo : success) {
            if ("6".equals(trackStatus)) {
                if (vo.getProcessComment().contains("变更为申报成功") || vo.getProcessComment().contains("变更为申报失败")) {
                    return vo.getRobotExecCode();
                }
            } else if ("5".equals(trackStatus)) {
                if (StringUtils.isNotBlank(vo.getNodeComment()) && vo.getNodeComment().contains("待网站审核")) {
                    return vo.getRobotExecCode();
                }
            }
        }
        return success.get(0).getRobotExecCode();
    }

    public static String getPhone(List<RobotTaskArgs> taskArgs) {
        if (CollectionUtils.isEmpty(taskArgs)) {
            return null;
        }
        RobotTaskArgs taskArg = taskArgs.stream().filter(x -> StringUtils.isNotBlank(x.getArgsKey()) &&
                (x.getArgsKey().toLowerCase().contains("mobile") || x.getArgsKey().toLowerCase().contains("phone"))).findFirst().orElse(null);
        if (taskArg != null) {
            return taskArg.getArgsValue();
        }
        return null;
    }

    public static <T> T getVal(Map<String, Object> reqMap, String key, Class<T> tClass) {
        if (!reqMap.containsKey(key)) {
            return null;
        }
        Object value = reqMap.get(key);
        if (value == null || StringUtils.isBlank(value.toString())) {
            return null;
        }
        if (tClass.equals(Integer.class)) {
            return tClass.cast(Integer.valueOf(value.toString()));
        } else if (tClass.equals(String.class)) {
            return tClass.cast(value.toString());
        } else if (tClass.equals(Long.class)) {
            return tClass.cast(Long.valueOf(value.toString()));
        } else if (tClass.equals(Double.class)) {
            return tClass.cast(Double.valueOf(value.toString()));
        }
        return null;
    }
}
