package cn.zhaosg.rtdb;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cfg {
    private static Configuration config;

    public static void load() {
        try {
            Configurations configs = new Configurations();
            config = configs.properties("config.properties");
        } catch (ConfigurationException cex) {
        }
    }

    public static int port() {
        return config.getInt("port");
    }

    public static int port(int port) {
        config.setProperty("port", port);
        return port();
    }

    public static boolean isPort(String portStr) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(portStr);
        if (isNum.matches() && portStr.length() < 6 && Integer.valueOf(portStr) >= 1
                && Integer.valueOf(portStr) <= 65535) {
            return true;
        }
        return false;
    }

}
