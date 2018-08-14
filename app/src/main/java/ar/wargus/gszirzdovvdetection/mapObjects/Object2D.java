package ar.wargus.gszirzdovvdetection.mapObjects;

import android.graphics.Point;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.wargus.gszirzdovvdetection.R;
import ar.wargus.gszirzdovvdetection.helper.Utils;

public abstract class Object2D {
//	@NonNull
//	Rectangle outerRectangle = new Rectangle();
	
	@NonNull
	List<Line> edges = new ArrayList<Line>();
	
//	double signalModifier = R.integer.Object2D_signalModifier;
	double signalModifier = 1;
	
	public double       getSignalModifier   () { return signalModifier; }
	@NonNull
	public List<Line>   getEdges            () { return edges;          }
	
	public void setEdges(@NonNull Line... edges) { this.edges = Arrays.asList(edges); }
	
	public Rectangle getOuterRectangle(){
		if(edges.isEmpty()){ return null; }
		
		int minY, minX, maxY, maxX;
		minX = maxX = edges.get(0).getP1().x;
		minY = maxY = edges.get(0).getP1().y;

		for (Line line: edges) {
			if      (line.getP1().x < minX) minX = line.getP1().x;
			else if (line.getP1().x > maxX) maxX = line.getP1().x;
			if      (line.getP2().x < minX) minX = line.getP2().x;
			else if (line.getP2().x > maxX) maxX = line.getP2().x;
			if      (line.getP1().y < minY) minY = line.getP1().y;
			else if (line.getP1().y > maxY) maxY = line.getP1().y;
			if      (line.getP2().y < minY) minY = line.getP2().y;
			else if (line.getP2().y > maxY) maxY = line.getP2().y;
		}

		return new Rectangle(minX,
		                     minY,
		                     maxX,
		                     maxY);
	}
}
