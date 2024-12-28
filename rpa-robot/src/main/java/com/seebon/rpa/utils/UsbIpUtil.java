package com.seebon.rpa.utils;

import cn.hutool.core.io.IoUtil;
import com.seebon.rpa.context.constant.RobotConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
public final class UsbIpUtil {

    private static Semaphore semaphore = new Semaphore(0);

    private static volatile boolean releaseFlag = false;

    private static final Lock lock = new ReentrantLock();

    private UsbIpUtil() {
    }

    public static String attach(String ip, String busId, int timeout) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/C", "usbip attach -r " + ip + " -b " + busId);
        log.info("command=" + processBuilder.command().toString());

        //结果
        List<String> result = Lists.newArrayList();
        try {
            //是否释放
            releaseFlag = true;
            //默认信号量
            semaphore = new Semaphore(0);

            Process process = processBuilder.start();
            CompletableFuture.runAsync(() -> {
                log.info("shell exec start");
                try {
                    //脚本执行正常时的输出信息
                    String inputResult = IoUtil.read(process.getInputStream(), Charset.forName("GBK"));
                    //脚本执行异常时的输出信息
                    String errorResult = IoUtil.read(process.getErrorStream(), Charset.forName("GBK"));
                    log.info("shell exec inputResult=" + inputResult + ",errorResult=" + errorResult);
                    result.add(inputResult);
                } catch (Exception e) {
                    log.error("shell exec error." + e.getMessage(), e);
                } finally {
                    UsbIpUtil.release();
                }
                log.info("shell exec end");
            });
            process.waitFor(10, TimeUnit.SECONDS);
            if (timeout > 10) {
                timeout = timeout - 10;
            }
            UsbIpUtil.tryAcquire(timeout);
        } catch (Exception e) {
            log.error("attach usb key error." + e.getMessage(), e);
        }
        return result.stream().collect(Collectors.joining(","));
    }

    public static String detach() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/C", "usbip detach --all");
        log.info("command=" + processBuilder.command().toString());

        //结果
        List<String> result = Lists.newArrayList();
        try {
            Process process = processBuilder.start();
            CompletableFuture.runAsync(() -> {
                log.info("shell exec start");
                try {
                    //脚本执行正常时的输出信息
                    String inputResult = IoUtil.read(process.getInputStream(), Charset.forName("GBK"));
                    //脚本执行异常时的输出信息
                    String errorResult = IoUtil.read(process.getErrorStream(), Charset.forName("GBK"));
                    log.info("shell exec inputResult=" + inputResult + ",errorResult=" + errorResult);
                    result.add(inputResult);
                } catch (Exception e) {
                    log.error("shell exec error." + e.getMessage(), e);
                }
                log.info("shell exec end");
            });
            process.waitFor();
        } catch (Exception e) {
            log.error("detach usb key error." + e.getMessage(), e);
        }
        return result.stream().collect(Collectors.joining(","));
    }

    private static void tryAcquire(int timeout) {
        try {
            UsbIpUtil.semaphore.tryAcquire(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("attach usb key timeout exception." + e.getMessage(), e);
        }
    }

    private static void release() {
        lock.lock();
        try {
            if (releaseFlag == true && semaphore.availablePermits() == 0) {
                UsbIpUtil.semaphore.release(1);
            }
            releaseFlag = false;
        } finally {
            lock.unlock();
        }
    }
}
