package com.seebon.rpa.actionTests;

import com.seebon.rpa.ActionBaseTests;
import org.junit.jupiter.api.Test;

public class DownloadTests extends ActionBaseTests {


    @Test
    void startRobot(){
        addStep("访问百度","browser","{actionType:\"visit\",address:\"http://www.baidu.com\"}");
    }

}
