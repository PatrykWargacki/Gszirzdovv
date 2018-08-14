package ar.wargus.gszirzdovvdetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.wargus.gszirzdovvdetection.helper.DependencyResolver;
import ar.wargus.gszirzdovvdetection.mapObjects.Map;
import ar.wargus.gszirzdovvdetection.mapObjects.Object2D;
import ar.wargus.gszirzdovvdetection.mapObjects.Polygon;
import ar.wargus.gszirzdovvdetection.mapObjects.Rectangle;
import ar.wargus.gszirzdovvdetection.mock.MockBluetoothProbeImpl;
import ar.wargus.gszirzdovvdetection.mock.MockMapObjectCreator;
import ar.wargus.gszirzdovvdetection.threads.MainLoop;

public class MainActivity
	extends AppCompatActivity {
	
	private MainLoop mainLoop;
	
	private void doStuff(){
		Map map = Map.getInstance();
		List<Object2D> object2DList = new ArrayList<Object2D>();
		
		try {
			object2DList.addAll(Arrays.asList(DependencyResolver.getFromResourceId(R.xml.polygons,
			                                                                       "Polygons",
			                                                                       Polygon[].class)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MockMapObjectCreator.createMap(object2DList,
		                               MockBluetoothProbeImpl.getStartDefaultRadars());
		
		Bitmap bitmap = MockMapObjectCreator.visalizeMap(MockBluetoothProbeImpl.getStartDefaultRadars());
		
		ImageView image = findViewById(R.id.imageView1);
		image.setImageBitmap(bitmap);
		
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			Integer counter = 0;
			File file = new File(path, "FitnessGirl1" + counter + ".png"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
			fOut.flush(); // Not really required
			fOut.close(); // do not forget to close the stream
			
			MediaStore.Images.Media.insertImage(getContentResolver(),
			                                    file.getAbsolutePath(),
			                                    file.getName(),
			                                    file.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(@Nullable
			                Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DependencyResolver.init(this);
		
		if (ContextCompat.checkSelfPermission(this,
		                                      Manifest.permission.READ_CONTACTS)
				    != PackageManager.PERMISSION_GRANTED) {
			if (! ActivityCompat.shouldShowRequestPermissionRationale(this,
			                                                        Manifest.permission.READ_CONTACTS)) {
				ActivityCompat.requestPermissions(this,
				                                  new String[]{Manifest.permission.READ_CONTACTS},
				                                  1);
			}
		} else {
			try {
				doStuff();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
//		processSignalStrength   = new ProcessSignalStrength();
//		mainLoop                = new MainLoop();
//
//		mainLoop.run();
//
//		try {
//			this.wait(5000L);
//		} catch (InterruptedException e) {
//			// Interrupted
//			System.out.println(e);
//		}finally {
//			mainLoop.setShouldRun(false);
//		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull
			                               String[] permissions,
	                                       @NonNull
			                               int[] grantResults) {
		try {
			doStuff();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
