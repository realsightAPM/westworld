package com.realsight.westworld.tsp.lib.text;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/* *
 * 
 * */

public class TextWriter {
	
	private String fileName = null;
	private Charset charset = null;
	
	private boolean closed = true;
	private boolean initialized = false;
	private boolean preserveText = false;
	
	private Writer outputStream = null;
	
	public TextWriter(String fileName, Charset charset, boolean preserveText) {
		
		if (fileName == null) {
			throw new IllegalArgumentException("Parameter fileName can not be null.");
		}

		if (charset == null) {
			throw new IllegalArgumentException("Parameter charset can not be null.");
		}

		this.fileName = fileName;
		this.charset = charset;
		this.preserveText = preserveText;
		this.closed = false;
	}
	
	public TextWriter(String fileName, boolean preserveText) {
		this(fileName, Charset.forName("ISO-8859-1"), preserveText);
	}
	
	public TextWriter(String fileName) {
		this(fileName, Charset.forName("ISO-8859-1"), false);
	}

	private void checkClosed() throws IOException {
		if (closed) {
			throw new IOException(
			"This instance of the CsvWriter class has already been closed.");
		}
	}
	
	public void write(String content)
			throws IOException {
		checkClosed();
		checkInit();
		outputStream.write(content);
	}
	
	public void flush() throws IOException {
		this.outputStream.flush();
	}
	
	public void close() {
		if (!closed) {

			try {
				if (initialized) {
					outputStream.close();
				}
			} catch (Exception e) {
				// just eat the exception
			}

			outputStream = null;

			closed = true;
		}
	}
	
	private void checkInit() throws IOException {
		if (!initialized) {
			if (fileName != null) {
				outputStream = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(fileName, this.preserveText), charset));
			}

			initialized = true;
		}
	}
	
}
