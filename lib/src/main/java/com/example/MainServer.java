package com.example;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class MainServer extends AbstractHandler
{
	FileOutputStream fos = null;
	ByteArrayOutputStream baos = null;
	InputStream is = null;
	int[] titleName = new int[100];
	int[] titleValue = new int[100];
    private static final Logger LOG = Logger.getLogger(MainServer.class.getName());
	
	int bytesRead = -1;
	MessageDigest md = null;

	public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException, EofException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Andrei Zimine</h1>");
        is = request.getInputStream();       
        int contentLength = request.getContentLength();
        String sha1FromSender = request.getHeader("X-checksum");
        try
        {
        	System.out.println("contentlength" + contentLength);
        	byte[] buffer = new byte[contentLength];
            baos = new ByteArrayOutputStream();
            int totalBytesRead = 0;
            while (true) {
	        	bytesRead = is.read(buffer, totalBytesRead, is.available());
	        	if (bytesRead == -1)
	        		break;
	        	totalBytesRead += bytesRead;
            }
            StringBuilder sb = new StringBuilder();
            if (contentLength != totalBytesRead) {
           		System.err.println("totalBytesRead does not match contentLength!");
    		}
       		if(buffer.length == contentLength){
            	try {
					md = MessageDigest.getInstance("SHA-1");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			md.update(buffer);
    			byte[] mdbytes = md.digest();
    	        System.out.println("buffer3: " + buffer.length);       
    	        for (int i = 0; i < mdbytes.length; i++) {
    	          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
    	        }

    	        md.reset();
    	        mdbytes = new byte[0];
    		}
       		
       		System.out.println("sha1 " + sha1FromSender + "--" + sb.toString());
       		if(!sha1FromSender.equals(sb.toString())){
       	        LOG.log(Level.WARNING,"checksum does not match!");
       	        LOG.warning("checksum does not match!");
       	        LOG.severe("Exiting!");
       	        response.setStatus(HttpServletResponse.SC_CONFLICT);
       		}
       		else{
       			fos = new FileOutputStream("output_temp" + request.hashCode() + ".txt");       	

            	baos.write(buffer);
       			baos.writeTo(fos);
            	fos.flush();
            	fos.close();
       		}
       		sb = null;
        	baos.flush();
        	baos.close();
        	baos.reset();
        	is.close();
        	buffer = new byte[0];
        	contentLength = 0;
        	
        	buffer = null;
        }
        
    	catch(IOException e)
    	{
    		System.err.println("Caught IOException: " + e.getMessage());
    	} 
        finally 
        {
        	if(fos != null)
        		fos.close();
        }
    }
		
    public static void main(String[] args) throws Exception
    {	
        Server server = new Server(8080);
        server.setHandler(new MainServer());
        server.start();
        server.join(); 
    }
}