package ar.wargus.gszirzdovvdetection.mapObjects;

import android.support.annotation.NonNull;

import java.util.Arrays;

public class Line{
	
	private Point p1;
	private Point p2;
	
	private Integer x1,
					y1,
					x2,
					y2;
			
	public Line(){}
	
	public Line(@NonNull
			    Point p1,
	            @NonNull
			    Point p2) {
		this.p1 = p1;
		this.p2 = p2;
		x1 = p1.x;
		y1 = p1.y;
		x2 = p2.x;
		y2 = p2.y;
	}
	
	public Line(int x1,
	            int y1,
	            int x2,
	            int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		p1 = new Point(x1,
		               y1);
		p2 = new Point(x2,
		               y2);
	}
	
	@Override
	public String toString() {
		return "Line: " + Arrays.asList(p1.x, p1.y, p2.x, p2.y).toString();
	}
	
	public Point    getP1() { return p1; }
	public Point    getP2() { return p2; }
	public Integer  getX1() { return x1; }
	public Integer  getY1() { return y1; }
	public Integer  getX2() { return x2; }
	public Integer  getY2() { return y2; }
	
	public void setP1(Point p1) { this.p1 = p1; }
	public void setP2(Point p2) { this.p2 = p2; }
	
	public void setX1(Integer x1) {
		this.x1 = x1;
		if(y1 != null) p1 = new Point(x1,
		                              y1);
	}
	public void setY1(Integer y1) {
		this.y1 = y1;
		if(x1 != null) p1 = new Point(x1,
		                              y1);
	}
	public void setX2(Integer x2) {
		this.x2 = x2;
		if(y2 != null) p1 = new Point(x2,
		                              y2);
	}
	public void setY2(Integer y2) {
		this.y2 = y2;
		if(x2 != null) p1 = new Point(x2,
		                              y2);
	}
}