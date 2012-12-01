package lab;

import java.net.Socket;
import java.util.LinkedList;

public class ConnectionPool {
	private LinkedList<Socket> connections;
	private LinkedList<Thread> threads;
	
	public ConnectionPool() {
		connections = new LinkedList<Socket>();
		threads = new LinkedList<Thread>();
		
	}

	public void start() {
		// TODO: create threads here
		
	}
	
	public void shutdown() {
		// TODO: kill threads here
		
	}
	
	public void handleConnection(Socket connection) {
		enqueue(connection);
	}
	
	protected void enqueue(Socket connection) {
		synchronized (connections) {
			connections.add(connection);
			connections.notifyAll();
		}
	}
	
	public Socket dequeue() throws InterruptedException {
		while (true) {
			synchronized (connections) {
				if (connections.isEmpty()) {
					connections.wait();
				} else {
					return connections.pop();
				}
			}
		}
	}
}
