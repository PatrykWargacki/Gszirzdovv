package ar.wargus.gszirzdovvdetection.threads;

import ar.wargus.gszirzdovvdetection.mock.MockBluetoothProbeImpl;
import ar.wargus.gszirzdovvdetection.interfaces.BluetoothProbe;
import ar.wargus.gszirzdovvdetection.classes.BluetoothRadarSubscriber;

public class MainLoop
	implements Runnable {
	
	private Long                        milisToWait;
	private boolean                     shouldRun;
	private BluetoothProbe              bluetoothProbe;
	private BluetoothRadarSubscriber    bluetoothRadarSubscriber;
	
	public MainLoop(){
		bluetoothRadarSubscriber    = BluetoothRadarSubscriber.getInstance();
		bluetoothProbe              = new MockBluetoothProbeImpl();
		milisToWait                 = 1000L;
		shouldRun                   = true;
	}
	
	@Override
	public void run() {
		while(shouldRun) {
			try {
				bluetoothRadarSubscriber.actualize(bluetoothProbe.getSignalStrength());
				this.wait(milisToWait);
			} catch (InterruptedException e) {
				// Interrupted
				System.out.println(e);
			}
		}
	}
	
	public void setShouldRun    (boolean    shouldRun)  { this.shouldRun    = shouldRun; }
	public void setMilisToWait  (Long       milisToWait){ this.milisToWait  = milisToWait; }
}
