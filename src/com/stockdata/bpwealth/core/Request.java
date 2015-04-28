
package com.stockdata.bpwealth.core;

import java.io.UnsupportedEncodingException;


public  class Request {
public CompressionHeader compHeader;
public MessageHeader msgHeader;
public byte[] struct;

    public byte[] getStruct() throws UnsupportedEncodingException{
        return struct;
    }


}
