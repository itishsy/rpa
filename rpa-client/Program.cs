using System;
using System.IO;
using System.Threading;
using System.Windows.Forms;

namespace rpa_client
{
    internal static class Program
    {

        //public static EventWaitHandle ProgramStarted;

        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            try
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);

                String[] args = System.Environment.GetCommandLineArgs();
                string key = null;
                string value = null;

                //第1個參數系統默認，當前exe的工作目錄
                Context.StartPath = args[0];
                Logger.info("Program Main() args[0]=" + args[0]);
                if (args.Length > 1)
                {
                    Logger.info("Program Main() args[1]=" + args[1]);
                    if (args[1].Contains("="))
                    {
                        string[] arr = args[1].Split('=');
                        //第2個參數，為自定義操作。cleanFile需要清理的exe文件，upgrade升級啓動
                        key = arr[0];
                        if (args.Length > 1)
                        {
                            value = arr[1];
                        }
                    }
                    else
                    {
                        key = args[1];
                    }
                }
                /*
                if (key == "install")
                {
                    Logger.info("key=install");
                    Logger.info("value=" + value);
                    //安裝后啓動
                    if (System.IO.File.Exists(value))
                    {
                        Thread.Sleep(1000);
                        System.IO.File.Delete(value);
                    }
                }*/
                if (key == "upgrade")
                {
                    Logger.info("key=upgrade");
                    if (System.IO.File.Exists(Constant.AppPath))
                    {
                        try
                        {
                            Thread.Sleep(2000);
                            System.IO.File.Delete(Constant.AppPath);
                        }
                        catch (Exception ex)
                        {
                            Logger.warn("删除" + Constant.AppPath + "异常。" + ex.Message);
                        }
                    }
                    try
                    {
                        Logger.info("copy from " + Context.StartPath + " to " + Constant.AppPath);
                        Thread.Sleep(1000);
                        //copy to WorkPath
                        System.IO.File.Copy(Context.StartPath, Constant.AppPath, true);
                        RobotProcess.restart();
                    }
                    catch (Exception ex)
                    {
                        Logger.warn("覆盖" + Constant.AppPath + "异常。" + ex.Message);
                    }


                    //升級后啓動
                    //Installer.success();
                }

                SingleInstance.CheckCreated();
                Application.Run(new MainForm());
            }
            catch (Exception e)
            {
                Logger.error("Main Program Error", e);
            }
        }
    }
}
