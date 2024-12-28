1.部署需要安装的包
#pip install -i https://mirrors.aliyun.com/pypi/simple uiautomation
#pip install -i https://mirrors.aliyun.com/pypi/simple pywinauto
#pip install -i https://mirrors.aliyun.com/pypi/simple cmake
#pip install -i https://mirrors.aliyun.com/pypi/simple pypiwin32
#pip install -i https://mirrors.aliyun.com/pypi/simple packaging
#pip install -i https://mirrors.aliyun.com/pypi/simple psutil
#pip install -i https://mirrors.aliyun.com/pypi/simple pyautogui
#设置pip的镜像代理地址 在这个路径下创建 pip.ini
C:\Users\自己的用户\AppData\Roaming\pip\pip.ini
内容如下
[global]
index-url = https://mirrors.aliyun.com/pypi/simple
trusted-host = mirrors.aliyun.com
在rpa-python 目录下执行
pip install -r requirements.txt

2.注意点
python -m pip install --upgrade pip
pip install -i https://mirrors.aliyun.com/pypi/simple/ cmake 安装慢可以选这个

3.查询节点
  3.1、pane[ClassName='',Name='']/document[ClassName='',Name='',index='1']/button[ClassName='',Name='']/child[index=0]

  3.2、进入python安装目录 F:\installSoft\Python36\Scripts
       python automation.py -r -d 15 -t 0 -p  23808(进程id)
    3.3.1、常用控件:
        WindowControl: 窗口控件类
        PaneControl: 窗格控件类型
        ButtonControl: 按钮控制类型
        CheckBoxControl: 复选框控件类型
        ComboBoxControl: 组合框控件类型
        EditControl: 编辑控件类型
        ListControl: 列表控件类型
        ListItemControl: ListItem 控件类型
        MenuControl: 菜单控制类型
        MenuBarControl: 菜单栏控件类型
        MenuItemControl: 菜单项控件类型
        ScrollBarControl: 滚动条控件类型
        SliderControl: 滑块控制类型
        TabControl: 选项卡控件类型
        TabItemControl: TabItem 控件类型
        TableControl: 表控件类型
        TextControl: 文本控件类型
        TitleBarControl: 标题栏控件类型
        ToolBarControl: 工具栏控件类型
        ToolTipControl: 工具提示控件类型
        TreeControl: 树控件类型
        TreeItemControl: 树项控件类型
        AppBarControlAppBar: 控件类型
        CalendarControl: 日历控件类型
        DataGridControl: 数据网格控件类型
        GroupControl: 群控类型
        HeaderControl: 标题控件类型
        HeaderItemControl: HeaderItem: 控件类型
        HyperlinkControl: 超链接控制类型
        ImageControl: 图像控制类型
        DataItemControl: 数据项控件类型
        DocumentControl: 文件控制类型
        ProgressBarControl: ProgressBar: 控件类型
        RadioButtonControl: 单选按钮控件类型
        SemanticZoomControl: SemanticZoom控制类型
        SeparatorControl: 分离器控制类型
        SpinnerControl: 微调控制类型
        SplitButtonControl: 拆分按钮控件类型
        StatusBarControl: 状态栏控件类型
        ThumbControl: 拇指控制类型
    3.3.2、控件支持的参数如下：
        searchFromControl: 从哪个控件开始查找，如果为None，从根节点Desktop开始查找
        searchDepth: 搜索深度
        searchInterval: 搜索间隔
        foundIndex: 搜索到的满足搜索条件的控件索引，索引从1开始
        Name: 控件名字
        SubName: 控件部分名字
        RegexName: 使用re.match匹配符合正则表达式的名字，Name,SubName,RegexName只能使用一个，不能同时使用
        ClassName: 类名字
        AutomationId: 控件AutomationId
        ControlType: 控件类型
        Depth: 控件相对于searchFromControl的精确深度
        Compare: 自定义比较函数function(control: Control, depth: int)->bool

4、PaddlePaddle
    4.1、您的机器是CPU，请运行以下命令安装
        python -m pip install -i https://mirror.baidu.com/pypi/simple paddlepaddle
    4.2、解压包安装
        cd F:\installSoft\Python36
        pip install -r PaddleOCR-2.6.0/requirments.txt https://mirror.baidu.com/pypi/simple
        pip install PaddleOCR==2.6.0
    4.3、pip安装
        pip install -i https://mirror.baidu.com/pypi/simple opencv-contrib-python==4.5.4.60
        pip install -i https://mirror.baidu.com/pypi/simple paddleocr==2.6.0
        这里的安装很有可能会报错，一般都是缺少模块的问题，看报的错误缺少什么模块就用pip把相关模块安装好之后再安装就行了
        如果还是报错可以手动下载相关模块的shapely安装包完成安装，地址：https://www.lfd.uci.edu/~gohlke/pythonlibs/#shapely
    4.3、注意
        https://www.paddlepaddle.org.cn/install/quick?docurl=/documentation/docs/zh/install/pip/windows-pip.html
    4.4、指令
        paddleocr --image_dir D:\seebon\rpa\python\images\22.png --use_angle_cls true --use_gpu false


5、对于部分电脑无法直接运行管理员模式，可以至本地组策略禁用【用户账户控制：以管理员批准模式运行所有管理员】，
如果策略组不存在，可以使用执行gpedit.bat（以管理员模式运行）打补丁后再执行下边禁用操作
    5.1、按下Win + R键，输入gpedit.msc并按回车，打开本地组策略编辑器。
    5.2、导航到以下路径：计算机配置 > Windows 设置 > 安全设置 > 本地策略 > 安全选项。
    5.3、在右侧面板中找到“用户账户控制：以管理员批准模式运行所有管理员”策略。
    5.4、双击它，选择“已禁用”，然后点击“确定”。