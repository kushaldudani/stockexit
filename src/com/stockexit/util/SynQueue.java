package com.stockexit.util;
import java.util.LinkedList;


public class SynQueue<E> {
	
	private LinkedList<E> queue = new LinkedList<E>();
	
	public SynQueue() {
	}
	
	public synchronized void enqueue(E entry){
		queue.add(entry);
	}
	
	public synchronized E dequeue(){
		E recent = null;
		E recentcp = null;
		while((recent=queue.poll()) != null){
			recentcp = recent;
		}
		return recentcp;
	}
	

}