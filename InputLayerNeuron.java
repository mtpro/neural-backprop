package com.proetsch.ann;

// A Neuron which sends out a constant value
// Only the link weight to hidden-layer neurons
//   should be modified - this Neuron's activation function 
//   should be identity
public class InputLayerNeuron extends Neuron {

	public InputLayerNeuron(Layer l) {
		super(l);
	}
	
	public void setInput(double in_val) {
		this.weighted_inputs_sum = in_val;
		this.output_value = in_val;
	}
	
	@Override
	protected double activation_function(double x) {
		return x;
	}
	
	@Override
	protected double d_activation_function() {
		return 1;
	}
	
	@Override
	protected double calculate_delta(double training_val) {
		return 0.0d;
	}
	
	@Override
	public double getOutput() {
		return weighted_inputs_sum;
	}

}
