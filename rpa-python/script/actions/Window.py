import sys
import GetWin as GW


def window():
    try:
        #窗口元素
        windowArgs = "window[ClassName='Tfrm_MainFrame']"
        # 操作类型
        actionType = "maximizing"

        # 窗口
        win = GW.get_win(windowArgs)

        #最大化窗口
        if actionType == 'maximizing':
            win.Maximize()
            print("最大化窗口成功")
        #最小化窗口
        elif actionType == 'minimize':
            win.Minimize()
            print("最小化窗口成功")
        #激活窗口
        elif actionType == 'active':
            win.SetFocus()
            print("激活窗口成功")
    except Exception as e:
        print("窗口操作异常 {}".format(e))


# 启动
window()