# coding:utf-8

import sys

import uiautomation as auto
import GetWin as GW

def text():
    try:
        # 超时时间
        auto.SetGlobalSearchTimeout(10)

        # 参数
        args = sys.argv[1]

        # 方式
        text_type = sys.argv[2]

        # 模型
        model_path = sys.argv[3]

        # 截图路径
        file_path = sys.argv[4]

        # 窗口
        win = GW.get_win(args)

        if win is not None:
            if text_type == 'text':
                # 文字结果
                text_result = win.GetWindowText()
                if text_result is None:
                    text_result = win.Name
                print(text_result)
            else:
                # 截图
                win.CaptureToImage(file_path)

                print("文本识别截图成功")
    except Exception as e:
        print("获取文本异常 {}".format(e))

# 文本
text()
