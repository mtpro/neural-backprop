package com.proetsch.ann;

// Link a neuron from the previous layer
//   to a neuron in this layer with a weight
public class Link {
	
	private Neuron prev_layer_neuron;
	private Neuron current_layer_neuron;
	public double weight;
	public double weight_old_old;
	public double weight_old;
	
	public Link(Neuron prev, Neuron curr, double w) {
		prev_layer_neuron = prev;
		current_layer_neuron = curr;
		weight = w;
		
		// used in calculating momentum
		weight_old_old = w;
		weight_old = w;
	}
	
	public boolean isTo(Neuron n) {
		return current_layer_neuron == n;
	}
	
	public boolean isFrom(Neuron n) {
		return prev_layer_neuron == n;
	}
	
	public Neuron fromNeuron() {
		return prev_layer_neuron;
	}
	
	public Neuron toNeuron() {
		return current_layer_neuron;
	}
	
	public double getWeight() {
		return weight;
	}
	
}
