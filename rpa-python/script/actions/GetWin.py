import uiautomation as auto
import Log as log

def get_win(args: str):
    arg_list = args.split("/")
    win = None
    auto.SetGlobalSearchTimeout(10)
    for arg in arg_list:
        tag = arg.split("[")[0]
        kwargs, index = get_kwargs(arg)
        log.logger.info("kwargs=%s", kwargs)

        if tag == "window":
            if win is None and index is None:
                win = auto.WindowControl(**kwargs)
            elif win is None and index is not None:
                win = auto.WindowControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.WindowControl(foundIndex=index, **kwargs)
            else:
                win = win.WindowControl(**kwargs)
        elif tag == "pane":
            if win is None and index is None:
                win = auto.PaneControl(**kwargs)
            elif win is None and index is not None:
                win = auto.PaneControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.PaneControl(foundIndex=index, **kwargs)
            else:
                win = win.PaneControl(**kwargs)
        elif tag == "button":
            if win is None and index is None:
                win = auto.ButtonControl(**kwargs)
            elif win is None and index is not None:
                win = auto.ButtonControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.ButtonControl(foundIndex=index, **kwargs)
            else:
                win = win.ButtonControl(**kwargs)
        elif tag == "edit":
            if win is None and index is None:
                win = auto.EditControl(**kwargs)
            elif win is None and index is not None:
                win = auto.EditControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.EditControl(foundIndex=index, **kwargs)
            else:
                win = win.EditControl(**kwargs)
        elif tag == "document":
            if win is None and index is None:
                win = auto.DocumentControl(**kwargs)
            elif win is None and index is not None:
                win = auto.DocumentControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.DocumentControl(foundIndex=index, **kwargs)
            else:
                win = win.DocumentControl(**kwargs)
        elif tag == "text":
            if win is None and index is None:
                win = auto.TextControl(**kwargs)
            elif win is None and index is not None:
                win = auto.TextControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.TextControl(foundIndex=index, **kwargs)
            else:
                win = win.TextControl(**kwargs)
        elif tag == "list":
            if win is None and index is None:
                win = auto.ListControl(**kwargs)
            elif win is None and index is not None:
                win = auto.ListControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.ListControl(foundIndex=index, **kwargs)
            else:
                win = win.ListControl(**kwargs)
        elif tag == "listItem":
            if win is None and index is None:
                win = auto.ListItemControl(**kwargs)
            elif win is None and index is not None:
                win = auto.ListItemControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.ListItemControl(foundIndex=index, **kwargs)
            else:
                win = win.ListItemControl(**kwargs)
        elif tag == "checkBox":
            if win is None and index is None:
                win = auto.CheckBoxControl(**kwargs)
            elif win is None and index is not None:
                win = auto.CheckBoxControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.CheckBoxControl(foundIndex=index, **kwargs)
            else:
                win = win.CheckBoxControl(**kwargs)
        elif tag == "comboBox":
            if win is None and index is None:
                win = auto.ComboBoxControl(**kwargs)
            elif win is None and index is not None:
                win = auto.ComboBoxControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.ComboBoxControl(foundIndex=index, **kwargs)
            else:
                win = win.ComboBoxControl(**kwargs)
        elif tag == "radioButton":
            if win is None and index is None:
                win = auto.RadioButtonControl(**kwargs)
            elif win is None and index is not None:
                win = auto.RadioButtonControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.RadioButtonControl(foundIndex=index, **kwargs)
            else:
                win = win.RadioButtonControl(**kwargs)
        elif tag == "image":
            if win is None and index is None:
                win = auto.ImageControl(**kwargs)
            elif win is None and index is not None:
                win = auto.ImageControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.ImageControl(foundIndex=index, **kwargs)
            else:
                win = win.ImageControl(**kwargs)
        elif tag == "hyperlink":
            if win is None and index is None:
                win = auto.HyperlinkControl(**kwargs)
            elif win is None and index is not None:
                win = auto.HyperlinkControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.HyperlinkControl(foundIndex=index, **kwargs)
            else:
                win = win.HyperlinkControl(**kwargs)
        elif tag == "scrollBar":
            if win is None and index is None:
                win = auto.ScrollBarControl(**kwargs)
            elif win is None and index is not None:
                win = auto.ScrollBarControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.ScrollBarControl(foundIndex=index, **kwargs)
            else:
                win = win.ScrollBarControl(**kwargs)
        elif tag == "statusBar":
            if win is None and index is None:
                win = auto.StatusBarControl(**kwargs)
            elif win is None and index is not None:
                win = auto.StatusBarControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.StatusBarControl(foundIndex=index, **kwargs)
            else:
                win = win.StatusBarControl(**kwargs)
        elif (tag == "header"):
            if win is None and index is None:
                win = auto.HeaderControl(**kwargs)
            elif win is None and index is not None:
                win = auto.HeaderControl(foundIndex=index, **kwargs)
            elif win is not None and index is not None:
                win = win.HeaderControl(foundIndex=index, **kwargs)
            else:
                win = win.HeaderControl(**kwargs)
        elif tag == "parent":
            if win is not None:
                win = win.GetParentControl()
        elif tag == "child":
            if win is None:
                win = auto.GetChildren()[index]
            else:
                win = win.GetChildren()[index]
    return win


def get_kwargs(arg):
    kwargs = {}
    index = None
    attr = ""
    split_ = arg.split("[")
    if len(split_) > 1:
        attr = split_[1].split("]")[0]
    if attr != "":
        items = []
        if (is_in(attr, "Name") and is_in(attr, "Class")) or (is_in(attr, "Name") and is_in(attr, "index")) or (
                is_in(attr, "Class") and is_in(attr, "index")):
            items = attr.split(",")
        else:
            items = [attr]
        for item in items:
            attr_name = item.split("=")[0]
            attr_value = item.split("=")[1]
            if attr_name == "index":
                index = int(attr_value)
                continue
            kwargs[str(attr_name)] = str(attr_value).replace("'", "")

    return kwargs, index


def is_in(full_str, sub_str):
    try:
        full_str.index(sub_str)
        return True
    except ValueError:
        return False
