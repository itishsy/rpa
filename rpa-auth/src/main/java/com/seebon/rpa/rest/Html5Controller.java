package com.seebon.rpa.rest;

import com.seebon.rpa.service.H5Service;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/h5")
@Api(value = "h5登录", tags = "h5登录")
@Slf4j
public class Html5Controller {

    @Autowired
    private H5Service h5Service;

    @GetMapping("/{shortUrl}")
    public void toH5Page(HttpServletResponse response, @PathVariable("shortUrl")String shortUrl) {
        h5Service.toH5Page(response, shortUrl);
    }

    @GetMapping("/auth")
    public void auth(HttpServletResponse response, @RequestParam(value = "code")String code, @RequestParam(value="state") String state) {
        h5Service.toH5AuthPage(response, code, state);
    }

}
