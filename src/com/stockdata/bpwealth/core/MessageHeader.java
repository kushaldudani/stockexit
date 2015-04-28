
package com.stockdata.bpwealth.core;


public class MessageHeader {
    public short    MsgLen;
    public short   MsgCode;

    public short getMsgLen() {
        return MsgLen;
    }

    public short getMsgCode() {
        return MsgCode;
    }
    public MessageHeader(byte[] bytes){
        MsgLen = CConstants.getInt16(bytes, 0);
        MsgCode = CConstants.getInt16(bytes, 2);
    }

    public MessageHeader(short message_length,short transaction_code) {
        this.MsgLen = message_length;
        this.MsgCode = transaction_code;
    }
    
    public byte[] getByteArray(){
        byte[] headerBytes = new byte[4];
        CConstants.setShort(MsgLen, headerBytes, 0);
        CConstants.setShort(MsgCode, headerBytes, 2);
        return headerBytes;
    }
}
