namespace rpa_client
{
    partial class SyncApp
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.label1 = new System.Windows.Forms.Label();
            this.txt_client_app_id = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.txt_task_code = new System.Windows.Forms.TextBox();
            this.btn_move_task = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(25, 35);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(209, 12);
            this.label1.TabIndex = 0;
            this.label1.Text = "客户应用ID（表：robot_client_app）";
            // 
            // txt_client_app_id
            // 
            this.txt_client_app_id.AccessibleName = "";
            this.txt_client_app_id.Location = new System.Drawing.Point(27, 60);
            this.txt_client_app_id.Name = "txt_client_app_id";
            this.txt_client_app_id.Size = new System.Drawing.Size(272, 21);
            this.txt_client_app_id.TabIndex = 1;
            this.txt_client_app_id.Tag = "";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(25, 100);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(125, 12);
            this.label2.TabIndex = 2;
            this.label2.Text = "任务编码（taskCode）";
            // 
            // txt_task_code
            // 
            this.txt_task_code.AccessibleName = "";
            this.txt_task_code.Location = new System.Drawing.Point(27, 124);
            this.txt_task_code.Name = "txt_task_code";
            this.txt_task_code.Size = new System.Drawing.Size(272, 21);
            this.txt_task_code.TabIndex = 3;
            this.txt_task_code.Tag = "";
            // 
            // btn_move_task
            // 
            this.btn_move_task.Location = new System.Drawing.Point(187, 173);
            this.btn_move_task.Name = "btn_move_task";
            this.btn_move_task.Size = new System.Drawing.Size(112, 30);
            this.btn_move_task.TabIndex = 4;
            this.btn_move_task.Text = "任务迁移到此设备";
            this.btn_move_task.UseVisualStyleBackColor = true;
            this.btn_move_task.Click += new System.EventHandler(this.btn_move_task_Click);
            // 
            // SyncApp
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(328, 219);
            this.Controls.Add(this.btn_move_task);
            this.Controls.Add(this.txt_task_code);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.txt_client_app_id);
            this.Controls.Add(this.label1);
            this.Name = "SyncApp";
            this.Text = "设备变更";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox txt_client_app_id;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.TextBox txt_task_code;
        private System.Windows.Forms.Button btn_move_task;
    }
}