package ar.wargus.gszirzdovvdetection.classes;

import java.util.ArrayList;
import java.util.List;

import ar.wargus.gszirzdovvdetection.interfaces.Subscriber;

public class BluetoothRadarSubscriber
	implements Runnable{
	
	private static final BluetoothRadarSubscriber instance;
	
	private volatile boolean                newValueAvailable;
	private volatile List<BluetoothRadar>   bluetoothRadars;
	
	private boolean             shouldRun;
	private List<Subscriber>    subscribers;
	
	static{
		instance = new BluetoothRadarSubscriber();
		instance.run();
	}
	
	private BluetoothRadarSubscriber(){
		subscribers         = new ArrayList<Subscriber>();
		newValueAvailable   = false;
		shouldRun           = true;}
	
	@Override
	public void run() {
		while(this.shouldRun) {
			if(this.newValueAvailable){
				this.newValueAvailable = false;
				for(Subscriber sub: subscribers){
					sub.onNewValueAvailable();
				}
			}
		}
	}
	
	public static BluetoothRadarSubscriber getInstance(){ return instance; }
	
	public void subscribe   (Subscriber subscriber) { this.subscribers.add(subscriber); }
	public void setShouldRun(boolean shouldRun)     { this.shouldRun = shouldRun;       }
	
	public synchronized List<BluetoothRadar> getBluetoothRadars() { return bluetoothRadars;	}
	
	public synchronized void actualize(List<BluetoothRadar>  bluetoothRadars){
		this.newValueAvailable  = true;
		this.bluetoothRadars    = bluetoothRadars;
	}
}
