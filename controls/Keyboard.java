package controls;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;


/*
 * Class used to operate the keyboard
 */
public class Keyboard {
	
	private Robot robot;
	// initialising the pressedKey to random value to avoid invalid key exception
	private int pressedKey = 48;
	
	public Keyboard()
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
	
	public void keyPress(int key)
	{
		robot.keyPress(key);
	}
	
	public void keyRelease(int key)
	{
		robot.keyRelease(key);
	}
	
	public void moveLeft()
	{
		robot.keyRelease(pressedKey);
		robot.keyPress(KeyEvent.VK_A);
		pressedKey = KeyEvent.VK_A;
	}
	public void moveRight()
	{
		robot.keyRelease(pressedKey);
		robot.keyPress(KeyEvent.VK_D);
		pressedKey = KeyEvent.VK_D;
	}
	public void moveUp()
	{
		robot.keyRelease(pressedKey);
		robot.keyPress(KeyEvent.VK_W);
		pressedKey = KeyEvent.VK_W;
	}
	public void moveDown()
	{
		robot.keyRelease(pressedKey);
		robot.keyPress(KeyEvent.VK_S);
		pressedKey = KeyEvent.VK_S;
	}
	public void jump()
	{
		robot.keyRelease(pressedKey);
		robot.keyPress(KeyEvent.VK_P);
		pressedKey = KeyEvent.VK_P;
	}
	public void action()
	{
		robot.keyRelease(pressedKey);
		robot.keyPress(KeyEvent.VK_O);
		pressedKey = KeyEvent.VK_O;
	}
}
