package com.jborza.hnews.demo;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.load();

    private EnvConfig() {
        // Private constructor to prevent instantiation
    }

    public static String get(String key) {
        return dotenv.get(key);
    }
}