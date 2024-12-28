package com.seebon.rpa.utils.python;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.python.listener.CommandExecOpenListener;
import com.seebon.rpa.utils.python.listener.CommandExecOutputListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public final class PythonUtil {
    private static Semaphore semaphore = new Semaphore(0);

    private static volatile boolean releaseFlag = false;

    private static final Lock lock = new ReentrantLock();

    private PythonUtil() {
    }

    public static List<String> runCommand(String command, String successMsg, int timeout, List<String> commands) {
        CommandExecOpenListener listener = new CommandExecOpenListener();
        PythonUtil.runCommand(commands, timeout, listener);
        List<String> list = listener.getLineList();
        log.info(command + "返回：" + JSON.toJSONString(list));
        PythonUtil.isRunTrue(command, successMsg, list);
        return list;
    }

    public static void runCommand(List<String> commandList, int timeout, CommandExecOutputListener listener) {
        List<String> commandsList = Lists.newArrayList();
        commandsList.add("cmd");
        commandsList.add("/c");
        commandsList.addAll(commandList);
        commandsList.add("runas");

        log.info("run command :" + JSON.toJSONString(commandsList));

        String[] commands = new String[commandsList.size()];
        commands = commandsList.toArray(commands);

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        try {
            Process process = builder.start();
            //是否释放
            releaseFlag = true;
            //默认信号量
            semaphore = new Semaphore(0);

            //脚本执行正常时的输出信息
            new Thread(() -> {
                new ShellHandler(process.getInputStream(), ShellHandler.INPUT_STREAM, listener).handleTheBuffer();
            }).start();

            //脚本执行异常时的输出信息
            new Thread(() -> {
                new ShellHandler(process.getErrorStream(), ShellHandler.ERROR_STREAM, listener).handleTheBuffer();
            }).start();

            process.waitFor(10, TimeUnit.SECONDS);

            if (timeout > 20) {
                timeout = timeout - 20;
            } else {
                timeout = timeout - 10;
            }

            PythonUtil.tryAcquire(timeout);
        } catch (Exception e) {
            log.error("run command=[" + JSON.toJSONString(commandList) + "] error." + e.getMessage(), e);
            PythonUtil.release();
        }
    }

    private static void tryAcquire(int timeout) {
        try {
            PythonUtil.semaphore.tryAcquire(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("执行指令异常." + e.getMessage(), e);
        }
    }

    private static void isRunTrue(String command, String containMsg, List<String> list) {
        if (StringUtils.isBlank(containMsg)) {
            return;
        }
        if (CollectionUtils.isEmpty(list) || !list.contains(containMsg)) {
            throw new BusinessException(command + "异常.");
        }
    }

    private static class ShellHandler {
        private static int INPUT_STREAM = 0;
        private static int ERROR_STREAM = 1;
        private InputStream input;
        private CommandExecOutputListener listener;
        private int type;

        public ShellHandler(InputStream input, int type, CommandExecOutputListener listener) {
            this.input = input;
            this.type = type;
            this.listener = listener;
        }

        public void handleTheBuffer() {
            List<String> lines = Convert.read(this.input);
            try {
                if (this.type == ShellHandler.ERROR_STREAM) {
                    this.listener.onError(lines);
                } else {
                    this.listener.onSuccess(lines);
                }
            } catch (Exception e) {
                log.error("shell handler error." + e.getMessage(), e);
            } finally {
                PythonUtil.release();
            }
        }
    }

    private static void release() {
        lock.lock();
        try {
            if (releaseFlag == true && semaphore.availablePermits() == 0) {
                PythonUtil.semaphore.release(1);
            }
            releaseFlag = false;
        } finally {
            lock.unlock();
        }
    }
}