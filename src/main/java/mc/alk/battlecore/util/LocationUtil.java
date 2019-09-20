package mc.alk.battlecore.util;

import mc.alk.mc.MCLocation;
import mc.alk.mc.MCPlatform;

public class LocationUtil {

    public static MCLocation fromString(String locStr) {
        String[] split = locStr.split(",");

        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);

        float pitch = 0;
        float yaw = 0;

        if (locStr.length() >= 6) {
            pitch = Float.parseFloat(split[4]);
            yaw = Float.parseFloat(split[5]);
        }

        return MCPlatform.getLocation(split[0], x, y, z, pitch, yaw);
    }

    public static String toString(MCLocation location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getPitch() + "," +
                location.getYaw();
    }
}
