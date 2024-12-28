# coding:utf-8

import sys
import psutil


def CheckApp():
    try:
        # 应用名称
        exe_name = sys.argv[1]

        # check if exe_name is open
        isOpen = exe_name in (i.name() for i in psutil.process_iter())

        print(isOpen)

        return
    except Exception as e:
        print("检查应用异常 {}".format(e))


# 检查应用
CheckApp()
