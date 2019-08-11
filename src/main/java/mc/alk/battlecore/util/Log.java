package mc.alk.battlecore.util;

import mc.alk.battlecore.controllers.MessageController;

public class Log {

    public static void info(String msg){
            System.out.println(colorChat(msg));
    }

    public static void warn(String msg){
            System.err.println(colorChat(msg));
    }

    public static void err(String msg){
            System.err.println(colorChat(msg));
    }

    public static String colorChat(String msg) {
        return msg.replace("&", String.valueOf(MessageController.COLOR_MC_CHAR));
    }

    public static void debug(String string) {
        System.out.println(string);
    }
}
