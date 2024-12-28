# coding:utf-8

import sys

import uiautomation as auto
import GetWin as GW

def checkElement():
    try:
        # 超时时间
        auto.SetGlobalSearchTimeout(10)

        # 参数
        args = sys.argv[1]

        # 窗口
        win = GW.get_win(args)

        # 参数
        if win is not None and len(win.GetChildren()) >= 0:
            print("true")
        else:
            print("false")
    except Exception as e:
        print("检查元素异常 {}".format(e))

# 检查元素
checkElement()
