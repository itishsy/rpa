import pyautogui
import time
import sys
import uiautomation as auto


def keyboard():
    try:
        # 超时时间
        auto.SetGlobalSearchTimeout(10)

        # 输入内容
        value = sys.argv[1]
        for char in value:
            pyautogui.typewrite(char)
            time.sleep(0.1)

        print("敲入成功")
    except Exception as e:
        print("敲入异常 {}".format(e))


# 敲入
keyboard()
