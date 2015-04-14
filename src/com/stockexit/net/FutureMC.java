package com.stockexit.net;



import java.util.List;



public class FutureMC  {
	
	private TickDataManager tickdatamanager;
	
	
	public FutureMC() {
		tickdatamanager = new TickDataManager();
	}

	
	public String downloadData(BuySell buysell){
		tickdatamanager.openSession();
		List<TickData> tickdatas = tickdatamanager.getTickDatas(buysell.getSymbol().split("-")[0]);
		int size = tickdatas.size();
		double price=0; double high=0; double low=0;
		TickData td = tickdatas.get(size-1);
		price = td.getLastprice();
		high = td.getHigh();
		low = td.getLow();
		
		tickdatamanager.closeSession();
	
		return price+"/"+low+"/"+high;
	}
	
	
	

}
