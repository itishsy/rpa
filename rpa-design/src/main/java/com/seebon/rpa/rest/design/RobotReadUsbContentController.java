package com.seebon.rpa.rest.design;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.device.ReadUsbContentApiService;
import com.seebon.rpa.device.UsbKeyApiService;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.device.request.ReadUsbContentRequest;
import com.seebon.rpa.entity.robot.device.request.UsbKeyRequest;
import com.seebon.rpa.entity.robot.device.response.PageResponse;
import com.seebon.rpa.entity.robot.device.response.ReadUsbContentResponse;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.vo.RobotUsbKeyVO;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "读取usb内容", tags = "读取usb内容")
@RestController
@RequestMapping("/readUsbContent")
public class RobotReadUsbContentController {
    @Autowired
    private ReadUsbContentApiService readUsbContentApiService;
    @Autowired
    private UsbKeyApiService usbKeyApiService;

    @ApiOperation(value = "读取usb内容-分页")
    @PostMapping("/page")
    public IgGridDefaultPage<ReadUsbContentResponse> page(@RequestBody IgRequestObject reqObj) {
        Dict dict = RequestParamsUtil.toDict(reqObj);
        ReadUsbContentRequest request = new ReadUsbContentRequest();
        request.setIdVendor(dict.getInt("idVendor"));
        request.setIdProduct(dict.getInt("idProduct"));
        request.setPageStart(dict.getInt("start"));
        request.setPageSize(dict.getInt("size"));
        PageResponse<ReadUsbContentResponse> page = readUsbContentApiService.list(request);
        return new IgGridDefaultPage<ReadUsbContentResponse>(page.getRows(), (int) page.getRecords());
    }

    @ApiOperation(value = "读取usb内容-保存")
    @PostMapping("/save")
    public Integer save(@RequestBody ReadUsbContentRequest request) {
        Session session = SecurityContext.currentUser();
        request.setCreateId((int) session.getId());
        request.setUpdateId((int) session.getId());
        if (CollectionUtils.isNotEmpty(request.getTableData())) {
            List<Map<String, Object>> contentList = Lists.newArrayList();
            for (Map<String, Object> tableData : request.getTableData()) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("type", tableData.get("type"));
                dataMap.put("data", tableData.get("data"));
                contentList.add(dataMap);
            }
            request.setContent(JSON.toJSONString(contentList));
        }
        readUsbContentApiService.save(request);
        return 1;
    }

    @ApiOperation(value = "读取usb内容-测试")
    @PostMapping("/testUserServer")
    public Integer testUserServer(@RequestBody ReadUsbContentRequest request) {
        Session session = SecurityContext.currentUser();
        request.setCreateId((int) session.getId());
        request.setUpdateId((int) session.getId());
        if (CollectionUtils.isNotEmpty(request.getTableData())) {
            List<Map<String, Object>> contentList = Lists.newArrayList();
            for (Map<String, Object> tableData : request.getTableData()) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put("type", tableData.get("type"));
                dataMap.put("data", tableData.get("data"));
                contentList.add(dataMap);
            }
            request.setContent(JSON.toJSONString(contentList));
        }
        readUsbContentApiService.save(request);
        return 1;
    }

    @ApiOperation(value = "读取usb内容-删除")
    @PostMapping("/deleteById")
    public void deleteById(@RequestParam("id") Integer id) {
        readUsbContentApiService.deleteById(id);
    }

    @ApiOperation(value = "读取usb内容-获取")
    @GetMapping("/getById")
    public ReadUsbContentResponse getById(@RequestParam("id") Integer id) throws Exception {
        ReadUsbContentRequest request = new ReadUsbContentRequest(id);
        PageResponse<ReadUsbContentResponse> page = readUsbContentApiService.list(request);
        if (CollectionUtils.isEmpty(page.getRows())) {
            return null;
        }
        ReadUsbContentResponse response = page.getRows().get(0);
        if (StringUtils.isNotBlank(response.getContent())) {
            List<Map<String, Object>> contentList = new ObjectMapper().readValue(response.getContent(), new TypeReference<List<Map<String, Object>>>() {});
            response.setTableData(contentList);
        }
        return response;
    }

    @ApiOperation(value = "读取usb内容-获取")
    @GetMapping("/getBusIdList")
    public List<UsbKeyResponse> getBusIdList(@RequestParam("companyId") Integer companyId, @RequestParam("macAddress") String macAddress) {
        UsbKeyRequest request = new UsbKeyRequest();
        request.setCompanyId(companyId);
        request.setHostMacAddress(macAddress);
        return usbKeyApiService.getBusIdList(request);
    }
}
