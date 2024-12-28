package com.seebon.rpa.actionTests;

import com.seebon.rpa.ActionBaseTests;
import org.junit.jupiter.api.Test;

public class PageTests extends ActionBaseTests {


    @Test
    void startRobot(){
        addStep("访问SaaS1","access","{timeout:\"10\",address:\"http://localhost:9527/api/sys/dict/getDictByKey?dataKey=10001\"}");
        addStep("访问SaaS2","access","{address:\"http://localhost:9527/api/sys/dict/getDictByKey?dataKey=10001\"}");
    }

}
