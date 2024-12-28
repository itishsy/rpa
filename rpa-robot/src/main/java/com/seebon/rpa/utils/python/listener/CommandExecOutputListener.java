package com.seebon.rpa.utils.python.listener;

import java.util.List;

/**
 * The command Listener
 *
 * @author pengdeyi
 */
public interface CommandExecOutputListener {

    /**
     * run success message
     *
     * @param lines
     */
    void onSuccess(List<String> lines);

    /**
     * run failed message
     *
     * @param lines
     */
    void onError(List<String> lines);
}
