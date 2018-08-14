package ar.wargus.gszirzdovvdetection.mapObjects;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class Rectangle
	extends Object2D{
	
	private Integer minX,
					minY,
					maxX,
					maxY;
	
	public Rectangle(){}
	
	public Rectangle(int minX,
	                 int minY,
	                 int maxX,
	                 int maxY){
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		Arrays.asList(new Line(minX, minY, maxX, minY),
		              new Line(minX, minY, minX, maxY),
		              new Line(maxX, maxY, minX, maxY),
		              new Line(maxX, maxY, maxX, minY));
	}
	
	@Override
	public Rectangle getOuterRectangle() {
		return this;
	}
	
	@NonNull
	@Override
	public List<Line> getEdges() {
		return edges.isEmpty()
		       ? edges = Arrays.asList(new Line(minX, minY, maxX, minY),
				                       new Line(minX, minY, minX, maxY),
				                       new Line(maxX, maxY, minX, maxY),
				                       new Line(maxX, maxY, maxX, minY))
			   : edges;
	}
	
	@Override
	public String toString() {
		return "Rectangle: " + Arrays.asList(minX, minY, maxX, maxY).toString();
	}
	
	public Integer getMinX() {
		return minX;
	}
	
	public Integer getMinY() {
		return minY;
	}
	
	public Integer getMaxX() {
		return maxX;
	}
	
	public Integer getMaxY() {
		return maxY;
	}
	
	public void setMinX(Integer minX) {
		this.minX = minX;
	}
	
	public void setMinY(Integer minY) {
		this.minY = minY;
	}
	
	public void setMaxX(Integer maxX) {
		this.maxX = maxX;
	}
	
	public void setMaxY(Integer maxY) {
		this.maxY = maxY;
	}
}
