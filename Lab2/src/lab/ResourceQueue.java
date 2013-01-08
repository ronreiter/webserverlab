package lab;

import java.util.LinkedList;
import java.util.List;

public class ResourceQueue {
	private final LinkedList<Resource> tasks;
    private boolean shutdown = false;
    private int maxThreads  = 0;

	public ResourceQueue(int maxThreads) {
		this.tasks = new LinkedList<Resource>();
        this.maxThreads = maxThreads;

	}

    public void shutdown() {
        shutdown = true;
        tasks.notifyAll();
    }

	public void enqueue(Resource connection) {
		synchronized (tasks) {
			tasks.add(connection);
			tasks.notifyAll();
		}
	}
	
	public Resource dequeue() throws InterruptedException {
        int threadsWaiting = 0;
		while (true) {
			synchronized (tasks) {
                if (tasks.isEmpty()) {
                    threadsWaiting += 1;
                    if (maxThreads > 0 && maxThreads == threadsWaiting) {
                        shutdown();
                    }

                    if (shutdown) {
                        return null;
                    }

                    tasks.wait();
                } else {
                    threadsWaiting -= 1;
					Resource taskToReturn = tasks.pop();
                    tasks.notifyAll();
                    return taskToReturn;
				}
			}
		}
	}

    public boolean isEmpty() {
        return tasks.size() == 0;
    }

    public boolean isFull() {
        return tasks.size() == maxThreads && maxThreads > 0;
    }

}
