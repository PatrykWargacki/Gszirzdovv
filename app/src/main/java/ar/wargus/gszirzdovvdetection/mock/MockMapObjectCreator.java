package ar.wargus.gszirzdovvdetection.mock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Pair;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import ar.wargus.gszirzdovvdetection.tasks.CalcSIgnalStrengthAsyncTask;
import ar.wargus.gszirzdovvdetection.tasks.FindCollisionPointsAsyncTask;

public class MockMapObjectCreator{
	
	//	Obiekty 2D mają listę par wierzchołków (krawędzi) i metodę intersects
//
//	PObierz mapę z zaktualizowaną mapą zasięgu,
//	gdy różne miejsca będą miały podobne wartości co klient wybierz najbliższe względem ostatniej lokacji
	
	
	public static List<Rectangle>  additRects      = new ArrayList<Rectangle>();
	public static List<Object2D>   additObjects    = new ArrayList<Object2D>();
	
	public static volatile List<Line>       additLines      = new ArrayList<>();
	public static volatile List<Line>       sharedLines      = Collections.synchronizedList(new ArrayList<>());
	
	/*
	Use with caution
	CPU may explode
	so uneffective way
	 */
	public static void createMap(List<Object2D>         objects,
	                             List<BluetoothRadar>   radars) {
		additObjects = objects;
		List<Pair<Point, Object2D>> sharedPoints = Collections.synchronizedList(new ArrayList<>());
		
		FindCollisionPointsAsyncTask findCollisionPointsAsyncTask   = new FindCollisionPointsAsyncTask(sharedLines,
		                                                                                               sharedPoints);
		CalcSIgnalStrengthAsyncTask calcSIgnalStrengthAsyncTask     = new CalcSIgnalStrengthAsyncTask(Collections.EMPTY_LIST);
		
		findCollisionPointsAsyncTask.execute();
		calcSIgnalStrengthAsyncTask.execute();
		
		Map map = Map.getInstance();
		
		for(GridFrag frag: map.getFragments()) {
			Point fragPoint = new Point(frag.getX_coordinate() + GridFrag.width,
			                            frag.getY_coordinate() + GridFrag.height);
			
			for (BluetoothRadar radar : radars) {
				// dostań środkowe współrzędne
				Point radarPoint = new Point(radar.getX_coordinate(),
				                             radar.getY_coordinate());
				
				if (Utils.pointPointDistance(fragPoint, radarPoint) > BluetoothRadar.maxDistanceToPoint)
					continue;
				
				Line line = new Line(fragPoint,
				                     radarPoint);
				
				sharedLines.add(line);
				additLines.add(line);
//				List<Pair<Point, Object2D>> points = findCollisionPointsOfLineObjects(line, objects);
//
//				int signal_strength = calcSignalStrength(points,
//				                                         radar.getIn_object(),
//				                                         radarPoint,
//				                                         fragPoint);
//
//				// jeśli siła sygnału jest to dodaj ją do mapy
//				if(signal_strength > 0) {
//					frag.getBluetoothRadarStrengthMap()
//					    .put(radar.getIdentifier(),
//					         signal_strength);
//				}
				
				// poczekaj aż findCollision stworzy punkty
				while (!sharedLines.isEmpty()) {}
				calcSIgnalStrengthAsyncTask.setNewValues(new ArrayList<>(sharedPoints),
				                                         radar,
				                                         frag);
				sharedPoints.clear();
			}
		}
		
		// poczekaj aż calcSIgnalStrength przetworzy wszystkie punkty
		while(!sharedPoints.isEmpty()) {}
		findCollisionPointsAsyncTask.shutdown();
		calcSIgnalStrengthAsyncTask.shutdown();
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
