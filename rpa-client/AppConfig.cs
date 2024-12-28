using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace rpa_client
{
    internal class AppConfig
    {
        public const string SERVER_PORT = "server.port";
        public const string RPA_SERVER = "rpa.server";
        public const string RPA_WORKPATH = "rpa.work-path";
        public const string RPA_TOKEN = "rpa.token";
        public const string RPA_BOT_TOKEN = "rpa.bot.token";
        public const string RPA_CLIENT_VERSION = "rpa.client-version";
        public const string RPA_ROBOT_VERSION = "rpa.robot-version";
        public const string RPA_ROBOT_HUB_VERSION = "rpa.robot-hub-version";
        public const string RPA_TPL_VERSION = "rpa.tpl-version";
        public const string RPA_PY_VERSION = "rpa.py-version";
        public const string RPA_CHROME_VERSION = "rpa.chrome-version";
        public const string RPA_FIREFOX_VERSION = "rpa.firefox-version";
        public const string RPA_JRE_VERSION = "rpa.jre-version";
        public const string RPA_CA_VERSION = "rpa.ca-version";
        public const string RPA_AUTO_SYNC = "developer.auto.sync";
        public const string RPA_AUTO_TASK = "developer.auto.task";
        public const string RPA_SMS_SERVER = "rpa.smsServer";
        public const string RPA_BIN360 = "rpa.bin360";
        public const string RPA_FIREFOX = "rpa.binFirefox";


        private static Dictionary<string, string> configDic = new Dictionary<string, string>();

        public static string ConfigPath
        {
            get
            {
                return Path.Combine(Constant.WorkPath, Constant.ConfigFile);
            }
        }

        /// <summary>
        /// 初始化配置文件
        /// </summary>
        /// <returns></returns>
        public static bool initConfig()
        {
            if (!File.Exists(Constant.ConfigPath))
            {
                File.Create(Constant.ConfigPath).Close();
                Logger.info("写入配置文件");
                configDic.Add(SERVER_PORT, Constant.Port.ToString());
                configDic.Add(RPA_SERVER, Context.Server);
                configDic.Add(RPA_WORKPATH, Constant.WorkPath.Replace("\\", "\\\\"));
                configDic.Add(RPA_CLIENT_VERSION, "0.0.0");
                configDic.Add(RPA_ROBOT_VERSION, "0.0.0");
                configDic.Add(RPA_TPL_VERSION, "0.0.0");
                configDic.Add(RPA_PY_VERSION, "0.0.0");

                string[] citys = Constant.RPA_CA_CITY.Split(',');
                int city_size = citys.Length;
                for (int i = 0; i < city_size; i++)
                {
                    configDic.Add(RPA_CA_VERSION + "." + citys[i], "0.0.0");
                }
                configDic.Add(RPA_AUTO_SYNC, "on");
                configDic.Add(RPA_AUTO_TASK, "on");
                configDic.Add(RPA_TOKEN, "");
                configDic.Add(RPA_BOT_TOKEN, "");
                configDic.Add("rpa.python-path", "D:\\\\Python\\\\Python36\\\\python.exe");
                //configDic.Add("spring.datasource.url", "jdbc:h2:file:./db/robot;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;AUTO_RECONNECT=TRUE;DATABASE_TO_UPPER=false");
                //configDic.Add("spring.datasource.username", "robot9y");
                //configDic.Add("spring.datasource.password", "robot9y");
                //configDic.Add("spring.datasource.initialization-mode", "ALWAYS");
                //configDic.Add("spring.datasource.sql-script-encoding", "utf-8");
                //configDic.Add("spring.datasource.schema", "classpath:db/schema-h2.sql");
                configDic.Add(RPA_BIN360, Constant.WorkPath.Replace("\\", "\\\\") + "\\\\browser\\\\360SE\\\\360se.exe");
                configDic.Add(RPA_FIREFOX, Constant.WorkPath.Replace("\\", "\\\\") + "\\\\browser\\\\firefox\\\\App\\\\Firefox64\\\\firefox.exe");
                overwrite();
            }
            else
            {
                upset(SERVER_PORT, Constant.Port.ToString());
                upset(RPA_SERVER, Context.Server);
                upset(RPA_WORKPATH, Constant.WorkPath.Replace("\\", "\\\\"));
                upset(RPA_AUTO_SYNC, "on");
                upset(RPA_AUTO_TASK, "on");
                if (!configDic.ContainsKey("rpa.python-path"))
                {
                    upset("rpa.python-path", "D:\\\\Python\\\\Python36\\\\python.exe");
                }
                if (configDic.ContainsKey("spring.datasource.url"))
                {
                    configDic.Remove("spring.datasource.url");
                    configDic.Remove("spring.datasource.username");
                    configDic.Remove("spring.datasource.password");
                    configDic.Remove("spring.datasource.initialization-mode");
                    configDic.Remove("spring.datasource.sql-script-encoding");
                    configDic.Remove("spring.datasource.schema");
                }
                //if (!configDic.ContainsKey("spring.datasource.url"))
                //{
                //    upset("spring.datasource.url", "jdbc:h2:file:./db/robot;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL;AUTO_RECONNECT=TRUE;DATABASE_TO_UPPER=false");
                //    upset("spring.datasource.username", "robot9y");
                //    upset("spring.datasource.password", "robot9y");
                //    upset("spring.datasource.initialization-mode", "ALWAYS");
                //    upset("spring.datasource.sql-script-encoding", "utf-8");
                //    upset("spring.datasource.schema", "classpath:db/schema-h2.sql");
                //}
                if (!configDic.ContainsKey(RPA_BIN360) || get(RPA_BIN360).Contains("360se6"))
                {
                    //upset(RPA_BIN360, Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData).Replace("\\", "\\\\") + "\\\\360se6\\\\Application\\\\360se.exe");
                    upset(RPA_BIN360, Constant.WorkPath.Replace("\\", "\\\\") + "\\\\browser\\\\360SE\\\\360se.exe");
                }
                if (!configDic.ContainsKey(RPA_FIREFOX))
                {
                    //upset(RPA_BIN360, Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData).Replace("\\", "\\\\") + "\\\\360se6\\\\Application\\\\360se.exe");
                    upset(RPA_FIREFOX, Constant.WorkPath.Replace("\\", "\\\\") + "\\\\browser\\\\firefox\\\\App\\\\Firefox64\\\\firefox.exe");
                }
                if (!configDic.ContainsKey(RPA_PY_VERSION))
                {
                    upset(RPA_PY_VERSION, "0.0.0");
                }
                string[] citys = Constant.RPA_CA_CITY.Split(',');
                int city_size = citys.Length;
                string caBasePath = Path.Combine(Constant.WorkPath, "ca");
                if (!Directory.Exists(caBasePath))
                {
                    Directory.CreateDirectory(caBasePath);
                }
                string caConfigPath = Path.Combine(Constant.WorkPath, "ca", "version.init");
                if (!File.Exists(caConfigPath))
                {
                    File.Create(caConfigPath).Close();
                }
                for (int i = 0; i < city_size; i++)
                {
                    if (configDic.ContainsKey(RPA_CA_VERSION + "." + citys[i]))
                    {
                        configDic.Remove(RPA_CA_VERSION + "." + citys[i]);
                    }
                }
                overwrite();
                reload();
            }
            return true;
        }

        public static void reload()
        {
            StreamReader reader = new StreamReader(Constant.ConfigPath);
            while (!reader.EndOfStream)
            {
                string line = reader.ReadLine();
                if (line != null && !string.IsNullOrEmpty(line))
                {
                    string[] arr = line.Split('=');
                    if (arr.Length > 1)
                    {
                        configDic[arr[0]] = line.Replace(arr[0]+"=","");
                        Logger.info("写入配置文件:" + arr[0] + " = " + configDic[arr[0]]);
                    }
                }
            }
            reader.Close();
        }

        public static string readCA(string caName)
        {
            string result = "";
            string caConfigPath = Path.Combine(Constant.WorkPath, "ca", "version.init");
            using (StreamReader reader = new StreamReader(caConfigPath))
            {
                while (!reader.EndOfStream)
                {
                    string line = reader.ReadLine();
                    if (line != null && !string.IsNullOrEmpty(line))
                    {
                        string[] arr = line.Split('=');
                        if (arr.Length > 1 && arr[0] == caName)
                        {
                            result = arr[1];
                        }
                    }
                }
                reader.Close();
            }
            return result;
        }


        public static void writeCA(string caName, string caVersion)
        {
            Dictionary<string, string> caDic = new Dictionary<string, string>();
            string[] cas = Constant.RPA_CA_CITY.Split(',');
            foreach (string ca in cas)
            {
                if (ca == caName)
                {
                    caDic.Add(ca, caVersion);
                }
                else
                {
                    caDic.Add(ca, readCA(ca));
                }
            }

            string caConfigPath = Path.Combine(Constant.WorkPath, "ca", "version.init");
            using (FileStream fs = new FileStream(caConfigPath, FileMode.Open, FileAccess.Write))
            {
                fs.Seek(0, SeekOrigin.Begin);
                fs.SetLength(0);
                fs.Close();

                StringBuilder sb = new StringBuilder();
                foreach (string key in caDic.Keys)
                {
                    sb.AppendLine(key + "=" + caDic[key]);
                }
                StreamWriter writer = new StreamWriter(caConfigPath);
                writer.Write(sb.ToString());
                writer.Close();
            }
        }

        /// <summary>
        /// 获取配置值
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        public static string get(string key)
        {
            if (configDic.ContainsKey(key))
            {
                return configDic[key];
            }
            return null;
        }
        

        /// <summary>
        /// 更新配置值
        /// </summary>
        /// <param name="key"></param>
        /// <param name="value"></param>
        public static void upset(string key, string value)
        {
            reload();
            if (configDic.ContainsKey(key))
            {
                configDic[key] = value;
            }
            else
            {
                configDic.Add(key, value);
            }
            overwrite();
        }

        private static void overwrite()
        {
            FileStream fs = new FileStream(ConfigPath, FileMode.Open, FileAccess.Write);
            fs.Seek(0, SeekOrigin.Begin);
            fs.SetLength(0);
            fs.Close();

            StringBuilder sb = new StringBuilder();
            foreach (string key in configDic.Keys)
            {
                sb.AppendLine(key + "=" + configDic[key]);
            }
            StreamWriter writer = new StreamWriter(ConfigPath);
            writer.Write(sb.ToString());
            writer.Close();
        }
    }
}
