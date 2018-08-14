package ar.wargus.gszirzdovvdetection.mock;

import android.graphics.Color;
import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ar.wargus.gszirzdovvdetection.R;
import ar.wargus.gszirzdovvdetection.classes.BluetoothRadar;
import ar.wargus.gszirzdovvdetection.helper.DependencyResolver;
import ar.wargus.gszirzdovvdetection.interfaces.BluetoothProbe;

public class MockBluetoothProbeImpl
	implements BluetoothProbe {
	
	/*
	Pomieszczenie
	 ___________________________
	|                           |
	|  1                     3  |
	|                           |
	|                  ty       |
	|          <---   *         |
	|           kierunek        |
	|  2        ruchu        4  |
	|___________________________|
	
	 */
	
	private int radar1SignalStrength = 25;
	private int radar2SignalStrength = 25;
	private int radar3SignalStrength = 75;
	private int radar4SignalStrength = 75;
	
	private Double radar1SignalStrengthModificator = 1.0;
	private Double radar2SignalStrengthModificator = 1.0;
	private Double radar3SignalStrengthModificator = -1.0;
	private Double radar4SignalStrengthModificator = -1.0;
	
	@Override
	public List<BluetoothRadar> getSignalStrength() {
		radar1SignalStrength += radar1SignalStrengthModificator;
		radar2SignalStrength += radar2SignalStrengthModificator;
		radar3SignalStrength *= radar3SignalStrengthModificator;
		radar4SignalStrength *= radar4SignalStrengthModificator;
		return Arrays.asList(new BluetoothRadar(1L, radar1SignalStrength),
		                     new BluetoothRadar(2L, radar2SignalStrength),
		                     new BluetoothRadar(3L, radar3SignalStrength),
		                     new BluetoothRadar(4L, radar4SignalStrength));
	}
	
	@NonNull
	public static List<BluetoothRadar> getStartDefaultRadars(){
		try {
			return Arrays.asList(DependencyResolver.getFromResourceId(R.xml.bluetoothradars,
			                                                          "BluetoothRadars",
			                                                          BluetoothRadar[].class));
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
}
