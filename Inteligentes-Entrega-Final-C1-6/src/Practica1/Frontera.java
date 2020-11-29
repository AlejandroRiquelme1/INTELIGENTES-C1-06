package Practica1;

import java.util.PriorityQueue;

public class Frontera {
	private PriorityQueue<Nodo> frontera;

	public Frontera() {
		this.frontera=new PriorityQueue<Nodo>();
	}
	
	public void push(Nodo n) {
		frontera.add(n);
	}

	public Nodo pop() {
		return frontera.remove();
	}
	public int getSize() {
		return frontera.size();
	}
}