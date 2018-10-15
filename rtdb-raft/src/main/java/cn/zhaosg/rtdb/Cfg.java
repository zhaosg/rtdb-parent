package cn.zhaosg.rtdb;

import cn.zhaosg.rtdb.raft.Member;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    public static List<Member> members() {
        if (!config.containsKey("members"))
            return null;
        String m = config.getString("members");
        if (StringUtils.isBlank(m))
            return null;
        String[] ss = m.split(",");
        List<Member> ms = new ArrayList<>();
        for (String hp : ss) {
            String[] hps = hp.split(":");
            ms.add(new Member(hps[0], Integer.valueOf(hps[1])));
        }
        return ms;
    }

    public static int port() {
        return config.getInt("port");
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
