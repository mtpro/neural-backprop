package com.proetsch.ann;

public class HiddenLayerNeuron extends Neuron {

	public HiddenLayerNeuron(Layer l) {
		super(l);
	}
	
	@Override
	// Sigmoid function
	protected double activation_function(double x) {
		return (double) 1 / ((double) 1 +
								Math.pow(Math.E, -x));
	}
	
	@Override
	// Derivative of sigmoid function at this neuron's net input
	// Called only after activation_function() has calculated
	//   the appropriate output_value for this Neuron
	protected double d_activation_function() {
		return output_value * (1 - output_value);
	}
	
	@Override
	public double calculate_delta(double unused) {
		double dError_dOutput = 0.0d;
		double dOutput_dInput = d_activation_function();
		
		for (Link l : layer.getNext().getLinks()) {
			if (l.isFrom(this)) {
				// Error signal back-propagates, so since we started with output layer
				//   we can request error signal from the forward layer
				dError_dOutput += l.toNeuron().getDelta() *
						l.getWeight();
			}
		}
		
		delta = dError_dOutput * dOutput_dInput;
		return delta;
	} 
}
