package com.seebon.rpa.web.view;

import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class RobotController {
    @Autowired
    private RobotFlowMapper flowMapper;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/flow")
    public String flow(ModelMap modelMap) {
        List<RobotFlow> flows = flowMapper.selectAll();
        modelMap.addAttribute("flows", flows);
        return "flow";
    }
}
