package ar.wargus.gszirzdovvdetection.mock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Pair;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ar.wargus.gszirzdovvdetection.R;
import ar.wargus.gszirzdovvdetection.classes.BluetoothRadar;
import ar.wargus.gszirzdovvdetection.helper.DependencyResolver;
import ar.wargus.gszirzdovvdetection.helper.Utils;
import ar.wargus.gszirzdovvdetection.mapObjects.GridFrag;
import ar.wargus.gszirzdovvdetection.mapObjects.Line;
import ar.wargus.gszirzdovvdetection.mapObjects.Map;
import ar.wargus.gszirzdovvdetection.mapObjects.Object2D;
import ar.wargus.gszirzdovvdetection.mapObjects.Point;
import ar.wargus.gszirzdovvdetection.mapObjects.Polygon;
import ar.wargus.gszirzdovvdetection.mapObjects.Rectangle;

public class MockMapObjectCreator{
	
	//	Obiekty 2D mają listę par wierzchołków (krawędzi) i metodę intersects
//
//	PObierz mapę z zaktualizowaną mapą zasięgu,
//	gdy różne miejsca będą miały podobne wartości co klient wybierz najbliższe względem ostatniej lokacji
	
	
	private static List<Rectangle>  additRects      = new ArrayList<Rectangle>();
	private static List<Object2D>   additObjects    = new ArrayList<Object2D>();
	private static List<Line>       additLines   = new ArrayList<Line>();
	
	/*
	Use with caution
	CPU may explode
	so uneffective way
	 */
	public static void createMap(List<Object2D>         objects,
	                             List<BluetoothRadar>   radars) {
		additObjects = objects;
		Map map = Map.getInstance();
		
		for(GridFrag frag: map.getFragments()){
			Point fragPoint = new Point(frag.getX_coordinate() + GridFrag.width,
			                            frag.getY_coordinate() + GridFrag.height);
			
			for(BluetoothRadar radar: radars) {
				// dostań środkowe współrzędne
				Point radarPoint = new Point(radar.getX_coordinate(),
				                             radar.getY_coordinate());
				
				if(Utils.pointPointDistance(fragPoint, radarPoint) > BluetoothRadar.maxDistanceToPoint) continue;
				
				Line line = new Line(fragPoint,
				                     radarPoint);
				additLines.add(line);
				List<Pair<Point, Object2D>> points = forLineFindCollisionPoints(line, objects);
				
				int signal_strength = calcSignalStrength(points,
				                                         radar.getIn_object(),
				                                         radarPoint,
				                                         fragPoint);
				
				// jeśli siła sygnału jest to dodaj ją do mapy
				if(signal_strength > 0) {
					frag.getBluetoothRadarStrengthMap()
					    .put(radar.getIdentifier(),
					         signal_strength);
				}
			}
		}
	}
	
	private static List<Pair<Point, Object2D>> forLineFindCollisionPoints(Line line,
	                                                                      List<Object2D> objects){
		List<Pair<Point, Object2D>> points = new ArrayList<Pair<Point, Object2D>>();
		java.util.Map<Double, Pair<Point, Object2D>> localPoints = new HashMap<Double, Pair<Point, Object2D>>();
		Set<Double> distances = new TreeSet<>();
		
		// znajdz wszystkie punkty kolizji z obiektem
		for(Object2D object: objects){
			//sprawdz (w miarę szybko) czy otaczający prostokąt jest w zasięgu nadajnika
			Rectangle outerRect = object.getOuterRectangle();
			if(!(Utils.pointPointDistance(outerRect.getMinX(),
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
			                                 line.getP2().y) < BluetoothRadar.maxDistanceToPoint)) continue;
			
			for (Line edge : object.getEdges()) {
				Point intersectionPoint = Utils.detect(line,
				                                       edge);
				if (intersectionPoint == null) continue;
				
				double distance = Utils.pointPointDistance(intersectionPoint,
				                                           line.getP2());
				distances   .add(distance);
				localPoints .put(distance, new Pair<Point, Object2D>(intersectionPoint,
				                                                     object));
			}
			
			if(localPoints.size()>1){
				for(double d: distances){
					Point intersectionPoint = localPoints.get(d).first;
					additRects.add(new Rectangle(intersectionPoint.x - 1,
					                             intersectionPoint.y - 1,
					                             intersectionPoint.x + 1,
					                             intersectionPoint.y + 1));
					points.add(localPoints.get(d));
				}
			}
			
			distances   .clear();
			localPoints .clear();
		}
		return points;
	}
	
