package main;

import java.io.PrintWriter;
import java.text.*;
import java.util.*;
 
public class NeuralNetwork extends Thread{

	
    static boolean isTrained = true;
    final DecimalFormat df;
    final Random rand = new Random();
    final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
    final static ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
    final static ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
    final Neuron bias = new Neuron();
    final int[] layers;
    int file = 0;
    final int randomWeightMultiplier = 1;
 
    final double epsilon = 0.00000000001;
 
    static double learningRate = 0.7f;
    final double momentum = 0.7f;
    
    TrainingData td = new TrainingData();
    
    double inputs[][] = td.getInputData("input.csv");
    final double expectedOutputs[][] = td.getOutputData("output.csv");
    double resultOutputs[][] = td.getOutputData("output.csv");
    String errors = "";
    double output[];
 
    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();
 
    
    public void setInputs(double[][] input)
    {
    	inputs = input;
    }
 
    public NeuralNetwork(int input, int hidden, int output) 
    {
        this.layers = new int[] { input, hidden, output };
        df = new DecimalFormat("#.0#");
 
        /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         */
        for (int i = 0; i < layers.length; i++) 
        {
            if (i == 0) 
            {
                for (int j = 0; j < layers[i]; j++) 
                {
                    Neuron neuron = new Neuron();
                    neuron.start();
                    inputLayer.add(neuron);
                }
            } 
            else if (i == 1) 
            {
                for (int j = 0; j < layers[i]; j++) 
                {
                    Neuron neuron = new Neuron();
                    neuron.start();
                    neuron.addInConnectionsS(inputLayer);
                    neuron.addBiasConnection(bias);
                    hiddenLayer.add(neuron);
                }
            }
 
            else if (i == 2) 
            {
                for (int j = 0; j < layers[i]; j++) 
                {
                    Neuron neuron = new Neuron();
                    neuron.start();
                    neuron.addInConnectionsS(hiddenLayer);
                    neuron.addBiasConnection(bias);
                    outputLayer.add(neuron);
                }
            } else {
                System.out.println("!Error NeuralNetwork init");
            }
        }
 
     // initialize random weights
        for (Neuron neuron : hiddenLayer) 
        {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) 
            {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : outputLayer) 
        {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) 
            {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        
        Neuron.counter = 0;
        Connection.counter = 0;
 
        if (isTrained) 
        {
        	trainedWeights();
            updateAllWeights();
        }
    }
 
    // random
    double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1);
    }
 
    /**
     * 
     * @param inputs
     *            There is equally many neurons in the input layer as there are
     *            in input variables
     */
    public void setInput(double inputs[]) 
    {
        for (int i = 0; i < inputLayer.size(); i++) 
        {
            inputLayer.get(i).setOutput(inputs[i]);
        }
    }
 
    public double[] getOutput() 
    {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
 
    /**
     * Calculate the output of the neural network based on the input The forward
     * operation
     */
    public void activate() 
    {
        for (Neuron n : hiddenLayer)
            n.calculateOutput();
        for (Neuron n : outputLayer)
            n.calculateOutput();
    }
 
    /**
     * all output propagate back
     * 
     * @param expectedOutput
     *            first calculate the partial derivative of the error with
     *            respect to each of the weight leading into the output neurons
     *            bias is also updated here
     */
    public void applyBackpropagation(double expectedOutput[]) 
    {
 
        // error check, normalize value ]0;1[
        for (int i = 0; i < expectedOutput.length; i++) 
        {
            double d = expectedOutput[i];
            if (d < 0 || d > 1) 
            {
                if (d < 0)
                    expectedOutput[i] = 0 + epsilon;
                else
                    expectedOutput[i] = 1 - epsilon;
            }
        }
 
        int i = 0;
        for (Neuron n : outputLayer) 
        {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) 
            {
                double ak = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double desiredOutput = expectedOutput[i];
 
                double partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }
 
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) 
            {
                double aj = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double sumKoutputs = 0;
                int j = 0;
                for (Neuron out_neu : outputLayer) 
                {
                    double wjk = out_neu.getConnection(n.id).getWeight();
                    double desiredOutput = (double) expectedOutput[j];
                    double ak = out_neu.getOutput();
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }
 
                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
    }
 
    void run(int maxSteps, double minError) 
    {
        int i;
        int ddd = 1;
        System.out.println("Runed: " + ddd);
        ddd++;
        // Train neural network until minError reached or maxSteps exceeded
        double error = 1;
        for (i = 0; i < maxSteps && error > minError; i++) 
        {
            error = 0;
            for (int p = 0; p < inputs.length; p++) 
            {
                setInput(inputs[p]);
 
                activate();
 
                output = getOutput();
                resultOutputs[p] = output;
 
                for (int j = 0; j < expectedOutputs[p].length; j++) 
                {
                    double err = Math.pow(output[j] - expectedOutputs[p][j], 2);
                    error += err;
                }
                applyBackpropagation(expectedOutputs[p]);
            }
        }
 
        printAllWeights();
        System.out.println("-------------------");
        printWeightUpdate();
         
        System.out.println("Sum of squared errors = " + error);
        System.out.println("##### EPOCH " + i+"\n");
        if (i == maxSteps) 
        {
            System.out.println("!Error training try again");
        } 
        else 
        {
            printAllWeights();
            printWeightUpdate();
        }
    }
     
    void printResult()
    {
        System.out.println("NN example with xor training");
        for (int p = 0; p < inputs.length; p++) 
        {
            System.out.print("INPUTS: ");
            for (int x = 0; x < layers[0]; x++) 
            {
                System.out.print(inputs[p][x] + " ");
            }
 
            System.out.print("EXPECTED: ");
            for (int x = 0; x < layers[2]; x++) 
            {
                System.out.print(expectedOutputs[p][x] + " ");
            }
 
            System.out.print("ACTUAL: ");
            for (int x = 0; x < layers[2]; x++) 
            {
                System.out.print(resultOutputs[p][x] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
 
    String weightKey(int neuronId, int conId) 
    {
        return "N" + neuronId + "_C" + conId;
    }
 
    /**
     * Take from hash table and put into all weights
     */
    public void updateAllWeights() 
    {
        // update weights for the output layer
        for (Neuron n : outputLayer) 
        {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) 
        {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) 
            {
                String key = weightKey(n.id, con.id);
                double newWeight = weightUpdate.get(key);
                con.setWeight(newWeight);
            }
        }
    }

 
    public void printWeightUpdate() {
        System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String w = df.format(con.getWeight());
                System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                        + con.id + "), " + w + ");");
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                String w = df.format(con.getWeight());
                System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
                        + con.id + "), " + w + ");");
            }
        }
        System.out.println();
    }
    
    void trainedWeights() {
        weightUpdate.clear();
         
        weightUpdate.put(weightKey(5, 0), .22);
        weightUpdate.put(weightKey(5, 1), -.9);
        weightUpdate.put(weightKey(5, 2), -.09);
        weightUpdate.put(weightKey(5, 3), .65);
        weightUpdate.put(weightKey(5, 4), -.39);
        weightUpdate.put(weightKey(6, 5), -.41);
        weightUpdate.put(weightKey(6, 6), -.32);
        weightUpdate.put(weightKey(6, 7), -.4);
        weightUpdate.put(weightKey(6, 8), -.69);
        weightUpdate.put(weightKey(6, 9), -.93);
        weightUpdate.put(weightKey(7, 10), -2.28);
        weightUpdate.put(weightKey(7, 11), -7.23);
        weightUpdate.put(weightKey(7, 12), 2.44);
        weightUpdate.put(weightKey(7, 13), 7.09);
        weightUpdate.put(weightKey(7, 14), .97);
        weightUpdate.put(weightKey(8, 15), -.26);
        weightUpdate.put(weightKey(8, 16), .83);
        weightUpdate.put(weightKey(8, 17), .54);
        weightUpdate.put(weightKey(8, 18), .57);
        weightUpdate.put(weightKey(8, 19), -.75);
        weightUpdate.put(weightKey(9, 20), -.61);
        weightUpdate.put(weightKey(9, 21), -.89);
        weightUpdate.put(weightKey(9, 22), .56);
        weightUpdate.put(weightKey(9, 23), .17);
        weightUpdate.put(weightKey(9, 24), -.76);
        weightUpdate.put(weightKey(10, 25), -5.4);
        weightUpdate.put(weightKey(10, 26), -8.2);
        weightUpdate.put(weightKey(10, 27), -.01);
        weightUpdate.put(weightKey(10, 28), .81);
        weightUpdate.put(weightKey(10, 29), -6.66);
        weightUpdate.put(weightKey(10, 30), -.58);
    }
 
    public static void printAllWeights() 
    {
        System.out.println("printAllWeights");
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) 
        {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) 
            {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) 
        {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) 
            {
                double w = con.getWeight();
                System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
            }
        }
        System.out.println();
    }
}