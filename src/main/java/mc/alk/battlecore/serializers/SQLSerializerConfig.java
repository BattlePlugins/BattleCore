package mc.alk.battlecore.serializers;

import mc.alk.battlecore.configuration.ConfigurationSection;
import mc.alk.battlecore.serializers.SQLSerializer.SQLType;
import mc.alk.battlecore.util.Log;
import mc.alk.mc.plugin.MCPlugin;

public class SQLSerializerConfig {

    public static void configureSQL(MCPlugin plugin, SQLSerializer sql, ConfigurationSection cs) {
        String type = cs.getString("type");
        String url = cs.getString("url");
        if (type != null && type.equalsIgnoreCase("sqlite")){
            url = plugin.getDataFolder().toString();
        }
        configureSQL(sql, type, url, cs.getString("db"),
                cs.getString("port"), cs.getString("username"), cs.getString("password"));
    }

    public static void configureSQL(SQLSerializer sql, String type, String urlOrPath,
            String db, String port, String user, String password) {
        try{
            if (db != null){
                sql.setDB(db);
            }
            if (type == null || type.equalsIgnoreCase("mysql")){
                sql.setType(SQLType.MYSQL);
                if (urlOrPath==null) urlOrPath = "localhost";
                if (port == null)  port = "3306";
                sql.setURL(urlOrPath);
                sql.setPort(port);
            } else { /// sqlite
                sql.setType(SQLType.SQLITE);
                sql.setURL(urlOrPath);
            }
            sql.setUsername(user);
            sql.setPassword(password);
            sql.init();
        } catch (Exception e){
            Log.err("Error configuring sql");
            Log.err("Error message = " + e.getMessage());
            e.printStackTrace();
        }
    }
}
