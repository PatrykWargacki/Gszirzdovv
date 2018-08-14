package ar.wargus.gszirzdovvdetection.mapObjects;

import java.util.HashMap;
import java.util.Map;

import ar.wargus.gszirzdovvdetection.R;

public class GridFrag {
	
	public static final int height;
	public static final int width;
	
	static{
		height  = 5;
		width   = 5;
//		height  = R.integer.GridFrag_height;
//		width   = R.integer.GridFrag_width;
	}
	
	private final int x_coordinate;
	private final int y_coordinate;
	
	//	BluetoothRadar identifier to signalStrength Map
	private Map<Long, Integer> bluetoothRadarStrengthMap = new HashMap<Long, Integer>();
	
	public GridFrag(int x_coordinate,
	                int y_coordinate) {
		this.x_coordinate = x_coordinate;
		this.y_coordinate = y_coordinate;
	}
	
	public int                  getX_coordinate             ()  { return x_coordinate;	            }
	public int                  getY_coordinate             ()  { return y_coordinate;	            }
	public Map<Long, Integer>   getBluetoothRadarStrengthMap()  { return bluetoothRadarStrengthMap; }
	
	public void setBluetoothRadarStrengthMap(Map<Long, Integer> bluetoothRadarStrengthMap) { this.bluetoothRadarStrengthMap = bluetoothRadarStrengthMap; }
	
}
