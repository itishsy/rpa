package com.seebon.rpa.scheduler;

import com.seebon.rpa.repository.redis.UserSessionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 根据当前用户，生成机器人安装文件
 */
//@Component
@Deprecated
public class InstallerBuilder {

    @Autowired
    private UserSessionDao userSessionDao;

    protected String username = null;

    @PostConstruct
    protected void doMsBuild() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (hasVal()){
                    String code = "namespace rpa_installer\n" +
                            "{\n" +
                            "    internal class User\n" +
                            "    {\n" +
                            "        public const string Name = \""+username+"\";\n" +
                            "    }\n" +
                            "}";

                }
            }
        }).start();
    }

    private boolean hasVal(){
        username = userSessionDao.popInstaller();
        return username != null;
    }
}
