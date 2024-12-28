# coding:utf-8

import sys
import os


def close():
    try:
        # 应用启动名称
        path = sys.argv[1]
        # 分割
        paths = path.split(",")
        for name in paths:
            # 根据进程名杀死进程
            pro = 'taskkill /f /im %s' % name
            os.system(pro)

        print("关闭成功")
    except Exception as e:
        print("关闭应用异常 {}".format(e))

# 关闭
close()
