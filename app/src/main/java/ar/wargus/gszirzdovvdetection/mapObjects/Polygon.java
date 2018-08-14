package ar.wargus.gszirzdovvdetection.mapObjects;

import java.util.List;

public class Polygon
	extends Object2D {
	
	public Polygon(){}
	
	public Polygon(List<Line> edges){
		this.edges = edges;
	}
	
	public void setNewPoints(Point... points){
		Point firstPoint    = points[0];
		Point lastPoint     = points[0];
		
		for(int i = 1; i < points.length; i++){
			Point p = points[i];
			edges.add(new Line(lastPoint, p));
			lastPoint = p;
		}
		
		edges.add(new Line(lastPoint, firstPoint));
	}
}
