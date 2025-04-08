package com.author.example_mod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Main {
    public static final String MOD_ID = "example_mod";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("Hello from MyMod!");
    }
}
