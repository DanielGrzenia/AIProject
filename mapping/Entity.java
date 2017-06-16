package mapping;

/*
 * Class holding information about entities
 */
public class Entity {
	
	private int type, state, x, y;
	private double area;
	private double[][] loc;
	
	// state, type and area currently unused in the system
	public Entity(int state, int type, int x, int y, double area)
	{
		this.state = state;
		this.type = type;
		this.x = x;
		this.y = y;
		this.area = area;
		loc = new double[1][2];
		loc[0][0] = x;
		loc[0][1] = y;
	}
	
	public int getState()
	{
		return state;
	}
	public int getType()
	{
		return type;
	}
	public int getY()
	{
		return y;
	}
	public int getX()
	{
		return x;
	}
	public double getArea()
	{
		return area;
	}
	public double[][] getLoc()
	{
		return loc;
	}

}
