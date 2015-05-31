package com.stockexit.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.stockdata.bpwealth.broadcast.TickData;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;





public class ExitWorker implements Runnable {
	
	private BuySell buysell;
	private SynQueue<TickData> qu;
	private String curdate;
	private List<Double> prices;
	
	
	public ExitWorker(BuySell buysell, SynQueue<TickData> qu, String curdate){
		this.buysell = buysell;
		this.qu = qu;
		this.curdate = curdate;
		prices = new ArrayList<Double>();
	}
	
	private double trendprice10 = 0;
	private double trendprice11 = 0;
	private double trendprice12 = 0;
	private double trendprice13 = 0;
	private double trendprice14 = 0;

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
		//intitialwait();
		while(true){
			String info = downloader.downloadData(qu, buysell.getType());
			if(info != null){
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
				if(lasttime.compareTo("09:16") >= 0 && lasttime.compareTo("09:45") < 0){
					sold = estimator.exitAtStart(prices,low,high,lasttime);
				}else if(lasttime.compareTo("10:00") >= 0 && lasttime.compareTo("10:45") < 0){
					if(trendprice10 == 0){
						trendprice10 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice10);
				}else if(lasttime.compareTo("11:00") >= 0 && lasttime.compareTo("11:45") < 0){
					if(trendprice11 == 0){
						trendprice11 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice11);
				}else if(lasttime.compareTo("12:00") >= 0 && lasttime.compareTo("12:45") < 0){
					if(trendprice12 == 0){
						trendprice12 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice12);
				}else if(lasttime.compareTo("13:00") >= 0 && lasttime.compareTo("13:45") < 0){
					if(trendprice13 == 0){
						trendprice13 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice13);
				}else if(lasttime.compareTo("14:00") >= 0 && lasttime.compareTo("14:25") < 0){
					if(trendprice14 == 0){
						trendprice14 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime, trendprice14);
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
			}
			intervalwait();
		}
		
	}


	/*private void intitialwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (id*2*1000);
		while(System.currentTimeMillis() < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-System.currentTimeMillis()+timestamp);
			} catch (InterruptedException e) {
				LoggerUtil.getLogger().info("Thread interrrupted in initial wait - "+buysell.getSymbol());
			}
		}
	}*/
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (1000);
		while(System.currentTimeMillis() < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-System.currentTimeMillis()+timestamp);
			} catch (InterruptedException e) {
				LoggerUtil.getLogger().info("Thread interrrupted in interval wait - "+buysell.getSymbol());
			}
		}
	}

}
