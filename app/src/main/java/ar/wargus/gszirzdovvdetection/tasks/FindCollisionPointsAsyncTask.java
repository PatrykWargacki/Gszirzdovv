package ar.wargus.gszirzdovvdetection.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ar.wargus.gszirzdovvdetection.classes.BluetoothRadar;
import ar.wargus.gszirzdovvdetection.helper.Utils;
import ar.wargus.gszirzdovvdetection.mapObjects.Line;
import ar.wargus.gszirzdovvdetection.mapObjects.Object2D;
import ar.wargus.gszirzdovvdetection.mapObjects.Point;
import ar.wargus.gszirzdovvdetection.mapObjects.Rectangle;
import ar.wargus.gszirzdovvdetection.mock.MockMapObjectCreator;

public class FindCollisionPointsAsyncTask extends AsyncTask<Void,
                                                            Void,
                                                            Void> {
	private List<Line>                  sharedLines;
	private List<Pair<Point, Object2D>> sharedPoints;
	
	private boolean shouldRun = true;
	
	public FindCollisionPointsAsyncTask(List<Line>                  sharedlines,
	                                    List<Pair<Point, Object2D>> sharedPoints) {
		this.sharedLines = sharedlines;
		this.sharedPoints = sharedPoints;
	}
	
	@Override
	protected Void doInBackground(Void... voids) {
		while (shouldRun) {
			while (!sharedLines.isEmpty()) {
				addCollisionPointsOfLineObjects(sharedLines.remove(0),
				                                MockMapObjectCreator.additObjects,
				                                sharedPoints);
			}
		}
		return null;
	}
	
	public void shutdown() {
		shouldRun = false;
	}
	
	private void addCollisionPointsOfLineObjects(Line line,
                                                 List<Object2D> objects,
                                                 List<Pair<Point, Object2D>> sharedPoints) {
//		List<Pair<Point, Object2D>> points = new ArrayList<Pair<Point, Object2D>>();
		java.util.Map<Double, Pair<Point, Object2D>> localPoints = new HashMap<Double, Pair<Point, Object2D>>();
		Set<Double> distances = new TreeSet<>();
		
		// znajdz wszystkie punkty kolizji z obiektem
		for (Object2D object : objects) {
			//sprawdz (w miarę szybko) czy otaczający prostokąt jest w zasięgu nadajnika
			Rectangle outerRect = object.getOuterRectangle();
			if (!(Utils.pointPointDistance(outerRect.getMinX(),
			                               outerRect.getMinY(),
			                               line.getP2().x,
			                               line.getP2().y) < BluetoothRadar.maxDistanceToPoint
			      || Utils.pointPointDistance(outerRect.getMaxX(),
			                                  outerRect.getMaxY(),
			                                  line.getP2().x,
			                                  line.getP2().y) < BluetoothRadar.maxDistanceToPoint
			      || Utils.pointPointDistance(outerRect.getMaxX(),
			                                  outerRect.getMinY(),
			                                  line.getP2().x,
			                                  line.getP2().y) < BluetoothRadar.maxDistanceToPoint
			      || Utils.pointPointDistance(outerRect.getMinX(),
			                                  outerRect.getMaxY(),
			                                  line.getP2().x,
			                                  line.getP2().y) < BluetoothRadar.maxDistanceToPoint))
				continue;
			
			for (Line edge : object.getEdges()) {
				Point intersectionPoint = Utils.detect(line,
				                                       edge);
				if (intersectionPoint == null) continue;
				
				double distance = Utils.pointPointDistance(intersectionPoint,
				                                           line.getP2());
				distances.add(distance);
				localPoints.put(distance,
				                new Pair<Point, Object2D>(intersectionPoint,
				                                          object));
			}
			
			if (localPoints.size() > 1) {
				for (double d : distances) {
					Point intersectionPoint = localPoints.get(d).first;
					MockMapObjectCreator.additRects.add(new Rectangle(intersectionPoint.x - 1,
										                              intersectionPoint.y - 1,
										                              intersectionPoint.x + 1,
										                              intersectionPoint.y + 1));
					sharedPoints.add(localPoints.get(d));
				}
			}
			
			distances.clear();
			localPoints.clear();
		}
	}
}
