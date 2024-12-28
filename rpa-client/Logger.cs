using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace rpa_client
{
    internal static class Logger
    {
        public static void info(String info)
        {
            writeLog("info", info);
        }

        public static void error(String error, Exception e)
        {
            writeLog("error", error);
            writeLog("异常信息：", e.Message);
            write(e.StackTrace);
        }

        public static void warn(String warn)
        {
            writeLog("warn", warn);
        }

        private static void writeLog(string logType, string msg)
        {
            string msgInfo = "[" + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + "] " + logType.ToUpper() + " " + msg + "\r\n";
            write(msgInfo);
        }

        private static void write(string msg)
        {
            FileStream fileStream = null;
            StreamWriter streamWriter = null;
            try
            {
                string dic = Path.Combine(Constant.WorkPath, "logs");
                if (!Directory.Exists(dic))
                {
                    Directory.CreateDirectory(dic);
                }
                string path = Path.Combine(Constant.WorkPath, "logs/client.log");
                if (!File.Exists(path))
                {
                    fileStream = new FileStream(path, FileMode.CreateNew, FileAccess.Write);
                }
                else
                {
                    fileStream = new FileStream(path, FileMode.Append, FileAccess.Write);
                }
                streamWriter = new StreamWriter(fileStream);
                streamWriter.Write(msg);
            }
            catch { }
            finally
            {
                if (streamWriter != null)
                    streamWriter.Close();
                if (fileStream != null)
                    fileStream.Close();
            }
        }
    }
}
