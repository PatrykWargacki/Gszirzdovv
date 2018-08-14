package ar.wargus.gszirzdovvdetection.interfaces;

import java.util.List;

import ar.wargus.gszirzdovvdetection.classes.BluetoothRadar;

public interface BluetoothProbe {
	
	public abstract List<BluetoothRadar> getSignalStrength();
}
