package com.author.example_mod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Main {
    public static final String MOD_ID = /*$ mod_id_string {*/"example_mod"/*$}*/;

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Hello from "+/*$ mod_name_string {*/"Example mod"/*$}*/+"!");
    }
}
