package com.stockexit.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;


public class StockExit {
	
	public static void main(String[] args) {
		
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
		db.closeSession();
		
		ExecutorService executorService = Executors.newFixedThreadPool(records.size());
		for(int i=0;i<records.size();i++){
			executorService.execute(new ExitWorker(records.get(i),i));
		}
		executorService.shutdown();
		boolean result = false;
		while(result==false){
			try {
				result = executorService.awaitTermination(7, TimeUnit.HOURS);
				LoggerUtil.getLogger().info("Executor service result - " + result);
			} catch (InterruptedException e) {
				LoggerUtil.getLogger().log(Level.SEVERE, "StockExit Executor failed", e);
			}
		}
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
