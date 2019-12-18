package mc.alk.battlecore.util;

import org.battleplugins.Platform;
import org.battleplugins.world.Location;

import java.util.StringTokenizer;

public class LocationUtil {

    public static Location fromString(String locStr) {
        StringTokenizer scan = new StringTokenizer(locStr,",");

        String world = scan.nextToken();
        double x = Double.parseDouble(scan.nextToken());
        double y = Double.parseDouble(scan.nextToken());
        double z = Double.parseDouble(scan.nextToken());

        float pitch = 0;
        float yaw = 0;

        if (scan.hasMoreTokens())
            pitch = Float.parseFloat(scan.nextToken());

        if (scan.hasMoreTokens())
            yaw = Float.parseFloat(scan.nextToken());

        return new Location(Platform.getPlatform().getWorld(world).orElseThrow(IllegalArgumentException::new), x, y, z, pitch, yaw);
    }

    public static String toString(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getPitch() + "," +
                location.getYaw();
    }
}
