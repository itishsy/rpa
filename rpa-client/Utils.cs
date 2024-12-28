using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Web.Script.Serialization;

namespace rpa_client
{
    internal class Utils
    {
        public static string httpPost(string url)
        {
            StreamReader reader = null;
            HttpWebResponse response = null;
            HttpWebRequest request = null;
            try
            {
                request = (HttpWebRequest)WebRequest.Create(url);
                request.Method = "POST";
                request.ContentType = "application/json";
                request.KeepAlive = false;
                if (!url.Contains("oauth/token"))
                {
                    request.Headers.Add("Authorization", "Bearer " + AppConfig.get(AppConfig.RPA_TOKEN));
                }
                request.Timeout = 180000;
                response = (HttpWebResponse)request.GetResponse();

                reader = new StreamReader(response.GetResponseStream(), Encoding.UTF8);
                string responseText = reader.ReadToEnd();
                responseText = responseText.Trim();
                if (responseText.Contains("\"access_token\":\""))
                {
                    string content = subString(responseText, "\"access_token\":\"", "\"");
                    return content;
                }

                if (responseText.Contains("\"code\":200"))
                {
                    JavaScriptSerializer scriptSerializer = new JavaScriptSerializer();
                    Result result = scriptSerializer.Deserialize<Result>(responseText);
                    //string content = subString(responseText, "\"data\":\"", "\"");
                    if (result.data == null)
                    {
                        return "";
                    }
                    if (result.data.GetType().FullName.IndexOf("Dictionary") > 0)
                    {
                        return scriptSerializer.Serialize(result.data);
                    }
                    return result.data.ToString();
                }
                throw new Exception(responseText);
            }
            catch (Exception e)
            {
                if (!url.StartsWith("http://localhost"))
                {
                    Logger.error("HTTP请求异常。url：" + url, e);
                }
                throw e;
            }
            finally
            {
                if (request != null) { request.Abort(); }
                if (reader != null) { reader.Close(); }
                if (response != null) { response.Close(); }
            }
        }

        public static string httpPost(string url, string token, string jsonBody)
        {
            StreamReader reader = null;
            HttpWebResponse response = null;
            HttpWebRequest request = null;
            try
            {
                request = (HttpWebRequest)WebRequest.Create(url);
                request.Method = "POST";
                request.ContentType = "application/json";
                request.KeepAlive = false;
                if (!url.Contains("oauth/token"))
                {
                    if (token == null) {
                        token = AppConfig.get(AppConfig.RPA_TOKEN);
                    }
                    request.Headers.Add("Authorization", "Bearer " + token);
                }
                if (jsonBody != null)
                {
                    byte[] byteData = Encoding.UTF8.GetBytes(jsonBody);
                    request.ContentLength = byteData.Length;
                    using (Stream postStream = request.GetRequestStream())
                    {
                        postStream.Write(byteData, 0, byteData.Length);
                    }
                }
                request.Timeout = 180000;
                response = (HttpWebResponse)request.GetResponse();

                reader = new StreamReader(response.GetResponseStream(), Encoding.UTF8);
                string responseText = reader.ReadToEnd();
                responseText = responseText.Trim();
                if (responseText.Contains("\"access_token\":\""))
                {
                    string content = subString(responseText, "\"access_token\":\"", "\"");
                    return content;
                }

                if (responseText.Contains("\"code\":200"))
                {
                    JavaScriptSerializer scriptSerializer = new JavaScriptSerializer();
                    Result result = scriptSerializer.Deserialize<Result>(responseText);
                    //string content = subString(responseText, "\"data\":\"", "\"");
                    if (result.data == null)
                    {
                        return "";
                    }
                    if (result.data.GetType().FullName.IndexOf("Dictionary") > 0 || result.data.GetType().FullName.IndexOf("Object[]") > 0)
                    {
                        return scriptSerializer.Serialize(result.data);
                    }
                    return result.data.ToString();
                }
                throw new Exception(responseText);
            }
            catch (Exception e)
            {
                if (!url.StartsWith("http://localhost"))
                {
                    Logger.error("HTTP请求异常。url：" + url, e);
                }
                throw e;
            }
            finally
            {
                if (request != null) { request.Abort(); }
                if (reader != null) { reader.Close(); }
                if (response != null) { response.Close(); }
            }
        }

