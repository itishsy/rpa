package com.seebon.rpa.context;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Semaphore;

@Slf4j
@Component
public class DebugContext {
    public static final Semaphore lock = new Semaphore(0);

    public static String command = null;

    public static final Map<String, Object> stepVariables = Maps.newHashMap();
}
