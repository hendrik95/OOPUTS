import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.text.DecimalFormat;
import java.awt.geom.*;
import java.awt.*;
public class Hole
{
	private double x, y, r;
	private Ellipse2D.Double circle;
	
	public Hole(double x, double y, double r)
	{
		this.x = x;
		this.y = y;
		this.r = r;
		circle = new Ellipse2D.Double(x-r, y-r, r*2, r*2);
	}
	
	public double getX() {return x;}
	public double getY() {return y;}
	public double getR() {return r;}
	
	public double distance(double xc, double yc)
	{
		double a = x - xc;
		double b = y - yc;

		return Math.sqrt(a*a + b*b);
	}
	public void draw(Graphics2D g)
	{
		g.setColor(Color.gray);
		g.fill(circle);
	}
}