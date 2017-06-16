package main;

import java.io.BufferedReader;
import java.io.FileReader;

/*
 * Class used to load training data from local files
 */
public class TrainingData {
	
	public double[][] getInputData(String fileName)
	{
		//training matrix type
		double[][] data = new double[26928][4];
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			int row = 0;
			while((line = reader.readLine()) != null)
			{
			   String[] cols = line.split(",");
			   int col = 0;
			   for(String  cc : cols)
			   {
			      data[row][col] = Double.parseDouble(cc);
			      col++;
			   }
			   row++;
			}
			reader.close();
			return data;
		}
		catch(Exception e)
		{
			System.out.println("Error while reading traning set: " + e);
			return data;
		}
	}
	public double[][] getOutputData(String fileName)
	{
		double[][] data = new double[26928][1];
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			int row = 0;
			while((line = reader.readLine()) != null)
			{
			   String[] cols = line.split(",");
			   int col = 0;
			   for(String  cc : cols)
			   {
			      data[row][col] = Double.parseDouble(cc);
			      col++;
			   }
			   row++;
			}
			reader.close();
			return data;
		}
		catch(Exception e)
		{
			System.out.println("Error while reading traning set: " + e);
			return data;
		}
	}

}