	private static int calcSignalStrength(List<Pair<Point, Object2D>> points,
	                                      boolean radarInObject,
	                                      Point radarPoint,
	                                      Point fragPoint){
		double  last_signal_strength    = BluetoothRadar.maxSignalStrength;
		boolean use_signal_modifier     = radarInObject;
		Point   last_point              = radarPoint;
		
		Pair<Point, Object2D>   last_pair = null;
		Point                   point;
		for(Pair<Point, Object2D> pair: points){
			last_pair   = pair;
			point       = pair.first;
			double distance = Utils.pointPointDistance(point,
			                                           last_point);
			last_point = point;
			last_signal_strength = last_signal_strength
			                       - BluetoothRadar.signalLostOverOneUnit
			                         * distance;
			if(use_signal_modifier){ last_signal_strength = last_signal_strength
			                                                - pair.second.getSignalModifier()
			                                                  * distance; }
			use_signal_modifier = !use_signal_modifier;
		}
		
		// oblicz jeszcze od ostatniego punktu do środka fragmentu
		double distance = Utils.pointPointDistance(fragPoint,
		                                           last_point);
		last_signal_strength = last_signal_strength
		                       - BluetoothRadar.signalLostOverOneUnit
		                         * distance;
		
		// jeśli punkt fragmentu jest wewn innego obiektu uwzględnij jego modifier
		if(last_pair != null
		   && use_signal_modifier){
			last_signal_strength = last_signal_strength
			                       - last_pair.second.getSignalModifier()
			                         * distance;
		}
		
		// ostatnie modyfikacje przed dodaniem siły sygnału do mapy
		return last_signal_strength     < 1
			   && last_signal_strength  > 0
		       ? 1
		       : (int) Math.round(last_signal_strength);
	}
	
	public static Bitmap visalizeMap(List<BluetoothRadar> radars){
		Map     map     = Map   .getInstance();
		Bitmap  bitmap  = Bitmap.createBitmap(Map.width,
		                                      Map.height,
		                                      Bitmap.Config.ARGB_8888);
		Canvas  c       = new Canvas(bitmap);
		Paint   paint   = new Paint();
		
		for(GridFrag frag: map.getFragments()){
			int[] color = {0,0,0};
			for(BluetoothRadar radar: radars) {
				if (!frag.getBluetoothRadarStrengthMap()
				         .containsKey(radar.getIdentifier())) { continue; }
				
				int strength = frag.getBluetoothRadarStrengthMap()
				                   .get(radar.getIdentifier());
				
				color[0] += (strength * radar.getColor()[0]) / radar.getSignalStrength();
				color[1] += (strength * radar.getColor()[1]) / radar.getSignalStrength();
				color[2] += (strength * radar.getColor()[2]) / radar.getSignalStrength();
			}
			if(color[0] > 255) color[0] = 255;
			if(color[1] > 255) color[1] = 255;
			if(color[2] > 255) color[2] = 255;
			paint.setColor(Color.argb(255,color[0],color[1],color[2]));
			c.drawRect(new RectF(frag.getX_coordinate(),
			                     frag.getY_coordinate(),
			                     frag.getX_coordinate() + GridFrag.width,
			                     frag.getY_coordinate() + GridFrag.height),
			           paint);
		}
		
		paint.reset();
		paint.setColor(Color.BLUE);
		for(Rectangle rect: additRects){
			c.drawRect(new Rect(rect.getMinX(),
			                     rect.getMinY(),
			                     rect.getMaxX(),
			                     rect.getMaxY()),
			           paint);
		}
		
		paint.reset();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(4);
		for(Object2D obj: additObjects){
			Path p = new Path();
			Point firstPoint = obj.getEdges().get(0).getP1();
			p.moveTo(firstPoint.x, firstPoint.y);
			for(Line line: obj.getEdges()) {
				p.lineTo(line.getP1().x,
				         line.getP1().y);
//				Rectangle rect = obj.getOuterRectangle();
//				c.drawLine(line.getP1().x,
//                           line.getP1().y,
//                           line.getP2().x,
//                           line.getP2().y,
//				           paint);
//				c.drawLine(new Rect(rect.getMinX(),
//				                    rect.getMinY(),
//				                    rect.getMaxX(),
//				                    rect.getMaxY()),
//				           paint);
			}
			c.drawPath(p,paint);
		}
		
		
		try {
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon1",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon2",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon3",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon4",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon5",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon6",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon7",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon8",
			                                                   Polygon.class),
			              Color.BLUE);
			paintObject2D(c,
			              DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                   "OuterPolygon9",
			                                                   Polygon.class),
			              Color.BLUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
//				Rectangle rect = obj.getOuterRectangle();
//				c.drawLine(line.getP1().x,
//                           line.getP1().y,
//                           line.getP2().x,
//                           line.getP2().y,
//				           paint);
//				c.drawLine(new Rect(rect.getMinX(),
//				                    rect.getMinY(),
//				                    rect.getMaxX(),
//				                    rect.getMaxY()),
//				           paint);


//		paint.setColor(Color.YELLOW);
//		for(Line obj: additLines){
//			c.drawLine(obj.getP1().x,
//                       obj.getP1().y,
//                       obj.getP2().x,
//                       obj.getP2().y,
//			           paint);
//		}
		
		return bitmap;
	}

		private static void paintObject2D(Canvas c,
		                                  Object2D obj,
		                                  int color){
			Paint paint = new Paint();
			paint.setColor(color);
			paint.setStrokeWidth(4);
			Path p = new Path();
			Point firstPoint = obj.getEdges().get(0).getP1();
			p.moveTo(firstPoint.x, firstPoint.y);
			for(Line line: obj.getEdges()) {
				p.lineTo(line.getP1().x,
				         line.getP1().y);
			}
			c.drawPath(p,paint);
		}
}
