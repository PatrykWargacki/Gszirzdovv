package ar.wargus.gszirzdovvdetection.classes;

import java.util.Arrays;

import ar.wargus.gszirzdovvdetection.R;

public class BluetoothRadar {
	
	public static Integer   maxSignalStrength       = 150;
	public static Integer   signalLostOverOneUnit   = 1;
	public static Integer   maxDistanceToPoint      = maxSignalStrength/signalLostOverOneUnit;
//	public static Integer   maxSignalStrength       = R.integer.BluetoothRadar_maxSignalStrength;
//	public static Integer   signalLostOverOneUnit   = R.integer.BluetoothRadar_signalLostOverOneUnit;
//	public static Integer   maxDistanceToPoint      = R.integer.BluetoothRadar_maxDistanceToPoint;
	
	private Long        identifier;
	private Integer     signalStrength;
	private Boolean     in_object;
	private Integer     x_coordinate;
	private Integer     y_coordinate;
	private Integer[]   color;
	
	// do użycia przez DI
	public BluetoothRadar() {}
	
	// do użytku przez Probe'y
	public BluetoothRadar(Long      identifier,
	                      Integer   signalStrength) {
		this.identifier     = identifier;
		this.signalStrength = signalStrength;
	}
	
	@Override
	public String toString() {
		return "BluetoothRadar:"
		       +" identifier: "        + identifier
		       +" signalStrength: "    + signalStrength
		       +" x_coordinate: "      + x_coordinate
		       +" y_coordinate: "      + y_coordinate
		       +" in_object: "         + in_object
		       +" color: "             + Arrays.asList(color).toString();
	}
	
	public Long         getIdentifier       () { return identifier;     }
	public Integer      getSignalStrength   () { return signalStrength; }
	public Integer      getX_coordinate     () { return x_coordinate;	}
	public Integer      getY_coordinate     () { return y_coordinate;	}
	public Boolean      getIn_object        () { return in_object;     	}
	public Integer[]    getColor            () { return color;          }
	
	public void setIdentifier       (Long identifier)           { this.identifier       = identifier;       }
	public void setSignalStrength   (Integer signalStrength)    { this.signalStrength   = signalStrength;   }
	public void setIn_object        (Boolean in_object)         { this.in_object        = in_object;        }
	public void setX_coordinate     (Integer x_coordinate)      { this.x_coordinate     = x_coordinate;     }
	public void setY_coordinate     (Integer y_coordinate)      { this.y_coordinate     = y_coordinate;     }
	public void setColor            (Integer[] color)           { this.color            = color;            }
}
