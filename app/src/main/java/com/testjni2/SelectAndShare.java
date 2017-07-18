package com.testjni2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Debug;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import java.util.Arrays;
public class SelectAndShare extends Application {
	FileOutputStream fos = null;
	ByteArrayOutputStream baos = null;
	InputStream is = null;
	byte[] buffer = null;
	Context globalContext;
	DataParser dp = new DataParser();
	StringBuilder dataBattery = new StringBuilder();
	Intent myIntent = null;
	MessageDigest md = null;
	int mAddNumber = 0;
	String folder;
	String file;
	static {
		System.loadLibrary("TestJNI2");
	}

	public native String connectToHostJNICPP2(String fileName);
	
	public void main(Intent intent, Context context) {
		globalContext = context;
		myIntent = intent;
	}

	public void getItems() throws IOException, SecurityException, RemoteException, ClassCastException{
		folder = globalContext.getFilesDir().getPath();
		try {
			file = completeWriteTempFile(returnAllElements());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Intent in1 = new Intent("byteArray");
		sendLocationBroadcast(in1);
	}

	public ByteArrayOutputStream returnAllElements() {
		ByteArrayOutputStream baosR = new ByteArrayOutputStream();
		try {	
			baosR.write(dp.getAsciInfoStream().getBytes());
			baosR.write(getInfoStreamToAsci().toByteArray());

			baosR.write(dp.getAsciNetworkInfoStream().getBytes());
			baosR.write(getNetworkInfoStreamToAsci().toByteArray());

			baosR.write(dp.getAsciRunningTaskInfoStream().getBytes());
			baosR.write(getRunningTaskInfoStreamToAsci().toByteArray());

			baosR.write(dp.getAsciTimeInfoStream().getBytes());
			baosR.write(getTimeInfoStreamToAsci().toByteArray());

			baosR.write(dp.getAsciRunningAppProcessInfo().getBytes());
			baosR.write(getRunningAppProcessInfoToAsci().toByteArray());

			baosR.write(dp.getAsciRunningServiceInfo().getBytes());
			baosR.write(getRunningServiceInfoToAsci().toByteArray());

			baosR.write(dp.getAsciSensorInfoStream().getBytes());
			baosR.write(getSensorInfoToAsci().toByteArray());

			baosR.write(dp.getAsciBatteryInfo().getBytes());
			baosR.write(getBatteryInfo().toByteArray());

			baosR.write(dp.getAsciNetworkInfo().getBytes());
			baosR.write(getNetworkInfoToAsci().toByteArray());

			baosR.write(dp.getAsciActiveNetworkInfo().getBytes());
			baosR.write(getActiveNetworkInfoToAsci().toByteArray());

			baosR.write(dp.getAsciAllNetworkInfo().getBytes());
			baosR.write(getAllNetworkInfoToAsci().toByteArray());

			//baosR.write(dp.getAsciProcMemo().getBytes());
			//baosR.write(getProcMemoToAsci().toByteArray());
			//baosR.write(dp.getAsciCpuMemo().getBytes());
			//baosR.write(getCpuMemoToAsci().toByteArray());

			baosR.write(dp.getAsciEndOfTransmission().getBytes());
			baosR.flush();
			baosR.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return baosR;
	}

	public String completeWriteTempFile(ByteArrayOutputStream baos) throws NoSuchAlgorithmException, IOException, SecurityException, RemoteException, ClassCastException {
		FileOutputStream outputStream = null;
		System.out.println("value" + baos.size());
		File[] files = new File[2];
		try {
			md = MessageDigest.getInstance("SHA-1");
				md.update(baos.toByteArray());
				byte[] mdbytes = md.digest();

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < mdbytes.length; i++) {
	          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	        }

	        md.reset();
	        mdbytes = new byte[0];
			if(mAddNumber == 0){
				mAddNumber = get_mAddBackNumber(globalContext.getFilesDir());
				mAddNumber+=1;
				outputStream = globalContext.openFileOutput("output_temp_" + mAddNumber + "_" + sb.toString() +".txt",Context.MODE_PRIVATE);
			}
			else{
				mAddNumber = get_mAddBackNumber(globalContext.getFilesDir());
				mAddNumber+=1;
				outputStream = globalContext.openFileOutput("output_temp_" + mAddNumber + "_" + sb.toString() +".txt",Context.MODE_PRIVATE);
			}
			baos.writeTo(outputStream);
			
			files[0] = new File(folder + "/" + "output_temp_" + mAddNumber + "_" + sb.toString() +".txt");
			String sha = sb.toString();
			sb = null;
			baos.flush();
			baos.reset();
			baos.close();
			outputStream.flush();
			outputStream.close();
			return "output_temp_" + mAddNumber + "_" + sha +  ".txt";
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		} 
		catch (NullPointerException e) {
			System.err.println("Caught NullPointerException: " + e.getMessage());
		}

	
		return null;
	}

	public int get_mAddBackNumber(File root){
		int i = 0;
		for (File child : root.listFiles()) {
			if (root.isDirectory()) {
				if (child.exists()) {
					i++;
				}
			}
		}
		return i;
	}
	
	private void sendLocationBroadcast(Intent intent) {
		intent.putExtra("numberFile", file);
		intent.putExtra("folder", folder);
		LocalBroadcastManager.getInstance(globalContext).sendBroadcast(intent);
	}

	public List<RunningAppProcessInfo> getRunningAppProcessInfo() {
		ActivityManager manager = (ActivityManager) globalContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> android = new ArrayList<RunningAppProcessInfo>();
		android.addAll(manager.getRunningAppProcesses());
		manager = null;
		return android;
	}

	public ByteArrayOutputStream getRunningAppProcessInfoToAsci() {
		int counter = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<RunningAppProcessInfo> android = new ArrayList<RunningAppProcessInfo>();
		android.addAll(getRunningAppProcessInfo());
		try {
			while (counter != android.size()) {
				String data = dp.getName(android.get(counter).processName) + dp.getAsciRecordSeparator();
				counter++;
				baos.write(data.getBytes());
			}
			android = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public List<RunningServiceInfo> getRunningServiceInfo() {
		ActivityManager manager = (ActivityManager) globalContext.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> android = new ArrayList<RunningServiceInfo>();
		android.addAll(manager.getRunningServices(manager.getRunningAppProcesses().size()));
		manager = null;
		return android;
	}

	public ByteArrayOutputStream getRunningServiceInfoToAsci() {
		int counter = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<RunningServiceInfo> android = new ArrayList<RunningServiceInfo>();
		android.addAll(getRunningServiceInfo());
		try {
							while (counter != android.size()) {
								String data = dp.getName(android.get(counter).toString()) + dp.getAsciRecordSeparator();
								counter++;
								baos.write(data.toString().getBytes());
			}
			android = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public ByteArrayOutputStream getBatteryInfo() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int status = myIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;

		int chargePlug = myIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
		boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
		String[] dataBattery = new String[5];
		dataBattery[0] = Integer.toString(status);
		dataBattery[1] = String.valueOf(isCharging);
		dataBattery[2] = String.valueOf(chargePlug);
		dataBattery[3] = String.valueOf(usbCharge);
		dataBattery[4] = String.valueOf(acCharge);

		String[] batteryNames = new String[5];
		batteryNames[0] = "status";
		batteryNames[1] = "isCharging";
		batteryNames[2] = "chargePlug";
		batteryNames[3] = "usbCharge";
		batteryNames[4] = "acCharge";
		
		for(int i = 0; i < dataBattery.length; i++){
			String data = dp.getName(batteryNames[i]) + dataBattery[i] + dp.getAsciRecordSeparator();
			baos.write(data.getBytes());
		}
		batteryNames = null;
		baos.flush();
		baos.close();
		return baos;
	}

	public List<Sensor> getSensorInfo() {
		SensorManager manager;
		manager = (SensorManager) globalContext.getSystemService(SENSOR_SERVICE);
		List<Sensor> android = new ArrayList<Sensor>();
		android.addAll(manager.getSensorList(Sensor.TYPE_ALL));
		manager = null;
		return android;
	}

	public ByteArrayOutputStream getSensorInfoToAsci() {
		int counter = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<Sensor> android = new ArrayList<Sensor>(); 
		android.addAll(getSensorInfo());
		try {
			while (counter != android.size()) {
				String data = dp.getName(android.get(counter).getName()) + android.get(counter).getVendor() + dp.getAsciRecordSeparator() + android.get(counter).getVersion() + dp.getAsciRecordSeparator();
				counter++;
				baos.write(data.toString().getBytes());
			}
			android = null;
			baos.flush();
			baos.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public List<ActivityManager.RunningTaskInfo> getRunningTaskInfo() throws RemoteException {
		ActivityManager am = (ActivityManager) globalContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> android = new ArrayList<ActivityManager.RunningTaskInfo>();
		android.addAll(am.getRunningTasks(10));
		am = null;
		return android;
	}

	public ByteArrayOutputStream getRunningTaskInfoToAsci() {
		int counter = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<ActivityManager.RunningTaskInfo> android= new ArrayList<ActivityManager.RunningTaskInfo>();
		try {
			android.addAll(getRunningTaskInfo());
			while (counter != android.size()) {
				String data = dp.getName(android.get(counter).toString()) + dp.getAsciRecordSeparator();
				counter++;
				baos.write(data.getBytes());
			}
			android = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return baos;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("static-access")
	public boolean getNetworkInfo() {
		ConnectivityManager manager = (ConnectivityManager) globalContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean android = manager.isNetworkTypeValid(manager.TYPE_WIFI);
		manager = null;
		return android;
	}

	public ByteArrayOutputStream getNetworkInfoToAsci() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean android;
		try {
			android = getNetworkInfo();
			String data = dp.getName("TYPE_WIFI") + android + dp.getAsciRecordSeparator();
			baos.write(data.toString().getBytes());
			baos.flush();
			baos.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public NetworkInfo getActiveNetworkInfo() {
		ConnectivityManager manager = (ConnectivityManager) globalContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = manager.getActiveNetworkInfo();
		manager = null;
		return ni;
	}

	public ByteArrayOutputStream getActiveNetworkInfoToAsci() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		NetworkInfo android;
		try {
			android = getActiveNetworkInfo();
			String data = dp.getName(android.toString()) + dp.getAsciRecordSeparator();
			baos.write(data.toString().getBytes());
			android = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public NetworkInfo[] getAllNetworkInfo() {
		ConnectivityManager manager = (ConnectivityManager) globalContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] ni = manager.getAllNetworkInfo();
		manager = null;
		return ni;
	}

	public ByteArrayOutputStream getAllNetworkInfoToAsci() {
		int counter = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		NetworkInfo[] android;
		try {
			android = getAllNetworkInfo();
			while (counter != android.length) {
				String data = dp.getName(android[counter].getTypeName()) + dp.getAsciRecordSeparator();
				counter++;
				baos.write(data.getBytes());
			}
			android = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public ByteArrayOutputStream getRunningTaskInfoStreamToAsci() throws RemoteException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ListIterator<ActivityManager.RunningTaskInfo> iterator = getRunningTaskInfo().listIterator();

		try {
			while (iterator.hasNext()) {
				ActivityManager.RunningTaskInfo version = iterator.next();
				String data = dp.getName(version.topActivity.getClassName().toString()) + dp.getAsciRecordSeparator();
				baos.write(data.getBytes());
			}
			iterator = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}

	public ByteArrayOutputStream getTimeInfoStreamToAsci() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ListIterator<RunningServiceInfo> iterator = getRunningServiceInfo().listIterator();
		Format formatter;
		try {
			while (iterator.hasNext()) {
				RunningServiceInfo version = iterator.next();
				int count = version.crashCount;
				long startTime = version.activeSince;
				long endTime = version.lastActivityTime;
				long estimatedTime = System.nanoTime() - startTime;
				long estimatedEndTime = System.nanoTime() - endTime;
				long finEnd = TimeUnit.SECONDS.convert(estimatedEndTime, TimeUnit.NANOSECONDS);
				long fin = TimeUnit.SECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS);
				formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
				String timeMillisFin = formatter.format(System.currentTimeMillis() - fin);
				String timeMillisFinEnd = formatter.format(System.currentTimeMillis() - finEnd);
				String append = dp.getName(version.process) + '"' + timeMillisFin +  '"' + dp.getAsciRecordSeparator() +  '"' + timeMillisFinEnd + '"' + dp.getAsciRecordSeparator();

				baos.write(append.getBytes());
				append = null;
			}
			iterator = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos;
	}

	public ByteArrayOutputStream getNetworkInfoStreamToAsci() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			boolean isNetworkTypeValid = getNetworkInfo();
			String append = dp.getName("WIFI") + isNetworkTypeValid + dp.getAsciRecordSeparator();
			baos.write(append.getBytes());
			append = null;
			baos.flush();
			baos.close();
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return baos;
	}

	@SuppressLint("ServiceCast")
	public ByteArrayOutputStream getInfoStreamToAsci()
			throws SecurityException, RemoteException, ClassCastException, NullPointerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ListIterator<RunningAppProcessInfo> iterator = getRunningAppProcessInfo().listIterator();
		ActivityManager manager = (ActivityManager) globalContext.getSystemService(Context.ACTIVITY_SERVICE);
		try {
			while (iterator.hasNext()) {
				RunningAppProcessInfo version = iterator.next();
				Debug.MemoryInfo[] memoryInfos = manager.getProcessMemoryInfo(new int[] { version.pid });

				String data = dp.getName(version.processName.toString()) + memoryInfos[0].getTotalPss()
						+ dp.getAsciRecordSeparator();
				baos.write(data.getBytes());
			}
			manager = null;
			iterator = null;
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos;
	}
	
	public ByteArrayOutputStream getCpuMemoToAsci() throws IOException {
		String fileNameProcMemo = "/proc/cpuinfo";
		//String dataProcMemo = connectToHostJNICPP2(fileNameProcMemo);
		String dataProcMemo = null;
		String delimiter = ":";
		String[] temp = dataProcMemo.split(delimiter);
		for (int i = 0; i < temp.length; i++) {
			System.out.println("data1" + temp[i]);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < temp.length; i++) {
			try {
				String data = dp.getName(temp[i]) + dp.getAsciRecordSeparator();
				baos.write(data.getBytes());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			baos.toString();
			baos.flush();
			baos.close();
			return baos;

		}
	
	public ByteArrayOutputStream getProcMemoToAsci() throws IOException {
		String fileNameProcMemo = "/proc/meminfo";
		String dataProcMemo = connectToHostJNICPP2(fileNameProcMemo);
		//String dataProcMemo = "dataProcMemo";

        String[] temp;
        String delimiter = ":";
        temp = dataProcMemo.split(delimiter);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(int i =0; i < temp.length; i++){
        	try {
				String data = dp.getName(temp[i].trim()) + dp.getAsciRecordSeparator();
            	baos.write(data.getBytes());
				baos.flush();
				baos.close();
				//baos.reset();
				data = null;
            } catch (IOException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            }

        }
		dataProcMemo = null;
		temp = null;

		return baos;
	}
}
