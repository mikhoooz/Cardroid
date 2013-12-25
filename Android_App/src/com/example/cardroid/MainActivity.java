package com.example.cardroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	TextView myLabel;
	EditText myTextbox;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	byte[] readBuffer;
	byte writeBuffer;
	int readBufferPosition;
	int counter;
	boolean stopWorker;
	Button openButton;
	boolean connectedBlueTooth;
	TextView dirTextView;
	public void initBlueTooth() {
		openButton = (Button) findViewById(R.id.open);
		myLabel = (TextView) findViewById(R.id.label);

		dirTextView = (TextView) findViewById(R.id.textDir);
		
		openButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				findBT();
				try {
					if (!connectedBlueTooth)
						openBT();
					else
						closeBT();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// Close button

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initDataBuffers();
		initBlueTooth();
		initSensors();
	}

	private void initDataBuffers() {
		// TODO Auto-generated method stub
		writeBuffer = 0;

	}

	private void initSensors() {
		// TODO Auto-generated method stub
		SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor accelSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelSensor,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	protected void closeBT() {
		// TODO Auto-generated method stub
		openButton.setTextColor(Color.RED);
		openButton.setText("Closed Connection");
		
		mmOutputStream = null;
	}

	protected void sendData() throws IOException {
		// TODO Auto-generated method stub

		if (mmOutputStream != null) {

			Log.d("Sensor Sending [0] : ", "" + (writeBuffer & 15));
			Log.d("Sensor Sending [1] : ", "" + (writeBuffer >> 4));

			mmOutputStream.write(writeBuffer);


		}

	}

	protected void openBT() throws IOException {
		// TODO Auto-generated method stub
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Standard
																				// //SerialPortService
																				// ID
		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		mmSocket.connect();
		mmOutputStream = mmSocket.getOutputStream();
		mmInputStream = mmSocket.getInputStream();
		
		
		connectedBlueTooth = true;
		openButton.setText("Opened Connection");
		openButton.setTextColor(Color.GREEN);
		
		beginTolisten();
		

		myLabel.setText("Bluetooth Opened : " + mmDevice.getName());

	}

	private void beginTolisten() {
		// TODO Auto-generated method stub
		final Handler handler = new Handler();
		final byte delimiter = 10; // This is the ASCII code for a newline
									// character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted() && !stopWorker) {
					try {
						int bytesAvailable = mmInputStream.available();
						Thread.sleep(100);
						if (bytesAvailable > 0) {

							readBuffer = new byte[2];

							mmInputStream.read(readBuffer);

							Log.d("Data Available", "readBuffer[0] = "
									+ readBuffer[0]);
							Log.d("Data Available", "readBuffer[1] = "
									+ readBuffer[1]);

						}
					} catch (IOException ex) {
						stopWorker = true;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		workerThread.start();

	}

	protected void findBT() {
		// TODO Auto-generated method stub
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.d("findBT ", "not supported by bluetooth");
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBluetooth = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, 0);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals("SeeedBTSlave")) {
					mmDevice = device;
					break;
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int x = (int) event.values[0];
		int y = (int) event.values[1];

		if (x > 7)
			x = 7;
		else if (x < -8)
			x = -8;
		x += 8;

		if (y > 7)
			y = 7;
		else if (y < -8)
			y = -8;
		y += 8;

		Log.d("X from senso`r", "" + x);
		Log.d("Y from sensor", "" + y);

		writeBuffer = 0;
		writeBuffer |= (x & 15);
		writeBuffer |= (y << 4);
		Log.d("Write buffeer", Integer.toBinaryString(writeBuffer));
		try {
			sendData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
