package mc.alk.battlecore.util;

import java.text.SimpleDateFormat;

public class TimeUtil {

    static final String version = "1.1";

    public static String convertToString(int minutes, int seconds){
        return convertSecondsToString(minutes * 60 + seconds);
    }

    public static String convertSecondsToString(long t){
        long s = t % 60;
        t /= 60;
        long m = t %60;
        t /=60;
        long h = t % 24;
        t /=24;
        long d = t;

        boolean has = false;
        StringBuilder sb = new StringBuilder();
        if (d > 0) {
            has = true;
            sb.append(d).append(" ").append(dayOrDays(d)).append(" ");
        }
        if (h > 0) {
            has = true;
            sb.append(h).append("&e ").append(hourOrHours(h)).append(" ");
        }
        if (m > 0) {
            has = true;
            sb.append(m).append(" ").append(minOrMins(m)).append(" ");
        }
        if (s > 0) {
            has = true;
            sb.append(s).append(" ").append(secOrSecs(s)).append(" ");
        }
        if (!has){
            return "0";
        }
        return sb.toString();
    }

    public static String convertToString(long t){
        t = t / 1000;
        return convertSecondsToString(t);
    }

    public static String convertSecondsToShortString(long t){
        long s = t % 60;
        t /= 60;
        long m = t %60;
        t /= 60;
        long h = t % 24;
        t /= 24;
        long d = t;

        boolean has = false;
        StringBuilder sb = new StringBuilder();
        if (d > 0) {
            has = true;
            sb.append(d).append("d ");
        }
        if (h > 0) {
            has = true;
            sb.append(h).append("h ");
        }
        if (m > 0) {
            has = true;
            sb.append(m).append("m ");
        }
        if (s > 0) {
            has = true;
            sb.append(s).append("s ");
        }
        if (!has){
            return "";
        }
        return sb.toString();
    }

    public static String convertToShortString(long t){
        t = t / 1000;
        return convertSecondsToShortString(t);
    }

    private static String dayOrDays(long t){
        return t > 1 || t == 0 ? "days" : "day";
    }

    private static String hourOrHours(long t){
        return t > 1 || t == 0 ? "hours" : "hour";
    }

    private static String minOrMins(long t){
        return t > 1 || t == 0 ? "minutes" : "minute";
    }

    private static String secOrSecs(long t){
        return t > 1 || t == 0 ? "second" : "seconds";
    }

    public static String convertLongToDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm:ss");
        return sdf.format(time);
    }
}
