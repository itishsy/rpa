import uiautomation as auto
from pywinauto.application import Application
import time

def start():
    program_path = r"E:\installSoft\WPS Office\11.1.0.14036\office6\wps.exe"
    file_path    = r"D:\test.xls"
    app = Application(backend='uia').start(r'{} "{}"'.format(program_path, file_path))
    # app = Application(backend='uia').connect(path="C:\Program Files (x86)\SHGJJ\gjj.exe")  # 链接
    # 等待
    time.sleep(10)
    # 打印
    top_win = app.top_window()
    top_win.print_control_identifiers(depth=None, filename=None)



start()
