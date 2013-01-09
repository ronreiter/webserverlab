package lab;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CrawlerRequestsJSONHandler extends RequestHandler {
    private Crawler crawler;
	public CrawlerRequestsJSONHandler() {
        crawler = Crawler.getInstance();
    }

    public String generateResults() {
        JSONArray results = new JSONArray();

        for (CrawlRequest crawlRequest : crawler.getRequests()) {
            JSONObject result = new JSONObject();

            JSONArray domainList = new JSONArray();
            for (String domain : crawlRequest.domainsConnected) {
                domainList.add(domain);
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

            result.put("domain", crawlRequest.urlToCrawl.getHost());
            result.put("robots", crawlRequest.ignoreRobots ? "Yes" : "No");
            result.put("num_images", crawlRequest.totalImages);
            result.put("total_size_images", crawlRequest.totalImagesBytes);
            result.put("num_videos", crawlRequest.totalVideos);
            result.put("total_size_videos", crawlRequest.totalVideosBytes);
            result.put("num_documents", crawlRequest.totalDocuments);
            result.put("total_size_documents", crawlRequest.totalDocumentsBytes);
            result.put("num_pages", crawlRequest.totalPages);
            result.put("total_size_pages", crawlRequest.totalPagesBytes);
            result.put("num_links", crawlRequest.totalLinks);
            result.put("num_domains", crawlRequest.domainsConnected.size());
            result.put("connected_domains", domainList);
            result.put("average_rtt", crawlRequest.averageRTT);
            result.put("progress", progressString);

            results.add(result);
        }

        return results.toJSONString();
    }

    public void get()
    {
        response.setBody(generateResults());
    }

}
