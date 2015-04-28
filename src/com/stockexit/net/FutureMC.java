package com.stockexit.net;




import com.stockdata.bpwealth.broadcast.TickData;
import com.stockexit.util.SynQueue;



public class FutureMC  {
	
	
	
	public FutureMC() {
	}

	
	public String downloadData(SynQueue<TickData> qu, String type){
		TickData td = qu.dequeue();
		if(td==null){
			return null;
		}
		double price=0; double high=0; double low=0;
		if(type.equals("Long")){
			price = td.getBidprice();
		}else{
			price = td.getAskprice();
		}
		high = td.getHigh();
		low = td.getLow();
		
		return price+"/"+low+"/"+high;
	}
	
	
	

}
