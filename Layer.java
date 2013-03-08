package com.proetsch.ann;

import java.util.ArrayList;
import java.util.Random;

// A Layer is an ArrayList of Neurons and is aware
//   of links to these Neurons from the previous Layer
public class Layer extends ArrayList<Neuron> {
	
	private static final long serialVersionUID = 8499826696545750705L;
	private Layer prev;
	private Layer next;
	private ArrayList<Link> links;
	public int ID;
	
	public Layer() {
		super();
		links = new ArrayList<Link>();
	}
	
	public void setPrev(Layer p) {
		this.prev = p;
	}
	
	public Layer getPrev() {
		return this.prev;
	}

	public void setNext(Layer n) {
		this.next = n;
	}
	
	public Layer getNext() {
		return this.next;
	}
	
	public void addLink(Link l) {
		links.add(l);
	}
	
	public ArrayList<Link> getLinks() {
		return links;
	}
	
	public void assignRandomWeightsToHere() {
		Random generator = new Random();
		for (Neuron p : this.prev) {
			for (Neuron c : this) {
				links.add(new Link(p, c, (generator.nextBoolean()) ? generator.nextDouble()/5.0d : 0-generator.nextDouble()/5.0d));
			}
		}
	}
	
	public void getOutput() {
		for (Neuron n : this) {
			n.getOutput();
		}
	}
	
	// Since error back-propagates, it makes sense to calculate deltas
	//   and update weights on a per-layer basis, so we can do it from
	//   the output layer back through to the input layer
	public void calculateDeltas(double target_val) {
		for (Neuron n : this)
			n.calculate_delta(target_val);
	}
	
	// Use the calculated deltas to update weights according to the deltas
	//   themselves, the output value of the neuron in the previous layer,
	//   and a learning constant provided by the network
	//   and also according to the previous weight change, termed momentum
	public void correctWeights(double learning_rate, double momentum) {
		for (Link l : links)  {
			
			l.weight_old_old = l.weight_old;
			l.weight_old = l.weight;
			l.weight += momentum * (l.weight_old - l.weight_old_old);
			l.weight += learning_rate *
					l.toNeuron().getDelta() * 
					l.fromNeuron().getOutput();
		
		}
	}
}
