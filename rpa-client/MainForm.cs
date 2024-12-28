using System;
using System.Drawing;
using System.IO;
using System.Management;
using System.Threading;
using System.Windows.Forms;
using System.Collections.Generic;

namespace rpa_client
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
            this.ClientSize = new System.Drawing.Size(373, 219);
            btn_stop.Enabled = false;
            new SingleInstance(this, this.notifyIcon1);
            Control.CheckForIllegalCrossThreadCalls = false;
        }

        public static System.Threading.Timer upgradeTimer;
        private static System.Threading.Timer healthTimer;
        private static System.Threading.Timer loginTimer;

        #region load
        private void robot9y_Load(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(Context.SeqNo))
            {
                MessageShow("无法获取本机机器码");
                System.Environment.Exit(0);
            }
            Context.init();
            txt_folderPath.Text = Constant.WorkPath;
            mainLoad();
        }

        public void mainLoad()
        {
            try
            {
                if (Security.isLogin())
                {
                    Security.Status = RobotStatus.New;
                    if(healthTimer == null)
                    {
                        healthTimer = new System.Threading.Timer(healthTick, null, TimeSpan.FromSeconds(10), TimeSpan.FromSeconds(10));
                    }
                    if (Installer.isInstalled())
                    {
                        RobotProcess.stop();
                        showPanel(pan_control);
                        btn_start_Click(null, null);
                        if(upgradeTimer == null)
                        {
                            upgradeTimer = new System.Threading.Timer(upgradeTick, null, TimeSpan.FromSeconds(60), TimeSpan.FromSeconds(60));
                        }
                    }
                    else
                    {
                        showPanel(pan_install);
                    }
                    if (loginTimer != null)
                    {
                        loginTimer.Change(-1,1);
                        loginTimer.Dispose();
                    }

                }
                else
                {
                    if(loginTimer == null)
                    {
                        loginTimer = new System.Threading.Timer(loginTick, null, TimeSpan.FromSeconds(10), TimeSpan.FromSeconds(30));
                    }
                    showPanel(pan_login);
                }
            }
            catch (Exception ex)
            {
                MessageShow(ex.Message);
            }
        }
        #endregion

        #region click

        private void btn_login_Click(object sender, EventArgs e)
        {
            try
            {
                if (Security.doLogin(this.txt_username.Text, this.txt_password.Text))
                {
                    mainLoad();
                }
                else
                {
                    MessageShow("登录失败，请检查用户名密码");
                }
            }
            catch (Exception ex)
            {
                MessageShow(ex.Message);
                Logger.error("登录异常。", ex);
                MessageBox.Show("登录失败，请检查网络");
            }
        }

        private void btn_install_Click(object sender, EventArgs e)
        {
            try
            {
                btn_install.Text = "正在安装中..";
                btn_install.Enabled = false;
                Installer.isOsStart = this.chk_osStart.Checked;
                Installer.isCoverUp = this.chk_coverUp.Checked;
                if (Installer.doInstall())
                {
                    Logger.info("安装功能。重启应用");
                    RobotProcess.restart();
                }
                else
                {
                    Logger.warn("机器人安装失败");
                    MessageBox.Show("机器人安装失败,请检查网络是否正常");
                }
            }
            catch (Exception ex)
            {
                Logger.error("机器人安装失败", ex);
                MessageBox.Show(ex.Message);
            }
            finally
            {
                btn_install.Text = "安装";
                btn_install.Enabled = false;
            }
        }

        private void btn_start_Click(object sender, EventArgs e)
        {
            try
            {
                lab_status.Text = "机器人正在启动中...";
                if (RobotProcess.start())
                {
                    btn_start.Enabled = false;
                    btn_start.BackColor = Color.FromArgb(218, 218, 218);
                    btn_start.ForeColor = Color.FromArgb(144, 147, 153);
                    btn_stop.Enabled = true;
                    btn_stop.BackColor = Color.FromArgb(231, 98, 18);
                    btn_stop.ForeColor = Color.FromArgb(242, 247, 255);
                }
                else
                {
                    lab_status.Text = "机器人啓動失敗！";
                }
            }
            catch(Exception e2)
            {
                Logger.error("【启动发生了异常】", e2);
            }
        }

        private void btn_stop_Click(object sender, EventArgs e)
        {
            try
            {
                if (RobotProcess.stop())
                {
                    lab_status.Text = "机器人已停用。";
                    Security.Status = RobotStatus.Closed;
                    btn_start.Enabled = true;
                    btn_start.BackColor = Color.FromArgb(231, 98, 18);
                    btn_start.ForeColor = Color.FromArgb(242, 247, 255);
                    btn_stop.Enabled = false;
                    btn_stop.BackColor = Color.FromArgb(218, 218, 218);
                    btn_stop.ForeColor = Color.FromArgb(144, 147, 153);
                }
            }
            catch (Exception e2)
            {
                Logger.error("【停用发生了异常】", e2);
            }
        }

        private void notifyIcon1_DoubleClick(object sender, EventArgs e)
        {
            if (this.Visible)
            {
                this.WindowState = FormWindowState.Minimized;
                this.notifyIcon1.Visible = true;
                this.Hide();
            }
            else
            {
                this.Visible = true;
                this.WindowState = FormWindowState.Normal;
                this.Activate();
            }
        }

        private void quit_Click(object sender, EventArgs e)
        {
            this.btn_stop_Click(null, null);
            AppConfig.upset(AppConfig.RPA_TOKEN, "");
            AppConfig.upset(AppConfig.RPA_BOT_TOKEN, "");
            System.Environment.Exit(0);
        }

        private void info_Click(object sender, EventArgs e)
        {
            string info = "【机器识别码】" + Context.SeqNo +
                "\n【客户端版本】" + AppConfig.get(AppConfig.RPA_CLIENT_VERSION) +
                "\n【机器人版本】" + AppConfig.get(AppConfig.RPA_ROBOT_VERSION) ;
            MessageBox.Show(info, "版本信息", MessageBoxButtons.OK, MessageBoxIcon.None, MessageBoxDefaultButton.Button1, MessageBoxOptions.ServiceNotification);
        }
        #endregion

        #region tick

        public void loginTick(object o)
        {
            AppConfig.reload();
            string token = AppConfig.get(AppConfig.RPA_TOKEN);
            if (!string.IsNullOrWhiteSpace(token) && Security.isLogin())
            {
                mainLoad();
            }
        }


        //每隔10s进行健康检测
        private bool isChecking = false;
        public void healthTick(object o)
        {
            if (isChecking) return;
            try
            {
                isChecking = true;
                RobotProcess.checkHealth();
                if (Security.Status == RobotStatus.Runnable)
                {
                    this.lab_status.Text = "机器人正常运行。";
                }
                else if (Security.Status == RobotStatus.Running)
                {
                    this.lab_status.Text = "机器人正在执行任务中...";
                }
                else if (Security.Status == RobotStatus.Upgrading)
                {
                    this.lab_status.Text = "机器人正在升级，请耐心等待.";
                }
                string text = Security.sendHeartbeat();
                if (!string.IsNullOrWhiteSpace(text) && text != "0")
                {
                    remoteCmd(text);
                }
            }
            catch (Exception e2)
            {
                Logger.error("健康检测发生了异常", e2);
            }
            finally
            {
                isChecking = false;
            }
        }

        //每隔1分钟进行版本检测
        public void upgradeTick(object o)
        {
            if (base.Visible)
            {
                base.WindowState = FormWindowState.Minimized;
                this.notifyIcon1.Visible = true;
                base.Hide();
            }

            if (Security.Status == RobotStatus.Running || Security.Status == RobotStatus.Upgrading) return;

            try
            {
                bool flag = true;

                if (Upgrader.checkVersion(AppConfig.RPA_CLIENT_VERSION, Upgrader.COMPONENT_CLIENT_NAME))
                {
                    flag = false;
                    Logger.info("【发现新版本】client.exe。关闭机器人，开始升级...");
                    RobotProcess.stop();
                    Upgrader.upgradeClient();
                    return;
                }


                if (Upgrader.checkVersion(AppConfig.RPA_ROBOT_VERSION, Upgrader.COMPONENT_ROBOT_NAME))
                {
                    flag = false;
                    Security.Status = RobotStatus.Upgrading;
                    Logger.info("【发现新版本】rpa-robot.jar，关闭机器人，开始升级...");
                    RobotProcess.stop();
                    if (Upgrader.upgradeJar(AppConfig.RPA_ROBOT_VERSION, Upgrader.COMPONENT_ROBOT_NAME, Constant.RobotName))
                    {
                        Logger.info("【升级成功】完成robot.jar升级，重启机器人");
                        Security.Status = RobotStatus.Runnable;
                        RobotProcess.start();
                        return;
                    }
                    else
                    {
                        Security.Status = RobotStatus.UpgradeFailed;
                    }
                }

                if (Upgrader.checkVersion(AppConfig.RPA_ROBOT_HUB_VERSION, Upgrader.COMPONENT_ROBOT_HUB_NAME))
                {
                    flag = false;
                    Security.Status = RobotStatus.Upgrading;
                    Logger.info("【发现新版本】rpa-robot.jar，关闭机器人，开始升级...");
                    RobotProcess.stop();
                    if (Upgrader.upgradeJar(AppConfig.RPA_ROBOT_HUB_VERSION, Upgrader.COMPONENT_ROBOT_HUB_NAME, Constant.RobotHubName))
                    {
                        Logger.info("【升级成功】完成robot.jar升级，重启机器人");
                        Security.Status = RobotStatus.Runnable;
                        RobotProcess.start();
                        return;
                    }
                    else
                    {
                        Security.Status = RobotStatus.UpgradeFailed;
                    }
                }

                if (Upgrader.checkVersion(null, Upgrader.COMPONENT_ZIPS))
                {
                    flag = false;
                    Security.Status = RobotStatus.Upgrading;
                    Logger.info("【发现新版本】zip文件，关闭机器人，开始升级...");
                    RobotProcess.stop();
                    if (Upgrader.upgradeZip())
                    {
                        Logger.info("【升级成功】完成zip文件升级。重启机器人");
                        Security.Status = RobotStatus.Runnable;
                        RobotProcess.start();
                        return;
                    }
                    else
                    {
                        Security.Status = RobotStatus.UpgradeFailed;
                    }
                }


                //if (Upgrader.checkVersion(AppConfig.RPA_TPL_VERSION, Upgrader.COMPONENT_TPL_NAME))
                //{
                //    flag = false;
                //    Security.Status = RobotStatus.Upgrading;
                //    Logger.info("【发现新版本】tpl.zip，关闭机器人，开始升级...");
                //    RobotProcess.stop();
                //    if (Upgrader.upgradeTpl())
                //    {
                //        Logger.info("【升级成功】完成tpl.zip升级。重启机器人");
                //        RobotProcess.start();
                //        return;
                //    }
                //    else
                //    {
                //        Security.Status = RobotStatus.UpgradeFailed;
                //    }
                //}

                //if (Upgrader.checkVersion(AppConfig.RPA_PY_VERSION, Upgrader.COMPONENT_PY_NAME))
                //{
                //    flag = false;
                //    Security.Status = RobotStatus.Upgrading;
                //    Logger.info("【发现新版本】python.zip，关闭机器人，开始升级...");
                //    RobotProcess.stop();
                //    if (Upgrader.upgradePy())
                //    {
                //        Logger.info("【升级成功】完成python.zip升级。重启机器人");
                //        RobotProcess.start();
                //        return;
                //    }
                //    else
                //    {
                //        Security.Status = RobotStatus.UpgradeFailed;
                //    }
                //}

                //if (Upgrader.checkVersion(AppConfig.RPA_CHROME_VERSION, Upgrader.COMPONENT_CHROME_NAME))
                //{
                //    flag = false;
                //    Security.Status = RobotStatus.Upgrading;
                //    Logger.info("【发现新版本】chrome.zip，关闭机器人，开始升级...");
                //    RobotProcess.stop();
                //    if (Upgrader.upgradeChrome())
                //    {
                //        Logger.info("【升级成功】chrome.zip升级。重启机器人");
                //        RobotProcess.start();
                //        return;
                //    }
                //    else
                //    {
                //        Security.Status = RobotStatus.UpgradeFailed;
                //    }
                //}

                //if (Upgrader.checkVersion(AppConfig.RPA_JRE_VERSION, Upgrader.COMPONENT_JRE_NAME))
                //{
                //    flag = false;
                //    Security.Status = RobotStatus.Upgrading;
                //    Logger.info("【发现新版本】jre.zip，关闭机器人，开始升级...");
                //    RobotProcess.stop();
                //    if (Upgrader.upgradeJre())
                //    {
                //        Logger.info("【升级成功】jre.zip升级。重启机器人");
                //        RobotProcess.start();
                //        return;
                //    }
                //    else
                //    {
                //        Security.Status = RobotStatus.UpgradeFailed;
                //    }
                //}


                List<string> versionList = new List<string>();
                List<Version> versions = Upgrader.findVersionList();
                if (versions != null)
                {
                    foreach (Version v in versions)
                    {
                        if (v.component.StartsWith("ca_"))
                        {
                            versionList.Add(v.component.Replace("ca_", ""));
                        }
                    }
                }

                string[] citys = versionList.Count > 0 ? versionList.ToArray() : Constant.RPA_CA_CITY.Split(',');

                int city_size = citys.Length;
                bool ca_flag = false;
                for (int i = 0; i < city_size; i++)
                {
                    if (Upgrader.checkCAVersion(citys[i]))
                    {
                        flag = false;
                        ca_flag = true;
                        Security.Status = RobotStatus.Upgrading;
                        Logger.info("【发现新版本】ca.zip，关闭机器人，开始升级...");
                        RobotProcess.stop();
                        if (Upgrader.upgradeCA(citys[i]))
                        {
                            Logger.info("【升级成功】完成"+ citys[i] + " ca.zip升级。重启机器人");
                        }
                        else
                        {
                            Security.Status = RobotStatus.UpgradeFailed;
                        }
                    }
                }
                if (ca_flag)
                {
                    Logger.info("【升级成功】ca.zip升级完成。重启机器人");
                    RobotProcess.stop();
                    Security.Status = RobotStatus.Runnable;
                    RobotProcess.start();
                    return;
                }

                if (flag)
                {
                    Logger.info("版本检测结果：无新版本");
                }
                if(DateTime.Now.Hour == 1)
                {
                    Security.cleanCacheFile();
                }
            }
            catch (Exception e2)
            {
                Security.Status = RobotStatus.UpgradeFailed;
                Logger.error("版本检测发生了异常", e2);
            }
        }
        
        //处理远程指令
        public void remoteCmd(string cmd)
        {
            Logger.info("接收到远程命令：" + cmd);
            if (cmd == "restart")
            {
                this.btn_stop_Click(null, null);
                Thread.Sleep(2000);
                this.btn_start_Click(null, null);
            }
            else if (cmd == "stop")
            {
                this.btn_stop_Click(null, null);
            }
            else if (cmd == "start")
            {
                this.btn_start_Click(null, null);
            }
            else if (cmd.StartsWith("file"))
            {
                string path = cmd.Split('=')[1];
                string text2 = Path.Combine(Constant.WorkPath, path);
                if (File.Exists(text2))
                {
                    Security.sendLog(text2);
                }
                else
                {
                    Logger.warn(cmd + "不存在");
                }
            }
        }

        #endregion

        #region panel

        public void showPanel(Panel panel)
        {
            hideAll();
            panel.Show();
            panel.Location = new System.Drawing.Point(1, 1);
        }

        public void hideAll()
        {
            pan_install.Hide();
            pan_control.Hide();
            pan_login.Hide();
        }

        #endregion

        #region message.show

        public void MessageShow(string ex, string msg)
        {
            if (string.IsNullOrWhiteSpace(ex))
            {
                MessageShow(msg);
            }
            else
            {
                Logger.warn(ex);
                MessageShow(ex);
            }
        }

        public void MessageShow(string msg)
        {
            Logger.info(msg);
            if (msg.Contains("\"code\":500"))
            {
                string message = Utils.subString(msg, "\"message\":\"", "\"");
                string data = Utils.subString(msg, "\"data\":\"", "\"");
                if (data == "")
                {
                    MessageBox.Show(message, "异常提示", MessageBoxButtons.OK, MessageBoxIcon.None, MessageBoxDefaultButton.Button1, MessageBoxOptions.ServiceNotification);
                }
                else
                {
                    MessageBox.Show(data, message, MessageBoxButtons.OK, MessageBoxIcon.None, MessageBoxDefaultButton.Button1, MessageBoxOptions.ServiceNotification);
                }
            }
            else
            {
                MessageBox.Show(msg, "消息提示", MessageBoxButtons.OK, MessageBoxIcon.None, MessageBoxDefaultButton.Button1, MessageBoxOptions.ServiceNotification);
            }
        }

        #endregion
        

        private void usbToolStripMenuItem_Click(object sender, EventArgs e)
        {
            UsbSetting usb = new UsbSetting();
            usb.ShowDialog();
        }

        private void usbCtrlToolStripMenuItem_Click(object sender, EventArgs e)
        {
            UsbCtrl usb = new UsbCtrl();
            usb.ShowDialog();
        }

        private void 设备变更ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SyncApp syncApp = new SyncApp();
            syncApp.ShowDialog();
        }

        private void 清理浏览器ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (RobotProcess.clearDriver())
            {
                MessageBox.Show("清理成功");
            }
            else
            {
                MessageBox.Show("清理失败，请联系管理员");
            }
        }

        private void executor_ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Executor executor = new Executor();
            executor.ShowDialog();
        }
    }
}
