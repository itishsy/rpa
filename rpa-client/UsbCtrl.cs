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
    public partial class UsbCtrl : Form
    {
        public UsbCtrl()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            try
            {
                string str = this.comboBox1.SelectedItem.ToString();
                if (!string.IsNullOrEmpty(str))
                {
                    this.button1.Text = "正在激活中...";
                    this.comboBox1.Enabled = false;
                    this.button1.Enabled = false;
                    MessageBox.Show(Utils.httpPost("http://localhost:" + Constant.Port.ToString() + "/api/robot/start/usb/open/" + str));
                }
                else
                {
                    MessageBox.Show("请选择要打开的端口号");
                }
            }
            catch (Exception ex)
            {
                Logger.error("绑定异常", ex);
                int num = (int)MessageBox.Show("绑定异常。" + ex.Message);
            }
            finally
            {
                this.button1.Text = "激活";
                this.comboBox1.Enabled = true;
                this.button1.Enabled = true;
            }
        }
    }
}
