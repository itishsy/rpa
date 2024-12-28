using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.IO.Compression;
using System.Web.Script.Serialization;

namespace rpa_client
{
    /// <summary>
    /// 客户端升级
    /// </summary>
    internal class Upgrader
    {
        public const string UPGRADER_SERVERR = "http://121.8.131.54:8072/api";

        public const string COMPONENT_CLIENT_NAME = "rpa-client";
        public const string COMPONENT_ROBOT_NAME = "rpa-robot";
        public const string COMPONENT_ROBOT_HUB_NAME = "rpa-robot-hub";
        //public const string COMPONENT_TPL_NAME = "tpl";
        //public const string COMPONENT_PY_NAME = "python";
        //public const string COMPONENT_CHROME_NAME = "chrome";
        //public const string COMPONENT_JRE_NAME = "jre";
        public const string COMPONENT_CA_NAME = "ca";
        public const string COMPONENT_ZIPS = "jre,tpl,python,chrome,chrome-plugins,opencv,360SE,firefox";

        public static bool checkVersion(string versionKey, string componentName)
        {
            if (componentName.Contains(","))
            {
                string[] comps = componentName.Split(',');
                foreach (string compName in comps)
                {
                    string compVersionKey = "rpa." + compName + "-version";
                    if (checkVersion(compVersionKey, compName))
                    {
                        return true;
                    }
                }
                return false;
            }
            else
            {
                string localVersion = AppConfig.get(versionKey);
                if (localVersion == null)
                {
                    return true;
                }
                string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, componentName, localVersion);
                Logger.info("RPA_BOT_TOKEN:" + AppConfig.get(AppConfig.RPA_BOT_TOKEN));
                string res = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);

