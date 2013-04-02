package com.proetsch.ann;

public abstract class Neuron {
	
	protected Double weighted_inputs_sum;
	protected Double output_value;
	protected Layer layer;
	
	// Error signal of this Neuron
	// Used in backwards propagation
	protected Double delta;
	
	public Neuron(Layer l) {
		this.weighted_inputs_sum = null;
		this.output_value = null;
		this.layer = l;
		
		this.delta = null;
	}
	
	protected abstract double activation_function(double x);
	protected abstract double d_activation_function();
	protected abstract double calculate_delta(double target_val);
	
	public double getDelta() {
		return this.delta;
	}
	
	public void calculateOutput() {
		weighted_inputs_sum = 0.0;
		for (Link link : layer.getLinks()) {
			if (link.isTo(this)) {
				weighted_inputs_sum += link.getWeight() * link.fromNeuron().getOutputValue();
			}
		}
		
		this.output_value = activation_function(weighted_inputs_sum);
		if (this instanceof OutputLayerNeuron) {
//			System.out.println("Weighted inputs sum: "+weighted_inputs_sum);
		}
	}
	
	public double getOutputValue() {
		return output_value;
	}
}