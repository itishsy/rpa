# coding:utf-8

import sys
import uiautomation as auto
import GetWin as GW

def input():
    try:
        # 超时时间
        auto.SetGlobalSearchTimeout(10)

        # 参数
        args = sys.argv[1]

        # 输入内容
        value = sys.argv[2]

        # 窗口
        win = GW.get_win(args)

        # 输入内容
        if win is not None:
            win.SendKeys(value)

        print("输入成功")
    except Exception as e:
        print("输入异常 {}".format(e))

# 输入
input()
