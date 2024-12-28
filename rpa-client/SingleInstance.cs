using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace rpa_client
{
    internal class SingleInstance
    {
        private Form mainForm;
        private NotifyIcon notifyIcon1;
        public static EventWaitHandle ProgramStarted;

        public SingleInstance(Form main, NotifyIcon notifyIcon)
        {
            this.mainForm = main;
            this.notifyIcon1 = notifyIcon;
            Load();
        }

        [DllImport("user32.dll")]
        public static extern bool SetForegroundWindow(IntPtr hWnd);

        private void Load()
        {
            //注册进程OnProgramStarted
            ThreadPool.RegisterWaitForSingleObject(ProgramStarted,
                (obj, timeout) => { ShowForm(); },
                null, -1, false);
        }

        private void ShowForm()
        {
            if (!mainForm.Visible)
            {
                mainForm.Visible = true;
                mainForm.WindowState = FormWindowState.Normal;
                mainForm.Activate();
            }
            //前置该窗体
            SetForegroundWindow(mainForm.Handle);
        }

        /// <summary>
        /// 检查是否启动过，如果启动则通知前一个进程，并退出当前进程
        /// </summary>
        public static void CheckCreated()
        {
            // 尝试创建一个命名事件
            bool createNew;
            ProgramStarted = new EventWaitHandle(false, EventResetMode.AutoReset, Application.ProductName, out createNew);

            if (!createNew)
            {
                SingleInstance.ProgramStarted.Set();
                Environment.Exit(1);
            }
        }
    }
}
