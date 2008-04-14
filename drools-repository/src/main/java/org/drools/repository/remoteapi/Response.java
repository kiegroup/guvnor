package org.drools.repository.remoteapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;



public abstract class Response {
	abstract String getContentType();
	abstract void writeData(OutputStream out) throws IOException ;
	public Calendar lastModified;


	public static class Text extends Response {
		String data;

		@Override
		String getContentType() {
			return "text/plain";
		}

		@Override
		void writeData(OutputStream out) throws IOException {
			out.write(data.getBytes());
		}
	}

	public static class Binary extends Response {

		InputStream stream;

		@Override
		String getContentType() {
			return "application/octet-stream";
		}


		@Override
		void writeData(OutputStream out) {

			// TODO Auto-generated method stub

		}

	}


}


