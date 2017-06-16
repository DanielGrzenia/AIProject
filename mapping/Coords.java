package mapping;

/*
 * Class for holding information about coordiantes of entities
 */
public class Coords {
	
	private int x,y;
	private double area;
	
	public Coords(int x, int y, double areaIn)
	{
		this.x = x;
		this.y = y;
		this.area = areaIn;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}

	public double getArea()
	{
		return area;
	}
	
	public void updateX(int x)
	{
		this.x = x;
	}
	public void updateY(int y)
	{
		this.y = y;
	}
}
