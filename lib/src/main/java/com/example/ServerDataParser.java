package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map.Entry;
import java.nio.file.*;

/*
 *
 * This program is supposed to be run as:
 * ServerDataParser -key InfoStream -title <title> -output TCPHitsPerUser.json output_temp4wrwrwer.txt
 * ServerDataParser -title <title> -key InfoStream -output TCPHitsPerUser.json output_temp4wrwrwer.txt
 * ServerDataParser -title <title> -key InfoStream -output TCPHitsPerUser.json c:\Users\Andrei\Desktop\output_temp4wrwrwer.txt
 * ServerDataParser -key InfoStream output_temp4wrwrwer.txt -title <title> -output TCPHitsPerUser.json
 * ServerDataParser -output TCPHitsPerUser.json output_temp4wrwrwer.txt -key InfoStream -title <title>
 * ServerDataParser -key InfoStream -title <title> -output TCPHitsPerUser.json output_temp4wrwrwer.txt
 * ServerDataParser -key InfoStream -title <title> output_temp4wrwrwer.txt
 * ServerDataParser output_temp4wrwrwer.txt
 * ServerDataParser output_*.txt
 * ServerDataParser ** /output_*.txt
 *
 *
 * Skriv ut på consolen samma sak som skrivs till filen.
 * Om -output filename.json slutar på '.json' skriv i jsonformat.
 * Eller skriv bara jsonformat hela tiden
 * Läs inte in filen igen, det behövs inte.
 * Kolla hur "glob:" funkar med matchern, vad blir det för skillnad?
 * Gör en launchkonfiguration för varje exempel ovan.
 * Fundera på hur du ska göra med filenames.txt, ska den vara hårdkodad som den är nu, eller ska den vara en parameter till programmet?
 * Gör alla hårdkodade pather till parametrar. Inga "lib/d3js/" i patherna i programmet. Använd bara patharg och output variablena.
 * I dom fallen du behöver skriva till mer än en fil (t ex när key används i filnamnet) så skriv till en fil som heter <outputfilnamn>_key
 * Alla värden kommer inte (är nog fel på klienten i Android), debugga det. Viktigt att förstå koden.
 *
 */
public class ServerDataParser {

	private static char ASCII_SOT = '\002';
	private static char ASCII_SOH = '\001';
	private static char ASCII_TE = '\003';
	private static char ASCII_US = '\037';
	private static char ASCII_RS = '\030';
	private static char ASCII_EOT = '\004';
	private static String absolutePath = null;
	private static String currentDirFile = null;
	private static ArrayList<File> pathsArgv = new ArrayList<File>();
	private static boolean titleAdded = false;
	ServerDataParser sdp = null;
    public static void main(String argv[]) throws IOException {
        String keyArgv = null;
        String titleArgv = null;
        String outputArgv = null;
		ServerDataParser sdp = new ServerDataParser();
		for(int i = 0; i < argv.length; i++){
			if(argv[i].startsWith("-key")){
				keyArgv = argv[i + 1];
				i++;
			}
			else if(argv[i].startsWith("-title")){
				titleArgv = argv[i + 1];
				i++;
			}
            else if(argv[i].startsWith("-output")){
                outputArgv = argv[i + 1];
                i++;
            }
			// add output argument
			else{
                String glob = null;
                Path path = null;

                if(argv[i].startsWith("glob"))
                    glob = argv[i];
                else
				    path = Paths.get(argv[i]);
				if(isDir(path)){
					pathsArgv.addAll(sdp.listByFiles(path.toString()));
				}
				else if(argv[i].endsWith(".txt") && !argv[i].startsWith("glob")){
					pathsArgv.add(new File(argv[i]));
				}
                else{
                    //String glob = "glob:**/output_temp*.txt";

                    //String pathArgv = "C:/Users/AndreiZimine/Desktop/TestJni2/";
                    String pathArgv = System.getProperty("user.dir");
                    match(glob, pathArgv);
                }
			}
		}

    for (File file : pathsArgv) {
        // add outputfile as argument (null if console should be used)
        // move json writer into this function
        sdp.readCompleteFileByTitle(file.getAbsolutePath(), System.getProperty("user.dir"), keyArgv, titleArgv);
        if(keyArgv != null) {
            if (outputArgv != null)
                fromTxtFileToJson(System.getProperty("user.dir"), outputArgv, keyArgv, null);
            else
                fromTxtFileToJson(System.getProperty("user.dir"), null, keyArgv, titleArgv);
        }
    }
    // move this into file writing (as  mentioned above)
}
    public static void match(String glob, String location) throws IOException {

        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);

        Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {

            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (pathMatcher.matches(path)) {
                    pathsArgv.add(path.toFile());
                }
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }


