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
import org.opencv.imgproc.Imgproc;

/*
 * Class used to identify pitfalls on the screen
 */

public class FindPit {
	
	private int scanWidth, scanHeight;
	
	public FindPit(int width, int height)
	{
		scanWidth = width;
		scanHeight = height;
	}
	// check distance between floor entities, if it is big enough return pitfall location
	public int findtPit()
	{
		int pitFall = 0;
		List<Coords> shapeCoords = search(scanScreen());
		int ii = 1;
		for(int i=0; i<shapeCoords.size(); i++)
		{
			int t = shapeCoords.get(i).getX() - shapeCoords.get(ii).getX();
			if(t > 35)
			{
				pitFall =  shapeCoords.get((i+1)).getX();
			}
			else if(shapeCoords.size() > (ii+1))
			{
				ii++;
			}
		}
		return pitFall;
	}
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
			MatOfDouble mu = new MatOfDouble();
			MatOfDouble stddev = new MatOfDouble();
			// calculating Canny
			Core.meanStdDev(destination, mu, stddev);
			double lowThresh = mu.get(0, 0)[0];
			double highThresh = stddev.get(0, 0)[0];
			Imgproc.Canny(source, destination, lowThresh,highThresh);
			
			// blur the image to reduce noise
			Imgproc.GaussianBlur(destination,  destination, new Size(7,7), 0, 0, Core.BORDER_DEFAULT);
			Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20,20));
			Imgproc.erode(destination, destination, element);
			Imgproc.dilate(destination, destination, element);
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(destination, contours, source, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
			for (int i = 0; i<contours.size(); i++)
			{
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if(rect.y > 465)
				{
					Coords c = new Coords(rect.x, rect.y, 0);
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
