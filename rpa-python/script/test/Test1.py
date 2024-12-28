import win32clipboard  as w

import uiautomation as auto
import win32con
from pywinauto.application import Application
import script.config.Log as log
from pywinauto.timings import wait_until
import time

def start():
    app = Application(backend='uia').start(r'C:\Program Files (x86)\SHGJJ\gjj.exe')  # 启动
    # app = Application(backend='uia').connect(path="C:\Program Files (x86)\SHGJJ\gjj.exe")  # 链接
    # 等待
    time.sleep(10)
    # 打印
    top_win = app.top_window()
    top_win.print_control_identifiers(depth=None, filename=None)
    # 主窗口
    login_main = app.window(class_name='TfLoginFirst')
    # 打印
    login_main.print_control_identifiers(depth=None, filename=None)
    # 点击-确定
    submitBtn = login_main.child_window(title="    确定", class_name="TBitBtn")  # 确定窗口
    submitBtn.click_input()

    # 密码窗口
    pass_main = app.window(class_name='TForm')
    # 打印
    pass_main.print_control_identifiers(depth=None, filename=None)
    # 输入密码
    password = pass_main.child_window(class_name="TEdit")
    password.type_keys('^a').type_keys('143a5f1b', with_spaces=True)
    # 点击-确定
    loginBtn = pass_main.child_window(title="确定", class_name="TButton")
    loginBtn.click_input()
    # 关闭
    # app.kill()

    win_main = app.window(title="单位公积金网上业务办理系统 （直联22.2版）", class_name="Gjj")
    win_main.wait('ready', timeout=30)
    print('等待通过，当前新建连接窗口处于不可见状态')
    # 打印
    win_main.print_control_identifiers(depth=None, filename=None)
    # 状态栏
    statusBar = win_main.child_window(class_name="TStatusBar")
    print(statusBar.texts())


start()
