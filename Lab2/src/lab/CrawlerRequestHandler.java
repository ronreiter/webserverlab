package lab;

public class CrawlerRequestHandler extends RequestHandler {
	
	public CrawlerRequestHandler() {
	}
	
	
	public void post()
	{		
        if (request.getHeaders().containsKey("chunked") && request.getHeaders().get("chunked").toLowerCase().equals("yes")) {
            Logger.debug("Setting chunked transfer encoding");
            this.response.setChunkedTransferEncoding(true);
        }        
             
        if (request.parameters.containsKey("pass"))
        {
        	if (ConfigManager.getInstance().getManagerPassword().equals(request.parameters.get("pass")))
        	{
        		for(String param: request.parameters.keySet())
        		{
        			String value = request.parameters.get(param);
        			if (param.equals(ConfigManager.PORT_KEY))
        			{
        				ConfigManager.getInstance().setPort(Integer.parseInt(value.trim()));
        			}
        			else if (param.equals(ConfigManager.MANAGER_PASSWORD_KEY))
        			{
        				ConfigManager.getInstance().setManagerPassword(value);
        			}
        			else if (param.equals(ConfigManager.MULTIPLE_SITES_SUPPORT_KEY))
        			{
        				ConfigManager.getInstance().setIsMultipleSites(value);
        			}
        			else if (param.equals(ConfigManager.MAX_THREADS_KEY))
        			{
        				ConfigManager.getInstance().setMaxThreads(Integer.parseInt(value.trim()));
        			}
        			else if (param.equals(ConfigManager.DEFAULT_PAGE_KEY))
        			{
        				ConfigManager.getInstance().setDefaultPage(value);
        			}
        			else if (param.equals(ConfigManager.ROOT_DIRECTORY_KEY))
        			{
        				ConfigManager.getInstance().setRoot(value);
        			}
        		}
       		}
        }        
	}
	
	public void get()
	{
		// return crawler form HTML
	}
}
