package com.seebon.rpa.utils.usb.utils;


import com.google.common.collect.Lists;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.usb.listener.CommandExecOutputListener;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wrapper the shell command line controller
 *
 * @author pengdeyi
 */
@Slf4j
public final class ShellUtils {
    private static Semaphore semaphore = new Semaphore(0);

    private static volatile boolean releaseFlag = false;

    private static final Lock lock = new ReentrantLock();

    private static final long timeout = 10;//10秒

    private ShellUtils() {
    }

    public static void runCommand(String command, CommandExecOutputListener listener) {
        List<String> commandsList = Lists.newArrayList();
        commandsList.add("CMD");
        commandsList.add("/C");
        commandsList.add(command);
        String[] commands = new String[commandsList.size()];
        commands = commandsList.toArray(commands);
        try {
            Process pid = Runtime.getRuntime().exec(commands);

            //是否释放
            releaseFlag = true;
            //默认信号量
            semaphore = new Semaphore(0);

            //脚本执行正常时的输出信息
            new Thread(() -> {
                new ShellHandler(pid.getInputStream(), ShellHandler.INPUT_STREAM, listener).handleTheBuffer();
            }).start();

            //脚本执行异常时的输出信息
            new Thread(() -> {
                new ShellHandler(pid.getErrorStream(), ShellHandler.ERROR_STREAM, listener).handleTheBuffer();
            }).start();

            pid.waitFor(10, TimeUnit.SECONDS);

            ShellUtils.tryAcquire();
        } catch (Exception e) {
            log.error("run command=[" + command + "] error." + e.getMessage(), e);
            ShellUtils.release();
        }
    }

    private static void tryAcquire() {
        try {
            ShellUtils.semaphore.tryAcquire(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("执行指令异常." + e.getMessage(), e);
        }
    }

    private static class ShellHandler {
        private static int INPUT_STREAM = 0;
        private static int ERROR_STREAM = 1;
        private InputStream in;
        private CommandExecOutputListener listener;
        private int type;

        public ShellHandler(InputStream in, int type, CommandExecOutputListener listener) {
            this.in = in;
            this.type = type;
            this.listener = listener;
        }

        public void handleTheBuffer() {
            List<String> lines = Convert.read(this.in);
            try {
                if (this.type == ShellHandler.ERROR_STREAM) {
                    this.listener.onError(lines);
                } else {
                    this.listener.onSuccess(lines);
                }
            } catch (Exception e) {
                log.error("shell handler error." + e.getMessage(), e);
            } finally {
                ShellUtils.release();
            }
        }
    }

    private static void release() {
        lock.lock();
        try {
            if (releaseFlag == true && semaphore.availablePermits() == 0) {
                ShellUtils.semaphore.release(1);
            }
            releaseFlag = false;
        } finally {
            lock.unlock();
        }
    }
}