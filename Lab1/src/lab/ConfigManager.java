package lab;

import org.omg.CORBA.StringHolder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    public static String CONFIG_FILE = "config.ini";

	public static String PORT_KEY = "port";
    public static int DEFAULT_PORT_VALUE = 8080;

    public static String ROOT_DIRECTORY_KEY = "root";
    public static String DEFAULT_ROOT_DIRECTORY_VALUE = "root";

    public static String DEFAULT_PAGE_KEY = "defaultPage";
    public static String DEFAULT_DEFAULT_PAGE_VALUE = "index.html";

    public static String SITES_ROOT_DIRECTORY_KEY = "sitesRoot";
    public static String DEFAULT_SITES_ROOT_DIRECTORY_VALUE = "sites";

    public static String MAX_THREADS_KEY = "maxThreads";
    public static int DEFAULT_MAX_THREADS_VALUE = 10;

    public static String FILE_DEBUG_LEVEL_KEY = "fileDebugLevel";
    public static int DEFAULT_FILE_DEBUG_LEVEL_VALUE = Logger.LOG_LEVEL_DEBUG;

    public static String SCREEN_DEBUG_LEVEL_KEY = "screenDebugLevel";
	public static int DEFAULT_SCREEN_DEBUG_LEVEL_VALUE = Logger.LOG_LEVEL_DEBUG;

    private static ConfigManager manager;
    private Map<String, String> configuration;
    private File configFile;

    public ConfigManager() {
        configuration = new HashMap<String, String>();

        configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(configFile));
            } catch (FileNotFoundException e) {
                Logger.critical("Can't open configuration file! " + e.getStackTrace());
                System.exit(1);
                return;
            }

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] iniLine = line.split("=");
                    if (iniLine.length != 2) {
                        throw new RuntimeException("Error parsing ini file on line: " + line);
                    }

                    configuration.put(iniLine[0], iniLine[1]);

                }

                reader.close();

            } catch (IOException e) {
                Logger.critical("Can't read configuration file!" + e.getStackTrace());
                System.exit(1);
            }

        } else {
            configuration.put(PORT_KEY, Integer.toString(DEFAULT_PORT_VALUE));
            configuration.put(ROOT_DIRECTORY_KEY, DEFAULT_ROOT_DIRECTORY_VALUE);
            configuration.put(DEFAULT_PAGE_KEY, DEFAULT_DEFAULT_PAGE_VALUE);
            configuration.put(SITES_ROOT_DIRECTORY_KEY, DEFAULT_SITES_ROOT_DIRECTORY_VALUE);
            configuration.put(MAX_THREADS_KEY, Integer.toString(DEFAULT_MAX_THREADS_VALUE));
            configuration.put(FILE_DEBUG_LEVEL_KEY, Integer.toString(DEFAULT_FILE_DEBUG_LEVEL_VALUE));
            configuration.put(SCREEN_DEBUG_LEVEL_KEY, Integer.toString(DEFAULT_SCREEN_DEBUG_LEVEL_VALUE));

            saveConfiguration();
        }
    }

    private void saveConfiguration() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(configFile));

            for (String key : configuration.keySet()) {
                out.println(key + "=" + configuration.get(key));
            }

            out.close();

        } catch (IOException e) {
            Logger.error("Error Writing configuration file! " + e.getStackTrace());
        }
    }

    private int getIntParameter(String key) {
        return Integer.parseInt(configuration.get(key));
    }

    private String getStringParameter(String key) {
        return configuration.get(key);
    }

    private void setIntParameter(String key, int value) {
        configuration.put(key, Integer.toString(value));
        saveConfiguration();
    }

    private void setStringParameter(String key, String value) {
        configuration.put(key, value);
        saveConfiguration();
    }

	public int getPort() {
		return getIntParameter(PORT_KEY);
	}
	
	public void setPort(int port) {
		setIntParameter(PORT_KEY, port);
	}
	
	public int getMaxThreads() {
		return getIntParameter(MAX_THREADS_KEY);
	}
	
	public void setMaxThreads(int maxThreads) {
		setIntParameter(MAX_THREADS_KEY, maxThreads);
	}
	
	public String getRoot() {
		return getStringParameter(DEFAULT_ROOT_DIRECTORY_VALUE);
	}
	
	public void setRoot(String root) {
		setStringParameter(DEFAULT_ROOT_DIRECTORY_VALUE, root);
	}
	
	public String getDefaultPage() {
		return getStringParameter(DEFAULT_PAGE_KEY);
	}
	
	public void setDefaultPage(String defaultPage) {
		setStringParameter(DEFAULT_PAGE_KEY, defaultPage);
	}
	
	public String getSitesRoot() {
		return getStringParameter(SITES_ROOT_DIRECTORY_KEY);
	}
	
	public void setSitesRoot(String sitesRoot) {
		setStringParameter(SITES_ROOT_DIRECTORY_KEY, sitesRoot);
	}

    public int getFileDebugLevel() {
        return getIntParameter(FILE_DEBUG_LEVEL_KEY);
    }

    public void setFileDebugLevel(int level) {
        setIntParameter(FILE_DEBUG_LEVEL_KEY, level);
    }

    public int getScreenDebugLevel() {
        return getIntParameter(SCREEN_DEBUG_LEVEL_KEY);
    }

    public void setScreenDebugLevel(int level) {
        setIntParameter(SCREEN_DEBUG_LEVEL_KEY, level);
    }

    public static ConfigManager getInstance() {
        if (manager == null) {
            manager = new ConfigManager();
        }
        return manager;
    }
	
}
