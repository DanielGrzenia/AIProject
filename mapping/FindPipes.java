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
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


/*
 * Allows for finding pipes on the screen, limited use only in Mario - not tested
 */
public class FindPipes {
	
	Random rand = new Random();
	private int l;
	private int scanWidth, scanHeight;
	
	public FindPipes(int width, int height)
	{
		scanWidth = width;
		scanHeight = height;
	}
	public void findtPipes()
	{	
		l = 1;
		boolean searching = true;
		while(searching)
		{
			List<Coords> shapeCoords = search(scanScreen(), l);
			l++;
			if(shapeCoords.size() <= 0)
			{
				searching = false;
			}
		}
		
		System.out.println("");
	}
	
	// find all pipes on the screen and return them as an array
	private List<Coords> search(BufferedImage image, int l)
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
			Imgcodecs.imwrite("C:/Users/Daniel/Desktop/match/output.jpg", destination);
			MatOfDouble mu = new MatOfDouble();
			MatOfDouble stddev = new MatOfDouble();
			// calculating Canny
			Core.meanStdDev(destination, mu, stddev);
			double lowThresh = mu.get(0, 0)[0];
			double highThresh = stddev.get(0, 0)[0];
			Imgproc.Canny(source, destination, lowThresh,highThresh);
			
			// blur the image to reduce noise
			Imgproc.GaussianBlur(destination,  destination, new Size(3,3), 0, 0, Core.BORDER_DEFAULT);
			Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_OPEN, new Size((l),(l)));
			Imgproc.erode(destination, destination, element);
			Imgproc.dilate(destination, destination, element);	
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(destination, contours, source, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
			for (int i = 0; i<contours.size(); i++)
			{
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if(rect.y > 105)
				{
					Coords c = new Coords(rect.x, rect.y, 0);
					shapeCoords.add(c);
				}
			}
			return shapeCoords;
			
		}
		catch(Exception e)
		{
			 System.out.println("FindPipes/search() error: " + e);
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
			System.out.println("FindPipes/scanScreen() error: " + e);
			return null;
		}
	}
}
