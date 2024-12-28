using System;
using System.Diagnostics;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading;

namespace rpa_client
{
    internal class RobotProcess
    {
        /// <summary>
        /// 启动
        /// </summary>
        public static bool start()
        {
            int pid = Utils.findPidByPort(Constant.Port);
            if (pid == -1)
            {
                ThreadStart thStart = new ThreadStart(runJar2);//threadStart委托 
                Thread thread = new Thread(thStart);
                thread.Priority = ThreadPriority.Highest;
                thread.IsBackground = true; //关闭窗体继续执行
                thread.Start();
            }
            return true;
        }


        private static void runJar(String args)
        {
            string logFile = @"logs\app.log";
            Process process = new Process();
            process.StartInfo.FileName = "cmd.exe";
            process.StartInfo.UseShellExecute = false;    //是否使用操作系统shell启动
            process.StartInfo.RedirectStandardInput = true;//接受来自调用程序的输入信息
            process.StartInfo.RedirectStandardOutput = true;//由调用程序获取输出信息
            process.StartInfo.RedirectStandardError = true;//重定向标准错误输出
            process.StartInfo.CreateNoWindow = true;//不显示程序窗口
            process.StartInfo.Verb = "runas";
            process.Start();//启动程序

            if (args != null && args.Length > 1 && !args.EndsWith(" "))
            {
                args = args.Trim() + " ";
            }

            string writelog = (File.Exists(Path.Combine(Constant.WorkPath, logFile)) ? " >> " : " > ") + logFile;
            process.StandardInput.WriteLine("bin\\java -jar robot9y-0.0.1-SNAPSHOT.jar " + args + writelog);
            process.StandardInput.AutoFlush = true;
            process.StandardInput.WriteLine("exit");
            process.Close();
        }

        private static void runJar2()
        {
            Process process = new Process();
            process.StartInfo.CreateNoWindow = true;
            process.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;
            Logger.info("【runJar：】" + Constant.WorkPath + "\\startup.bat");
            process.StartInfo.FileName = Constant.WorkPath + "\\startup.bat";
            process.StartInfo.Verb = "runas";
            process.Start();
            process.WaitForExit();
        }

        /// <summary>
        /// 停止
        /// </summary>
        /// <returns></returns>
        public static bool stop()
        {
            try
            {
                Utils.ExecuteCMD("taskkill /F /IM java.exe");
            }
            catch (Exception ex)
            {
                Logger.error("停止机器人异常", ex);
                Security.Status = RobotStatus.ClientError;
            }

            int pid = Utils.findPidByPort(Constant.Port);
            if (pid != -1)
            {
                Process thisproc = Process.GetProcessById(pid);
                if (!thisproc.CloseMainWindow()) //尝试关闭进程 释放资源
                {
                    thisproc.Kill(); //强制关闭
                }
            }
            return true;
        }

        /// <summary>
        /// 清理所有浏览器
        /// </summary>
        public static bool clearDriver()
        {
            try
            {
                Utils.ExecuteCMD("taskkill /F /IM chromedriver.exe");
                Utils.ExecuteCMD("taskkill /F /IM IEDriverServer.exe");
                Utils.ExecuteCMD("taskkill /F /IM geckodriver.exe");
                Utils.ExecuteCMD("taskkill /F /IM chromedriver360.exe");
                Utils.ExecuteCMD("taskkill /F /IM chrome.exe");
                Utils.ExecuteCMD("taskkill /F /IM firefox.exe");
                Utils.ExecuteCMD("taskkill /F /IM cmd.exe");
                return true;
            }
            catch (Exception ex)
            {
                Logger.error("停止机器人异常", ex);
                return false;
            }
        }

        /// <summary>
        /// 机器人是否正在执行任务
        /// </summary>
        /// <returns></returns>
        public static void checkRobotStatus()
        {
            try
            {
                if (Security.Status == RobotStatus.Upgrading)
                {
                    return;
                }
                int pid = Utils.findPidByPort(Constant.Port);
                if (pid == -1)
                {
                    Security.Status = RobotStatus.Closed;
                }
                else
                {
                    try
                    {
                        string res = Utils.httpPost("http://localhost:" + Constant.Port.ToString() + "/api/robot/start/status");
                        int stat = int.Parse(res);
                        if (stat == 0)
                        {
                            Security.Status = RobotStatus.Runnable;
                        }
                        else if (stat == 1)
                        {
                            Security.Status = RobotStatus.Running;
                        }
                        else if(stat == 2)
                        {
                            Logger.info("停止机器人");
                            stop();
                            start();
                        }
                        else
                        {
                            Security.Status = RobotStatus.RobotError;
                        }
                    }
                    catch (Exception)
                    {
                        if (Security.Status != RobotStatus.Upgrading && Security.Status != RobotStatus.Closed)
                        {
                            Security.Status = RobotStatus.ClientError;
                        }
                    }
                }
            }
            catch (Exception)
            {
                Security.Status = RobotStatus.ClientError;
            }
        }

        /// <summary>
        /// 健康检查
        /// </summary>
        /// <returns></returns>
        public static void checkHealth()
        {
            try
            {
                int num = int.Parse(Utils.httpPost("http://localhost:" + Constant.Port.ToString() + "/api/robot/start/status"));
                if (num == 0)
                {
                    Security.Status = RobotStatus.Runnable;
                }
                else if (num > 0)
                {
                    Security.Status = RobotStatus.Running;
                }
            }
            catch (Exception)
            {
                if (Security.Status != RobotStatus.New
                    && Security.Status != RobotStatus.Upgrading
                    && Security.Status != RobotStatus.Closed
                    && Security.Status != RobotStatus.UpgradeFailed)
                {
                    Security.Status = RobotStatus.RobotError;
                }
            }
            finally
            {
                Logger.info("【机器人状态】" + Security.Status);
            }
        }

        /// <summary>
        /// 重启程序
        /// </summary>
        public static void restart()
        {
            System.Diagnostics.Process process = new System.Diagnostics.Process();
            process.StartInfo.WorkingDirectory = Constant.WorkPath;
            Logger.info("【Constant.WorkPath】" + Constant.WorkPath);
            process.StartInfo.FileName = Constant.AppName;
            Logger.info("【Constant.AppName】" + Constant.AppName);
            process.Start();
            System.Environment.Exit(0);
        }
    }
}
