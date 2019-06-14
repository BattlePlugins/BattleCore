package mc.alk.v1r7.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.text.SimpleDateFormat;

public class TimeUtil {

	static final String version = "1.2";
	static long lastCheck = 0;

	public static void testClock(Plugin plugin) {
		final long start = System.currentTimeMillis();
		if (start - lastCheck < 10000)
			return;
		lastCheck = start;
		final int seconds = 2;

		final double millis = seconds * 1000;
		final int nTicks = 20 * seconds;
		Runnable task = new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();

				long elapsedTime = now - start;
				double mult = millis/elapsedTime;
				if (mult < 0.2){
					mult = 0.2;}
			}
		};

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, nTicks);
	}

	public static String convertToString(int minutes, int seconds){
		return convertSecondsToString(minutes*60+seconds);
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
			has=true;
			sb.append("&6"+ d +"&e "+dayOrDays(d));
		}
		if (h > 0) {
			has=true;
			sb.append("&6"+ h +"&e "+hourOrHours(h));
		}
		if (m > 0) {
			has=true;
			sb.append("&6"+ m +"&e "+minOrMins(m));
		}
		if (s > 0) {
			has=true;
			sb.append("&6"+ s +"&e "+secOrSecs(s));
		}
		if (!has){
			return "&60";
		}
		return sb.toString();
	}

	public static String convertToString(long t){
		t = t / 1000;
		return convertSecondsToString(t);
	}

	public static String dayOrDays(long t){
		return t > 1 || t == 0? "days" : "day";
	}

	public static String hourOrHours(long t){
		return t > 1 || t ==0 ? "hours" : "hour";
	}

	public static String minOrMins(long t){
		return t > 1 || t == 0? "minutes" : "minute";
	}
	public static String secOrSecs(long t){
		return t > 1 || t == 0? "sec" : "secs";
	}

	public static String convertLongToDate(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd hh:mm");
		return sdf.format(time);
	}

	public static String convertLongToSimpleDate(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		return sdf.format(time);
	}

	public static String PorP(int size) {
		return size == 1 ? "person" : "people";
	}
}
