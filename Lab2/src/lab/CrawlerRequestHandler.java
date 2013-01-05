package lab;

import java.util.HashMap;
import java.util.Map;

public class CrawlerRequestHandler extends RequestHandler {
	public static final String CRAWLER_TEMPLATE = "templates/index.html";
    private Crawler crawler;
	public CrawlerRequestHandler() {
        crawler = Crawler.getInstance();
    }

    public void setCrawlerStatus(Map<String, Object> templateValues) {
        if (crawler.getStatus() == Crawler.STATUS_READY) {
            templateValues.put("status", "<div class='alert alert-success'>Crawler is ready to run</div>");
        } else if (crawler.getStatus() == Crawler.STATUS_BUSY) {
            templateValues.put("status", "<div class='alert alert-warn'>Crawler is busy</div>");
        } else {
            templateValues.put("status", "");
        }

    }

    public void get()
    {
        Map<String, Object> templateValues = new HashMap<String, Object>();
        setCrawlerStatus(templateValues);

        templateValues.put("run_status", "");
        // return crawler form HTML
        renderTemplate(CRAWLER_TEMPLATE, templateValues);
    }
	public void post()
	{
        int addStatus = 0;
        Map<String, Object> templateValues = new HashMap<String, Object>();


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

        addStatus = crawler.add(request.parameters.get("domain"), request.parameters.get("ignore-robots").equals("checked"));

        if (addStatus == Crawler.ADD_STATUS_SUCCESS) {
            templateValues.put("run_status", "<div class='alert alert-success>Crawler started successfully</div>");
        } else if (addStatus == Crawler.ADD_STATUS_RUNNING) {
            templateValues.put("run_status", "<div class='alert alert-warn>Crawler already running</div>");
        } else {
            templateValues.put("run_status", "<div class='alert alert-error>Crawler failed to start</div>");
        }

        setCrawlerStatus(templateValues);

        templateValues.put("crawl_status", "");

        renderTemplate(CRAWLER_TEMPLATE, templateValues);

    }

}
