package ar.wargus.gszirzdovvdetection.classes;

import java.util.List;

import ar.wargus.gszirzdovvdetection.interfaces.Subscriber;
import ar.wargus.gszirzdovvdetection.mapObjects.GridFrag;
import ar.wargus.gszirzdovvdetection.mapObjects.Map;

public class ProcessSignalStrength
	implements Subscriber {
	
	private final BluetoothRadarSubscriber radarSubscriber = BluetoothRadarSubscriber.getInstance();
	private final Map map = Map.getInstance();
	
	public ProcessSignalStrength(){
		radarSubscriber.subscribe(this);
	}
	
	@Override
	public void onNewValueAvailable() {
		java.util.Map<Integer, List<GridFrag>> locations = map.findAllPossibleLocations(radarSubscriber.getBluetoothRadars(), null);
		
		//zaznacz na mapie mozliwe miejsca
		// czy cos
	}
	
	private GridFrag chooseMostProbablyFrag(java.util.Map<Integer, List<GridFrag>> frags){
		//wybierz najbliższy ostatniej lokacji
		return null;
	}
}
