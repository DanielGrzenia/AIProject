package mapping;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FindEntities extends Thread{
	
	private int scanWidth, scanHeight;
	
	public FindEntities(int width, int height)
	{
		scanWidth = width;
		scanHeight = height;
	}
	// creating an array of entities, which would later be used to identify and classify them - unimplemented
	public List<Entity> identifyEntites()
	{
		List<Entity> entities = new ArrayList<Entity>();
		try 
		{
			List<Coords> shapeCoords = search(scanScreen());
			List<Coords> shapeCoords2 = search(scanScreen());
			
			for(int i=0; i<shapeCoords.size(); i++)
			{
				try
				{
					if(shapeCoords.get(i).getX() == shapeCoords2.get(i).getX()
							&& shapeCoords.get(i).getY() == shapeCoords2.get(i).getY())
					{
						Entity entity = new Entity(1, 3, shapeCoords.get(i).getX(), shapeCoords.get(i).getY(), shapeCoords.get(i).getArea());
						entities.add(entity);
					}
					else if(shapeCoords.get(i).getX() != shapeCoords2.get(i).getX()
							|| shapeCoords.get(i).getY() != shapeCoords2.get(i).getY())
					{
						Entity entity = new Entity(2, 3, shapeCoords.get(i).getX(), shapeCoords.get(i).getY(), shapeCoords.get(i).getArea());
						entities.add(entity);
					}
				}
				catch(Exception e)
				{
					System.out.println("FindEntities/indentifyEntities() error: " + e);
				}
			}
			return entities;
		} 
		catch (Exception e) 
		{
			System.out.println("FindEntities/indentifyEntities() error: " + e);
			return entities;
		}
	}
	// identifying entities location on the screen and returning them in an array
	private List<Coords> search(BufferedImage image)
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
			Imgcodecs.imwrite("gray.jpg", destination);
			MatOfDouble mu = new MatOfDouble();
			MatOfDouble stddev = new MatOfDouble();
			// calculating Canny
			Core.meanStdDev(destination, mu, stddev);
			double lowThresh = mu.get(0, 0)[0];
			double highThresh = stddev.get(0, 0)[0];
			Imgproc.Canny(source, destination, lowThresh,highThresh);
			
			// blur the image to reduce noise
			Imgproc.GaussianBlur(destination,  destination, new Size(5,5), 0, 0, Core.BORDER_DEFAULT);
			Imgcodecs.imwrite("blur.jpg", destination);
			Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_OPEN, new Size(13,13));
			Imgproc.erode(destination, destination, element);
			Imgproc.dilate(destination, destination, element);
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(destination, contours, source, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
			for (int i = 0; i<contours.size(); i++)
			{
				Rect rect = Imgproc.boundingRect(contours.get(i));
				double area = rect.width * rect.height;
				
				if(rect.y > 105)
				{
					Coords c = new Coords(rect.x, rect.y, area);
					shapeCoords.add(c);
				}
			}
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
			BufferedImage image = new Robot().createScreenCapture(rect);
			return image;
		}
		catch(Exception e)
		{
			System.out.println("FindEntities/scanScreen() error: " + e);
			return null;
		}
	}
}
