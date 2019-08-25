package mc.alk.battlecore.metrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import mc.alk.battlecore.configuration.Configuration;
import mc.alk.mc.MCServer;
import mc.alk.mc.logger.MCLogger;
import mc.alk.mc.plugin.MCPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

/**
 * bStats collects some data for plugin authors.
 *
 * Check out https://bStats.org/ to learn more about bStats!
 */
public class Metrics {

    private MCPlugin plugin;

    // The version of this bStats class
    public static final int B_STATS_VERSION = 1;

    // The url to which the data is sent
    private static final String URL = "https://bStats.org/submitData/bukkit";
    // Should failed requests be logged?
    private static boolean logFailedRequests;
    // Should the sent data be logged?
    private static boolean logSentData;
    // Should the response text be logged?
    private static boolean logResponseStatusText;
    // The uuid of the server
    private static String serverUUID;
    // A list with all custom charts
    private final List<CustomChart> charts;

    // Is bStats enabled on this server?
    private boolean enabled;

    public Metrics(MCPlugin plugin) {
        this.plugin = plugin;
        this.charts = new ArrayList<>();

        // Get the config file
        File bStatsFolder = new File(plugin.getDataFolder(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        Configuration config = new Configuration(configFile);

        // Check if the config file exists
        if (!config.contains("serverUuid")) {

            // Add default values
            config.set("enabled", true);
            // Every server gets it's unique random id.
            config.set("serverUuid", UUID.randomUUID().toString());
            // Should failed request be logged?
            config.set("logFailedRequests", false);
            // Should the sent data be logged?
            config.set("logSentData", false);
            // Should the response text be logged?
            config.set("logResponseStatusText", false);

            // Inform the server owners about bStats
            //config.options().header(
            //        "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
            //                "To honor their work, you should not disable it.\n" +
            //                "This has nearly no effect on the server performance!\n" +
            //                "Check out https://bStats.org/ to learn more :)"
            //).copyDefaults(true);
            try {
                config.save(configFile);
            } catch (Exception ignored) {
                /* do nothing */
            }
        }

        // Load the data
        enabled = config.getBoolean("enabled", true);
        serverUUID = config.getString("serverUuid");
        logFailedRequests = config.getBoolean("logFailedRequests", false);
        logSentData = config.getBoolean("logSentData", false);
        logResponseStatusText = config.getBoolean("logResponseStatusText", false);

        startSubmitting();
    }

    private static void sendData(MCLogger logger, JsonObject data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }
        if (MCServer.isMainThread()) {
            throw new IllegalAccessException("This method must not be called from the main thread!");
        }
        if (logSentData) {
            logger.info("Sending data to bStats: " + data.toString());
        }
        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

        // Compress the data to save bandwidth
        byte[] compressedData = compress(data.toString());

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        // Send data
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(compressedData);
        outputStream.flush();
        outputStream.close();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        bufferedReader.close();
        if (logResponseStatusText) {
            logger.info("Sent data to bStats and received response: " + builder.toString());
        }
    }

    /**
     * Gzips the given String.
     *
     * @param str The string to gzip.
     * @return The gzipped String.
     * @throws IOException If the compression failed.
     */
    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return outputStream.toByteArray();
    }

    /**
     * Checks if bStats is enabled.
     *
     * @return Whether bStats is enabled or not.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Adds a custom chart.
     *
     * @param chart The chart to add.
     */
    public void addCustomChart(CustomChart chart) {
        if (chart == null) {
            throw new IllegalArgumentException("Chart cannot be null!");
        }
        charts.add(chart);
    }

