package lab;

import java.util.LinkedList;

public class ResourceQueue {
	private final LinkedList<Resource> analyzeTasks;
    private final LinkedList<Resource> downloadTasks;
    private boolean shutdown = false;
    public Mutex resourceMutex;

	public ResourceQueue() {
		this.analyzeTasks = new LinkedList<Resource>();
        this.downloadTasks = new LinkedList<Resource>();
        this.resourceMutex = new Mutex();
	}

    public void waitUntilFinished() {
        synchronized (resourceMutex) {
            while (true) {
                Logger.debug("Lock state: count: " + resourceMutex.count() + " analyze tasks: " + analyzeTasks.size() + " download tasks: " + downloadTasks.size());
                if (0 == resourceMutex.count() && analyzeTasks.size() == 0 && downloadTasks.size() == 0) {
                    return;
                }
                try {
                    resourceMutex.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (resourceMutex) {
            resourceMutex.notifyAll();
        }
    }

    public void enqueueToAnalyze(Resource resource) {
        enqueue(analyzeTasks, resource);
    }

    public Resource dequeueToAnalyze() throws InterruptedException {
        Logger.info("Number of items left to analyze: " + downloadTasks.size());
        return dequeue(analyzeTasks);
    }

    public void enqueueToDownload(Resource resource) {
        enqueue(downloadTasks, resource);
    }

    public Resource dequeueToDownload() throws InterruptedException {
        Logger.info("Number of items left to download: " + downloadTasks.size());
        return dequeue(downloadTasks);
    }

    private void enqueue(LinkedList<Resource> queue, Resource resource) {
		synchronized (resourceMutex) {
			queue.add(resource);
            resourceMutex.notifyAll();
		}
	}
	
	private Resource dequeue(LinkedList<Resource> queue) throws InterruptedException {
		while (true) {
			synchronized (resourceMutex) {
                if (queue.isEmpty()) {
                    if (shutdown) {
                        return null;
                    }

                    resourceMutex.wait();
                } else {
					Resource taskToReturn = queue.pop();
                    resourceMutex.register("Resource");
                    resourceMutex.notifyAll();
                    return taskToReturn;
				}
			}
		}
	}

    public void releaseResource() {
        synchronized (resourceMutex) {
            resourceMutex.unregister("Resource");
            resourceMutex.notifyAll();
        }
    }


}
