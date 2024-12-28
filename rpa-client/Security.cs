using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace rpa_client
{
    internal class Security
    {
        public static RobotStatus Status { get; set; }
        public static int RestartTimer { get; set; }

        /// <summary>
        /// 是否已登录
        /// </summary>
        /// <returns></returns>
        public static bool isLogin()
        {
            if (AppConfig.get(AppConfig.RPA_TOKEN) == null)
            {
                Logger.info("客户端未登录");
                return false;
            }
            else
            {
                try
                {
                    string url = string.Format(Constant.CLIENT_CHECK_URL, Context.Server, Context.SeqNo);
                    string res = Utils.httpPost(url);
                    if (!res.Equals("1"))
                    {
                        Logger.info("客户端Token无效");
                        return false;
                    }
                    else
                    {
                        Logger.info("判断客户端认证状态：已登录");
                        return true;
                    }
                }
                catch (Exception ex)
                {
                    Logger.warn("判断是否登录异常。" + ex.Message);
                    return false;
                }
            }
        }

        /// <summary>
        /// 登录
        /// </summary>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <returns></returns>
        public static bool doLogin(string username, string password)
        {
            string url = string.Format(Constant.LOGIN_URL, Context.Server, username, password);
            Logger.info("发送登录请求。url：" + url);
            string responseText = Utils.httpPost(url);
            if (responseText != null)
            {
                AppConfig.upset(AppConfig.RPA_TOKEN, responseText);
                Logger.info("登录成功。token：" + AppConfig.get(AppConfig.RPA_TOKEN));
                try
                {
                    if (!Context.Server.Equals(Upgrader.UPGRADER_SERVERR))
                    {
                        string url2 = string.Format(Constant.LOGIN_URL, Upgrader.UPGRADER_SERVERR, username, password);
                        Logger.info("发送登录请求。url2：" + url2);
                        string responseText2 = Utils.httpPost(url2);
                        if(responseText2 != null)
                        {
                            Logger.info("发送登录请求。url2 token:" + responseText2);
                            AppConfig.upset(AppConfig.RPA_BOT_TOKEN, responseText2);
                        }
                    }
                    else
                    {
                        AppConfig.upset(AppConfig.RPA_BOT_TOKEN, responseText);
                    }
                } catch(Exception e)
                {
                    Logger.info("发送登录请求。url2："+ Upgrader.UPGRADER_SERVERR + " 异常info:" + e.Message);
                }
                return true;
            }
            return false;
        }

        /// <summary>
        /// 客户端注册
        /// </summary>
        /// <returns></returns>
        public static bool register()
        {
            string url = string.Format(Constant.CLIENT_CHECK_URL, Context.Server, Context.SeqNo);
            Logger.info("向服务端注册本地信息。url：" + url);
            string str = Utils.httpPost(url);
            return str == "1";
        }

        /// <summary>
        /// 客户端心跳发送
        /// </summary>
        /// <returns></returns>
        public static string sendHeartbeat()
        {
            if (Context.SeqNo != "")
            {
                try
                {
                    string url = string.Format(Constant.HEARTBEAT_URL, Context.Server, Context.SeqNo, (int)Security.Status);
                    Logger.info("向服务端发送心跳。url：" + url);
                    string str = Utils.httpPost(url);
                    if(Context.Server != Upgrader.UPGRADER_SERVERR)
                    {
                        string url2 = string.Format(Constant.HEARTBEAT_URL, Upgrader.UPGRADER_SERVERR, Context.SeqNo, (int)Security.Status);
                        Logger.info("向bot服务端发送心跳。url2：" + url2);
                        string str2 = Utils.httpPost(url2, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
                        return str2;
                    }
                    return str;
                } catch(Exception e)
                {
                    Logger.info("向服务端发送心跳异常。" + e.Message);
                    return null;
                }
            }
            else {
                Logger.error("机器码空",null);
                return null;
            }
        }

        public static void sendLog(string logFile)
        {
            try
            {
                Utils.upload(Context.Server + Constant.SEND_LOG_URL, logFile);
            }
            catch (Exception e)
            {
                Logger.error("向服务端发送日志。logFile：" + logFile, e);
            }
        }


        public static void cleanCacheFile()
        {
            try
            {
                string currentDateString = DateTime.Now.ToString("yyyy-MM-dd");
                string clientLogBakFile = Path.Combine(Constant.WorkPath, "logs", "client." + currentDateString + ".log");
                if (!File.Exists(clientLogBakFile))
                {
                    string upgradePath = Path.Combine(Constant.WorkPath, "upgrade");

                    if (Directory.Exists(upgradePath))
                    {
                        Directory.Delete(upgradePath, true);
                    }

                    if (!Directory.Exists(upgradePath))
                    {
                        Directory.CreateDirectory(upgradePath);
                    }

                    string clientLogFile = Path.Combine(Constant.WorkPath, "logs", "client.log");

                    if (File.Exists(clientLogFile))
                    {
                        File.Copy(clientLogFile, clientLogBakFile);
                        File.Delete(clientLogFile);
                    }

                    cleanDirectoryFile("file", 60);
                    cleanDirectoryFile("cache", 60);
                    cleanDirectoryFile(Path.Combine("cache", "webDriver"), 2);
                    cleanDirectoryFile("logs", 60);
                }
            }
            catch (Exception e)
            {
                Logger.error("清理历史文件发生异常。", e);
            }
        }

        private static void cleanDirectoryFile(string dir, int keepTime)
        {
            string filePath = Path.Combine(Constant.WorkPath, dir);
            // 获取指定目录下的所有文件
            string[] files = Directory.GetFiles(filePath);

            // 获取当前日期时间
            DateTime currentDate = DateTime.Now;

            // 定义时间间隔
            TimeSpan oneWeek = TimeSpan.FromDays(keepTime);

            // 遍历所有文件
            foreach (string file in files)
            {
                DateTime creationTime = File.GetCreationTime(file);
                if (file.Contains("robot.log"))
                    continue;

                // 判断文件创建时间是否过期
                if (currentDate - creationTime > oneWeek)
                {
                    // 删除创建时间超时的文件
                    File.Delete(file);
                }
            }

            foreach (string dir2 in Directory.GetDirectories(filePath))
            {
                string subDir = Path.Combine(dir, dir2);
                cleanDirectoryFile(subDir, keepTime);
            }
        }

    }
}
