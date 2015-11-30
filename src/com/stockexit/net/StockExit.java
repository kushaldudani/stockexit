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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

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
		String secondlastentry = dates.get(dates.size()-2);
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
		db.closeSession();
		
		Set<String> stocksForNews = new HashSet<String>();
		final ReentrantLock lock = new ReentrantLock();
		Map<String,List<SynQueue<TickData>>> queuemap = new HashMap<>();
		for(BuySell bsell : records){
			if(bsell.isExited()==false){
				String sss = bsell.getSymbol().split("-")[0];
				/*if(!sss.equals("NIFTY") && bsell.getType().equals("Long")){
					setLongEntry(sss);
				}else if(!sss.equals("NIFTY") && bsell.getType().equals("Short")){
					setShortEntry(sss);
				}*/
				stocksForNews.add(sss);
				SynQueue<TickData> qu = new SynQueue<TickData>();
				new Thread(new ExitWorker(bsell,null,qu,lastentry,lock)).start();
				if(!queuemap.containsKey(sss)){
					queuemap.put(sss, new ArrayList<SynQueue<TickData>>());
				}
				queuemap.get(sss).add(qu);
			}
		}
		if(!queuemap.containsKey("NIFTY")){
			queuemap.put("NIFTY", new ArrayList<SynQueue<TickData>>());
		}
		new Thread(new NewsCache(stocksForNews, lastentry, secondlastentry)).start();
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
