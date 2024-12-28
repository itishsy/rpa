# coding:utf-8

import sys
import time

import uiautomation as auto
import GetWin as GW

def screenshot():
    try:
        # 超时时间
        auto.SetGlobalSearchTimeout(10)

        # 参数
        args = sys.argv[1]

        # 截图文件路径
        file_path = sys.argv[2]

        # 窗口
        win = GW.get_win(args)

        # 截图
        if win is not None:
            win.CaptureToImage(file_path)

        print("截图成功")
    except Exception as e:
        print("获取文本异常 {}".format(e))

# 截图
screenshot()
