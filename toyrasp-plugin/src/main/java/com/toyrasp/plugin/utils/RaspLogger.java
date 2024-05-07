package com.toyrasp.plugin.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RaspLogger implements BasicLogger {
    private static final Logger LOGGER = Logger.getLogger(RaspLogger.class.getName());

    public void init() {
        String logDir = "logs";
        Path logPath = Paths.get(logDir);
        boolean exists = Files.exists(logPath);
        if (exists) {
            System.out.println("Logs directory already exist!");
        } else {
            try {
                Files.createDirectory(logPath);
                FileHandler fileHandler = new FileHandler("logs/rasp.log", 1024 * 1024, 3, true);
                fileHandler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fileHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void trace(Object... arg) {

    }

    @Override
    public void warn(Object... arg) {
        LOGGER.setLevel(Level.WARNING);
        String msg = (String) arg[0];
        Exception exception = (Exception) arg[1];
        LOGGER.warning(String.format("Warning :\n%s\n%s", msg, exception.getMessage()));
    }

    @Override
    public void info(Object... arg) {
        LOGGER.setLevel(Level.INFO);
        if (arg.length >= 2) {
            String msg = (String) arg[0];
            Exception exception = (Exception) arg[1];
            LOGGER.info(String.format("INFO :\n%s\n%s", msg, exception.getMessage()));
        } else {
            LOGGER.info(String.format("INFO :\n%s\n", arg[0]));

        }
    }

    @Override
    public void error(Object... arg) {
        LOGGER.setLevel(Level.SEVERE);
        String msg = (String) arg[0];
        Exception exception = (Exception) arg[1];
        LOGGER.severe(String.format("Error :\n%s\n%s", msg, exception.getMessage()));
    }
}
