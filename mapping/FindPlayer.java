package mapping;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import controls.Keyboard;
import main.ShowResult;

public class FindPlayer extends Thread {
	
	private Random rand = new Random();
	
	private int scanHeight, scanWidth;
	ShowResult showResult;
	
	
	public FindPlayer(int width, int length)
	{
		scanWidth = width;
		scanHeight = length;
		showResult = new ShowResult();
		showResult.start();
	}
	
	// move around until player is located, then return its coordinates
	public Coords findPlayer()
	{
		List<Coords> possiblePlayers = new ArrayList<Coords>();
		List<Coords> players = new ArrayList<Coords>();
		try 
		{
			List<Coords> shapeCoords = search(scanScreen(), false, 0);
			
			Keyboard keyboard = new Keyboard();
			boolean searching = true;
			
			while(searching)
			{
				possiblePlayers = new ArrayList<Coords>();
				players = new ArrayList<Coords>();	
				
				Thread.sleep(200);
				keyboard.moveLeft();
				Thread.sleep(200);
				keyboard.moveRight();			
				Thread.sleep(500);
				List<Coords> shapeCoords2 = search(scanScreen(), false, 0);
				
				for(int i=0; i<shapeCoords.size(); i++)
				{

					if(shapeCoords.get(i).getX() > shapeCoords2.get(i).getX())
					{
						possiblePlayers.add(shapeCoords.get(i));
					}
				}
				
				if(possiblePlayers.size() <=0 || possiblePlayers.size() > 1)
				{
					List<Coords> shapeCoords3 = search(scanScreen(), false, 0);
					keyboard.moveRight();
					Thread.sleep(500);
					keyboard.moveRight();
					Thread.sleep(500);
					List<Coords> shapeCoords4 = search(scanScreen(), false, 0);

					for(int i=0; i<shapeCoords3.size(); i++)
					{
						if(shapeCoords3.get(i).getX() < shapeCoords4.get(i).getX())
						{
							players.add(shapeCoords3.get(i));
						}
					}
				}
				else
				{
					players = possiblePlayers;
					searching = false;
				}
				
				if(players.size() == 1)
				{
					searching = false;
				}
			}
			Coords player = new Coords(players.get(0).getX(), players.get(0).getY(), players.get(0).getArea());
			return player;
		}
		catch(Exception e)
		{
			System.out.println("FindPlayer/findPlayer() error: " + e);
			return null;
		}
	}
	
	// find player by area, faster then above method - unimplemented
	public Coords findPlayerByArea(double area)
	{
		List<Coords> shapeCoords = search(scanScreen(), true, area);
		Coords player = shapeCoords.get(0);
		return player;
	}
	private List<Coords> search(BufferedImage image, boolean byArea, double area)
	{
		List<Coords> shapeCoords = new ArrayList<Coords>();
		try
		{
			// load library
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
			BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			convertedImage.getGraphics().drawImage(image, 0, 0, null);
			Mat source = new Mat(convertedImage.getHeight(), convertedImage.getWidth(), CvType.CV_8UC3);
			byte[] imageBytes = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
			source.put(0, 0, imageBytes);
			Mat destination = new Mat(source.rows(), source.cols(), source.type());
			Mat output = new Mat(source.rows(), source.cols(), source.type());
			output.put(0, 0, imageBytes);
			
			// apply grayscale
			Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
			MatOfDouble mu = new MatOfDouble();
			MatOfDouble stddev = new MatOfDouble();
			Core.meanStdDev(destination, mu, stddev);
			double lowThresh = mu.get(0, 0)[0];
			double highThresh = stddev.get(0, 0)[0];
			Imgproc.Canny(source, destination, lowThresh,highThresh);
			
			// blur the image to reduce noise
			Imgproc.GaussianBlur(destination,  destination, new Size(5,5), 0, 0, Core.BORDER_DEFAULT);
			Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_OPEN, new Size(16,16));
			Imgproc.erode(destination, destination, element);
			Imgproc.dilate(destination, destination, element);
			
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(destination, contours, source, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
			for (int i = 0; i<contours.size(); i++)
			{
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if(rect.y > 105 && byArea && rect.area() >= (area-200) && rect.area() <= (area+200))
				{
					Coords c = new Coords(rect.x, rect.y, rect.area());
					shapeCoords.add(c);
					Imgproc.drawContours(output, contours, i, new Scalar(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)),-1);
					
				}
				else if(rect.y > 105)
				{
					Coords c = new Coords(rect.x, rect.y, rect.area());
					shapeCoords.add(c);
					Imgproc.drawContours(output, contours, i, new Scalar(0, 0, 255),-1);
					
				}
			}
			showResult.display(output);
			return shapeCoords;
		}
		catch(Exception e)
		{
			 System.out.println("error: " + e.getMessage());
			 return shapeCoords;
		}
	}
	
	private BufferedImage scanScreen()
	{
		Rectangle rect = new Rectangle(scanWidth, scanHeight);
		try
		{
			final BufferedImage image = new Robot().createScreenCapture(rect);
			return image;
		}
		catch(Exception e)
		{
			System.out.println("FindPlayer/scanScreen() error: " + e);
			return null;
		}
	}
}