    /**
     * Starts the Scheduler which submits our data every 30 minutes.
     */
    private void startSubmitting() {
        final Timer timer = new Timer(true); // We use a timer cause the Bukkit scheduler is affected by server lags
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                submitData();
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);
        // Submit the data every 30 minutes, first time after 5 minutes to give other plugins enough time to start
        // WARNING: Changing the frequency has no effect but your plugin WILL be blocked/deleted!
        // WARNING: Just don't do it!
    }

    /**
     * Gets the plugin specific data.
     * This method is called using Reflection.
     *
     * @return The plugin specific data.
     */
    public JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        data.addProperty("pluginName", plugin.getName()); // Append the name of the plugin
        data.addProperty("pluginVersion", plugin.getVersion()); // Append the version of the plugin
        JsonArray customCharts = new JsonArray();
        for (CustomChart customChart : charts) {
            // Add the data of the custom charts
            JsonObject chart = customChart.getRequestJsonObject(plugin.getMCLogger());
            if (chart == null) { // If the chart is null, we skip it
                continue;
            }
            customCharts.add(chart);
        }
        data.add("customCharts", customCharts);

        return data;
    }

    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private JsonObject getServerData() {
        // Minecraft specific data
        int playerAmount = MCServer.getOnlinePlayers().size(); // Just use the new method if the Reflection failed
        int onlineMode = MCServer.isOnlineMode() ? 1 : 0;
        String bukkitVersion = MCServer.getVersion();

        // OS/Java specific data
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();

        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("bukkitVersion", bukkitVersion);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    /**
     * Collects the data and sends it afterwards.
     */
    private void submitData() {
        final JsonObject data = getServerData();
        JsonArray pluginData = new JsonArray();
        pluginData.add(getPluginData());
        data.add("plugins", pluginData);

        // Create a new thread for the connection to the bStats server
        new Thread(() -> {
            try {
                // Send the data
                sendData(plugin.getMCLogger(), data);
            } catch (Exception ex) {
                // Something went wrong! :(
                if (logFailedRequests) {
                    plugin.getMCLogger().warning("Could not submit plugin stats of " + plugin.getMCLogger());
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Represents a custom chart.
     */
    public static abstract class CustomChart {

        private MCLogger logger;

        // The id of the chart
        final String chartId;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         */
        CustomChart(String chartId) {
            this.logger = logger;

            if (chartId == null || chartId.isEmpty()) {
                throw new IllegalArgumentException("ChartId cannot be null or empty!");
            }
            this.chartId = chartId;
        }

        private JsonObject getRequestJsonObject(MCLogger logger) {
            JsonObject chart = new JsonObject();
            chart.addProperty("chartId", chartId);
            try {
                JsonObject data = getChartData();
                if (data == null) {
                    // If the data is null we don't send the chart.
                    return null;
                }
                chart.add("data", data);
            } catch (Throwable t) {
                if (logFailedRequests) {
                    logger.warning("Failed to get data for custom chart with id " + chartId);
                    t.printStackTrace();
                }
                return null;
            }
            return chart;
        }

        protected abstract JsonObject getChartData() throws Exception;

    }

    /**
     * Represents a custom simple pie.
     */
    public static class SimplePie extends CustomChart {

        private final Callable<String> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public SimplePie(String chartId, Callable<String> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            String value = callable.call();
            if (value == null || value.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            data.addProperty("value", value);
            return data;
        }
    }

    /**
     * Represents a custom advanced pie.
     */
    public static class AdvancedPie extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                values.addProperty(entry.getKey(), entry.getValue());
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }
    }

    /**
     * Represents a custom drilldown pie.
     */
    public static class DrilldownPie extends CustomChart {

        private final Callable<Map<String, Map<String, Integer>>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        public JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Map<String, Integer>> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean reallyAllSkipped = true;
            for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
                JsonObject value = new JsonObject();
                boolean allSkipped = true;
                for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
                    value.addProperty(valueEntry.getKey(), valueEntry.getValue());
                    allSkipped = false;
                }
                if (!allSkipped) {
                    reallyAllSkipped = false;
                    values.add(entryValues.getKey(), value);
                }
            }
            if (reallyAllSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }
    }

    /**
     * Represents a custom single line chart.
     */
    public static class SingleLineChart extends CustomChart {

        private final Callable<Integer> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public SingleLineChart(String chartId, Callable<Integer> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            int value = callable.call();
            if (value == 0) {
                // Null = skip the chart
                return null;
            }
            data.addProperty("value", value);
            return data;
        }

    }

    /**
     * Represents a custom multi line chart.
     */
    public static class MultiLineChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                values.addProperty(entry.getKey(), entry.getValue());
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }

    }

    /**
     * Represents a custom simple bar chart.
     */
    public static class SimpleBarChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, Integer> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                JsonArray categoryValues = new JsonArray();
                categoryValues.add(new JsonPrimitive(entry.getValue()));
                values.add(entry.getKey(), categoryValues);
            }
            data.add("values", values);
            return data;
        }

    }

    /**
     * Represents a custom advanced bar chart.
     */
    public static class AdvancedBarChart extends CustomChart {

        private final Callable<Map<String, int[]>> callable;

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         * @param callable The callable which is used to request the chart data.
         */
        public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            JsonObject values = new JsonObject();
            Map<String, int[]> map = callable.call();
            if (map == null || map.isEmpty()) {
                // Null = skip the chart
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                if (entry.getValue().length == 0) {
                    continue; // Skip this invalid
                }
                allSkipped = false;
                JsonArray categoryValues = new JsonArray();
                for (int categoryValue : entry.getValue()) {
                    categoryValues.add(new JsonPrimitive(categoryValue));
                }
                values.add(entry.getKey(), categoryValues);
            }
            if (allSkipped) {
                // Null = skip the chart
                return null;
            }
            data.add("values", values);
            return data;
        }
    }

}
