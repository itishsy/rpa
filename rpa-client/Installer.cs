using IWshRuntimeLibrary;
using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.IO;
using System.IO.Compression;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace rpa_client
{
    /// <summary>
    /// 远程调用类
    /// </summary>
    internal class Installer
    {
        public static bool isDesktopLink = true;
        public static bool isOsStart = true;
        public static bool isCoverUp = false;

        public static bool doInstall()
        {
            //下载应用程序
            if (!downloadFiles()) { return false; }
            
            //下载最新执行文件
            if (!downloadExeAndJar()) return false;

            //桌面快捷方式
            if (!desktopShortcut()) { return false; }

            //开机启动
            if (!OsBootStart()) { return false; }

            //安装成功登记
            if (!Security.register()) { return false; }

            return true;
        }

        /// <summary>
        /// 检查工作目录是否已安装
        /// </summary>
        /// <returns></returns>
        public static bool isInstalled()
        {
            if (!System.IO.File.Exists(Constant.AppPath) || !System.IO.File.Exists(Constant.ConfigPath)) { return false; }
            Logger.info("bot token:" + AppConfig.get(AppConfig.RPA_BOT_TOKEN));
            string dirs = Utils.httpPost(string.Format(Constant.FIND_WORK_DIR_URL, Upgrader.UPGRADER_SERVERR), AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
            if (dirs != null)
            {
                string[] dirArr = dirs.Split(',');
                foreach (string dir in dirArr)
                {
                    string filePath = Path.Combine(Constant.WorkPath, dir);
                    bool isExist = false;
                    if (filePath.EndsWith(".zip"))
                    {
                        isExist = Directory.Exists(filePath.Replace(".zip", ""));
                    }
                    else
                    {
                        isExist = Directory.Exists(filePath) || System.IO.File.Exists(filePath);
                    }
                    if (!isExist) { return false; }
                }
                return true;
            }
            return false;
        }

        /// <summary>
        /// 下载安装文件到本地工作目录
        /// </summary>
        /// <returns></returns>
        private static bool downloadFiles()
        {
            Logger.info("向服务器下载应用程序目录。url:" + Constant.FIND_WORK_DIR_URL + " token:" + AppConfig.get(AppConfig.RPA_BOT_TOKEN));
            string files = Utils.httpPost(string.Format(Constant.FIND_WORK_DIR_URL, Upgrader.UPGRADER_SERVERR), AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
            string[] fileArr = files.Split(',');
            foreach (string file in fileArr)
            {
                string filePath = Path.Combine(Constant.WorkPath, file);
                if (file.IndexOf('.') < 0)
                {
                    //创建目录
                    if (!Directory.Exists(filePath))
                    {
                        Directory.CreateDirectory(filePath);
                    }
                }
                else if (filePath.EndsWith(".zip"))
                {
                    //处理打包文件
                    if (System.IO.File.Exists(filePath))
                    {
                        System.IO.File.Delete(filePath);
                    }
                    string dirFile = filePath.Replace(".zip", "");
                    if (Directory.Exists(dirFile))
                    {
                        if (isCoverUp)
                        {
                            Directory.Delete(dirFile, true);
                        }
                        else
                        {
                            //已经存在，不覆盖，跳过下载
                            continue;
                        }
                    }
                    string downUrl = string.Format(Constant.DOWNLOAD_WORK_FILE_URL, Upgrader.UPGRADER_SERVERR, file);
                    if (!Utils.download(downUrl, filePath, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
                    {
                        return false;
                    }
                    ZipFile.ExtractToDirectory(filePath, dirFile);
                    System.IO.File.Delete(filePath);
                }
                else
                {
                    //下载文件
                    if (System.IO.File.Exists(filePath))
                    {
                        if (isCoverUp)
                        {

                            System.IO.File.Delete(filePath);
                        }
                        else
                        {
                            continue;
                        }
                    }
                    string downUrl = string.Format(Constant.DOWNLOAD_WORK_FILE_URL, Upgrader.UPGRADER_SERVERR, file);
                    if (!Utils.download(downUrl, filePath, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
                    {
                        return false;
                    }

                }
            }
            return true;
        }

        /// <summary>
        /// 安装最新的组件（jar、exe、tpl)
        /// </summary>
        /// <returns></returns>
        private static bool downloadExeAndJar()
        {
            if (!System.IO.File.Exists(Constant.AppPath) || (System.IO.File.Exists(Constant.AppPath) && isCoverUp))
            {
                System.IO.File.Delete(Constant.AppPath);
                string resClient = Utils.httpPost(string.Format(Constant.CHECK_COMPONENT_URL, Upgrader.UPGRADER_SERVERR, "rpa-client", "0.0.0"), AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
                if (!resClient.Contains("version"))
                {
                    Logger.warn("获取client版本信息失败");
                    return false;
                }
                string clientVersion = Utils.subString(resClient, "\"version\":\"", "\"");
                string clientFileName = Utils.subString(resClient, "\"fileName\":\"", "\"");
                if (!Utils.download(string.Format(Constant.DOWNLOAD_COMPONENT_URL, Upgrader.UPGRADER_SERVERR, "rpa-client", clientVersion, clientFileName), Constant.AppPath, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
                {
                    Logger.warn("下载最新exe失败");
                    return false;
                }
                AppConfig.upset(AppConfig.RPA_CLIENT_VERSION, clientVersion);
            }

            if (!System.IO.File.Exists(Constant.RobotPath) || (System.IO.File.Exists(Constant.RobotPath) && isCoverUp))
            {
                System.IO.File.Delete(Constant.RobotPath);
                string resRobot = Utils.httpPost(string.Format(Constant.CHECK_COMPONENT_URL, Upgrader.UPGRADER_SERVERR, "rpa-robot", "0.0.0"), AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
                if (!resRobot.Contains("version"))
                {
                    Logger.warn("获取jar版本信息失败");
                    return false;
                }
                string jarVersion = Utils.subString(resRobot, "\"version\":\"", "\"");
                string jarFileName = Utils.subString(resRobot, "\"fileName\":\"", "\"");
                if (!Utils.download(string.Format(Constant.DOWNLOAD_COMPONENT_URL, Upgrader.UPGRADER_SERVERR, "rpa-robot", jarVersion, jarFileName), Constant.RobotPath, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
                {
                    Logger.warn("下载最新jar失败");
                    return false;
                }
                AppConfig.upset(AppConfig.RPA_ROBOT_VERSION, jarVersion);
            }

            return true;
        }

        /// <summary>
        /// 生成桌面快捷方式
        /// </summary>
        /// <returns></returns>
        private static bool desktopShortcut()
        {
            if (System.IO.File.Exists(Constant.AppPath))
            {
                string shortcutPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Desktop), "seebot.lnk");
                WshShell shell = new WshShell();
                IWshShortcut shortcut = (IWshShortcut)shell.CreateShortcut(shortcutPath);//创建快捷方式对象
                shortcut.TargetPath = Constant.AppPath;//指定目标路径
                shortcut.WorkingDirectory = Constant.WorkPath;//设置起始位置
                shortcut.WindowStyle = 1;//设置运行方式，默认为常规窗口
                shortcut.Description = "seebot";// "仕邦RPA机器人";//设置备注
                //shortcut.IconLocation = string.IsNullOrWhiteSpace(iconLocation) ? targetPath : iconLocation;//设置图标路径
                shortcut.Save();//保存快捷方式
                return System.IO.File.Exists(shortcutPath);
            }
            return false;
        }


        /// <summary>
        /// 开机启动
        /// </summary>
        /// <returns></returns>
        private static bool OsBootStart()
        {
            if (isOsStart)
            {
                string appName = "seebot";
                RegistryKey rk = Registry.CurrentUser;
                RegistryKey rk2 = rk.CreateSubKey(@"Software\Microsoft\Windows\CurrentVersion\Run");
                string oriPath = rk2.GetValue(appName) == null ? "" : rk2.GetValue(appName).ToString();
                if (oriPath == null || !Constant.AppPath.Equals(oriPath))
                {
                    rk2.SetValue(appName, Constant.AppPath);
                }
                rk2.Close();
                rk.Close();
            }
            return true;
        }


        public static void success()
        {
            System.Diagnostics.Process process = new System.Diagnostics.Process();
            process.StartInfo.WorkingDirectory = Constant.WorkPath;
            process.StartInfo.FileName = Constant.AppName;
            //process.StartInfo.Arguments = "install=" + Context.StartPath;
            process.Start();
            System.Environment.Exit(0);
        }
    }
}
