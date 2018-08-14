package ar.wargus.gszirzdovvdetection.mapObjects;

public class Point {
	public Integer x;
	public Integer y;
	
	public Point() {}
	
	public Point(Integer x,
	             Integer y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public void setX(Integer x) { this.x = x; }
	public void setY(Integer y) { this.y = y; }
}
