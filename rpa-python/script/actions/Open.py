# coding:utf-8

import sys
import os
import time
import subprocess as process


def open():
    try:
        # 应用安装路径
        path = sys.argv[1]
        # 分割
        paths = path.split(",")
        for name in paths:
            # 打开
            if os.path.isabs(r'' + name):
                if not os.path.exists(r'' + name):
                    continue
                    print("不存在[%s]" % name)
            process.Popen(r'' + name +' --force-renderer-accessibility')
            # 等待
            time.sleep(1)
        print("打开成功")
    except Exception as e:
        print("打开应用异常 {}".format(e))


# 打开
open()
