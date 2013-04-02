package com.proetsch.ann;

public class OutputLayerNeuron extends Neuron {

	public OutputLayerNeuron(Layer l) {
		super(l);
	}
	
	@Override
	// Sigmoid function
	protected double activation_function(double x) {
//		return x;
		return  ((double) 1 / ((double) 1 +
							Math.pow(Math.E, ((double)-x/(double)1) )));
	}
	
	@Override
	// Derivative of sigmoid function at this neuron's net input
	protected double d_activation_function() {
//		return 1.0d;
		return output_value * (1 - output_value);
	}

	@Override
	public double calculate_delta(double target_val) {
		delta = (target_val - this.output_value) * 
				d_activation_function();
		return delta;
	}
}
