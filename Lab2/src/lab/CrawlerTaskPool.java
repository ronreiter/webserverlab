package lab;

import java.net.Socket;
import java.util.LinkedList;

public class CrawlerTaskPool {
	private final LinkedList<String> crawlerTasks;
	private LinkedList<Thread> threads;
	private boolean stopServing;

    private CrawlerTaskMutex taskMutex;

	public CrawlerTaskPool() {
		crawlerTasks = new LinkedList<String>();
		threads = new LinkedList<Thread>();
        taskMutex = new CrawlerTaskMutex();
	}

	public void start() {
        stopServing = false;

        int numThreads = ConfigManager.getInstance().getMaxThreads();
        numThreads = 1;

        for (int i = 0; i < numThreads; i++) {
            Thread newThread = new Thread(new CrawlTask(this, taskMutex));
            threads.add(newThread);
            newThread.start();
        }
		
	}
	
	public void shutdown() {
		stopServing = true;
        synchronized (crawlerTasks) {
            crawlerTasks.notifyAll();
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
	
	public void handleTask(String task) {
		enqueue(task);
	}
	
	public void enqueue(String task) {
		synchronized (crawlerTasks) {
			crawlerTasks.add(task);
			crawlerTasks.notifyAll();
		}
	}
	
	public String dequeue() throws InterruptedException {
		while (true) {
			synchronized (crawlerTasks) {
                if (stopServing) {
                    break;
                }

                if (crawlerTasks.isEmpty()) {
					crawlerTasks.wait();
                } else {
					return crawlerTasks.pop();
				}
			}
		}
        return null;
	}

    public int getTaskCount()
    {
        return taskMutex.count();
    }
}
