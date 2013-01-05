package lab;

import java.net.Socket;
import java.util.LinkedList;

public class TaskQueue {
	private final LinkedList<Socket> connections;
	private LinkedList<Thread> threads;
	private boolean stopServing;

	public TaskQueue() {
		connections = new LinkedList<Socket>();
		threads = new LinkedList<Thread>();
	}

	public void shutdown() {
		stopServing = true;
        synchronized (connections) {
            connections.notifyAll();
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
	
	public void handleConnection(Socket connection) {
		enqueue(connection);
	}
	
	public void enqueue(Socket connection) {
		synchronized (connections) {
			connections.add(connection);
			connections.notifyAll();
		}
	}
	
	public Socket dequeue() throws InterruptedException {
		while (true) {
			synchronized (connections) {
                if (stopServing) {
                    break;
                }

                if (connections.isEmpty()) {
					connections.wait();
                } else {
					return connections.pop();
				}
			}
		}
        return null;
	}
}
