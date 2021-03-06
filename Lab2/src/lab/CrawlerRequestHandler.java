package lab;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CrawlerRequestHandler extends RequestHandler {
	public static final String CRAWLER_TEMPLATE = "templates/index.html";
    private Crawler crawler;

    public CrawlerRequestHandler() {
        crawler = Crawler.getInstance();
    }

    public void setCrawlerStatus(Map<String, Object> templateValues) {
        String statusString = crawler.crawlTaskPool.taskMutex.count() + " out of " +
                ConfigManager.getInstance().getMaxCrawlerThreads() + " crawler threads are busy.";

        if (crawler.getStatus() == Crawler.STATUS_READY) {
            templateValues.put("status", "<div class='alert alert-success'>Crawler is ready to run - " + statusString + "</div>");
        } else if (crawler.getStatus() == Crawler.STATUS_BUSY) {
            templateValues.put("status", "<div class='alert alert-warn'>Crawler is busy - " + statusString + "</div>");
        } else {
            templateValues.put("status", "");
        }

    }

    public static String getCrawledDomainReport(String domain) {
        File folder = new File(new File("root"), "reports");
        for (File file : folder.listFiles()) {
            if (file.getName().startsWith(domain)) {
                return file.getName();
            }
        }

        return null;
    }

    public static String generateTemplateWithResults(CrawlRequest crawlRequest, String templateFileName) throws IOException {
        StringBuilder domainListBuilder = new StringBuilder();
        for (String domain : crawlRequest.domainsConnected) {
            String crawledDomainReport = getCrawledDomainReport(domain);
            if (crawledDomainReport != null) {
                domainListBuilder.append("<li><a href='/reports/" + crawledDomainReport + "'>").append(domain).append("</a></li>\n");
            } else {
                domainListBuilder.append("<li>").append(domain).append("</li>\n");
            }
        }

        String progressString = null;
        switch (crawlRequest.progress) {
            case CrawlRequest.PROGRESS_NEW:
                progressString = "New";
                break;
            case CrawlRequest.PROGRESS_WAITING_TO_START:
                progressString = "Waiting to start";
                break;
            case CrawlRequest.PROGRESS_WORKING:
                progressString = "Working";
                break;
            case CrawlRequest.PROGRESS_FINISHED:
                progressString = "Finished";
                break;
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
        templateValues.put("progress", progressString);

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

    public String getCompletedJobsFileTable() {
        StringBuilder fileList = new StringBuilder();

        File folder = new File(new File("root"), "reports");

        if (!folder.exists()) {
            folder.mkdir();
        }

        for (File file : folder.listFiles()) {
            fileList.append("<tr><td><a href='/reports/" + file.getName() + "'>" + file.getName() + "</a></td></tr>");
        }

        return fileList.toString();

    }

    public void get()
    {
        Map<String, Object> templateValues = new HashMap<String, Object>();
        setCrawlerStatus(templateValues);


        templateValues.put("files", getCompletedJobsFileTable());
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

        String host = null;
        try {
            host = URLDecoder.decode(request.parameters.get("domain"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (host != null) {
            addStatus = crawler.add(host, request.parameters.containsKey("robots") && (request.parameters.get("robots") != null));
        }

        Logger.info("Adding task to crawler - result: " + addStatus);
        if (addStatus == Crawler.ADD_STATUS_SUCCESS) {
            templateValues.put("run_status", "<div class='alert alert-success'>Crawler started successfully</div>");
        } else if (addStatus == Crawler.ADD_STATUS_RUNNING) {
            templateValues.put("run_status", "<div class='alert alert-error'>Crawler already running (at maximum capacity)!</div>");
        } else if (addStatus == Crawler.STATUS_ERROR_BAD_URL) {
            templateValues.put("run_status", "<div class='alert alert-error'>Bad URL!</div>");
        } else if (addStatus == Crawler.STATUS_ERROR_UNKNOWN_HOST) {
            templateValues.put("run_status", "<div class='alert alert-error'>Unknown host!</div>");
        } else {
            templateValues.put("run_status", "<div class='alert alert-error'>Crawler failed to start</div>");
        }

        setCrawlerStatus(templateValues);

        templateValues.put("files", getCompletedJobsFileTable());
        templateValues.put("crawl_status", getCrawlStatus());

        renderTemplate(CRAWLER_TEMPLATE, templateValues);

    }

}
