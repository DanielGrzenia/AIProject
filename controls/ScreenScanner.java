package controls;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * Initial method of scanning and comparing game images
 */
public class ScreenScanner {
	
	private Rectangle rect;
	BufferedImage image; 
	
	public ScreenScanner(int width, int height)
	{
		rect = new Rectangle(width,height);
	}
	public BufferedImage capture() throws IOException
	{
		try 
		{
			image = new Robot().createScreenCapture(rect);
			return image;
		} 
		catch (AWTException e) 
		{
			System.out.println(e);
			return image;
		}
	}
	public boolean compare(BufferedImage img1, BufferedImage img2)
	{
		for (int x = 1; x<img2.getWidth(); x++)
		{
			for(int y = 1; y<img2.getHeight(); y++)
			{
				if(img1.getRGB(x, y) != img2.getRGB(x, y))
				{
					return true;
				}
			}
		}
		return false;
	}
}