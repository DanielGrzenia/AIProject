package main;

import java.util.Arrays;
import java.util.Random;

/*
 * Single layer perceptron, incapable of handing complex inputs, can learn logic gates
 */
public class NeuronMine {
	
	private double[] weights;
	private double learningRate = 0.2;
	private double th = 0.8;
	
	public NeuronMine(int n)
	{
		Random random = new Random();
		weights = new double[n];
		System.out.println("Weights length: " + weights.length);
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = (random.nextDouble() - 0.5f) * 4f;
			System.out.println("weight: " + weights[i] );
		}
	}
	
	private double activate(double sum)
	{
		if(sum >= th) return 0.8; else return 0;
	}
	
	public double feedforward(double[] inputs)
	{
		double sum = 0;
		for (int i = 0; i<weights.length; i++)
		{
			sum += inputs[i]*weights[i];
		}
		System.out.println("SUM: " + sum);
		System.out.println("Sigmoid: " + sigmoid(sum));
		return activate(sum);
	}
	public boolean train(double[][][] inputs)
	{
		boolean train = true;
		int training = 0;
		while(train)
		{
			training++;
			System.out.println("Attempt: " + training);
			int errorCount = 0;
			for(int i = 0; i<inputs.length; i++)
			{	
				// calculate weighted sum of inputs
				double weightedSum = 0;
				for(int ii = 0; ii<inputs[i][0].length; ii++)
				{
					weightedSum += inputs[i][0][ii] * weights[ii];
					System.out.println("Input: " + inputs[i][0][ii]);
					System.out.println("Weight: " + weights[ii]);
				}
				System.out.println("Threshold: " + th + "\nWeightedSum: " + weightedSum);
				
				// calculate output
				double output = 0;
				System.out.println("weightedsUm sigmoid: " + sigmoid(weightedSum));
				if(th <= weightedSum) output = 0.8;
				System.out.println("Target output: " + inputs[i][1][0] + ", Actual output: " + output);
				
				// calculate error
				double error = inputs[i][1][0] - output;
				// increase error count for incorrect outputs
				if(error != 0)
				{ 
					errorCount++;
				}
				System.out.println("ErrorCount: " + errorCount);
				
				// update weights
				for(int ii = 0; ii<inputs[i][0].length; ii++)
				{
					System.out.println("Error: " + error);
					weights[ii] += learningRate * error * inputs[i][1][0];
					System.out.println("inputs: " + inputs[i][0][ii]);
					
				}
				
				System.out.println("New weights: " + Arrays.toString(weights));
				System.out.println("------------------------------------------------");
			}
			
			// if there are no errors, stop
			if(errorCount == 0)
			{
				System.out.println("Final weights: " + Arrays.toString(weights));
				train = false;
			}
			if(weights[0] > 1 || weights[1] > 1) System.exit(0);
		}
		return train;
	}
	public double sigmoid(double sum)
	{
		sum = sum/1000;
		double sig = (1.0 / (1 + Math.exp(-sum)));
		sig = sig * (1-sig);
		return sig;
	}
}
