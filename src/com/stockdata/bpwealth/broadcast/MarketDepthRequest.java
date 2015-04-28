
package com.stockdata.bpwealth.broadcast;

import java.util.List;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.CompressionHeader;
import com.stockdata.bpwealth.core.MessageHeader;
import com.stockdata.bpwealth.core.Request;




public class MarketDepthRequest extends Request {
    //private int token;

    public MarketDepthRequest(List<Integer> tokens)
    {
        //this.token = token;
        this.compHeader = new CompressionHeader((short)410,(short) 410);
        this.msgHeader =  new  MessageHeader((short)410,CConstants.TransactionCode.Broadcast_Request_Response);
        this.struct = new byte[414];
        System.arraycopy(this.compHeader.getByteArray(), 0, this.struct, 0, 4);
        System.arraycopy(this.msgHeader.getByteArray(), 0, this.struct, 4, 4);
        CConstants.setShort((short)1, struct, 8);
        int kindex = 10;
        for(int token : tokens){
        	CConstants.setInt(token, struct, kindex);
        	kindex = kindex + 4;
        }
        CConstants.setShort((short)1, struct, struct.length-3);
    }
    
    
}
