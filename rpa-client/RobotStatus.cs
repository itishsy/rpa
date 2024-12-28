using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace rpa_client
{
    enum RobotStatus
    {
        //新建
        New,
        //准备就绪
        Runnable,
        //运行中
        Running,
        //已关闭（没有机器人进程）
        Closed,
        //机器人异常
        RobotError,
        //客户端异常
        ClientError,
        //升级中
        Upgrading,
        //升级失败
        UpgradeFailed
    }
}
