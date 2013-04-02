package com.proetsch.ann;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Config {

	public static int numInputs = 0;
	public static int numOutputs = 0;
	public static int numHiddenLayers = 0;
	public static int numNeuronsPerHiddenLayer = 0;
	
	static void load(String filename) {
		try {
			Scanner fin = new Scanner(new File(filename));
			fin.useDelimiter(",|\n");
			numInputs = fin.nextInt();
			numOutputs = fin.nextInt();
			numHiddenLayers = fin.nextInt();
			numNeuronsPerHiddenLayer = fin.nextInt();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
