package main;

/*
 * Connection class representing connections between neurons
 */
public class Connection {
    double weight = 0;
    double prevDeltaWeight = 0;
    double deltaWeight = 0;
 
    final Neuron leftNeuron;
    final Neuron rightNeuron;
    static int counter = 0;
    final public int id;
 
    public Connection(Neuron fromN, Neuron toN) {
        leftNeuron = fromN;
        rightNeuron = toN;
        id = counter;
        counter++;
    }
 
    public double getWeight() {
        return weight;
    }
 
    public void setWeight(double w) {
        weight = w;
    }
 
    public void setDeltaWeight(double w) {
        prevDeltaWeight = deltaWeight;
        deltaWeight = w;
    }
 
    public double getPrevDeltaWeight() {
        return prevDeltaWeight;
    }
 
    public Neuron getFromNeuron() {
        return leftNeuron;
    }
 
    public Neuron getToNeuron() {
        return rightNeuron;
    }
    public int getCounter()
    {
    	return counter;
    }
}