	public String getNextBlock(InputStream input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int singleChar = 0;
		String value = null;

		while((singleChar = input.read()) != -1){
			value = String.valueOf((char)singleChar);
			baos.write(value.getBytes());
			if(singleChar == ASCII_SOT)
				break;
			if(singleChar == ASCII_EOT)
				return baos.toString();	
		}
		if(singleChar == -1)
			return null;
		
		return baos.toString();
	}

	public String getTitleFromBlock(String block) throws IOException {
		StringReader reader = new StringReader(block);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int singleChar = 0; 
		String stringValueOf = null;
		while((singleChar = reader.read()) != -1){
			stringValueOf = String.valueOf((char)singleChar);
			baos.write(stringValueOf.getBytes());
			if(singleChar == ASCII_TE){
				return baos.toString().trim();
			}
		}
		return "";
	}

	public LinkedHashMap<String,String> getKeyValues(String block) throws IOException {
		LinkedHashMap<String, String> keyValueFinal = new LinkedHashMap<String, String>();

		Iterator<String> keyValue2Iterator = getKeys(block).iterator();
		Iterator<String> keyValueIterator = getValues(block).iterator();
		while(keyValue2Iterator.hasNext() && keyValueIterator.hasNext()){
			keyValueFinal.put(keyValue2Iterator.next(), keyValueIterator.next());
		}
		
		return keyValueFinal;	
	}
	
	public List<String> getKeys(String block) throws IOException{
		StringReader reader = new StringReader(block);
		int singleChar = 0;
		String value = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> keyValue = new ArrayList<String>();
		while((singleChar = reader.read()) != -1){	
			value = String.valueOf((char)singleChar);
			baos.write(value.getBytes()); 
			if(singleChar == ASCII_TE){
				baos.reset();
			}
			if(singleChar == ASCII_US){
				keyValue.add(baos.toString());
				baos.reset();
				while((singleChar = reader.read()) != -1){
					value = String.valueOf((char)singleChar);
					baos.write(value.getBytes());
					if(singleChar == ASCII_RS){	
						baos.reset();
						break;
					}
				}
			}
		}
		return keyValue;
	}
	
	public List<String> getValues(String block) throws IOException{
		StringReader reader = new StringReader(block);
		int singleChar = 0;
		String value = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		List<String> keyValue = new ArrayList<String>();
		while((singleChar = reader.read()) != -1){
			value = String.valueOf((char)singleChar);
			baos.write(value.getBytes());
			if(singleChar == ASCII_US){
				baos.reset();
				while((singleChar = reader.read()) != -1){
					value = String.valueOf((char)singleChar);
					baos.write(value.getBytes());
					if(singleChar == ASCII_RS){
						keyValue.add(baos.toString());
						baos.reset();
						break;
					}
				}
			}
		}
		return keyValue;
	}

	private void writeFileNameToFile(String key, String outputPath){
		File file= new File (outputPath + "/lib/d3js/fileNames.txt");
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		String line;
		boolean lineCheck = false;
		try {
			FileWriter writer = null;
			if (file.exists())
			{
				inputStreamReader = new InputStreamReader(new FileInputStream (file));
                bufferedReader = new BufferedReader(inputStreamReader);
                writer = new FileWriter(file, true);
        }
        else if(!file.exists())
			{
			    file.createNewFile();
			    writer = new FileWriter(file);
			    inputStreamReader = new InputStreamReader(new FileInputStream (file));
			    bufferedReader = new BufferedReader(inputStreamReader);
			    writer.write("Files");
			    writer.write(System.getProperty("line.separator"));
			}
			while((line = bufferedReader.readLine()) != null){
				if(Objects.equals(line, new String(key))){
					lineCheck = true;
					break;
				}
			}
			if(lineCheck == false){
				writer.write(key);
				writer.write(System.getProperty("line.separator"));
			}
			writer.close();
			inputStreamReader.close();
			bufferedReader.close();
		}
		catch(IOException io){
			io.printStackTrace();
		}
	}

