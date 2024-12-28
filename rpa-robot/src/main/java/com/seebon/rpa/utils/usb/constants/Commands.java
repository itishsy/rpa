package com.seebon.rpa.utils.usb.constants;

/**
 * define all commands in this place
 *
 * @author pengdeyi
 */
public final class Commands {

    /**
     * list all devices
     */
    public static final String LIST_ALL_DEVICE = "devcon find USB*";

    /**
     * disable device
     */
    public static final String DISABLE_DEVICE = "devcon disable \"@#\"";

    /**
     * restart device
     */
    public static final String RESTART_DEVICE = "devcon restart \"@#\"";

    /**
     * enable device
     */
    public static final String ENABLE_DEVICE = "devcon enable \"@#\"";

    /**
     * device status
     */
    public static final String STATUS_DEVICE = "devcon status \"@#\"";

    /**
     * device remove
     */
    public static final String REMOVE_DEVICE = "devcon remove \"@#\"";

    /**
     * device rescan
     */
    public static final String RESCAN_DEVICE = "devcon rescan";

    /**
     * find device
     */
    public static final String FIND_DEVICE = "devcon find \"@#\"";

    /**
     * get completed command....
     *
     * @param command
     * @param argument
     * @return
     */
    public static String parseCommand(String command, String argument) {
        return command.replace("#", argument);
    }
}
