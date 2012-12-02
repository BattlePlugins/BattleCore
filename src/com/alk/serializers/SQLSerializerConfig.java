package com.alk.serializers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.alk.executors.CustomCommandExecutor.InvalidArgumentException;
import com.alk.serializers.SQLSerializer.SQLType;
import com.alk.util.Log;

public class SQLSerializerConfig {
	
	public static void configureSQL(JavaPlugin plugin, SQLSerializer sql, ConfigurationSection cs) {
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@  " + sql);

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
//			Log.debug("########################################  " + db);
			if (db != null){
				db = wrapDBName(db);
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

	public static String wrapDBName(String db) {
//		Log.debug("1###################################  db = " + db);
		try {
			Integer.valueOf(db);
//			db = "`"+db+"`";
//			Log.debug("###################################  db = " + db);
			throw new InvalidArgumentException("Database name cannot be all numbers!");
		} catch (Exception e){
		}
		return db;
	}

}