	private void writeBlockToFile(String block, String key, String title, String filePath){
            try {
                File file = null;
                if(key != null)
                    file= new File(filePath + "/lib/" + key + ".txt");
			FileWriter writer = null;
			if (file.exists())
			{
				writer = new FileWriter(file, true);
			}
			else if(!file.exists())
			{
			   file.createNewFile();
			   writer = new FileWriter(file);
			}
			String titleReturned = getTitleFromBlock(block);
                //if (title != null && !title.equals(titleReturned))
                 //   return;
                if (key == null && titleReturned != "")	{
                    System.out.println("Title:"  + titleReturned);
                }
			if (key.equals(titleReturned) && titleAdded == false){
				titleAdded = true;
             writeFileNameToFile(title, filePath);
             //writer.write(titleReturned);
                        writer.write(System.getProperty("line.separator"));
                    }
                LinkedHashMap<String,String> keyValues = getKeyValues(block);
                for (Entry<String, String> entry : keyValues.entrySet()) {
                    String keyReturn = entry.getKey();
                        String valueReturn = entry.getValue();
                    if (key == null || title.equals(keyReturn.trim())){
                        System.out.println(titleReturned + "\t" + keyReturn.trim() + "\t" + valueReturn.trim());
                    }
                        if (key == null || title.equals(keyReturn.trim())) {
                            if (valueReturn.length() > 1) {
                                writer.write(keyReturn.trim() + "\t" + valueReturn.trim());
                                writer.write(System.getProperty("line.separator"));
                            }
                        }
			}
			writer.close();
		 } catch (IOException io) {
			 io.printStackTrace();
	     }
	}
	
	public void readCompleteFileByTitle(String fileName, String output, String key, String title)  {
		InputStream is;
		try {
			is = new FileInputStream(fileName);
			sdp = new ServerDataParser();
			String block = sdp.getNextBlock(is);
                    while (block != null) {
                        String titlePassed = sdp.getTitleFromBlock(block);
                        //sdp.printBlock(block, key, title);
                        if(block != null && key != null && title != null)
					    sdp.writeBlockToFile(block, key, title, output);
                    block = sdp.getNextBlock(is);
                }
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<File> listByFiles(String directory){
		File f = null;
	    File[] paths = null;
	    ArrayList<File> rPaths = new ArrayList<File>();
	    
	    ServerDataParser sdp = new ServerDataParser();
	    try{
                f = new File(directory);
                paths = f.listFiles();
                for(File path:paths)
                {
	        	if(path.getAbsolutePath().endsWith(".txt")){
	        		rPaths.add(path);
	        	}
	        	else if(path.isDirectory())
	        	{
	        		sdp.listByFiles(path.getAbsolutePath().toString());
	        	}
	        }
	      }
	      catch(Exception e){
	    	  e.printStackTrace();
		  }
	      
		return rPaths;
	}
	
	static Boolean isDir(Path path) {
		  if (path == null || !Files.exists(path)) 
			  return false;
		  else 
			  return Files.isDirectory(path);
	}
	
	private static void fromTxtFileToJson(String outputDir, String output, String keyArgv, String title){
            Writer writer = null;
            try{
                File file = new File(outputDir + "/lib/" + keyArgv + ".txt");
                if(output.endsWith(".json"))
                    writer = new FileWriter(outputDir + "/lib/d3js/" + output);
                else
                    writer = new FileWriter(outputDir + "/lib/d3js/" + title + ".json");
			StringBuilder sb = new StringBuilder();
			String line;
			boolean keyCheck = false;
                System.out.println(file.getPath());
			if(file.exists()){
				InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream (file));
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    line = bufferedReader.readLine();
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        String[] parts = line.split("\\s+", 2);
			        if (parts.length >= 2)
			        {
			            String key = parts[0];
			            String value = parts[1];
			            if(keyCheck == false){
			            	keyCheck = true;
			            	sb.append("[{" + "\"" + key + "\"" + " : [");
			            	sb.append(value);
			            }
			            else{
			            	sb.append("," + value);
			            }
			        }
			    }
			    if(sb.length() > 0){
			    	sb.append("]");
			    	sb.append("}]");
			    }
			    writer.write(sb.toString());
			    bufferedReader.close();
			    writer.close();
			    writer.flush();
                file.deleteOnExit();
			}
		}
		catch(IOException e){
			//e.printStackTrace();	
		}
	}
	
	private void printBlock(String block, String key, String title) throws IOException {
		String titleReturned = getTitleFromBlock(block);
		if (title != null && !title.equals(titleReturned))
			return;
		if (key == null && titleReturned != "")	{
			System.out.println("Title:"  + titleReturned);
		}
		LinkedHashMap<String,String> keyValues = getKeyValues(block);
		for (Entry<String, String> entry : keyValues.entrySet()) {
		    String keyReturn = entry.getKey();
		    String valueReturn = entry.getValue();
			if (key == null || key.equals(keyReturn.trim())){
				System.out.println(titleReturned + "\t" + keyReturn.trim() + "\t" + valueReturn.trim());		
			}
		}
	}
}
