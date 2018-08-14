package ar.wargus.gszirzdovvdetection.mapObjects;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.wargus.gszirzdovvdetection.R;
import ar.wargus.gszirzdovvdetection.classes.BluetoothRadar;

public class Map {
	
	private static Map instance;
	private static int maxOffset;
	
	public static final int height;
	public static final int width;
	
	static{
		height      = 2990;
		width       = 6520;
		maxOffset   = 10;
//		height      = R.integer.Map_height;
//		width       = R.integer.Map_width;
//		maxOffset   = R.integer.Map_maxOffset;
		instance    = new Map();
	}
	
	private List<GridFrag> fragments;
	
	// 1 Unit = 10 cm
	private Map(){
		fragments   = new ArrayList<GridFrag>();
		
		this.init();
	}
	
	public static Map getInstance(){ return instance; }
	
	public java.util.Map<Integer, List<GridFrag>> findAllPossibleLocations(@NonNull
			                                                               List<BluetoothRadar> radars,
	                                                                       @Nullable
	                                                                       Integer maxOffset){
		if(maxOffset == null){ maxOffset = Map.maxOffset; }
		
		java.util.Map<Integer, List<GridFrag>> pairMap = new HashMap<Integer, List<GridFrag>>();
		
		for(GridFrag frag: fragments){
			int offset = 0;
			// sprawdz czy zawiera wymienione radary
			// pobierz siłę a absolutną różnicę zapisz jako offset
			java.util.Map<Long, Integer> strengthMap = frag.getBluetoothRadarStrengthMap();
			boolean containsAll = true;
			for(BluetoothRadar radar: radars){
				if(strengthMap.containsKey(radar.getIdentifier())){
					int localOffset = Math.abs(strengthMap.get(radar.getIdentifier())
							                   - radar.getSignalStrength());
					if(localOffset > offset
					   && localOffset <= maxOffset){
						offset = localOffset;
					}
				}else{
					containsAll = false;
					break;
				}
			}
			if(!containsAll){ continue;	}
			
			List<GridFrag> fragList = pairMap.get(offset);
			if(fragList == null){ fragList = new ArrayList<GridFrag>();	}
			
			fragList.add(frag);
			pairMap.put(offset, fragList);
		}
		
		return pairMap;
	}
	
	public List<GridFrag> getFragments() { return fragments; }
	
	private void init(){
		for(int x=0; x<width; x+=GridFrag.width){
			for(int y=0; y<height; y+=GridFrag.height) {
				fragments.add(new GridFrag(x, y));
			}
		}
	}
}
