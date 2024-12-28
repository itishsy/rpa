import time

import uiautomation as auto
from pywinauto.application import Application
import easyocr

class WxAccountTest():

    def start(self):
        app = Application(backend='uia').connect(path="C:\Program Files (x86)\SHGJJ\gjj.exe")  # 链接
        # 等待
        time.sleep(10)
        # 打印
        # top_win = app.top_window()
        # top_win.print_control_identifiers(depth=None, filename=None)
        win_main = app.window(title="单位公积金网上业务办理系统 （直联22.2版）", class_name="Gjj")
        win_main.wait('ready', timeout=30)
        print('等待通过，当前新建连接窗口处于不可见状态')
        # 打印
        #win_main.print_control_identifiers(depth=None, filename=None)
        # 状态栏
        body = win_main.child_window(class_name="TfFrmMainBody")
        # 打印
        #body.print_control_identifiers(depth=None, filename=None)
        print(body.texts())
        # 状态栏
        statusBar = win_main.child_window(class_name="TStatusBar")
        # 打印
        statusBar.print_control_identifiers(depth=None, filename=None)
        children = statusBar.children()
        children_pic = children[4].capture_as_image()
        print(children_pic)
        children_pic.save('01.png')

        reader = easyocr.Reader(['ch_sim', 'en'], gpu=False, model_storage_directory=r'D:\seebon\rpa\python\model',
                                verbose=True, download_enabled=False)
        result = reader.readtext('01.png', detail=0)
        for res in result:
            print(res)

        print(statusBar.texts())
        print(statusBar.children_texts())
        print(children)
        print(children[0].texts())
        print(children[0].window_text())
        print(statusBar.get_properties())
        # 打印
        text = statusBar['Static']
        text0 = statusBar['Static0']
        text1 = statusBar['Static1']
        text2 = statusBar['Static2']
        text3 = statusBar['Static3']
        text4 = statusBar['Static4']
        text5 = statusBar['Static5']
        print(text.texts())
        print(text0.texts())
        print(text1.texts())
        print(text2.texts())
        print(text3.texts())
        print(text4.texts())
        print(text5.texts())


if __name__ == '__main__':
    ddd = WxAccountTest()
    ddd.start();
