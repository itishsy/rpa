package com.seebon.rpa.utils.usb.core;

import com.seebon.rpa.utils.usb.constants.Commands;
import com.seebon.rpa.utils.usb.listener.CommandExec4DisableListener;
import com.seebon.rpa.utils.usb.listener.CommandExec4EnableListener;
import com.seebon.rpa.utils.usb.listener.CommandExec4RestartListener;
import com.seebon.rpa.utils.usb.listener.CommandExecOutputListener;
import com.seebon.rpa.utils.usb.utils.ShellUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * usb device command center
 *
 * @author pengdeyi
 */
@Slf4j
public class UsbCommandCore {

    public static void enableCommand(String orginal) {
        ShellUtils.runCommand(Commands.parseCommand(Commands.ENABLE_DEVICE, orginal), new CommandExec4EnableListener());
    }

    public static void disableCommand(String orginal, CommandExec4DisableListener listener) {
        ShellUtils.runCommand(Commands.parseCommand(Commands.DISABLE_DEVICE, orginal), listener);
    }

    public static void restartCommand(String orginal) {
        ShellUtils.runCommand(Commands.parseCommand(Commands.RESTART_DEVICE, orginal), new CommandExec4RestartListener());
    }

    public static void listCommand(CommandExecOutputListener listener) {
        ShellUtils.runCommand(Commands.LIST_ALL_DEVICE, listener);
    }

    public static void removeCommand(String orginal, CommandExecOutputListener listener) {
        ShellUtils.runCommand(Commands.parseCommand(Commands.REMOVE_DEVICE, orginal), listener);
    }

    public static void rescanCommand(CommandExecOutputListener listener) {
        ShellUtils.runCommand(Commands.RESCAN_DEVICE, listener);
    }
}
