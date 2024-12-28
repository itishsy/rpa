package com.seebon.rpa.actions.impl.custom;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
@RobotAction(name = "执行shell脚本", targetType = TargetsTarget.class, comment = "执行shell脚本")
public class ExecuteShell extends AbstractAction {
    @ActionArgs("传入要执行的脚本")
    private String expression;

    @Value("${rpa.work-path}")
    private String path;

    @Override
    public void run() {
        System.out.println("当前进程的工作空间:" + System.getProperty("user.dir"));
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", path + "/" + expression);
        int runningStatus = 0;
        try {
            Process p = pb.start();
            log.info("开始执行");
            runningStatus = p.waitFor();
            // 读取进程的输出流
            Scanner scanner = new Scanner(p.getInputStream());
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
            // 等待进程执行完毕
            int exitCode = p.waitFor();
            log.info("Process exited with code " + exitCode);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        if (runningStatus != 0) {
            log.info("执行成功-----------");
        }
    }
}