        public static bool download(string downloadUrl, string fileName)
        {
            try
            {
                WebClient wc = new WebClient();
                wc.Headers.Add("Content-Type", "application/x-www-form-urlencoded");
                wc.Headers.Add("Authorization", "Bearer " + AppConfig.get(AppConfig.RPA_TOKEN));
                wc.DownloadFile(new Uri(downloadUrl), fileName);
                return File.Exists(fileName);
            }
            catch (Exception e)
            {
                Logger.error("执行下载失败。url=" + downloadUrl + ",fileName=" + fileName, e);
                return false;
            }
        }

        public static bool download(string downloadUrl, string fileName, string token)
        {
            try
            {
                if(token == null)
                {
                    token = AppConfig.get(AppConfig.RPA_TOKEN);
                }

                WebClient wc = new WebClient();
                wc.Headers.Add("Content-Type", "application/x-www-form-urlencoded");
                wc.Headers.Add("Authorization", "Bearer " + token);
                wc.DownloadFile(new Uri(downloadUrl), fileName);
                return File.Exists(fileName);
            }
            catch (Exception e)
            {
                Logger.error("执行下载失败。url=" + downloadUrl + ",fileName=" + fileName, e);
                return false;
            }
        }

        public static bool upload(string uploadUrl, string fileName)
        {
            bool result;
            try
            {
                new WebClient
                {
                    Headers =
                    {
                        {
                            "Authorization",
                            "Bearer " + AppConfig.get(AppConfig.RPA_TOKEN)
                        }
                    }
                }.UploadFile(new Uri(uploadUrl), fileName);
                result = File.Exists(fileName);
            }
            catch (Exception e)
            {
                Logger.error("执行上传失败。url=" + uploadUrl + ",fileName=" + fileName, e);
                result = false;
            }
            return result;
        }

        public static string subString(string str, string startStr, string endStr)
        {
            if (str == null || startStr == null || endStr == null)
            {
                return null;
            }
            int startLen = startStr.Length;
            int start = str.IndexOf(startStr) + startStr.Length;
            string rightStr = str.Substring(start);
            int endLen = endStr.Length;
            int end = rightStr.IndexOf(endStr);
            string result = rightStr.Substring(0, end);
            return result;
        }


        public static string getOsBit()
        {
            return "64";
        }

        public static string readText(string fileName)
        {
            if (File.Exists(fileName))
            {
                StringBuilder sb = new StringBuilder();
                StreamReader sr = new StreamReader(fileName, Encoding.GetEncoding("GB2312"));
                String line;
                while ((line = sr.ReadLine()) != null)
                {
                    sb.AppendLine(line.ToString());
                }
                return sb.ToString();
            }
            return "";
        }

        public static string getIp()
        {
            try
            {
                IPHostEntry IpEntry = Dns.GetHostEntry(Dns.GetHostName());
                foreach (IPAddress ipa in IpEntry.AddressList)
                {
                    if (ipa.AddressFamily == AddressFamily.InterNetwork)
                    {
                        return ipa.ToString();
                    }
                }
            }
            catch{ }
            return "";
        }

        #region 获取机器唯一码
        public static string GetComputerUUID()
        {
            var uuid = GetSmBIOSUUID();
            if (string.IsNullOrWhiteSpace(uuid))
            {
                var cpuID = GetCPUID();
                var biosSerialNumber = GetBIOSSerialNumber();
                var diskSerialNumber = GetDiskDriveSerialNumber();
                uuid = $"{cpuID}__{biosSerialNumber}__{diskSerialNumber}";
            }
            return uuid;
        }

