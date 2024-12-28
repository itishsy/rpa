import time


def findWin(app, sys):
    # 参数
    args = sys.argv[3]
    # 超时时间
    timeout = sys.argv[2]
    # 窗口
    win = None
    # 窗口参数
    argList = args.split("/")

    for arg in argList:
        tag = arg.split("[")[0]
        attr = arg.split("[")[1].split("]")[0]
        items = attr.split(",")
        kwargs = {}
        for item in items:
            attr_name = item.split("=")[0]
            attr_value = item.split("=")[1]
            kwargs[str(attr_name)] = str(attr_value).replace("'", "")
        print(kwargs)
        if (tag == "window"):
            if win is None:
                win = app.window(**kwargs)
            else:
                win = win.child_window(**kwargs)
            # 等待
            time.sleep(int(timeout))
            # 打印
            win.print_control_identifiers()

    return win
