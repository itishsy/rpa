# coding:utf-8

import sys
import uiautomation as auto
from pywinauto.application import Application
import time


def start():
    try:
        # 应用安装路径
        path = sys.argv[1]
        # 超时时间
        timeout = sys.argv[2]

        # 启动
        if path.find(",") >= 0:
            exe_path = r"" + path.split(",")[0]
            file_path = r"" + path.split(",")[1]
            app = Application(backend='uia').start(r'{} "{}"'.format(exe_path, file_path))
        else:
            app = Application(backend='uia').start(r'' + path)

        # 等待
        time.sleep(3)

        # 应用程序当前顶部窗口
        # top_win = app.top_window()

        # 打印
        # top_win.print_control_identifiers()

        print("启动成功")
    except Exception as e:
        print("启动应用异常 {}".format(e))


# 启动
start()
