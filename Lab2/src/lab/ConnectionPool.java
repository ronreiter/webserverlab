package lab;

import java.net.Socket;
import java.util.LinkedList;

public class ConnectionPool {
	private final LinkedList<Socket> connections;
	private LinkedList<Thread> threads;
	private boolean stopServing;

	public ConnectionPool() {
		connections = new LinkedList<Socket>();
		threads = new LinkedList<Thread>();
		
	}

	public void start() {
        stopServing = false;
		// TODO: create threads here
        int numThreads = ConfigManager.getInstance().getMaxThreads();
        for (int i = 0; i < numThreads; i++) {
            Thread newThread = new Thread(new ConnectionHandler(this));
            newThread.setName("Connection-" + (i+1));
            threads.add(newThread);
            newThread.start();
        }
		
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
