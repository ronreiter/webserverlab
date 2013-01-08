package lab;

import java.util.LinkedList;

public class CrawlTaskPool {
	private final LinkedList<CrawlRequest> requests;
	private LinkedList<Thread> threads;
	private boolean stopServing;

	public CrawlTaskPool() {
		requests = new LinkedList<CrawlRequest>();
		threads = new LinkedList<Thread>();
	}

	public void start() {
        stopServing = false;
		// TODO: create threads here
        int numThreads = ConfigManager.getInstance().getMaxCrawlerThreads();
        for (int i = 0; i < numThreads; i++) {
            Thread newThread = new Thread(new CrawlTask(this));
            threads.add(newThread);
            newThread.start();
        }
	}
	
	public void shutdown() {
		stopServing = true;
        synchronized (requests) {
            requests.notifyAll();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

	}

	public void enqueue(CrawlRequest connection) {
		synchronized (requests) {
			requests.add(connection);
			requests.notifyAll();
		}
	}
	
	public CrawlRequest dequeue() throws InterruptedException {
		while (true) {
			synchronized (requests) {
                if (stopServing) {
                    break;
                }

                if (requests.isEmpty()) {
					requests.wait();
                } else {
					return requests.pop();
				}
			}
		}
        return null;
	}
}