                if (res.Contains("version"))
                {
                    string version = Utils.subString(res, "\"version\":\"", "\"");
                    return (localVersion != version);
                }
                return false;
            }
        }

        public static bool checkCAVersion(string caName)
        {
            string localVersion = AppConfig.readCA(caName);
            if (localVersion == null)
            {
                return true;
            }
            string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, Upgrader.COMPONENT_CA_NAME + "_" + caName, localVersion);
            string res = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);

            if (res.Contains("version"))
            {
                string version = Utils.subString(res, "\"version\":\"", "\"");
                return (localVersion != version);
            }
            return false;
        }

        public static List<Version> findVersionList()
        {
            string url = string.Format(Constant.FIND_VERSION_LIST_URL, UPGRADER_SERVERR);
            try
            {
                string res = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);

                if (res.Contains("component"))
                {
                    JavaScriptSerializer scriptSerializer = new JavaScriptSerializer();
                    List<Version> result = scriptSerializer.Deserialize<List<Version>>(res);
                    return result;
                }
                return null;
            }
            catch (Exception e)
            {
                Logger.error("HTTP请求异常。url：" + url, e);
                return null;
            }
        }

        /// <summary>
        /// 升级jar
        /// </summary>
        public static bool upgradeJar(string robotVersionKey, string componentRobotName, string jarName)
        {
            string robotVersion = AppConfig.get(robotVersionKey);
            string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, componentRobotName, robotVersion);
            string res = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
            string version = Utils.subString(res, "\"version\":\"", "\"");
            if (robotVersion == version) return true;
            

            string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", componentRobotName, version);
            Directory.CreateDirectory(upgradePath);

            string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

            string upgradeFile = Path.Combine(upgradePath, jarName);
            if (File.Exists(upgradeFile))
            {
                File.Delete(upgradeFile);
            }
            string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, componentRobotName, version, fileName);
            Logger.info("正在下载最新版本机器人，downloadUrl=" + downloadUrl);

            string jarPath = Path.Combine(Constant.WorkPath, jarName);
            if (Utils.download(downloadUrl, upgradeFile, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
            {
                if (File.Exists(jarPath))
                {
                    File.Delete(jarPath);
                }
                File.Copy(upgradeFile, jarPath);
                if (File.Exists(jarPath))
                {
                    AppConfig.upset(robotVersionKey, version);
                    Logger.info("机器人("+ componentRobotName + ")升级成功！写入最新版本到配置文件，version=" + version);
                    return true;
                }
                else
                {
                    Logger.warn("机器人(" + componentRobotName + ")升级失败！从upgradeFile(" + upgradeFile + ")复制到工作目录失败");
                    return false;
                }
            }
            else
            {
                Logger.warn("发现新版本(" + componentRobotName + ")，下载失败！！");
                return false;
            }
        }


        /// <summary>
        /// 组件更新。
        /// </summary>
        public static bool upgradeZip()
        {
            bool result = true;
            string[] zips = COMPONENT_ZIPS.Split(',');
            foreach (string compName in zips)
            {
                string versionKey = "rpa." + compName + "-version";
                string zipFileName = compName + ".zip";

                string compVersion = AppConfig.get(versionKey);
                string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, compName, compVersion);
                Logger.info("向服务端检测" + compName + "是否最新版本, url=" + url);
                string res = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
                string version = Utils.subString(res, "\"version\":\"", "\"");
                if (compVersion == version)
                    continue;

                // 下载文件到upgrade目录
                string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", compName, version);
                Directory.CreateDirectory(upgradePath);

                string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

                string upgradeFile = Path.Combine(upgradePath, fileName);
                if (File.Exists(upgradeFile))
                {
                    File.Delete(upgradeFile);
                }
                string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, compName, version, fileName);

                Logger.info("正在下载最新" + compName + "包文件，downloadUrl=" + downloadUrl);
                if (Utils.download(downloadUrl, upgradeFile, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
                {
                    string workFile = Path.Combine(Constant.WorkPath, compName);
                    if (compName.Contains("chrome") || compName.Contains("360") || compName.Contains("firefox"))
                    {
                        workFile = Path.Combine(Constant.WorkPath, "browser", compName);
                    }
                    if (Directory.Exists(workFile))
                    {
                        Directory.Delete(workFile, true);
                    }

                    string zipFile = Path.Combine(Constant.WorkPath, zipFileName);
                    if (File.Exists(zipFile))
                    {
                        File.Delete(zipFile);
                    }
                    File.Copy(upgradeFile, zipFile);

                    ZipFile.ExtractToDirectory(zipFile, workFile);
                    System.IO.File.Delete(zipFile);

                    if(compName == "jre")
                    {
                        string binFile = Path.Combine(Constant.WorkPath, "bin");
                        string libFlie = Path.Combine(Constant.WorkPath, "lib");
                        if (Directory.Exists(binFile))
                        {
                            Directory.Delete(binFile, true);
                        }
                        if (Directory.Exists(libFlie))
                        {
                            Directory.Delete(libFlie, true);
                        }

                        Directory.Move(Path.Combine(workFile, "bin"), binFile);
                        Directory.Move(Path.Combine(workFile, "lib"), libFlie);
                        Directory.Delete(workFile);
                        workFile = binFile;
                    }

                    if (Directory.Exists(workFile))
                    {
                        AppConfig.upset(versionKey, version);
                        Logger.info(compName + "包更新成功！写入最新版本到配置文件，version=" + version);
                        result = true;
                    }
                    else
                    {
                        Logger.warn(compName + "包文件更新失败！从upgradeFile(" + upgradeFile + ")复制到chromeFile（" + zipFile + "）失败");
                        result = false;
                    }
                }
                else
                {
                    Logger.warn("发现新版本，下载失败！！");
                    result = false;
                }
            }
            return result;
        }


        /// <summary>
        /// 更新CA驱动文件
        /// </summary>
        public static bool upgradeCA(string caName)
        {
            string component_name = "ca_" + caName;

            string caVersion = AppConfig.readCA(caName);
            string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, component_name, caVersion);
            Logger.info("向服务端检测CA文件是否最新版本, url=" + url);
            string res = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);
            string version = Utils.subString(res, "\"version\":\"", "\"");
            if (caVersion == version) return true;


            string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", "ca", caName, version);
            Directory.CreateDirectory(upgradePath);

            string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

            string upgradeFile = Path.Combine(upgradePath, Constant.CaName);
            if (File.Exists(upgradeFile))
            {
                File.Delete(upgradeFile);
            }
            string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, component_name, version, fileName);
            Logger.info("正在下载最新CA文件，downloadUrl=" + downloadUrl);
            if (Utils.download(downloadUrl, upgradeFile, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
            {
                string caFile = Path.Combine(Constant.WorkPath, "ca", caName);
                if (Directory.Exists(caFile))
                {
                    Directory.Delete(caFile, true);
                }
                Directory.CreateDirectory(caFile);
                string caZip = Path.Combine(Constant.WorkPath, "ca", caName, Constant.CaName);
                if (File.Exists(caZip))
                {
                    File.Delete(caZip);
                }
                File.Copy(upgradeFile, caZip);

                ZipFile.ExtractToDirectory(caZip, caFile);
                System.IO.File.Delete(caZip);

                if (Directory.Exists(caFile))
                {
                    AppConfig.writeCA(caName, version);
                    Logger.info("CA文件更新成功！写入最新版本到配置文件，version=" + version);
                    return true;
                }
                else
                {
                    Logger.warn("CA文件更新失败！从upgradeFile(" + upgradeFile + ")复制到caFile（" + caFile + "）失败");
                    return false;
                }
            }
            else
            {
                Logger.warn("发现新版本，下载失败！！");
                return false;
            }
        }

        /// <summary>
        /// 升级Client
        /// </summary>
        /// <returns></returns>
        public static void upgradeClient()
        {
            //获取当前客户端的版本。
            string clientVersion = AppConfig.get(AppConfig.RPA_CLIENT_VERSION);
            string res = Utils.httpPost(string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_CLIENT_NAME, clientVersion), AppConfig.get(AppConfig.RPA_BOT_TOKEN), null);

            string version = Utils.subString(res, "\"version\":\"", "\"");
            if (clientVersion == version) return ;

            string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", "rpa-client", version);
            Directory.CreateDirectory(upgradePath);

            string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

            string upgradeFile = Path.Combine(upgradePath, Constant.AppOriName);
            if (File.Exists(upgradeFile))
            {
                File.Delete(upgradeFile);
            }
            if (Utils.download(string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_CLIENT_NAME, version, fileName), upgradeFile, AppConfig.get(AppConfig.RPA_BOT_TOKEN)))
            {
                AppConfig.upset(AppConfig.RPA_CLIENT_VERSION, version);
                Logger.info("更新到版本：" + version + "重启客户端");
                upgradeExe(upgradePath);
            }
        }

        private static void upgradeExe(string workingDirectory)
        {
            System.Diagnostics.Process process = new System.Diagnostics.Process();
            process.StartInfo.WorkingDirectory = workingDirectory;
            process.StartInfo.FileName = Path.Combine(workingDirectory, Constant.AppOriName);
            process.StartInfo.Arguments = "upgrade";
            process.Start();
            System.Environment.Exit(0);
        }
        
        #region 不用的

        ///// <summary>
        ///// 更新chrome文件包
        ///// </summary>
        //public static bool upgradeChrome()
        //{
        //    string chromeVersion = AppConfig.get(AppConfig.RPA_CHROME_VERSION);
        //    string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_CHROME_NAME, chromeVersion);
        //    Logger.info("向服务端检测Chrome包是否最新版本, url=" + url);
        //    string res = Utils.httpPost(url, UPGRADER_TOKEN, null);
        //    string version = Utils.subString(res, "\"version\":\"", "\"");
        //    if (chromeVersion == version) return true;
        //    string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", COMPONENT_CHROME_NAME, version);
        //    Directory.CreateDirectory(upgradePath);

        //    string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

        //    string upgradeFile = Path.Combine(upgradePath, Constant.ChromeName);
        //    if (File.Exists(upgradeFile))
        //    {
        //        File.Delete(upgradeFile);
        //    }
        //    string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_CHROME_NAME, version, fileName);
        //    Logger.info("正在下载最新Chrome包文件，downloadUrl=" + downloadUrl);
        //    if (Utils.download(downloadUrl, upgradeFile))
        //    {
        //        string chromeFile = Path.Combine(Constant.WorkPath, "browser", COMPONENT_CHROME_NAME);
        //        if (Directory.Exists(chromeFile))
        //        {
        //            Directory.Delete(chromeFile, true);
        //        }
        //        string chromeZip = Path.Combine(Constant.WorkPath, Constant.ChromeName);
        //        if (File.Exists(chromeZip))
        //        {
        //            File.Delete(chromeZip);
        //        }
        //        File.Copy(upgradeFile, chromeZip);

        //        ZipFile.ExtractToDirectory(chromeZip, chromeFile);
        //        System.IO.File.Delete(chromeZip);

        //        if (Directory.Exists(chromeFile))
        //        {
        //            AppConfig.upset(AppConfig.RPA_CHROME_VERSION, version);
        //            Logger.info("Chrome包更新成功！写入最新版本到配置文件，version=" + version);
        //            return true;
        //        }
        //        else
        //        {
        //            Logger.warn("Chrome包文件更新失败！从upgradeFile(" + upgradeFile + ")复制到chromeFile（" + chromeFile + "）失败");
        //            return false;
        //        }
        //    }
        //    else
        //    {
        //        Logger.warn("发现新版本，下载失败！！");
        //        return false;
        //    }
        //}

        ///// <summary>
        ///// 更新jre文件包
        ///// </summary>
        //public static bool upgradeJre()
        //{
        //    string jreVersion = AppConfig.get(AppConfig.RPA_JRE_VERSION);
        //    string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_JRE_NAME, jreVersion);
        //    Logger.info("向服务端检测Jre包是否最新版本, url=" + url);
        //    string res = Utils.httpPost(url, UPGRADER_TOKEN, null);
        //    string version = Utils.subString(res, "\"version\":\"", "\"");
        //    if (jreVersion == version) return true;
        //    string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", COMPONENT_JRE_NAME, version);
        //    Directory.CreateDirectory(upgradePath);

        //    string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

        //    string upgradeFile = Path.Combine(upgradePath, Constant.JreName);
        //    if (File.Exists(upgradeFile))
        //    {
        //        File.Delete(upgradeFile);
        //    }
        //    string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_JRE_NAME, version, fileName);
        //    Logger.info("正在下载最新Jre包文件，downloadUrl=" + downloadUrl);
        //    if (Utils.download(downloadUrl, upgradeFile))
        //    {
        //        string jreFile = Path.Combine(Constant.WorkPath, COMPONENT_JRE_NAME);
        //        if (Directory.Exists(jreFile))
        //        {
        //            Directory.Delete(jreFile, true);
        //        }
        //        string jreZip = Path.Combine(Constant.WorkPath, Constant.JreName);
        //        if (File.Exists(jreZip))
        //        {
        //            File.Delete(jreZip);
        //        }
        //        File.Copy(upgradeFile, jreZip);

        //        ZipFile.ExtractToDirectory(jreZip, jreFile);
        //        System.IO.File.Delete(jreZip);

        //        string binFlie = Path.Combine(Constant.WorkPath, "bin");
        //        if (Directory.Exists(binFlie))
        //        {
        //            Directory.Delete(binFlie, true);
        //        }
        //        Directory.Move(Path.Combine(jreFile, "bin"), binFlie);
        //        string libFlie = Path.Combine(Constant.WorkPath, "lib");
        //        if (Directory.Exists(libFlie))
        //        {
        //            Directory.Delete(libFlie, true);
        //        }
        //        Directory.Move(Path.Combine(jreFile, "lib"), libFlie);
        //        Directory.Delete(jreFile);

        //        if (Directory.Exists(binFlie) && Directory.Exists(libFlie))
        //        {
        //            AppConfig.upset(AppConfig.RPA_JRE_VERSION, version);
        //            Logger.info("Jre包更新成功！写入最新版本到配置文件，version=" + version);
        //            return true;
        //        }
        //        else
        //        {
        //            Logger.warn("Jre包文件更新失败！从upgradeFile(" + upgradeFile + ")复制到jreFile（" + jreFile + "）失败");
        //            return false;
        //        }
        //    }
        //    else
        //    {
        //        Logger.warn("发现新版本，下载失败！！");
        //        return false;
        //    }
        //}


        ///// <summary>
        ///// 更新模板文件
        ///// </summary>
        //public static bool upgradeTpl()
        //{
        //    string tplVersion = AppConfig.get(AppConfig.RPA_TPL_VERSION);
        //    string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_TPL_NAME, tplVersion);
        //    Logger.info("向服务端检测模板文件是否最新版本, url=" + url);
        //    string res = Utils.httpPost(url, UPGRADER_TOKEN, null);
        //    string version = Utils.subString(res, "\"version\":\"", "\"");
        //    if (tplVersion == version) return true;
        //    string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", COMPONENT_TPL_NAME, version);
        //    Directory.CreateDirectory(upgradePath);

        //    string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

        //    string upgradeFile = Path.Combine(upgradePath, Constant.TplName);
        //    if (File.Exists(upgradeFile))
        //    {
        //        File.Delete(upgradeFile);
        //    }
        //    string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_TPL_NAME, version, fileName);
        //    Logger.info("正在下载最新模板文件，downloadUrl=" + downloadUrl);
        //    if (Utils.download(downloadUrl, upgradeFile))
        //    {
        //        string tplFile = Path.Combine(Constant.WorkPath, COMPONENT_TPL_NAME);
        //        if (Directory.Exists(tplFile))
        //        {
        //            Directory.Delete(tplFile, true);
        //        }
        //        string tplZip = Path.Combine(Constant.WorkPath, Constant.TplName);
        //        if (File.Exists(tplZip))
        //        {
        //            File.Delete(tplZip);
        //        }
        //        File.Copy(upgradeFile, tplZip);

        //        ZipFile.ExtractToDirectory(tplZip, tplFile);
        //        System.IO.File.Delete(tplZip);

        //        if (Directory.Exists(tplFile))
        //        {
        //            AppConfig.upset(AppConfig.RPA_TPL_VERSION, version);
        //            Logger.info("模板文件更新成功！写入最新版本到配置文件，version=" + version);
        //            return true;
        //        }
        //        else
        //        {
        //            Logger.warn("模板文件更新失败！从upgradeFile(" + upgradeFile + ")复制到jarFile（" + tplFile + "）失败");
        //            return false;
        //        }
        //    }
        //    else
        //    {
        //        Logger.warn("发现新版本，下载失败！！");
        //        return false;
        //    }
        //}


        ///// <summary>
        ///// 更新Py文件包
        ///// </summary>
        //public static bool upgradePy()
        //{
        //    string pyVersion = AppConfig.get(AppConfig.RPA_PY_VERSION);
        //    string url = string.Format(Constant.CHECK_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_PY_NAME, pyVersion);
        //    Logger.info("向服务端检测Py包是否最新版本, url=" + url);
        //    string res = Utils.httpPost(url, UPGRADER_TOKEN, null);
        //    string version = Utils.subString(res, "\"version\":\"", "\"");
        //    if (pyVersion == version) return true;
        //    string upgradePath = Path.Combine(Constant.WorkPath, "upgrade", COMPONENT_PY_NAME, version);
        //    Directory.CreateDirectory(upgradePath);

        //    string fileName = Utils.subString(res, "\"fileName\":\"", "\"");

        //    string upgradeFile = Path.Combine(upgradePath, Constant.PyName);
        //    if (File.Exists(upgradeFile))
        //    {
        //        File.Delete(upgradeFile);
        //    }
        //    string downloadUrl = string.Format(Constant.DOWNLOAD_COMPONENT_URL, UPGRADER_SERVERR, COMPONENT_PY_NAME, version, fileName);
        //    Logger.info("正在下载最新Py包文件，downloadUrl=" + downloadUrl);
        //    if (Utils.download(downloadUrl, upgradeFile))
        //    {
        //        string pyFile = Path.Combine(Constant.WorkPath, COMPONENT_PY_NAME);
        //        if (Directory.Exists(pyFile))
        //        {
        //            Directory.Delete(pyFile, true);
        //        }
        //        string pyZip = Path.Combine(Constant.WorkPath, Constant.PyName);
        //        if (File.Exists(pyZip))
        //        {
        //            File.Delete(pyZip);
        //        }
        //        File.Copy(upgradeFile, pyZip);

        //        ZipFile.ExtractToDirectory(pyZip, pyFile);
        //        System.IO.File.Delete(pyZip);

        //        if (Directory.Exists(pyFile))
        //        {
        //            AppConfig.upset(AppConfig.RPA_PY_VERSION, version);
        //            Logger.info("PY包更新成功！写入最新版本到配置文件，version=" + version);
        //            return true;
        //        }
        //        else
        //        {
        //            Logger.warn("PY包文件更新失败！从upgradeFile(" + upgradeFile + ")复制到pyFile（" + pyFile + "）失败");
        //            return false;
        //        }
        //    }
        //    else
        //    {
        //        Logger.warn("发现新版本，下载失败！！");
        //        return false;
        //    }
        //}

        #endregion

    }
}
