package ar.wargus.gszirzdovvdetection.tasks;

import android.os.AsyncTask;
import android.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ar.wargus.gszirzdovvdetection.classes.BluetoothRadar;
import ar.wargus.gszirzdovvdetection.helper.Utils;
import ar.wargus.gszirzdovvdetection.mapObjects.GridFrag;
import ar.wargus.gszirzdovvdetection.mapObjects.Line;
import ar.wargus.gszirzdovvdetection.mapObjects.Object2D;
import ar.wargus.gszirzdovvdetection.mapObjects.Point;
import ar.wargus.gszirzdovvdetection.mapObjects.Rectangle;
import ar.wargus.gszirzdovvdetection.mock.MockMapObjectCreator;

public class CalcSIgnalStrengthAsyncTask extends AsyncTask<Void,
                                                           Void,
                                                           Void> {
	private List<Pair<Point, Object2D>> sharedPoints;
	private BluetoothRadar              radar;
	private GridFrag                    frag;
	
	private boolean shouldRun = true;
	
	public CalcSIgnalStrengthAsyncTask(List<Pair<Point, Object2D>> sharedPoints) { this.sharedPoints = sharedPoints; }
	
	@Override
	protected Void doInBackground(Void... voids) {
		while (shouldRun) {
			while (!sharedPoints.isEmpty()) {
				int signal_strength = calcSignalStrength(sharedPoints,
				                                         radar.getIn_object(),
									                     new Point(radar.getX_coordinate(),
									                               radar.getY_coordinate()),
									                     new Point(frag.getX_coordinate(),
									                               frag.getY_coordinate()));
				if(signal_strength > 0) {
					frag.getBluetoothRadarStrengthMap()
					    .put(radar.getIdentifier(),
					         signal_strength);
				}
			}
		}
		return null;
	}
	
	public void setNewValues(List<Pair<Point, Object2D>>    sharedPoints,
	                         BluetoothRadar                 radar,
	                         GridFrag                       frag) {
		while(!sharedPoints.isEmpty()) {}
		
		this.sharedPoints   = sharedPoints;
		this.radar          = radar;
		this.frag           = frag;
		
	}
	
	public void shutdown() {
		shouldRun = false;
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
}
