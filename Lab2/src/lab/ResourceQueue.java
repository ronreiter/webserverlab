package lab;

import java.util.LinkedList;

public class ResourceQueue {
	private final LinkedList<Resource> analyzeTasks;
    private final LinkedList<Resource> downloadTasks;
    private boolean shutdown = false;
    private Integer threadsWaiting = 0;
    private int maxThreads = 0;

	public ResourceQueue(int maxThreads) {
		this.analyzeTasks = new LinkedList<Resource>();
        this.downloadTasks = new LinkedList<Resource>();
        this.maxThreads = maxThreads;
	}

    public void shutdown() {
        shutdown = true;
        analyzeTasks.notifyAll();
    }

    public void enqueueToAnalyze(Resource resource) {
        enqueue(analyzeTasks, resource);
    }

    public Resource dequeueToAnalyze() throws InterruptedException {
        return dequeue(analyzeTasks);
    }

    public void enqueueToDownload(Resource resource) {
        enqueue(downloadTasks, resource);
    }

    public Resource dequeueToDownload() throws InterruptedException {
        return dequeue(downloadTasks);
    }

    private void enqueue(LinkedList<Resource> queue, Resource resource) {
		synchronized (threadsWaiting) {
			queue.add(resource);
            threadsWaiting.notifyAll();
		}
	}
	
	private Resource dequeue(LinkedList<Resource> queue) throws InterruptedException {
		while (true) {
			synchronized (threadsWaiting) {
                if (queue.isEmpty()) {
                    threadsWaiting += 1;
                    if (maxThreads > 0 && maxThreads == threadsWaiting) {
                        shutdown();
                    }

                    if (shutdown) {
                        return null;
                    }

                    queue.wait();
                } else {
                    threadsWaiting -= 1;
					Resource taskToReturn = queue.pop();
                    threadsWaiting.notifyAll();
                    return taskToReturn;
				}
			}
		}
	}


}
