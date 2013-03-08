/* Matthew Proetsch
 * Artificial Neural Network implementation - 
 * COP3930h
 */
package com.proetsch.ann;

import java.util.ArrayList;
import java.util.Arrays;

public class Network {
	
	ArrayList<Layer> layers;
	private double least_mean_squared_error;
	private double momentum;
	private double learning_rate;
	
	public Network(double lmse, double p, double lr) {
		least_mean_squared_error = lmse;
		momentum = p;
		learning_rate = lr;
		layers = new ArrayList<Layer>();
	}
	
	public void initialize(int numInputs, int numHiddenNodes, int numHiddenLayers) throws Exception {
		Layer inputLayer = new Layer();
		inputLayer.ID = 0;
		
		Layer[] hiddenLayers = new Layer[numHiddenLayers];
		Layer outputLayer = new Layer();
		outputLayer.ID = numHiddenLayers + 1;
		
		if (numHiddenLayers == 0) {
			throw new Exception("You must specify at least one hidden layer");
		}
		
		// Initialize all layers, and set prev/next fields
		for (int i = 0; i < numHiddenLayers; ++i) {
			hiddenLayers[i] = new Layer();
			hiddenLayers[i].ID = i + 1;
			if (i == 0) {
				hiddenLayers[0].setPrev(inputLayer);
				inputLayer.setNext(hiddenLayers[0]);
			}
			else if (i > 0) {
				hiddenLayers[i].setPrev(hiddenLayers[i-1]);
				hiddenLayers[i-1].setNext(hiddenLayers[i]);
			}
			if (i == numHiddenLayers - 1) {
				outputLayer.setPrev(hiddenLayers[i]);
				hiddenLayers[i].setNext(outputLayer);
			}
		}
		
		for (int i = 0; i < numInputs; ++i) {
			inputLayer.add(new InputLayerNeuron(inputLayer));
		}
		
		for (int i = 0; i < numHiddenNodes; ++i) {
			for (int j = 0; j < numHiddenLayers; ++j) {
				hiddenLayers[j].add(new HiddenLayerNeuron(hiddenLayers[j]));
			}
		}
		
//		bias node
		inputLayer.add(new InputLayerNeuron(inputLayer));
		
		for (int i = 0; i < numHiddenLayers; ++i) {
			hiddenLayers[i].assignRandomWeightsToHere();
		}
		
		outputLayer.add(new OutputLayerNeuron(outputLayer));
		outputLayer.assignRandomWeightsToHere();
		
		layers.add(inputLayer);
		layers.addAll(Arrays.asList(hiddenLayers));
		layers.add(outputLayer);
	}
	
	public void simulate(double[] inputs) throws Exception {
		if (inputs.length != layers.get(0).size() - 1) {
			throw new Exception(String.format("Input size: %1$s  Expected: %2$s", inputs.length, layers.get(0).size()));
		}
		
		Layer inputLayer = layers.get(0);
		for (int i = 0; i < inputs.length; ++i) {
			InputLayerNeuron n = (InputLayerNeuron) inputLayer.get(i);
			n.setInput(inputs[i]);
		}
		InputLayerNeuron biasNode = (InputLayerNeuron) inputLayer.get(inputs.length);
		biasNode.setInput(1.0d);
		
		for (int i = 1; i < layers.size(); ++i) {
			layers.get(i).calculateOutput();
		}
	}
	
	// Train the network via backpropagation of error
	public void train(ArrayList<double[]> inputs, ArrayList<Double> expectedOutputs) throws Exception {
		if (inputs.size() != expectedOutputs.size()) {
			throw new Exception(String.format("Input ArrayList size: %1$s  ExpectedOutput size: %2$s", inputs.size(), expectedOutputs.size()));
		}
		
		for (int i = 0; i < inputs.size(); ++i) {
			// 1. Simulate network on input
			simulate(inputs.get(i));
			
			// 2. Calculate output layer delta
			Layer outputLayer = layers.get(layers.size() - 1);
			outputLayer.calculateDeltas(expectedOutputs.get(i));
			
			// Calculate deltas for layers in reverse order (second-to-last, third-to-last, ..., the one before input)
			for (int j = layers.size() - 2; j > 0; --j) {
				// calculateDeltas's argument doesn't matter in the hidden layer
				layers.get(j).calculateDeltas(expectedOutputs.get(i));
			}
			
			// 3. Finally, update weights of each link
			for (int j = 1; j < layers.size(); ++j) {
				layers.get(j).correctWeights(learning_rate, momentum);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Network ann = new Network(0.0d, 0.8d, 0.30d);
		ann.initialize(2, 3, 1);
		
		ArrayList<double[]> in = new ArrayList<double[]>();
		in.add(new double[] { 0.0d, 0.0d, }); 
		in.add(new double[] { 1.0d, 1.0d }); 
		in.add(new double[] { 1.0d, 0.0d }); 
		in.add(new double[] { 0.0d, 1.0d }); 
		
		ArrayList<Double> expected = new ArrayList<Double>();
		double[] answers = new double[] { 0.0d, 0.0d, 1.0d, 1.0d };
		for (int i = 0; i < answers.length; ++i) {
			expected.add(answers[i]);
		}
		
		for (int i = 0; i < 40000; ++i)
			ann.train(in, expected); 
		
		ann.simulate(new double[] { 0.0d, 1.0d });
		System.out.println("0, 1: "+ann.layers.get(ann.layers.size()-1).get(0).output_value);
		ann.simulate(new double[] { 1.0d, 0.0d });
		System.out.println("1, 0: "+ann.layers.get(ann.layers.size()-1).get(0).output_value);
		ann.simulate(new double[] { 1.0d, 1.0d });
		System.out.println("1, 1: "+ann.layers.get(ann.layers.size()-1).get(0).output_value);
		ann.simulate(new double[] { 0.0d, 0.0d });
		System.out.println("0, 0: "+ann.layers.get(ann.layers.size()-1).get(0).output_value);
	}
}