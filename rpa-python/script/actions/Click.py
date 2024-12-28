# coding:utf-8

import sys
import uiautomation as auto
from pywinauto import mouse as auto_mouse
import GetWin as GW

def click():
    try:
        # 超时时间
        auto.SetGlobalSearchTimeout(10)

        # 参数
        args = sys.argv[1]

        # 横坐标
        xPoint = sys.argv[2]

        # 纵坐标
        yPoint = sys.argv[3]

        # 窗口
        win = GW.get_win(args)

        # 点击
        if win is not None:
            print(win)
            if (xPoint != "0" or yPoint != "0"):
                # x, y = win.GetPosition()
                x = win.BoundingRectangle.left
                y = win.BoundingRectangle.top
                print("x======" + str(x))
                print("y======" + str(y))
                print("x + xPoint======" + str(x) + "-" + xPoint)
                print("y + yPoint======" + str(y) + "-" + yPoint)
                auto_mouse.click(button='left', coords=(int(x + int(xPoint)), int(y + int(yPoint))))
            else:
                win.Click()

        print("点击成功")
    except Exception as e:
        print("点击异常 {}".format(e))

# 启动
click()
