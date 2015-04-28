
package com.stockdata.bpwealth.core;


public class CompressionHeader {
    public short    MsgCompLen;
    public short    MsgLen;

    public CompressionHeader(short MsgCompLen, short MsgLen) {
        this.MsgCompLen = MsgCompLen;
        this.MsgLen = MsgLen;
    }
     public CompressionHeader(byte[] bytes){
        MsgCompLen = CConstants.getInt16(bytes, 0);
        MsgLen = CConstants.getInt16(bytes, 2);
    }
    
      public byte[] getByteArray(){
        byte[] headerBytes = new byte[4];
        CConstants.setShort(MsgCompLen, headerBytes, 0);
        CConstants.setShort(MsgLen, headerBytes, 2);
        return headerBytes;
    }
}
