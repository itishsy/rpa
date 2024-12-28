using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace rpa_client
{
    public partial class SyncApp : Form
    {
        public SyncApp()
        {
            InitializeComponent();
        }
        
        private void btn_move_task_Click(object sender, EventArgs e)
        {
            string clientAppId = txt_client_app_id.Text;
            string taskCode = txt_task_code.Text;
            try
            {
                string url = string.Format(Constant.CHANGE_ROBOT_CLIENT_APP, Context.Server);
                string jsonBody = "{\"id\":\"" + clientAppId + "\", \"machineCode\":\"" + Context.SeqNo + "\", \"taskCode\":\"" + taskCode + "\"}";

                if (string.IsNullOrEmpty(taskCode))
                    jsonBody = "{\"id\":\"" + clientAppId + "\", \"machineCode\":\"" + Context.SeqNo + "\"}";
                Logger.info("设备迁移不可用。url：" + url);
                string str = Utils.httpPost(url, AppConfig.get(AppConfig.RPA_TOKEN), jsonBody);
                MessageBox.Show("完成。info："+ str);
                //MessageBox.Show("设备迁移不可用。");
            }
            catch (Exception ex)
            {
                MessageBox.Show("设备迁移发生了异常。info："+ex.Message);
            }
        }
    }
}
