package com.boogipop.easyrasp.utils;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RaspLogger implements BasicLogger {
    private static final Logger logger = Logger.getLogger(RaspLogger.class.getName());
    public static void init(){
        String logDir="logs";
        Path logPath = Paths.get(logDir);
        boolean exists = Files.exists(logPath);
        if (exists){
            System.out.println("Logs directory already exist!");
        }
        else {
            try{
                Files.createDirectory(logPath);
                FileHandler fileHandler = new FileHandler("logs/rasp.log", 1024 * 1024, 3, true);
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                logger.addHandler(fileHandler);
            }
            catch (Exception e){
               e.printStackTrace();
            }
        }
    }

    @Override
    public void trace(Object... arg) {

    }

    @Override
    public void warn(Object... arg) {
        logger.setLevel(Level.WARNING);
        String msg= (String) arg[0];
        Exception exception= (Exception) arg[1];
        logger.warning(String.format("Warning :\n%s\n%s",msg,exception.getMessage()));

    }

    @Override
    public void info(Object... arg) {
        logger.setLevel(Level.INFO);
        if (arg.length>=2) {
            String msg = (String) arg[0];
            Exception exception = (Exception) arg[1];
            logger.info(String.format("INFO :\n%s\n%s", msg, exception.getMessage()));
        }
        else {
            logger.info(String.format("INFO :\n%s\n",arg[0]));

        }

    }

    @Override
    public void error(Object... arg) {
        logger.setLevel(Level.SEVERE);
        String msg= (String) arg[0];
        Exception exception= (Exception) arg[1];
        logger.severe(String.format("Error :\n%s\n%s",msg,exception.getMessage()));
    }
}
