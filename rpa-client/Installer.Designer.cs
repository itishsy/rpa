namespace rpa_installer
{
    partial class Installer
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
            this.btn_login = new System.Windows.Forms.Button();
            this.txt_username = new System.Windows.Forms.TextBox();
            this.txt_password = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.btn_install = new System.Windows.Forms.Button();
            this.txt_folderPath = new System.Windows.Forms.TextBox();
            this.btn_start = new System.Windows.Forms.Button();
            this.btn_stop = new System.Windows.Forms.Button();
            this.pan_install = new System.Windows.Forms.Panel();
            this.checkBox2 = new System.Windows.Forms.CheckBox();
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.label7 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.txt_port = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.btn_folder = new System.Windows.Forms.Button();
            this.pan_login = new System.Windows.Forms.Panel();
            this.label6 = new System.Windows.Forms.Label();
            this.pan_control = new System.Windows.Forms.Panel();
            this.btn_upgrade = new System.Windows.Forms.Button();
            this.lab_status = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.pan_install.SuspendLayout();
            this.pan_login.SuspendLayout();
            this.pan_control.SuspendLayout();
            this.SuspendLayout();
            // 
            // btn_login
            // 
            this.btn_login.Location = new System.Drawing.Point(208, 162);
            this.btn_login.Name = "btn_login";
            this.btn_login.Size = new System.Drawing.Size(75, 23);
            this.btn_login.TabIndex = 0;
            this.btn_login.Text = "登录";
            this.btn_login.UseVisualStyleBackColor = true;
            this.btn_login.Click += new System.EventHandler(this.btn_login_Click);
            // 
            // txt_username
            // 
            this.txt_username.Location = new System.Drawing.Point(105, 66);
            this.txt_username.Name = "txt_username";
            this.txt_username.Size = new System.Drawing.Size(188, 21);
            this.txt_username.TabIndex = 1;
            this.txt_username.Text = "admin";
            // 
            // txt_password
            // 
            this.txt_password.Location = new System.Drawing.Point(105, 114);
            this.txt_password.Name = "txt_password";
            this.txt_password.Size = new System.Drawing.Size(188, 21);
            this.txt_password.TabIndex = 2;
            this.txt_password.Text = "123456";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(37, 69);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(53, 12);
            this.label1.TabIndex = 3;
            this.label1.Text = "用户名：";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(37, 114);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(35, 12);
            this.label2.TabIndex = 4;
            this.label2.Text = "密码:";
            // 
            // btn_install
            // 
            this.btn_install.Location = new System.Drawing.Point(326, 209);
            this.btn_install.Name = "btn_install";
            this.btn_install.Size = new System.Drawing.Size(75, 23);
            this.btn_install.TabIndex = 7;
            this.btn_install.Text = "启动";
            this.btn_install.UseVisualStyleBackColor = true;
            this.btn_install.Click += new System.EventHandler(this.btn_install_Click);
            // 
            // txt_folderPath
            // 
            this.txt_folderPath.Location = new System.Drawing.Point(108, 117);
            this.txt_folderPath.Name = "txt_folderPath";
            this.txt_folderPath.ReadOnly = true;
            this.txt_folderPath.Size = new System.Drawing.Size(206, 21);
            this.txt_folderPath.TabIndex = 8;
            this.txt_folderPath.Text = "D:\\seebon\\rpa";
            // 
            // btn_start
            // 
            this.btn_start.Location = new System.Drawing.Point(167, 173);
            this.btn_start.Name = "btn_start";
            this.btn_start.Size = new System.Drawing.Size(75, 23);
            this.btn_start.TabIndex = 9;
            this.btn_start.Text = "启动";
            this.btn_start.UseVisualStyleBackColor = true;
            this.btn_start.Click += new System.EventHandler(this.btn_start_Click);
            // 
            // btn_stop
            // 
            this.btn_stop.Location = new System.Drawing.Point(259, 173);
            this.btn_stop.Name = "btn_stop";
            this.btn_stop.Size = new System.Drawing.Size(75, 23);
            this.btn_stop.TabIndex = 10;
            this.btn_stop.Text = "停用";
            this.btn_stop.UseVisualStyleBackColor = true;
            this.btn_stop.Click += new System.EventHandler(this.btn_stop_Click);
            // 
            // pan_install
            // 
            this.pan_install.Controls.Add(this.checkBox2);
            this.pan_install.Controls.Add(this.checkBox1);
            this.pan_install.Controls.Add(this.label7);
            this.pan_install.Controls.Add(this.label5);
            this.pan_install.Controls.Add(this.txt_port);
            this.pan_install.Controls.Add(this.label4);
            this.pan_install.Controls.Add(this.btn_folder);
            this.pan_install.Controls.Add(this.txt_folderPath);
            this.pan_install.Controls.Add(this.btn_install);
            this.pan_install.Location = new System.Drawing.Point(530, 46);
            this.pan_install.Name = "pan_install";
            this.pan_install.Size = new System.Drawing.Size(455, 245);
            this.pan_install.TabIndex = 11;
            this.pan_install.Tag = "";
            // 
            // checkBox2
            // 
            this.checkBox2.AutoSize = true;
            this.checkBox2.Location = new System.Drawing.Point(44, 194);
            this.checkBox2.Name = "checkBox2";
            this.checkBox2.Size = new System.Drawing.Size(120, 16);
            this.checkBox2.TabIndex = 16;
            this.checkBox2.Text = "生成桌面快捷方式";
            this.checkBox2.UseVisualStyleBackColor = true;
            // 
            // checkBox1
            // 
            this.checkBox1.AutoSize = true;
            this.checkBox1.Location = new System.Drawing.Point(44, 162);
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.Size = new System.Drawing.Size(72, 16);
            this.checkBox1.TabIndex = 15;
            this.checkBox1.Text = "开机启动";
            this.checkBox1.UseVisualStyleBackColor = true;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(42, 120);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(53, 12);
            this.label7.TabIndex = 13;
            this.label7.Text = "工作目录";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(66, 78);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(29, 12);
            this.label5.TabIndex = 12;
            this.label5.Text = "端口";
            // 
            // txt_port
            // 
            this.txt_port.Location = new System.Drawing.Point(108, 69);
            this.txt_port.Name = "txt_port";
            this.txt_port.Size = new System.Drawing.Size(100, 21);
            this.txt_port.TabIndex = 11;
            this.txt_port.Text = "8996";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(37, 38);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(365, 12);
            this.label4.TabIndex = 10;
            this.label4.Text = "首次使用，初始化机器人，大约十几秒初始化（跟客户端网络有关）";
            // 
            // btn_folder
            // 
            this.btn_folder.Location = new System.Drawing.Point(316, 117);
            this.btn_folder.Name = "btn_folder";
            this.btn_folder.Size = new System.Drawing.Size(62, 23);
            this.btn_folder.TabIndex = 9;
            this.btn_folder.Text = "浏览";
            this.btn_folder.UseVisualStyleBackColor = true;
            this.btn_folder.Click += new System.EventHandler(this.btn_folder_Click);
            // 
            // pan_login
            // 
            this.pan_login.Controls.Add(this.label6);
            this.pan_login.Controls.Add(this.txt_username);
            this.pan_login.Controls.Add(this.txt_password);
            this.pan_login.Controls.Add(this.label1);
            this.pan_login.Controls.Add(this.label2);
            this.pan_login.Controls.Add(this.btn_login);
            this.pan_login.Location = new System.Drawing.Point(34, 46);
            this.pan_login.Name = "pan_login";
            this.pan_login.Size = new System.Drawing.Size(455, 245);
            this.pan_login.TabIndex = 12;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(37, 38);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(137, 12);
            this.label6.TabIndex = 11;
            this.label6.Text = "首次使用，输入身份验证";
            // 
            // pan_control
            // 
            this.pan_control.Controls.Add(this.btn_upgrade);
            this.pan_control.Controls.Add(this.lab_status);
            this.pan_control.Controls.Add(this.label3);
            this.pan_control.Controls.Add(this.btn_start);
            this.pan_control.Controls.Add(this.btn_stop);
            this.pan_control.Location = new System.Drawing.Point(1033, 46);
            this.pan_control.Name = "pan_control";
            this.pan_control.Size = new System.Drawing.Size(455, 245);
            this.pan_control.TabIndex = 13;
            // 
            // btn_upgrade
            // 
            this.btn_upgrade.Location = new System.Drawing.Point(349, 173);
            this.btn_upgrade.Name = "btn_upgrade";
            this.btn_upgrade.Size = new System.Drawing.Size(75, 23);
            this.btn_upgrade.TabIndex = 1;
            this.btn_upgrade.Text = "升级";
            this.btn_upgrade.UseVisualStyleBackColor = true;
            this.btn_upgrade.Click += new System.EventHandler(this.btn_upgrade_Click);
            // 
            // lab_status
            // 
            this.lab_status.AutoSize = true;
            this.lab_status.Location = new System.Drawing.Point(61, 95);
            this.lab_status.Name = "lab_status";
            this.lab_status.Size = new System.Drawing.Size(95, 12);
            this.lab_status.TabIndex = 11;
            this.lab_status.Text = "机器人工作中...";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(243, 214);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(161, 12);
            this.label3.TabIndex = 0;
            this.label3.Text = "发现新版本，是否立即升级？";
            // 
            // Installer
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1577, 330);
            this.Controls.Add(this.pan_control);
            this.Controls.Add(this.pan_login);
            this.Controls.Add(this.pan_install);
            this.Name = "Installer";
            this.Text = "机器人控制台";
            this.Load += new System.EventHandler(this.robot9y_Load);
            this.pan_install.ResumeLayout(false);
            this.pan_install.PerformLayout();
            this.pan_login.ResumeLayout(false);
            this.pan_login.PerformLayout();
            this.pan_control.ResumeLayout(false);
            this.pan_control.PerformLayout();
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
        private System.Windows.Forms.Button btn_folder;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Button btn_upgrade;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox txt_port;
        private System.Windows.Forms.Label lab_status;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.CheckBox checkBox2;
    }
}

