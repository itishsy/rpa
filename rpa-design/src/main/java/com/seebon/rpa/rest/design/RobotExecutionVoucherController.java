package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import com.seebon.rpa.entity.robot.vo.RobotExecutionVoucherVO;
import com.seebon.rpa.service.RobotExecutionVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(value = "执行凭证", tags = "客户机器人应用执行凭证")
@RestController
@RequestMapping("/voucher")
public class RobotExecutionVoucherController {
    @Autowired
    private RobotExecutionVoucherService executionVoucherService;

    @ApiOperation(value = "执行凭证-查询执行凭证")
    @PostMapping("/hasVoucher")
    public List<RobotExecutionVoucher> hasVoucher(@RequestBody RobotExecutionVoucherVO voucher) {
        return executionVoucherService.listVoucher(voucher);
    }
}
