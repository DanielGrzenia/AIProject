package main;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;

/*
 * Class displaying what does the neural network actually sees on the screen
 */
public class ShowResult extends Thread{
	
	JFrame frame;
	JLabel pic;
	
	public ShowResult()
	{
		pic = new JLabel();
		frame = new JFrame("Preview");
		frame.setSize(600,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private BufferedImage bufferedImage(Mat mat)
	{
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if(mat.channels() > 1)
		{
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = mat.channels()*mat.cols()*mat.rows();
		byte[] bytes = new byte[bufferSize];
		mat.get(0, 0, bytes);
		BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
		return image;
	}
	public void display(Mat mat)
	{
		ImageIcon image = new ImageIcon(bufferedImage(mat));
		try
		{
			pic.setIcon(image);
			frame.add(pic);
			frame.pack();
		}
		catch(Exception e)
		{
			System.out.println("showResult exception: " + e);
		}
	}
}
