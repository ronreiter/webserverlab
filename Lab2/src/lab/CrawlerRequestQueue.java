package lab;

import java.util.LinkedList;

public class CrawlerRequestQueue {
	private final LinkedList<CrawlRequest> crawlerRequests;
	private boolean stopServing;

	public CrawlerRequestQueue() {
		crawlerRequests = new LinkedList<CrawlRequest>();
	}

	public void enqueue(CrawlRequest task) {
		synchronized (crawlerRequests) {
			crawlerRequests.add(task);
			crawlerRequests.notifyAll();
		}
	}
	
	public CrawlRequest dequeue() throws InterruptedException {
		while (true) {
			synchronized (crawlerRequests) {
                if (stopServing) {
                    break;
                }

                if (crawlerRequests.isEmpty()) {
					crawlerRequests.wait();
                } else {
					return crawlerRequests.pop();
				}
			}
		}
        return null;
	}

    public int getTaskCount()
    {
        return crawlerRequests.size();
    }
}
