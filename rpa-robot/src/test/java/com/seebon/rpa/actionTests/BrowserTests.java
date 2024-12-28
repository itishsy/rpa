package com.seebon.rpa.actionTests;

import com.seebon.rpa.ActionBaseTests;
import org.junit.jupiter.api.Test;

public class BrowserTests extends ActionBaseTests {

    @Test
    void startRobot(){
        addStep("打开谷歌浏览器","browser","{driverType:\"chrome\",actionType:\"start\"}");
        addStep("访问百度","browser","{actionType:\"visit\",address:\"http://www.baidu.com\"}");
        addStep("点击新闻","click",null, "{attrType:\"linkText\",attrValue:\"新闻\"}");
        addStep("切换到新窗口","browser","{actionType:\"switchTo\"}");
        addStep("点击帮助","click",null, "{attrType:\"linkText\",attrValue:\"帮助\"}");
        addStep("关闭新窗口","browser","{actionType:\"close\"}");
        addStep("打开IE浏览器","browser","{driverType:\"ie\",actionType:\"start\"}");
        addStep("IE访问百度","browser","{actionType:\"visit\",address:\"http://www.baidu.com\"}");
        addStep("IE点击新闻","click",null, "{attrType:\"linkText\",attrValue:\"新闻\"}");
        addStep("IE切换到新窗口","browser","{actionType:\"switchTo\"}");
        addStep("IE点击帮助","click",null, "{attrType:\"linkText\",attrValue:\"帮助\"}");
        addStep("IE关闭新窗口","browser","{actionType:\"close\"}");
        addStep("IE输入搜索条件","input","{inputValue:\"rpa\"}", "{attrType:\"id\",attrValue:\"kw\"}");
        addStep("IE点击搜索","click",null, "{attrType:\"id\",attrValue:\"su\"}");
        addStep("IE窗口","browser","{actionType:\"close\"}");
        addStep("输入搜索条件","input","{inputValue:\"rpa\"}", "{attrType:\"id\",attrValue:\"kw\"}");
        addStep("点击搜索","click",null, "{attrType:\"id\",attrValue:\"su\"}");
        addStep("关闭谷歌浏览器","browser","{actionType:\"quit\"}");
        addStep("等待10s","wait","{timeout:5}");
        addStep("访问百度2","browser","{actionType:\"visit\",address:\"http://www.baidu.com\"}");
        addStep("等待15s","wait","{timeout:5}");
    }

}
