package com.seebon.rpa.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ProcessUtil {
    private static final String STORAGE_FILE_PATH = System.getenv("ProgramFiles");

    private static final String RPA_FILE_DIR_NAME = "rpa";

    public static final String PROCESS_FILE_INIT_NAME = "process.init";

    public static final String SERVICE_FILE_INIT_NAME = "service.init";

    static {
        File dir = new File(STORAGE_FILE_PATH + File.separator + RPA_FILE_DIR_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static Map<String, Object> compare() {
        Map<String, Object> resultMap = Maps.newHashMap();

        //初始化缓存的进程路径
        Set<String> cacheProcessNames = ProcessUtil.loadFromDisk(PROCESS_FILE_INIT_NAME);
        //获取最新进程路径
        Set<String> processPaths = ProcessUtil.getProcessPath();
        //比对进程
        List<String> newProcessPaths = processPaths.stream().filter(path -> !cacheProcessNames.contains(path)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newProcessPaths)) {
            log.info("未发现新的进程路径.");
        } else {
            newProcessPaths.stream().forEach(filePath -> {
                log.info("新的进程路径：" + filePath);
            });
            resultMap.put("processPaths", newProcessPaths);
        }

        //初始化缓存的服务路径
        Set<String> cacheServicePaths = ProcessUtil.loadFromDisk(SERVICE_FILE_INIT_NAME);
        //获取最新服务路径
        Set<String> servicePaths = ProcessUtil.getServicePath();
        //比对服务
        List<String> newServicePaths = servicePaths.stream().filter(path -> !cacheServicePaths.contains(path)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newServicePaths)) {
            log.info("未发现新的服务路径.");
        } else {
            newServicePaths.stream().forEach(filePath -> {
                log.info("新的服务路径：" + filePath);
            });
            resultMap.put("servicePaths", newServicePaths);
        }
        return resultMap;
    }

    public static void init() {
        //获取进程路径
        Set<String> processPaths = ProcessUtil.getProcessPath();
        //缓存进程路径
        ProcessUtil.diskSave(PROCESS_FILE_INIT_NAME, processPaths);

        //获取服务路径
        Set<String> servicePaths = ProcessUtil.getServicePath();
        //缓存服务路径
        ProcessUtil.diskSave(SERVICE_FILE_INIT_NAME, servicePaths);

        log.info("初始化进程路径和服务路径完成.");
    }

    public static Set<String> getProcessPath() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("CMD");
        commandList.add("/C");
        commandList.add("wmic process get executablepath");
        return ProcessUtil.exec(commandList);
    }

    public static Set<String> getServicePath() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("CMD");
        commandList.add("/C");
        commandList.add("wmic service get PathName");
        return ProcessUtil.exec(commandList);
    }

    public static List<Map<String, String>> getServiceName() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("CMD");
        commandList.add("/C");
        commandList.add("wmic service get Name,PathName /format:HTABLE");
        List<String> list = ProcessUtil.exec2(commandList);
        List<Map<String, String>> dataList = Lists.newArrayList();
        Document document = Jsoup.parse(list.toString(), "");
        Elements elements = document.getElementsByTag("tr");
        for (Element element : elements) {
            Elements tdElements = element.getElementsByTag("td");
            if (tdElements.size() != 3) {
                continue;
            }
            Map<String, String> dataMap = Maps.newHashMap();
            String name = StringUtils.substringBeforeLast(tdElements.get(1).text(), ".");
            String pathName = StringUtils.substringBeforeLast(tdElements.get(2).text(), ".");
            dataMap.put(name, StringUtils.substringBeforeLast(pathName.replace("\"", ""), ".exe") + ".exe");
            System.out.println(dataMap);
            dataList.add(dataMap);
        }
        return dataList;
    }

    public static Set<String> exec(List<String> commandList) {
        String[] commands = new String[commandList.size()];
        Set<String> results = Sets.newHashSet();
        try {
            Process process = Runtime.getRuntime().exec(commandList.toArray(commands));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    //log.info(line);
                    results.add(line.trim());
                }
            }
        } catch (Exception e) {
            log.error("获取路径异常." + e.getMessage(), e);
        }
        return results;
    }

    public static List<String> exec2(List<String> commandList) {
        String[] commands = new String[commandList.size()];
        List<String> results = Lists.newArrayList();
        try {
            Process process = Runtime.getRuntime().exec(commandList.toArray(commands));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    //log.info(line);
                    results.add(line.trim());
                }
            }
        } catch (Exception e) {
            log.error("获取路径异常." + e.getMessage(), e);
        }
        return results;
    }

    public static Set<String> loadFromDisk(String fileName) {
        File tempFile = getFile(fileName);
        if (!tempFile.exists()) {
            return Sets.newHashSet();
        }
        Set<String> result = Sets.newHashSet();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(tempFile));
            String device = null;
            while ((device = br.readLine()) != null) {
                result.add(device);
            }
        } catch (Exception e) {
            log.error("load disk error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(br);
        }
        return result;
    }

    public static void diskSave(String fileName, Set<String> processPaths) {
        if (CollectionUtils.isEmpty(processPaths)) {
            return;
        }
        //获取缓存文件
        File tempFile = ProcessUtil.getFile(fileName);
        if (tempFile.exists()) {
            //先删除
            tempFile.delete();
        }
        //重新获取
        tempFile = ProcessUtil.getFile(fileName);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(tempFile));
            for (String path : processPaths) {
                bw.write(path + "\n");
            }
            bw.flush();
        } catch (Exception e) {
            log.error("disk save error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
    }

    public static File getFile(String fileName) {
        return new File(STORAGE_FILE_PATH + File.separator + RPA_FILE_DIR_NAME + File.separator + fileName);
    }
}
