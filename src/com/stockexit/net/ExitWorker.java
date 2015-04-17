package com.stockexit.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;





public class ExitWorker implements Runnable {
	
	private BuySell buysell;
	private int id;
	private String curdate;
	private List<Double> prices;
	
	public ExitWorker(BuySell buysell, int id, String curdate){
		this.buysell = buysell;
		this.id = id;
		this.curdate = curdate;
		prices = new ArrayList<Double>();
	}
	

	@Override
	public void run() {
		FutureMC downloader = null;
		if(StockExitUtil.runFuture){
			downloader = new FutureMC();
		}else{
			System.exit(1);
		}
		SymbolEstimator estimator = new SymbolEstimator(buysell, curdate);
		boolean sold = false;
		intitialwait();
		while(true){
			String info = downloader.downloadData(buysell);
			String lasttime; double high; double low; double price;
			try{
				Calendar cal = Calendar.getInstance();
				String hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
				String minute = String.format("%02d",cal.get(Calendar.MINUTE));
				lasttime = hour+":"+minute;
				high = Double.parseDouble(info.split("/")[2]);
				low = Double.parseDouble(info.split("/")[1]);
				price = Double.parseDouble(info.split("/")[0]);
			}catch(Exception e){
				LoggerUtil.getLogger().info("***************Broadcast did not give valid result*************"+"Thread - " + buysell.getSymbol());
				continue;
			}
			prices.add(price);
			if(lasttime.compareTo("09:35") >= 0 && lasttime.compareTo("14:25") < 0){
				sold = estimator.exitAtMidday(prices,low,high,lasttime);
			}else if(lasttime.compareTo("14:25") >= 0 && lasttime.compareTo("15:28") <= 0){
				sold = estimator.exitAtEnd(prices,low,high,lasttime);
			}
			
			if(sold){
				break;
			}
			else if(lasttime.compareTo("15:28") > 0){
				estimator.updateStock();
				break;
			}
			else{
				intervalwait();
			}
		}
		
	}


	private void intitialwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (id*2*1000);
		while(System.currentTimeMillis() < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-System.currentTimeMillis()+timestamp);
			} catch (InterruptedException e) {
				LoggerUtil.getLogger().info("Thread interrrupted in initial wait - "+buysell.getSymbol());
			}
		}
	}
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (60*1000);
		while(System.currentTimeMillis() < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-System.currentTimeMillis()+timestamp);
			} catch (InterruptedException e) {
				LoggerUtil.getLogger().info("Thread interrrupted in interval wait - "+buysell.getSymbol());
			}
		}
	}

}
