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
    public partial class UsbSetting : Form
    {

        public UsbSetting()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                Utils.httpPost(string.Format("http://localhost:{0}/api/robot/start/usb/bing/{1}", Constant.Port.ToString(), this.comboBox1.SelectedItem.ToString()));
                this.reloadList();
            }
            catch (Exception ex)
            {
                Logger.error("绑定异常", ex);
                MessageBox.Show("绑定异常。" + ex.Message);
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            try
            {
                Utils.httpPost(string.Format("http://localhost:{0}/api/robot/start/usb/init", Constant.Port.ToString()));
                this.reloadList();
                int num = (int)MessageBox.Show("初始化完成");
            }
            catch (Exception ex)
            {
                Logger.error("初始化异常", ex);
                MessageBox.Show("初始化异常。" + ex.Message);
            }
        }

        private void reloadList()
        {
            string str1 = Utils.httpPost(string.Format("http://localhost:{0}/api/robot/start/usb/data", Constant.Port.ToString()));
            this.listBox1.Items.Clear();
            if (string.IsNullOrWhiteSpace(str1) || str1.IndexOf(";") < 1)
            {
                this.listBox1.Items.Add((object)"未绑定");
            }
            else
            {
                string[] strArray = str1.Split(';');
                for (int index = 0; index < strArray.Length; ++index)
                {
                    string str2 = strArray[index].Split('|')[0];
                    this.listBox1.Items.Add((object)strArray[index]);
                }
            }
        }

        private void UsbSetting_Load(object sender, EventArgs e)
        {
            try
            {
                this.reloadList();
            }
            catch(Exception ex)
            {
                Logger.error("初始化启动异常", ex);
                MessageBox.Show("初始化启动异常。" + ex.Message);
            }
        }
    }
}