        private static string GetSmBIOSUUID()
        {
            var cmd = "wmic csproduct get UUID";
            return ExecuteCMD(cmd, output =>
            {
                string uuid = GetTextAfterSpecialText(output, "UUID");
                if (uuid == "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF")
                {
                    uuid = null;
                }
                return uuid;
            });
        }
        private static string GetCPUID()
        {
            var cmd = "wmic cpu get processorid";
            return ExecuteCMD(cmd, output =>
            {
                var cpuid = GetTextAfterSpecialText(output, "ProcessorId");
                return cpuid;
            });
        }
        private static string GetBIOSSerialNumber()
        {
            var cmd = "wmic bios get serialnumber";
            return ExecuteCMD(cmd, output =>
            {
                var serialNumber = GetTextAfterSpecialText(output, "SerialNumber");
                return serialNumber;
            });
        }
        private static string GetDiskDriveSerialNumber()
        {
            var cmd = "wmic diskdrive get serialnumber";
            return ExecuteCMD(cmd, output =>
            {
                var serialNumber = GetTextAfterSpecialText(output, "SerialNumber");
                return serialNumber;
            });
        }
        private static string GetTextAfterSpecialText(string fullText, string specialText)
        {
            if (string.IsNullOrWhiteSpace(fullText) || string.IsNullOrWhiteSpace(specialText))
            {
                return null;
            }
            string lastText = null;
            var idx = fullText.LastIndexOf(specialText);
            if (idx > 0)
            {
                lastText = fullText.Substring(idx + specialText.Length).Trim();
            }
            return lastText;
        }
        public static string ExecuteCMD(string cmd, Func<string, string> filterFunc)
        {
            Process process = new Process();
            process.StartInfo.FileName = "cmd.exe";
            process.StartInfo.UseShellExecute = false;//是否使用操作系统shell启动
            process.StartInfo.RedirectStandardInput = true;//接受来自调用程序的输入信息
            process.StartInfo.RedirectStandardOutput = true;//由调用程序获取输出信息
            process.StartInfo.RedirectStandardError = true;//重定向标准错误输出
            process.StartInfo.CreateNoWindow = true;//不显示程序窗口
            process.Start();//启动程序
            process.StandardInput.WriteLine(cmd + " &exit");
            process.StandardInput.AutoFlush = true;
            //获取cmd窗口的输出信息
            var output = process.StandardOutput.ReadToEnd();
            process.WaitForExit();
            process.Close();
            return filterFunc(output);
        }
        public static void ExecuteCMD(string cmd)
        {
            Process process = new Process();
            process.StartInfo.FileName = "cmd.exe";
            process.StartInfo.UseShellExecute = false;//是否使用操作系统shell启动
            process.StartInfo.RedirectStandardInput = true;//接受来自调用程序的输入信息
            process.StartInfo.RedirectStandardOutput = true;//由调用程序获取输出信息
            process.StartInfo.RedirectStandardError = true;//重定向标准错误输出
            process.StartInfo.CreateNoWindow = true;//不显示程序窗口
            process.Start();//启动程序
            process.StandardInput.WriteLine(cmd + " &exit");
            process.StandardInput.AutoFlush = true;
            process.WaitForExit();
            process.Close();
        }

        #endregion

        public static int findPidByPort(int port)
        {
            if (port > 0)
            {
                string ipPort = getIp() + ":" + port;
                string ipPort2 = "0.0.0.0:" + port;
                Process pro = new Process();

                // 设置命令行、参数
                pro.StartInfo.FileName = "cmd.exe";
                pro.StartInfo.UseShellExecute = false;
                pro.StartInfo.RedirectStandardInput = true;
                pro.StartInfo.RedirectStandardOutput = true;
                pro.StartInfo.RedirectStandardError = true;
                pro.StartInfo.CreateNoWindow = true;
                // 启动CMD
                pro.Start();
                // 运行端口检查命令
                pro.StandardInput.WriteLine("netstat -ano | findstr " + port);
                pro.StandardInput.WriteLine("exit");

                // 获取结果
                string line = pro.StandardOutput.ReadLine();
                while (line != null)
                {
                    line = line.Trim();
                    if (line.StartsWith("TCP", StringComparison.OrdinalIgnoreCase) && (line.Contains(ipPort) || line.Contains(ipPort2)))
                    {
                        string[] arr = line.Split(' ');
                        int len = arr.Length;
                        if (len > 0)
                        {
                            try
                            {
                                int pid = int.Parse(arr[len - 1]);
                                return pid;
                            }
                            catch { }
                        }
                    }
                    line = pro.StandardOutput.ReadLine();
                }
            }
            return -1;
        }
    }
}
