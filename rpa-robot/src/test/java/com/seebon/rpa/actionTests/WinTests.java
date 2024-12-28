package com.seebon.rpa.actionTests;

import com.google.common.collect.Lists;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.ActionBaseTests;
import com.seebon.rpa.utils.python.PythonUtil;
import com.seebon.rpa.utils.python.listener.CommandExecOpenListener;

import java.util.List;

public class WinTests extends ActionBaseTests {

    public static void main(String[] args) {
        autonomousOCR();
    }

    private static void open() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Open.py");
        commandList.add("C:\\Program Files (x86)\\南京公积金证书签章助手工具包\\南京公积金证书签章助手.exe");

        PythonUtil.runCommand("打开应用", null, 30, commandList);
    }

    private static void start() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Open.py");
        commandList.add("C:\\Program Files (x86)\\Sheca\\UniClient\\UniClient.exe");

        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void closeAll() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Close.py");
        commandList.add("gjj.exe,UniClient.exe");

        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void keyboard() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Keyboard.py");

        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void text() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Text.py");
        commandList.add("window[class_name='Gjj',title='单位公积金网上业务办理系统 （直联22.2版）']/window[class_name='TmxOutlookBarPro']");
        commandList.add("D:\\seebon\\rpa\\python\\model");
        commandList.add("D:\\seebon\\rpa\\python\\images\\");
        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void click() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("F:\\workspace-spfcore\\rpa\\rpa-python\\script\\actions\\Click.py");
        commandList.add("window[ClassName='TfLoginFirst']/button[ClassName='TBitBtn',Name='    确定']");
        commandList.add("0");
        commandList.add("0");

        PythonUtil.runCommand("win点击", "点击成功", 30, commandList);
    }

    private static void screenshot() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Screenshot.py");
        commandList.add("pane[ClassName='Chrome_WidgetWin_0']/document[ClassName='Chrome_RenderWidgetHostHWND']/button[Name='登 录']");
        commandList.add("D:\\seebon\\rpa\\python\\images\\" + UUIDGenerator.uuidStringWithoutLine() + ".png");

        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void checkApp() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\CheckApp.py");
        commandList.add("potected.exe");

        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void checkElement() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\CheckElement.py");
        commandList.add("window[ClassName='TfLoginFirst']/button[ClassName='TBitBtn',Name='    确定']");

        PythonUtil.runCommand("元素检查", null, 30, commandList);
    }

    private static void input() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("E:\\workspace-spfcore\\rpa9y\\rpa-python\\script\\actions\\Input.py");
        commandList.add("window[class_name='TForm']/window[class_name='TEdit']");
        commandList.add("143a5f1b");
        PythonUtil.runCommand(commandList, 30, new CommandExecOpenListener());
    }

    private static void autonomousOCR() {
        List<String> commandList = Lists.newArrayList();
        commandList.add("E:\\installSoft\\Python36\\python.exe");
        commandList.add("F:\\workspace-spfcore\\rpa\\rpa-python\\script\\actions\\OcrIdentify.py");
        commandList.add("D:\\seebon\\rpa\\python\\model");
        commandList.add("D:\\seebon\\rpa\\python\\images\\22.png");
        commandList.add("0");
        commandList.add("ch_sim,en");
        List<String> texts = PythonUtil.runCommand("OCR文字识别", null, 44, commandList);
        System.out.println("text=" + texts);
    }
}
