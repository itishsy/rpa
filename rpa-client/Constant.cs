using System.IO;

namespace rpa_client
{
    internal class Constant
    {
        public const string RpaHost = "http://121.8.131.54:8072/api";
        public const string ConfigFile = "application.properties";
        public const string AppName = "robot.exe";
        public const string AppOriName = "rpa-client.exe";
        public const string RobotName = "rpa-robot-0.0.1.jar";
        public const string RobotHubName = "rpa-robot-hub-0.0.1.jar";
        public const string TplName = "tpl.zip";
        public const string PyName = "python.zip";
        public const string ChromeName = "chrome.zip";
        public const string JreName = "jre.zip";
        public const string CaName = "ca.zip";

        public const string DefaultWorkPath = "D:\\seebon\\rpa";
        public const int Port = 9090;


        private static string workpath;
        public static string WorkPath
        {
            get
            {
                if (workpath == null)
                {
                    try
                    {
                        workpath = Utils.readText(Constant.WORK_PATH_CONF_FILE).Trim();
                    }
                    catch
                    {

                    }
                }
                if (workpath == null || string.IsNullOrEmpty(workpath))
                {
                    workpath = DefaultWorkPath;
                }
                return workpath;
            }
        }

        public static string ConfigPath
        {
            get { return Path.Combine(WorkPath, ConfigFile); }
        }
        public static string LogPath
        {
            get { return Path.Combine(WorkPath, @"logs\\client.log"); }
        }
        public static string AppPath
        {
            get { return Path.Combine(WorkPath, AppName); }
        }
        public static string RobotPath
        {
            get { return Path.Combine(WorkPath, RobotName); }
        }

        //机器码文件
        public const string MACHINE_CODE_PATH = @"C:\Program Files\rpa\rpa.init";
        //机器码文件
        public const string SERVER_URL = @"C:\Program Files\rpa\server.init";
        //workpath配置文件
        public const string WORK_PATH_CONF_FILE = @"C:\Program Files\rpa\workpath.init";

        #region saas
        //1. 登录
        public const string LOGIN_URL = "{0}/oauth/token?client_id=rpa&client_secret=123&grant_type=password&username={1}&password={2}";

        //2. 认证检查
        public const string CLIENT_CHECK_URL = "{0}/robot/client/check?machineCode={1}";

        //3. 查询工作目录结构
        public const string FIND_WORK_DIR_URL = "{0}/robot/client/install/dirs";

        //查询版本列表
        public const string FIND_VERSION_LIST_URL = "{0}/robot/version/list";

        //获取最新组件(升级时调用)
        public const string CHECK_COMPONENT_URL = "{0}/robot/client/latest/{1}?version={2}";

        //组件下载地址(升级时调用)
        public const string DOWNLOAD_COMPONENT_URL = "{0}/robot/download/jar/{1}/{2}/{3}"; //0-component,1-version,2-fileName

        //更新本机登录凭证
        public const string FIND_TOKEN_URL = "{0}/robot/client/find/token?machineCode={1}";
        
        //下载安装文件
        public const string DOWNLOAD_WORK_FILE_URL = "{0}/robot/download/{1}";


        //心跳发送
        public const string HEARTBEAT_URL = "{0}/robot/client/heartbeat?machineCode={1}&status={2}";

        //捞取日志
        public const string SEND_LOG_URL = "/robot/client/log";

        //设备变更
        public const string CHANGE_ROBOT_CLIENT_APP = "{0}/robot/app/client/changeRobotClientApp";


        public const string RPA_CA_CITY = "北京公积金,北京国管公积金,北京社保,东莞公积金,惠州公积金,昆山公积金,昆山社保,柳州社保,南宁社保,青岛公积金,苏州公积金,苏州社保,天津公积金,天津社保,武汉公积金,郑州社保,珠海公积金";
        #endregion
    }
}
