package mc.alk.battlecore.util;

import mc.alk.battlecore.configuration.ConfigurationSection;
import mc.alk.mc.MCLocation;
import mc.alk.mc.MCPlatform;
import mc.alk.mc.StringLocation;
import mc.alk.mc.block.MCBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class SerializerUtil {

    public static final boolean TESTING = false;

    public static HashMap<String, String> createSaveableLocations(Map<Integer, MCLocation> mlocs) {
        HashMap<String,String> locations = new HashMap<String,String>();
        for (Integer key: mlocs.keySet()){
            String s = SerializerUtil.getLocString(mlocs.get(key));
            locations.put(key + "",s);
        }
        return locations;
    }


    public static void expandMapIntoConfig(ConfigurationSection conf, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                ConfigurationSection section = new ConfigurationSection();
                Map<String, Object> subMap = (Map<String, Object>) entry.getValue();
                expandMapIntoConfig(section, subMap);
            } else {
                conf.set(entry.getKey(), entry.getValue());
            }
        }
    }

    public static MCLocation getLocation(String locstr) throws IllegalArgumentException {
        //		String loc = node.getString(nodestr,null);
        if (locstr == null)
            throw new IllegalArgumentException("Error parsing location. Location string was null");
        StringTokenizer scan = new StringTokenizer(locstr,",");
        String world = scan.nextToken();
        float x = Float.valueOf(scan.nextToken());
        float y = Float.valueOf(scan.nextToken());
        float z = Float.valueOf(scan.nextToken());
        float yaw = 0, pitch = 0;
        if (scan.hasMoreTokens()){yaw = Float.valueOf(scan.nextToken());}
        if (scan.hasMoreTokens()){pitch = Float.valueOf(scan.nextToken());}

        if (TESTING)
            return MCPlatform.getLocation(world, x, y, z);

        return new StringLocation(world, x, y, z, pitch, yaw);
    }

    public static String getLocString(MCLocation l){
        if (l == null)
            return null;

        if (TESTING && l.getWorld() == null)
            return "null," + l.getX() + "," + l.getY() + "," + l.getZ()+","+l.getYaw()+","+l.getPitch();


        return l.getWorld().getName() + "," + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
    }

    public static String getBlockLocString(MCLocation l){
        if (l == null) return null;
        return l.getWorld().getName() +"," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
    }

    public static Map<Integer, MCLocation> parseLocations(ConfigurationSection cs) throws IllegalArgumentException {
        if (cs == null)
            return null;

        Map<Integer, MCLocation> locs = new HashMap<>();
        Set<String> indices = cs.getKeys(false);
        for (String locIndexStr : indices){
            MCLocation loc = SerializerUtil.getLocation(cs.getString(locIndexStr));
            int i = Integer.parseInt(locIndexStr);
            locs.put(i, loc);
        }
        return locs;
    }

    public static String getBlockString(MCBlock b) {
        return b.getType() + ";" + getBlockLocString(b.getLocation());
    }

    public static MCBlock parseBlock(String string) {
        String[] split = string.split(";");
        MCLocation l = getLocation(split[2]);
        return l.getWorld().getBlockAt(l);
    }
}
