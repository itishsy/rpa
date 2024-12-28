# coding:utf-8

import sys
import time
import uiautomation as auto
from pywinauto.application import Application
from pywinauto import mouse as auto_mouse


def mouse():
    try:
        #鼠标操作类型
        mouse_type = sys.argv[1]
        # 横坐标
        x_p = sys.argv[2]
        # 纵坐标
        y_p = sys.argv[3]

        #左键点击
        if mouse_type == 'leftClick':
            auto_mouse.click(button='left', coords=(int(x_p), int(y_p)))
            # 等待
            time.sleep(2)
            print("点击成功")
        #滚动鼠标
        elif mouse_type == 'scroll':
            wheel_dist = sys.argv[4]
            time.sleep(2)
            auto_mouse.scroll(coords=(int(x_p), int(y_p)), wheel_dist=int(wheel_dist))
            print("滚动成功")
    except Exception as e:
        print("鼠标操作异常 {}".format(e))


# 启动
mouse()