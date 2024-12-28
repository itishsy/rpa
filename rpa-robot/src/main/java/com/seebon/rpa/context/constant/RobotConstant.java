package com.seebon.rpa.context.constant;

import com.google.common.collect.Maps;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;

import java.util.Map;

public class RobotConstant {
    /**
     * Target类的bean后缀名
     */
    public static final String TARGET_BEAN_SUFFIX = "Target";
    /**
     * 申报账户对应的浏览器key
     */
    public static final String ACCOUNTNUMBER_DRIVER = "accountNumberDriver";
    /**
     * 存放在流程变量的浏览器驱动键值
     */
    public static final String DEFAULT_DRIVER_KEY = "driver";
    /**
     * 执行一次任务的唯一码
     */
    public static final String EXECUTION_CODE_KEY = "executionCode";
    /**
     * 执行任务的流程编码
     */
    public static final String EXECUTION_FLOW_CODE_KEY = "executionFlowCode";
    /**
     * 执行一次任务的一个主流程的批次码
     */
    public static final String BATCH_CODE = "batchCode";
    /**
     * 执行一次任务的一个主流程的批次码
     */
    public static final String INST_ID = "instId";
    /**
     * 固定默认一个报盘文件下载路径
     */
    public static final String FILE_PATH = "filePath";
    /**
     * 执行一次任务的流程计数器
     */
    public static final String FLOW_NUM_KEY = "flowNumKey";
    /**
     * 标记为继续执行到下一步
     */
    public static final String NEXT_STEP = "NEXT";
    /**
     * 流程开始
     */
    public static final String START_ACTION_CODE = "start";
    /**
     * 流程结束
     */
    public static final String END_ACTION_CODE = "end";
    /**
     * 子流程
     */
    public static final String SUB_ACTION_CODE = "subFlow";
    /**
     * 最后子流程
     */
    public static final String FINAL_ACTION_CODE = "finalFlow";
    /**
     * 浏览器窗口
     */
    public static final String WINDOW_HANDLE = "windowHandle";
    /**
     * 浏览器文件下载默认保存路径
     */
    public static final String DOWNLOAD_DEFAULT_PATH = "downloadDefaultPath";
    /**
     * 浏览器文件下载默认保存路径-兼容之前的流程配置
     */
    public static final String DATA_PATH = "dataPath";
    /**
     * 浏览器默认保存文件路径下的缓存文件名称
     */
    public static final String CACHE_DOWNLOAD_FILE_NAME = "cacheDownloadFileName";
    /**
     * 是否开启拦截
     */
    public static final String OPEN_INTERCEPT_API = "openInterceptApi";
    /**
     * rpa录制工具打开
     */
    public static final String OPEN_TOOL_BROWSER = "openToolBrowser";
    /**
     * 指令执行的超时时间(秒）
     */
    public static final int DEFAULT_ACTION_TIMEOUT = 30;
    /**
     * 单独执行登录流程,不关闭浏览器
     */
    public static final String LOGIN_FLOW_FLAG = "loginFlowFlag";
    /**
     * 任务运行缓存
     */
    public static final Map<String, RobotTaskQueueVO> taskRunMap = Maps.newConcurrentMap();

    /**
     * 接口响应拦截链接
     */
    public static final String INTERCEPTOR_URLS = "interceptorUrls";

    /**
     * 接口响应拦截参数
     */
    public static final String INTERCEPTOR_PARAM_KEYS = "interceptorParamKeys";
    /**
     * 断点调试步骤编码
     */
    public static final String DEBUG_STEP_CODES = "debugStepCodes";
    /**
     * 申报数据
     */
    public static final String DECLARE_LIST = "declareList";
    /**
     * 核验数据
     */
    public static final String CHECK_LIST = "checkList";
    /**
     * 转入文件路径
     */
    public static final String ZR_PATH = "zrPath";
    /**
     * 启封文件路径
     */
    public static final String QF_PATH = "qfPath";
    /**
     * 转入数据列表
     */
    public static final String ZR_LIST = "zrList";
    /**
     * 启封数据列表
     */
    public static final String QF_LIST = "qfList";
    /**
     * 申报数据补全后不为空列表
     */
    public static final String DECLARE_LIST_BQ = "declareListBq";
    /**
     * 转入数据补全后不为空列表
     */
    public static final String ZR_LIST_BQ = "zrListBq";
    /**
     * 启封数据补全后不为空列表
     */
    public static final String QF_LIST_BQ = "qfListBq";
    /**
     * 报盘sheet的索引
     */
    public static final String SHEET_INDEX = "sheetIndex";
    /**
     * 报盘表头行的行索引
     */
    public static final String HEARD_INDEX = "heardIndex";
    /**
     * 证件号码列索引
     */
    public static final String ID_CARD_COL_INDEX = "idCardColIndex";
    /**
     * 证件号码列名称
     */
    public static final String ID_CARD_COL_NAME = "idCardColName";
    /**
     * 个人社保号列索引
     */
    public static final String SOCIAL_NUMBER_COL_INDEX = "socialNumberColIndex";
    /**
     * 个人社保号列名称
     */
    public static final String SOCIAL_NUMBER_COL_NAME = "socialNumberColName";
    /**
     * 个人公积金号列索引
     */
    public static final String ACCFUND_NUMBER_COL_INDEX = "accfundNumberColIndex";
    /**
     * 个人公积金号列名称
     */
    public static final String ACCFUND_NUMBER_COL_NAME = "accfundNumberColName";
    /**
     * 个人电脑编号列索引
     */
    public static final String COMPUTER_NUMBER_COL_INDEX = "computerNumberColIndex";
    /**
     * 个人电脑编号列名称
     */
    public static final String COMPUTER_NUMBER_COL_NAME = "computerNumberColName";
    /**
     * 证件号码key
     */
    public static final String ID_CARD_KEY_NAME = "idCardKeyName";
    /**
     * 远程调试端口（本地开发）
     */
    public static final String REMOTE_DEBUG_PORT = "remoteDebugPort";
    /**
     * 最后通过指令打开的页面
     */
    public static final String LAST_OPEN_PAGE = "lastOpenPage";
    /**
     * 最后通过指令打开的页面
     */
    public static final String LAST_OPEN_PAGE_LIST = "lastOpenPageList";
    /**
     * 重启机器人标志,true:重启机器人,false:不重启机器人
     */
    public static volatile Boolean RESTART_ROBOT = false;
    /**
     * usbKey挂载
     */
    public static volatile Boolean USB_KEY_FLAG = false;
    /**
     * usbKey挂载Ip
     */
    public static final String USB_KEY_IP_ADDRESS = "usbKeyIpAddress";

    /**
     * usbKey挂载busId
     */
    public static final String USB_KEY_BUS_ID = "usbKeyBusId";
}
