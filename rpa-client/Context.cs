using System.IO;

namespace rpa_client
{
    internal class Context
    {
        private static string seqNo;
        private static string server;
        private static string workpath;

        /// <summary>
        /// 启动路径
        /// </summary>
        public static string StartPath { get; set; }


        /// <summary>
        /// 当前机器人的唯一编码
        /// </summary>
        public static string SeqNo
        {
            get
            {
                if (seqNo == null)
                {
                    seqNo = Utils.readText(Constant.MACHINE_CODE_PATH);
                }
                return seqNo.Trim();
            }
        }

        public static string Server
        {
            get
            {
                if (server == null)
                {
                    try
                    {
                        server = Utils.readText(Constant.SERVER_URL).Trim();
                    }
                    catch
                    {

                    }
                }
                if (string.IsNullOrEmpty(server))
                {
                    server = Constant.RpaHost;
                }
                return server;
            }
        }

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
                if (string.IsNullOrEmpty(workpath))
                {
                    workpath = Constant.WorkPath;
                }
                return workpath;
            }
        }

        /// <summary>
        /// 初始化运行环境
        /// </summary>
        public static void init()
        {
            if (!Directory.Exists(WorkPath))
            {
                Logger.info("创建工作目录");
                Directory.CreateDirectory(WorkPath);
            }
            string logsPath = Path.Combine(WorkPath, "logs");
            if (!Directory.Exists(logsPath))
            {
                Logger.info("创建logs目录");
                Directory.CreateDirectory(logsPath);
            }
            string browserPath = Path.Combine(WorkPath, "browser");
            if (!Directory.Exists(browserPath))
            {
                Logger.info("创建browserPath目录");
                Directory.CreateDirectory(browserPath);
            }
            string filePath = Path.Combine(WorkPath, "file");
            if (!Directory.Exists(filePath))
            {
                Logger.info("创建file目录");
                Directory.CreateDirectory(filePath);
            }
            if (!File.Exists(Constant.LogPath))
            {
                Logger.info("创建日志文件");
                File.Create(Constant.LogPath);
            }
            AppConfig.initConfig();
            RobotProcess.stop();
            Logger.info("初始化运行环境：完成");
        }

    }
}
