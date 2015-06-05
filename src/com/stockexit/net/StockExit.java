package com.stockexit.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import com.stockdata.bpwealth.OrderDispatcher;
import com.stockdata.bpwealth.broadcast.BroadCastManager;
import com.stockdata.bpwealth.broadcast.TickData;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.SynQueue;


public class StockExit {
	
	public static void main(String[] args)  {
		
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		
		List<String> dates = readDates();
		if(dates.size()<1){
			LoggerUtil.getLogger().info("Not sufficient entries in dates file");
			System.exit(1);
		}
		String lastentry = dates.get(dates.size()-1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		if(!(sdf.format(today.getTime()).equals(lastentry)) ){
			LoggerUtil.getLogger().info("Market should be closed today");
			System.exit(1);
		}
		
		DbManager db = new DbManager();
		db.openSession();
		List<BuySell> records = db.getBusSells();
		if(records.size() == 0){
			LoggerUtil.getLogger().info("No Stocks to exit");
		    System.exit(1);
		}
		OrderDispatcher od=null;
		try {
			od = new OrderDispatcher();
			od.connect();
		} catch (Exception e) {
			LoggerUtil.getLogger().info("Cannot create the instance of OrderDispatcher");
		    System.exit(1);
		}
		
		for(int ii=1;ii<records.size();ii++){
			if((records.get(ii).getSymbol().split("-")[0]).
					equals(records.get(ii-1).getSymbol().split("-")[0])){
				BuySell prevbs = records.get(ii-1);
				BuySell curbs = records.get(ii);
				//prevbs.setDaystried(-1);
				prevbs.setExited(true);
				db.insertOrUpdate(prevbs);
				int prevbudget = prevbs.getbudget();
				int curbudget = curbs.getbudget();
				int totalbudget = (prevbudget+curbudget);
				double newenterprice = ((prevbs.getEnterprice()*prevbudget)+
						(curbs.getEnterprice()*curbudget))/(totalbudget);
				curbs.setEnterprice(newenterprice);
				curbs.setHasbudget(totalbudget);
				curbs.setDaystried(prevbs.getDaystried());
				db.insertOrUpdate(curbs);
			}
		}
		db.closeSession();
		
		Map<String,SynQueue<TickData>> queuemap = new HashMap<String,SynQueue<TickData>>();
		for(BuySell bsell : records){
			if(bsell.isExited()==false){
				String sss = bsell.getSymbol().split("-")[0];
				SynQueue<TickData> qu = new SynQueue<TickData>();
				new Thread(new ExitWorker(bsell,qu,lastentry, od)).start();
				queuemap.put(sss, qu);
			}
		}
		
		BroadCastManager.mainrun(queuemap);
	}
	
	
	private static List<String> readDates(){
		InputStreamReader is = null;
		BufferedReader br = null;
		List<String> dates = new ArrayList<String>();
		
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("/Users/kushd/nse/dates")));
			br =  new BufferedReader(is);
			
			
			String line; 
			while ((line = br.readLine()) != null) {
				String date = line.trim();
				dates.add(date);
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "Stock Exit reading of dates failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
				
			} catch (IOException e) {}
		}
		return dates;
	}

}
