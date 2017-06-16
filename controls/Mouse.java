package controls;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

/*
 * Class allowing for control of the mouse pointer
 */
public class Mouse {
	
	private Robot robot;
	
	public Mouse()
	{
		try
		{
			robot = new Robot();
		}
		catch(AWTException e)
		{
			System.out.println(e);
		}
	}
	
	public void move(int xIn, int yIn)
	{
		int x = 0, y = 0;
		
		for(int i=0; i<100; i++)
		{			
			Point mouseCP = MouseInfo.getPointerInfo().getLocation();
			
			x = ((xIn * i)/100) + (mouseCP.x*(100-i)/100);
			y = ((yIn * i)/100) + (mouseCP.y*(100-i)/100);
			
			robot.mouseMove(x, y);
			
			try
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				System.out.println(e);
			}
		}
	}
	
	public void click(int button)
	{
		if(button == 1)
		{
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		else
		{
			robot.mousePress(InputEvent.BUTTON3_MASK);
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		}
	}
	
	public void buttonHold(int button)
	{
		if(button == 1)
		{
			robot.mousePress(InputEvent.BUTTON1_MASK);
		}
		else
		{
			robot.mousePress(InputEvent.BUTTON3_MASK);
		}
	}
	public void buttonRelease(int button)
	{
		if(button == 1)
		{
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		else
		{
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		}
	}
}
