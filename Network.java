/* Matthew Proetsch
 * Artificial Neural Network implementation - 
 * COP3930h
 */
package com.proetsch.ann;

import java.util.ArrayList;
import java.util.Arrays;

public class Network {
	
	private static double LEAST_MEAN_SQUARED_ERROR = 1;
	private static double LEARNING_RATE = 0.3;
	private static double MOMENTUM = 0.8;
	private static final String CONFIG_FILENAME = "config";
	private static final int NUM_DAYS = 10;
	
	private double least_mean_squared_error;
	private double learning_rate;
	private double momentum;
	
	// Keep track of how many epocs we've seen so far (incremented at the end of a train() call)
	private int epochs;
	
	// Internal representation of the layers of the ANN
	ArrayList<Layer> layers;
	
	public Network(double lmse, double p, double lr) {
		least_mean_squared_error = lmse;
		momentum = p;
		epochs = 0;
		learning_rate = lr;
		layers = new ArrayList<Layer>();
	}
	
	// For now, initialize() ignores numOutputs, since we are probably just going to have one output -
	//   the value of tomorrow's bitcoin
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
		if (inputs.length != layers.get(0).size() - 1) { // inputLayer.size()-1 to account for bias node
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
	public boolean train(Parse[] inputs) throws Exception {
		// Iterate through all (n-3) iterations, taking all consecutive 3-day cross-sections
		//   of data from day = 2 up to day = n-1
		double mse = 0.0d;
		
		for (int i = 2; i < NUM_DAYS - 3; ++i) {
			double[] annInput = new double[layers.get(0).size() - 1];
			// Calculate error based on closing value of tomorrow's Bitcoin
			double expectedOutput = inputs[0].getClose(i+1);
			int index = 0;
			
			// 1. Simulate network on input
			for (int j = 0; j < inputs.length; ++j) {
				//   a) construct appropriate input using a 3-day data slice from each input
				annInput[index++] = inputs[j].getOpen(i-2);
				annInput[index++] = inputs[j].getHigh(i-2);
				annInput[index++] = inputs[j].getLow(i-2);
				annInput[index++] = inputs[j].getClose(i-2);
				annInput[index++] = inputs[j].getVol(i-2) / (double) 10000;
				annInput[index++] = inputs[j].getOpen(i-1);
				annInput[index++] = inputs[j].getHigh(i-1);
				annInput[index++] = inputs[j].getLow(i-1);
				annInput[index++] = inputs[j].getClose(i-1);
				annInput[index++] = inputs[j].getVol(i-1) / (double) 10000;
				annInput[index++] = inputs[j].getOpen(i);
				annInput[index++] = inputs[j].getHigh(i);
				annInput[index++] = inputs[j].getLow(i);
				annInput[index++] = inputs[j].getClose(i);
				annInput[index++] = inputs[j].getVol(i) / (double) 10000;
			}
			
			simulate(annInput);
			
			// 2. Calculate output layer delta
			Layer outputLayer = layers.get(layers.size() - 1);
			outputLayer.calculateDeltas(expectedOutput);
			
			// Calculate deltas for layers in reverse order (second-to-last, third-to-last, ..., the one before input)
			for (int j = layers.size() - 2; j > 0; --j) {
				// calculateDeltas's argument doesn't matter in the hidden layer
				layers.get(j).calculateDeltas(0);
			}
			
			// 3. Finally, update weights of each link
			for (int j = 1; j < layers.size(); ++j) {
				layers.get(j).correctWeights(learning_rate, momentum);
			}
			
			// Update mean-squared-error
			mse += Math.pow(layers.get(layers.size()-1).get(0).output_value - expectedOutput, 2);
//			System.out.println("Got: "+layers.get(layers.size()-1).get(0).output_value+" Expected: "+expectedOutput);
		}
			
		epochs++;
		if (epochs % 1500 == 0) {
			System.out.println(epochs+" epoch error: "+mse/(double)(15 * inputs.length));
		}
		return (mse / ((double) 15*inputs.length)) < least_mean_squared_error;
	}
	
	public static void main(String[] args) throws Exception {
		
		Config.load(CONFIG_FILENAME);
		Network ann = new Network(LEAST_MEAN_SQUARED_ERROR, MOMENTUM, LEARNING_RATE);
		// 3 days considered at a time for each input file, each day with 5 aspects: open, high, low, close, volume
		ann.initialize(15*Config.numInputs, Config.numNeuronsPerHiddenLayer, Config.numHiddenLayers);
		
		// Load all input files into memory using Jon's Parse class
		Parse[] inputs = new Parse[Config.numInputs];
		for (int i = 0; i < Config.numInputs; ++i) {
			inputs[i] = new Parse(String.valueOf(i+1));
		}
		
		// Train the network on these inputs, taking input file 1 to be the value of bitcoin
		while(! ann.train(inputs));
		
		// How many epochs did it take for the network to learn?
		System.out.println(ann.epochs+" epochs for least mean square error of "+ann.least_mean_squared_error);
	}
}