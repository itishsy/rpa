package com.seebon.rpa.utils;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * temple file storage 4 the path
 *
 * @author ken.peng
 */
@Slf4j
public final class FileStorage {

    private static final String STORAGE_FILE_PATH = System.getenv("ProgramFiles");

    private static final String STORAGE_FILE_DIR_NAME = "rpa";

    public static final String STORAGE_FILE_INIT_NAME = "usb.init";

    public static final String STORAGE_FILE_PORT_NAME = "usb.port";

    private static final String STORAGE_FILE_RPA_NAME = "rpa.init";

    static {
        File dir = new File(STORAGE_FILE_PATH + File.separator + STORAGE_FILE_DIR_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static String loadMachineCodeFromDisk() {
        File tempFile = getFile(STORAGE_FILE_RPA_NAME);
        if (!tempFile.exists()) {
            throw new BusinessException("机器码为空,请联系管理员.");
        }
        List<String> codes = Lists.newArrayList();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(tempFile));
            String code = null;
            while ((code = br.readLine()) != null) {
                codes.add(code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(br);
        }
        return codes.get(0);
    }

    public static List<String> loadDeviceFromDisk() {
        File tempFile = getFile(STORAGE_FILE_INIT_NAME);
        if (!tempFile.exists()) {
            return Lists.newArrayList();
        }
        List<String> result = Lists.newArrayList();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(tempFile));
            String device = null;
            while ((device = br.readLine()) != null) {
                result.add(device);
            }
        } catch (Exception e) {
            log.error("load disk the device file error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(br);
        }
        return result;
    }

    public static void diskSaveDevices(List<String> devices) {
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        File tempFile = getFile(STORAGE_FILE_INIT_NAME);
        if (tempFile.exists()) {
            return;
        }
        BufferedWriter bw = null;
        try {
            if (!tempFile.exists() || tempFile.length() == 0) {
                bw = new BufferedWriter(new FileWriter(tempFile));
                for (String device : devices) {
                    bw.write(device + "\n");
                }
                bw.flush();
            }
        } catch (Exception e) {
            log.error("disk save the device file error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
    }

    public static Map<String, String> loadPortFromDisk() {
        File file = getFile(STORAGE_FILE_PORT_NAME);
        if (!file.exists()) {
            return Maps.newConcurrentMap();
        }
        Map<String, String> resultMap = Maps.newConcurrentMap();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String device = null;
            while ((device = br.readLine()) != null) {
                resultMap.put(device.split("\\|")[0], device.split("\\|")[1]);
            }
        } catch (Exception e) {
            log.error("load disk the port file error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(br);
        }
        return resultMap;
    }

    public static void diskSavePorts(String port) {
        if (StringUtils.isBlank(port)) {
            return;
        }
        Map<String, String> portMap = loadPortFromDisk();
        if (portMap.get(port.split("\\|")[0]) != null) {
            portMap.remove(port.split("\\|")[0]);
        }
        portMap.put(port.split("\\|")[0], port.split("\\|")[1]);

        //获取缓存文件
        File tempFile = getFile(STORAGE_FILE_PORT_NAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        tempFile = getFile(STORAGE_FILE_PORT_NAME);
        BufferedWriter bw = null;
        try {
            if (!tempFile.exists() || tempFile.length() == 0) {
                bw = new BufferedWriter(new FileWriter(tempFile));
                for (String key : portMap.keySet()) {
                    bw.write(key + "|" + portMap.get(key) + "\n");
                }
                bw.flush();
            }
        } catch (Exception e) {
            log.error("disk save the port file error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
    }

    public static File getFile(String fileName) {
        return new File(STORAGE_FILE_PATH + File.separator + STORAGE_FILE_DIR_NAME + File.separator + fileName);
    }
}
