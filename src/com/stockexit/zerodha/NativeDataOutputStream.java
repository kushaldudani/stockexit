package com.stockexit.zerodha;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * NativeDataOutputStream
 */
public class NativeDataOutputStream extends FilterOutputStream {			  
	  public NativeDataOutputStream(BufferedOutputStream out) {
		    super(out);
		  }
		 
		  public void writeShort(short value) throws IOException {
			    ByteBuffer buffer = ByteBuffer.allocate(2).order(ByteOrder.nativeOrder());
			    buffer.putShort(value);
			    out.write(buffer.array());
				out.flush();
		  }
		 
		  public void writeInt(int value) throws IOException {
			    ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
			    buffer.putInt(value);
			    out.write(buffer.array());
				out.flush();
		  }
		  public void writeDouble(double value) throws IOException {
			    ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.nativeOrder());
			    buffer.putDouble(value);
			    out.write(buffer.array());
				out.flush();
			  }

		  

		  public void writeBytes(byte[] value) throws IOException {			  
			    out.write(value);
				out.flush();
		  }
		  
	}