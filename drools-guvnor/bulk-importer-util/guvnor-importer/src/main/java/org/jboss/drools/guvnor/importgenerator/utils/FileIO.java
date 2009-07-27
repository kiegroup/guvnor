package org.jboss.drools.guvnor.importgenerator.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
/**
 * File IO helper class for reading/writing files and converting to/from base64 
 * 
 * @author <a href="mailto:mallen@redhat.com">Mat Allen</a>
 */

public class FileIO {
  public static final String FORMAT="utf-8";
  
  public static void write(String data, File destination) throws IOException{
//    if (!destination.getParentFile().exists()){
//      Logger.logln("creating folder for: "+ destination.getParentFile().getAbsolutePath());
//      destination.mkdirs();
//    }
    BufferedWriter out = new BufferedWriter(new FileWriter(destination));
    out.write(data.toString());
    out.flush();
    out.close();
  }
	public static String getExtension(File file){
		int dotpos=file.getName().lastIndexOf(".")+1;
		return file.getName().substring(dotpos);
	}
	public static String readAllAsBase64(File f) throws UnsupportedEncodingException{
		byte[] bytes=FileIO.readAll(f);
		byte[] base64bytes=Base64.encodeBase64(bytes);
		String base64String=new String(base64bytes, "utf-8");
		return base64String;
	}
	public static String toBase64(byte[] b) throws UnsupportedEncodingException{
    byte[] b64=Base64.encodeBase64(b);
    return new String(b64, "utf-8");
	}
	public static String fromBase64(byte[] b64) throws UnsupportedEncodingException{
	  byte[] b=Base64.decodeBase64(b64);
	  return new String(b, "utf-8");
	}
	public static byte[] readAll(File f) {
		FileInputStream in=null;
		byte[] buf=null;
		try {
			in = new FileInputStream(f);
			buf = new byte[new Long(f.length()).intValue()+1]; //and hope the file is not too large!
			in.read(buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buf;
	}
	public static String readAll(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
