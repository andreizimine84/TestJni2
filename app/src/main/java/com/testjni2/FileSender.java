package com.testjni2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import android.util.Log;

public class FileSender {
	Context globalContext;
	public boolean exceptionDelete = false;
	
	public void main(Context context) throws IOException {
		globalContext = context;
		loadFile(globalContext.getFilesDir());
	}

	public void loadFile(File root) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader inputReader = null;
			for (File child : root.listFiles()) {
				if (root.isDirectory()) {					
					if (child.exists()) {
						if(child.length() != 0){
							String inputString;
							String fileName = child.getName();
							String fileNameParts[] = fileName.substring(0, fileName.length()-4).split("_");
							String sha1 = fileNameParts[3];
							fis = globalContext.openFileInput(child.getName().toString());
							isr = new InputStreamReader(fis);
							inputReader = new BufferedReader(isr);
							while ((inputString = inputReader.readLine()) != null) {
								System.out.println("inputString" + inputString);
								baos.write(inputString.getBytes());
							}
							exceptionDelete = connectAndSendHttp(baos, sha1);
							if(exceptionDelete == false)
								(new File(child.getCanonicalPath())).delete();
							baos.flush();
							baos.close();
							baos.reset();
							fis.close();
							isr.close();
							inputReader.close();
						}
						else
							child.delete();
					}
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {			
			Log.e("tag", e.getMessage());
		}
	}

	public boolean connectAndSendHttp(ByteArrayOutputStream baos, String sha1){
		try {
			URL url;
			url = new URL("http://10.0.2.2:8080");

			String charset = "UTF-8";

			HttpURLConnection conn;
			
			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Accept-Charset", charset);
			conn.setRequestProperty("X-checksum", sha1);
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
			
			OutputStream output = conn.getOutputStream();

			System.out.println("baos size: " + baos.size());

			output.write(baos.toByteArray());
			baos.flush();
			baos.close();
			baos.reset();
			output.flush();
			output.close();	
			int responseCode = conn.getResponseCode();
			if(responseCode == 409){
				return true;
			}
			conn.getInputStream();
			conn = null;
		}
		catch (ConnectException e) {
			e.printStackTrace();
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
			return true;
		}
		return false;
	}
}
