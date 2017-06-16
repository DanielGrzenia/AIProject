package main;

import java.util.ArrayList;
import java.util.List;

import controls.Keyboard;
import mapping.Coords;
import mapping.Entity;
import mapping.FindEntities;
import mapping.FindPlayer;


public class main {

	public static void main(String[] args) {
		
		
		try
		{
			FindEntities fe = new FindEntities(510,503);
			fe.start();
			FindPlayer fp = new FindPlayer(510,503);
			fp.start();
			NeuralNetwork nn = new NeuralNetwork(4, 4, 1);
			nn.start();
			Keyboard keyboard = new Keyboard();
			
			List<Entity> entities = new ArrayList<Entity>();
			double[][] input = new double[1][4];
			
			while(true)
			{
				try
				{
					entities = fe.identifyEntites();
					Coords player = fp.findPlayer();
					
					input[0][0] = player.getX();
					input[0][1] = player.getY();
					
					for(int i=0; i<entities.size(); i++)
					{
						input[0][2] = entities.get(i).getX();
						input[0][3] = entities.get(i).getY();
						
						nn.setInputs(input);
						nn.activate();
						
						/*
						 * action = 0.9
						 * right = 0.8
						 * left = 0.7
						 * down = 0.6
						 * up = 0.5
						 */
						double action = nn.getOutput()[0];
						if(action <= 0.9 && action > 0.8)
						{
							keyboard.action();
						}
						else if(action <= 0.8 && action > 0.7)
						{
							keyboard.moveRight();
						}
						else if(action <= 0.7 && action > 0.6)
						{
							keyboard.moveLeft();
						}
						else if(action <= 0.6 && action > 0.5)
						{
							keyboard.moveDown();
						}
						else if(action <= 0.5 && action > 0.4)
						{
							keyboard.moveUp();
						}
						else if(action <= 0.4 && action > 0.3)
						{
							keyboard.jump();
						}
					}
				}
				catch(Exception e)
				{
					System.out.println("main error: " + e);
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("main error: " + e);
		}
		}
		
}