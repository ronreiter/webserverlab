package lab;

public class ConfigManager {
	public static String PORT_KEY = "port";
	public static String ROOT_DIRECTORY_KEY = "root";
	public static String DEFAULT_PAGE_KEY = "defaultPage";
	public static String DEFAULT_DOMAIN_DIRECTORY_KEY = "defaultDirectory";
	public static String MAX_THREADS_KEY = "maxThreads";
	public static String FILE_DEBUG_LEVEL_KEY = "fileDebugLevel";
	public static String SCREEN_DEBUG_LEVEL_KEY = "screenDebugLevel";

	public static int DEFAULT_PORT_VALUE = 80;
	public static String DEFAULT_ROOT_DIRECTORY_VALUE = "root";
	public static String DEFAULT_DEFAULT_PAGE_VALUE = "defaultPage";
	public static String DEFAULT_DOMAIN_DIRECTORY_VALUE = "defaultDirectory";
	public static int DEFAULT_MAX_THREADS_VALUE = 10;
	public static int DEFAULT_FILE_DEBUG_LEVEL_VALUE = Logger.LOG_LEVEL_DEBUG;
	public static int DEFAULT_SCREEN_DEBUG_LEVEL_VALUE = Logger.LOG_LEVEL_DEBUG;

	public int getPort() {
		return 0;
	}
	
	public void setPort(int port) {
		
	}
	
	public int getMaxThreads() {
		return 0;
	}
	
	public void setMaxThreads(int maxThreads) {
		
	}
	
	public String getRoot() {
		return null;
	}
	
	public void setRoot(String root) {
		
	}
	
	public String getDefaultPage() {
		return null;
	}
	
	public void setDefaultPage(String defaultPage) {
		
	}
	
	public String getDefaultDirectory() {
		return null;
	}
	
	public void setDefaultDirectory(String defaultDirectory) {
		
	}
	
	
	
}
