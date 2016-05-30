package com.stockdata.bpwealth.broadcast;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.stockdata.bpwealth.core.CConstants;
import com.stockexit.util.LoggerUtil;

public class DepthResponse {
	
	public int instrument_token;
    public int ltp;
    public int ltq;
    public int atp;
    public int volume;
    public int totalbuy;
    public int totalsell;
    public int open;
    public int high;
    public int low;
    public int close;
    
    
    public DepthResponse(byte[] data){
    	
    	instrument_token = CConstants.getInt32(data, 0);
    	ltp = CConstants.getInt32(data, 4);
    	ltq = CConstants.getInt32(data, 8);
    	atp = CConstants.getInt32(data, 12);
    	volume = CConstants.getInt32(data, 16);
    	totalbuy = CConstants.getInt32(data, 20);
    	totalsell = CConstants.getInt32(data, 24);
    	open = CConstants.getInt32(data, 28);
    	high = CConstants.getInt32(data, 32);
    	low = CConstants.getInt32(data, 36);
    	close = CConstants.getInt32(data, 40);
        
        
    }
    
    public static List<DepthResponse> parse(byte[] data){
    	List<DepthResponse> depths = new ArrayList<>();
    	if(data.length >=2){
    		try {
    			short packetcnt = CConstants.getInt16(data, 0);
    			int index = 2;
    			for(short i=0;i<packetcnt;i++){
    				short packetlength = CConstants.getInt16(data, index);
    				byte[] packet = new byte[packetlength];
    				System.arraycopy(data, index+2, packet, 0, packetlength);
    				DepthResponse depth = new DepthResponse(packet);
    				depths.add(depth);
    				index = index + packetlength;
    			}
    		}catch(Exception e){
    			LoggerUtil.getLogger().log(Level.SEVERE, "exception in parsing broadcast data ", e);
    		}
    	}
    	return depths;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DepthResponse [instrument_token=" + instrument_token + ", ltp="
				+ ltp + ", ltq=" + ltq + ", atp=" + atp + ", volume=" + volume
				+ ", totalbuy=" + totalbuy + ", totalsell=" + totalsell
				+ ", open=" + open + ", high=" + high + ", low=" + low
				+ ", close=" + close + "]";
	}
    
	
    public float getDayHigh(){
        return (float)(this.high/(float)100.00);
    }
    public float getDayLow(){
        return (float)(this.low/(float)100.00);
    }
    
    public float getLTP(){
        return (float)(this.ltp/(float)100.00);
    }
    public float getOpen(){
        return (float)(this.open/(float)100.00);
    }
    
    public long getVolume(){
    	return this.volume;
    }
   

}
