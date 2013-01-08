package lab;

import java.io.IOException;
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

    public static String generateTemplateWithResults(CrawlRequest crawlRequest, String templateFileName) throws IOException {
        StringBuilder domainListBuilder = new StringBuilder();

        for (String domain : crawlRequest.domainsConnected) {
            domainListBuilder.append("<li>").append(domain).append("</li>\n");
        }

        Map<String, Object> templateValues = new HashMap<String, Object>();
        templateValues.put("domain", crawlRequest.urlToCrawl.getHost());
        templateValues.put("robots", crawlRequest.ignoreRobots ? "Yes" : "No");
        templateValues.put("num_images", crawlRequest.totalImages);
        templateValues.put("total_size_images", crawlRequest.totalImagesBytes);
        templateValues.put("num_videos", crawlRequest.totalVideos);
        templateValues.put("total_size_videos", crawlRequest.totalVideosBytes);
        templateValues.put("num_documents", crawlRequest.totalDocuments);
        templateValues.put("total_size_documents", crawlRequest.totalDocumentsBytes);
        templateValues.put("num_pages", crawlRequest.totalPages);
        templateValues.put("total_size_pages", crawlRequest.totalPagesBytes);
        templateValues.put("num_links", crawlRequest.totalLinks);
        templateValues.put("num_domains", crawlRequest.domainsConnected.size());
        templateValues.put("connected_domains", domainListBuilder.toString());
        templateValues.put("average_rtt", crawlRequest.averageRTT);
        templateValues.put("progress", crawlRequest.progress);

        String resultsTemplate = RequestHandler.readFile(templateFileName);

        return RequestHandler.renderString(resultsTemplate, templateValues);
    }

    public String getCrawlStatus() {
        StringBuilder crawlStatus = new StringBuilder();
        try {
            for (CrawlRequest crawlerRequest : crawler.getRequests()) {
                crawlStatus.append("<li>");
                crawlStatus.append(generateTemplateWithResults(crawlerRequest, "templates/crawl_status.html"));
                crawlStatus.append("</li>");
            }
            return crawlStatus.toString();
        } catch (IOException e) {
            return "Error!";
        }
    }

    public void get()
    {
        Map<String, Object> templateValues = new HashMap<String, Object>();
        setCrawlerStatus(templateValues);

        templateValues.put("run_status", "");
        templateValues.put("crawl_status", getCrawlStatus());

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

        addStatus = crawler.add(request.parameters.get("domain"), request.parameters.containsKey("robots") && request.parameters.get("robots").equals("checked"));
        Logger.info("Adding task to crawler - result: " + addStatus);
        if (addStatus == Crawler.ADD_STATUS_SUCCESS) {
            templateValues.put("run_status", "<div class='alert alert-success'>Crawler started successfully</div>");
        } else if (addStatus == Crawler.ADD_STATUS_RUNNING) {
            templateValues.put("run_status", "<div class='alert alert-warn'>Crawler already running</div>");
        } else if (addStatus == Crawler.STATUS_ERROR_BAD_URL) {
            templateValues.put("run_status", "<div class='alert alert-error'>Bad URL!</div>");
        } else {
            templateValues.put("run_status", "<div class='alert alert-error'>Crawler failed to start</div>");
        }

        setCrawlerStatus(templateValues);

        templateValues.put("crawl_status", getCrawlStatus());

        renderTemplate(CRAWLER_TEMPLATE, templateValues);

    }

}
