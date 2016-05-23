package com.stockexit.zerodha;

import java.io.IOException;

import com.stockdata.bpwealth.core.CConstants;


public class ZerodhaHeader {
	short checksum = 255;
	short length = 243; 
	short messageCode; 
	int errorCode=0; 
	int time=0;

	public ZerodhaHeader(short messagecode) {
		this.messageCode = messagecode;
	}
	
	public ZerodhaHeader(byte[] bytes) throws Exception{
		messageCode = CConstants.getInt16(bytes, 4);
	}
    
    public void toStream(NativeDataOutputStream dos) throws IOException{
		
    	dos.writeShort(checksum); //checksum
		dos.writeShort(length);
		dos.writeShort(messageCode);
		dos.writeInt(errorCode); //error code
		dos.writeInt(time); //time
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ZerodhaHeader [checksum=" + checksum + ", length=" + length
				+ ", messageCode=" + messageCode + ", errorCode=" + errorCode
				+ ", time=" + time + "]";
	}
}
