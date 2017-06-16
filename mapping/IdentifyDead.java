package mapping;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;

/*
 * Class to identifying dead player, not needed for supervised learning, created to work with genetic algorithm
 */
public class IdentifyDead extends Thread {
	
	private int scanWidth, scanHeight;
	private BufferedImage templateImg;
	
	public IdentifyDead(int width, int height, BufferedImage templateImg)
	{
		scanWidth = width;
		scanHeight = height;
		this.templateImg = templateImg;
	}
	public boolean find()
	{
		// load library and images
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat source = convertToMat(scanScreen());
		Mat template = convertToMat(templateImg);
		
		// create result mat
		int cols = source.cols() - template.cols() + 1;
		int rows = source.rows() - template.rows() + 1;
		Mat result = new Mat(rows, cols, CvType.CV_32FC1);
		
		// Match and normalise
		Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);
		
		// find best match
		MinMaxLocResult locResult = Core.minMaxLoc(result);
		
		// threshold tested only on Mario (0.87)
		if(locResult.maxVal >= 0.8) return true;
		else return false;

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
			System.out.println("IdentifyDead/scanScreen() error: " + e);
			return null;
		}
	}
	
	private Mat convertToMat(BufferedImage image)
	{
		BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		convertedImage.getGraphics().drawImage(image, 0, 0, null);
		Mat source = new Mat(convertedImage.getHeight(), convertedImage.getWidth(), CvType.CV_8UC3);
		byte[] imageBytes = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
		source.put(0, 0, imageBytes);
		return source;
	}
}
