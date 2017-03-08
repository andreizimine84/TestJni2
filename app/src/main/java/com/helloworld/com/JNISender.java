package com.helloworld.com;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

public class JNISender {

	Context globalContext;
	final String addr = "10.0.2.2";
	final String port = "8080";

	boolean check = false;
	public boolean connectCppException = false;
	public boolean connectRWCppException = false;


	static {
		System.loadLibrary("TestJNI2");
	}

	public native boolean connectToHostJNICPP(String port, String addr, String data, int maxDataLength);

	public void main(Intent intent, Context context) {
		globalContext = context;
		boolean check = false;
		try {
			connectRWCppException = true;
			check = connectCppAndSend();
			if (check == true) {
				if (connectCppException == true) {
					//mOfflineHelpSender.main(globalContext.getApplicationContext());
					connectCppException = false;
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public boolean connectCppAndSend()
			throws IOException, SecurityException, RemoteException, ClassCastException, NullPointerException {
		boolean check = false;
		if(connectRWCppException == true) {
			final String addr = "10.0.2.2";
			final String port = "8080";
			check = connectToHostJNICPP(port, addr, completeLoadFile().toString(), 3000);
			connectRWCppException = false;
		}
		return check;
	}

	public ByteArrayOutputStream completeLoadFile() throws FileNotFoundException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//if (TempFolder.mAddBackNumber == 1) {
			baos.write(loadFile("output_temp" + "??" + ".txt").toByteArray());
			new File(globalContext.getFilesDir(), "output_temp" + "??" + ".txt").delete();
		//} else {
		//	TempFolder.mAddBackNumber--;
			//baos.write(loadFile("output_temp" + "??" + ".txt").toByteArray());
			//new File(globalContext.getFilesDir(), "output_temp" + "??" + ".txt").delete();
		//}
		return baos;
	}

	public ByteArrayOutputStream loadFile(String inputFile) throws FileNotFoundException {
		String inputString;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedReader inputReader = null;
		inputReader = new BufferedReader(new InputStreamReader(globalContext.openFileInput(inputFile)));
		try {
			// StringBuffer stringBuffer = new StringBuffer();

			while ((inputString = inputReader.readLine()) != null) {
				// stringBuffer.append(inputString);
				baos.write(inputString.getBytes());
			}

			// out.close();
			baos.flush();
			// baos.close();
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}
		return baos;
	}
}
