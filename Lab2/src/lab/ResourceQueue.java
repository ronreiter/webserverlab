package lab;

import java.util.LinkedList;

public class ResourceQueue {
	private final LinkedList<Resource> analyzeTasks;
    private final LinkedList<Resource> downloadTasks;
    private boolean shutdown = false;
    public Mutex resourceMutex;
    private int maxThreads = 0;
    private Object lock = null;

	public ResourceQueue(int maxThreads) {
		this.analyzeTasks = new LinkedList<Resource>();
        this.downloadTasks = new LinkedList<Resource>();
        this.maxThreads = maxThreads;
        this.resourceMutex = new Mutex();
        this.lock = new Object();
	}

    public void waitUntilFinished() {
        synchronized (lock) {
            while (true) {
                if (maxThreads == resourceMutex.count()) {
                    return;
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (lock) {
            lock.notifyAll();
        }
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
		synchronized (lock) {
			queue.add(resource);
            lock.notifyAll();
		}
	}
	
	private Resource dequeue(LinkedList<Resource> queue) throws InterruptedException {
		while (true) {
			synchronized (lock) {
                if (queue.isEmpty()) {
                    if (shutdown) {
                        return null;
                    }

                    lock.wait();
                } else {
					Resource taskToReturn = queue.pop();
                    resourceMutex.register();
                    lock.notifyAll();
                    return taskToReturn;
				}
			}
		}
	}


}
