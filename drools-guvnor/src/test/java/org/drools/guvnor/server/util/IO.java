package org.drools.guvnor.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IO {


	public static String read(InputStream st) {
	    try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(st));
	        StringBuilder sb = new StringBuilder();
	        String str;
	        while ((str = in.readLine()) != null) {
	            sb.append(str);
	            sb.append('\n');
	        }
	        in.close();
	        return sb.toString();
	    } catch (IOException e) {
	    	throw new IllegalStateException(e);
	    }

	}
}
