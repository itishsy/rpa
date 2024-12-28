﻿namespace rpa_client
{
    partial class MainForm
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.notifyIcon1 = new System.Windows.Forms.NotifyIcon(this.components);
            this.contextMenuStrip1 = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.清理浏览器ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.usbCtrlToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.设备变更ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.executor_ToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.usbToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.info = new System.Windows.Forms.ToolStripMenuItem();
            this.quit = new System.Windows.Forms.ToolStripMenuItem();
            this.health = new System.Windows.Forms.Timer(this.components);
            this.upgrade = new System.Windows.Forms.Timer(this.components);
            this.pan_control = new System.Windows.Forms.Panel();
            this.lab_status = new System.Windows.Forms.Label();
            this.btn_start = new System.Windows.Forms.Button();
            this.btn_stop = new System.Windows.Forms.Button();
            this.pan_login = new System.Windows.Forms.Panel();
            this.txt_username = new System.Windows.Forms.TextBox();
            this.txt_password = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.btn_login = new System.Windows.Forms.Button();
            this.pan_install = new System.Windows.Forms.Panel();
            this.chk_coverUp = new System.Windows.Forms.CheckBox();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.chk_osStart = new System.Windows.Forms.CheckBox();
            this.txt_port = new System.Windows.Forms.TextBox();
            this.txt_folderPath = new System.Windows.Forms.TextBox();
            this.btn_install = new System.Windows.Forms.Button();
            this.contextMenuStrip1.SuspendLayout();
            this.pan_control.SuspendLayout();
            this.pan_login.SuspendLayout();
            this.pan_install.SuspendLayout();
            this.SuspendLayout();
            // 
            // notifyIcon1
            // 
            this.notifyIcon1.ContextMenuStrip = this.contextMenuStrip1;
            this.notifyIcon1.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyIcon1.Icon")));
            this.notifyIcon1.Text = "仕邦RPA";
            this.notifyIcon1.Visible = true;
            this.notifyIcon1.DoubleClick += new System.EventHandler(this.notifyIcon1_DoubleClick);
            // 
            // contextMenuStrip1
            // 
            this.contextMenuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.清理浏览器ToolStripMenuItem,
            this.usbCtrlToolStripMenuItem,
            this.设备变更ToolStripMenuItem,
            this.executor_ToolStripMenuItem,
            this.usbToolStripMenuItem,
            this.info,
            this.quit});
            this.contextMenuStrip1.Name = "contextMenuStrip1";
            this.contextMenuStrip1.Size = new System.Drawing.Size(153, 180);
            // 
            // 清理浏览器ToolStripMenuItem
            // 
            this.清理浏览器ToolStripMenuItem.Name = "清理浏览器ToolStripMenuItem";
            this.清理浏览器ToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.清理浏览器ToolStripMenuItem.Text = "清理浏览器";
            this.清理浏览器ToolStripMenuItem.Click += new System.EventHandler(this.清理浏览器ToolStripMenuItem_Click);
            // 
            // usbCtrlToolStripMenuItem
            // 
            this.usbCtrlToolStripMenuItem.Name = "usbCtrlToolStripMenuItem";
            this.usbCtrlToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.usbCtrlToolStripMenuItem.Text = "激活USB端口";
            this.usbCtrlToolStripMenuItem.Click += new System.EventHandler(this.usbCtrlToolStripMenuItem_Click);
            // 
            // 设备变更ToolStripMenuItem
            // 
            this.设备变更ToolStripMenuItem.Name = "设备变更ToolStripMenuItem";
            this.设备变更ToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.设备变更ToolStripMenuItem.Text = "设备变更";
            this.设备变更ToolStripMenuItem.Click += new System.EventHandler(this.设备变更ToolStripMenuItem_Click);
            // 
            // executor_ToolStripMenuItem
            // 
            this.executor_ToolStripMenuItem.Name = "executor_ToolStripMenuItem";
            this.executor_ToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.executor_ToolStripMenuItem.Text = "执行器";
            this.executor_ToolStripMenuItem.Click += new System.EventHandler(this.executor_ToolStripMenuItem_Click);
            // 
            // usbToolStripMenuItem
            // 
            this.usbToolStripMenuItem.Name = "usbToolStripMenuItem";
            this.usbToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.usbToolStripMenuItem.Text = "USB设置";
            this.usbToolStripMenuItem.Click += new System.EventHandler(this.usbToolStripMenuItem_Click);
            // 
            // info
            // 
            this.info.Name = "info";
            this.info.Size = new System.Drawing.Size(152, 22);
            this.info.Text = "版本信息";
            this.info.Click += new System.EventHandler(this.info_Click);
            // 
            // quit
            // 
            this.quit.Name = "quit";
            this.quit.Size = new System.Drawing.Size(152, 22);
            this.quit.Text = "退出";
            this.quit.Click += new System.EventHandler(this.quit_Click);
            // 
            // health
            // 
            this.health.Interval = 10000;
            // 
            // upgrade
            // 
            this.upgrade.Interval = 60000;
            // 
            // pan_control
            // 
            this.pan_control.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("pan_control.BackgroundImage")));
            this.pan_control.Controls.Add(this.lab_status);
            this.pan_control.Controls.Add(this.btn_start);
            this.pan_control.Controls.Add(this.btn_stop);
            this.pan_control.Location = new System.Drawing.Point(853, 1);
            this.pan_control.Name = "pan_control";
            this.pan_control.Size = new System.Drawing.Size(373, 219);
            this.pan_control.TabIndex = 13;
            // 
            // lab_status
            // 
            this.lab_status.AutoSize = true;
            this.lab_status.BackColor = System.Drawing.Color.Transparent;
            this.lab_status.Font = new System.Drawing.Font("微软雅黑 Light", 12F);
            this.lab_status.ForeColor = System.Drawing.SystemColors.ActiveCaptionText;
            this.lab_status.Location = new System.Drawing.Point(61, 65);
            this.lab_status.Name = "lab_status";
            this.lab_status.Size = new System.Drawing.Size(182, 21);
            this.lab_status.TabIndex = 11;
            this.lab_status.Text = "请点击“启动”运行机器人";
            // 
            // btn_start
            // 
            this.btn_start.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(231)))), ((int)(((byte)(98)))), ((int)(((byte)(18)))));
            this.btn_start.FlatAppearance.BorderSize = 0;
            this.btn_start.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_start.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.btn_start.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(242)))), ((int)(((byte)(247)))), ((int)(((byte)(255)))));
            this.btn_start.Location = new System.Drawing.Point(60, 148);
            this.btn_start.Name = "btn_start";
            this.btn_start.Size = new System.Drawing.Size(108, 39);
            this.btn_start.TabIndex = 9;
            this.btn_start.Text = "启动";
            this.btn_start.UseVisualStyleBackColor = false;
            this.btn_start.Click += new System.EventHandler(this.btn_start_Click);
            // 
            // btn_stop
            // 
            this.btn_stop.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(218)))), ((int)(((byte)(218)))), ((int)(((byte)(218)))));
            this.btn_stop.FlatAppearance.BorderSize = 0;
            this.btn_stop.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_stop.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.btn_stop.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(144)))), ((int)(((byte)(147)))), ((int)(((byte)(153)))));
            this.btn_stop.Location = new System.Drawing.Point(211, 146);
            this.btn_stop.Name = "btn_stop";
            this.btn_stop.Size = new System.Drawing.Size(108, 40);
            this.btn_stop.TabIndex = 10;
            this.btn_stop.Text = "停用";
            this.btn_stop.UseVisualStyleBackColor = false;
            this.btn_stop.Click += new System.EventHandler(this.btn_stop_Click);
            // 
            // pan_login
            // 
            this.pan_login.BackColor = System.Drawing.Color.Transparent;
            this.pan_login.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("pan_login.BackgroundImage")));
            this.pan_login.Controls.Add(this.txt_username);
            this.pan_login.Controls.Add(this.txt_password);
            this.pan_login.Controls.Add(this.label1);
            this.pan_login.Controls.Add(this.label2);
            this.pan_login.Controls.Add(this.btn_login);
            this.pan_login.Location = new System.Drawing.Point(1, 1);
            this.pan_login.Name = "pan_login";
            this.pan_login.Size = new System.Drawing.Size(373, 219);
            this.pan_login.TabIndex = 12;
            // 
            // txt_username
            // 
            this.txt_username.BackColor = System.Drawing.Color.AliceBlue;
            this.txt_username.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.txt_username.Font = new System.Drawing.Font("微软雅黑", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.txt_username.ForeColor = System.Drawing.SystemColors.WindowFrame;
            this.txt_username.Location = new System.Drawing.Point(139, 45);
            this.txt_username.Name = "txt_username";
            this.txt_username.Size = new System.Drawing.Size(163, 22);
            this.txt_username.TabIndex = 1;
            // 
            // txt_password
            // 
            this.txt_password.BackColor = System.Drawing.Color.AliceBlue;
            this.txt_password.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.txt_password.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.txt_password.ForeColor = System.Drawing.SystemColors.WindowFrame;
            this.txt_password.Location = new System.Drawing.Point(139, 98);
            this.txt_password.Name = "txt_password";
            this.txt_password.Size = new System.Drawing.Size(163, 22);
            this.txt_password.TabIndex = 2;
            this.txt_password.UseSystemPasswordChar = true;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.BackColor = System.Drawing.Color.Transparent;
            this.label1.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.label1.Location = new System.Drawing.Point(59, 45);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(74, 21);
            this.label1.TabIndex = 3;
            this.label1.Text = "用户名：";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.BackColor = System.Drawing.Color.Transparent;
            this.label2.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.label2.Location = new System.Drawing.Point(75, 98);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(58, 21);
            this.label2.TabIndex = 4;
            this.label2.Text = "密码：";
            // 
            // btn_login
            // 
            this.btn_login.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(231)))), ((int)(((byte)(98)))), ((int)(((byte)(18)))));
            this.btn_login.FlatAppearance.BorderSize = 0;
            this.btn_login.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_login.Font = new System.Drawing.Font("微软雅黑", 14F);
            this.btn_login.ForeColor = System.Drawing.Color.White;
            this.btn_login.Location = new System.Drawing.Point(79, 147);
            this.btn_login.Margin = new System.Windows.Forms.Padding(0);
            this.btn_login.Name = "btn_login";
            this.btn_login.Padding = new System.Windows.Forms.Padding(3);
            this.btn_login.Size = new System.Drawing.Size(223, 39);
            this.btn_login.TabIndex = 0;
            this.btn_login.Text = "登录";
            this.btn_login.UseVisualStyleBackColor = false;
            this.btn_login.Click += new System.EventHandler(this.btn_login_Click);
            // 
            // pan_install
            // 
            this.pan_install.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("pan_install.BackgroundImage")));
            this.pan_install.Controls.Add(this.chk_coverUp);
            this.pan_install.Controls.Add(this.label4);
            this.pan_install.Controls.Add(this.label3);
            this.pan_install.Controls.Add(this.chk_osStart);
            this.pan_install.Controls.Add(this.txt_port);
            this.pan_install.Controls.Add(this.txt_folderPath);
            this.pan_install.Controls.Add(this.btn_install);
            this.pan_install.Location = new System.Drawing.Point(427, 1);
            this.pan_install.Name = "pan_install";
            this.pan_install.Size = new System.Drawing.Size(373, 219);
            this.pan_install.TabIndex = 11;
            this.pan_install.Tag = "";
            // 
            // chk_coverUp
            // 
            this.chk_coverUp.AutoSize = true;
            this.chk_coverUp.BackColor = System.Drawing.Color.Transparent;
            this.chk_coverUp.Location = new System.Drawing.Point(134, 107);
            this.chk_coverUp.Name = "chk_coverUp";
            this.chk_coverUp.Size = new System.Drawing.Size(72, 16);
            this.chk_coverUp.TabIndex = 18;
            this.chk_coverUp.Text = "覆盖安装";
            this.chk_coverUp.UseVisualStyleBackColor = false;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.BackColor = System.Drawing.Color.Transparent;
            this.label4.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.label4.Location = new System.Drawing.Point(38, 67);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(90, 21);
            this.label4.TabIndex = 17;
            this.label4.Text = "工作目录：";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.BackColor = System.Drawing.Color.Transparent;
            this.label3.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.label3.Location = new System.Drawing.Point(70, 31);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(58, 21);
            this.label3.TabIndex = 5;
            this.label3.Text = "端口：";
            // 
            // chk_osStart
            // 
            this.chk_osStart.AutoSize = true;
            this.chk_osStart.BackColor = System.Drawing.Color.Transparent;
            this.chk_osStart.Checked = true;
            this.chk_osStart.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chk_osStart.Location = new System.Drawing.Point(42, 107);
            this.chk_osStart.Name = "chk_osStart";
            this.chk_osStart.Size = new System.Drawing.Size(72, 16);
            this.chk_osStart.TabIndex = 15;
            this.chk_osStart.Text = "开机启动";
            this.chk_osStart.UseVisualStyleBackColor = false;
            // 
            // txt_port
            // 
            this.txt_port.BackColor = System.Drawing.Color.AliceBlue;
            this.txt_port.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.txt_port.Enabled = false;
            this.txt_port.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.txt_port.Location = new System.Drawing.Point(134, 31);
            this.txt_port.Name = "txt_port";
            this.txt_port.ReadOnly = true;
            this.txt_port.Size = new System.Drawing.Size(181, 22);
            this.txt_port.TabIndex = 11;
            this.txt_port.Text = "9090";
            // 
            // txt_folderPath
            // 
            this.txt_folderPath.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.txt_folderPath.Enabled = false;
            this.txt_folderPath.Font = new System.Drawing.Font("微软雅黑", 12F);
            this.txt_folderPath.Location = new System.Drawing.Point(134, 67);
            this.txt_folderPath.Name = "txt_folderPath";
            this.txt_folderPath.ReadOnly = true;
            this.txt_folderPath.Size = new System.Drawing.Size(181, 22);
            this.txt_folderPath.TabIndex = 8;
            this.txt_folderPath.Text = "D:\\seebon\\rpa";
            // 
            // btn_install
            // 
            this.btn_install.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(231)))), ((int)(((byte)(98)))), ((int)(((byte)(18)))));
            this.btn_install.FlatAppearance.BorderSize = 0;
            this.btn_install.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btn_install.Font = new System.Drawing.Font("微软雅黑", 14F);
            this.btn_install.ForeColor = System.Drawing.Color.White;
            this.btn_install.Location = new System.Drawing.Point(74, 147);
            this.btn_install.Name = "btn_install";
            this.btn_install.Size = new System.Drawing.Size(241, 39);
            this.btn_install.TabIndex = 7;
            this.btn_install.Text = "安装";
            this.btn_install.UseVisualStyleBackColor = false;
            this.btn_install.Click += new System.EventHandler(this.btn_install_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(242)))), ((int)(((byte)(247)))), ((int)(((byte)(255)))));
            this.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.ClientSize = new System.Drawing.Size(1293, 283);
            this.ControlBox = false;
            this.Controls.Add(this.pan_control);
            this.Controls.Add(this.pan_login);
            this.Controls.Add(this.pan_install);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "MainForm";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Load += new System.EventHandler(this.robot9y_Load);
            this.contextMenuStrip1.ResumeLayout(false);
            this.pan_control.ResumeLayout(false);
            this.pan_control.PerformLayout();
            this.pan_login.ResumeLayout(false);
            this.pan_login.PerformLayout();
            this.pan_install.ResumeLayout(false);
            this.pan_install.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button btn_login;
        private System.Windows.Forms.TextBox txt_username;
        private System.Windows.Forms.TextBox txt_password;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button btn_install;
        private System.Windows.Forms.TextBox txt_folderPath;
        private System.Windows.Forms.Button btn_start;
        private System.Windows.Forms.Button btn_stop;
        private System.Windows.Forms.Panel pan_install;
        private System.Windows.Forms.Panel pan_login;
        private System.Windows.Forms.Panel pan_control;
        private System.Windows.Forms.TextBox txt_port;
        private System.Windows.Forms.Label lab_status;
        private System.Windows.Forms.CheckBox chk_osStart;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.NotifyIcon notifyIcon1;
        private System.Windows.Forms.Timer health;
        private System.Windows.Forms.Timer upgrade;
        private System.Windows.Forms.ContextMenuStrip contextMenuStrip1;
        private System.Windows.Forms.ToolStripMenuItem quit;
        private System.Windows.Forms.CheckBox chk_coverUp;
        private System.Windows.Forms.ToolStripMenuItem info;
        private System.Windows.Forms.ToolStripMenuItem usbToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem usbCtrlToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 设备变更ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem 清理浏览器ToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem executor_ToolStripMenuItem;
    }
}

