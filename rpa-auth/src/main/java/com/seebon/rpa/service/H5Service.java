package com.seebon.rpa.service;

import javax.servlet.http.HttpServletResponse;

public interface H5Service {

    void toH5Page(HttpServletResponse response, String shortUrl);

    void toH5AuthPage(HttpServletResponse response, String code, String state);
}
