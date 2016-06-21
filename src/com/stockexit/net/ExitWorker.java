package com.stockexit.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.stockdata.bpwealth.broadcast.TickData;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;





public class ExitWorker implements Runnable {
	
	private BuySell buysell;
	private SecondModel smodel;
	private SynQueue<TickData> qu;
	private String curdate;
	private ReentrantLock lock;
	private List<Double> prices;
	
	
	public ExitWorker(BuySell buysell, SecondModel smodel, 
			SynQueue<TickData> qu, String curdate, ReentrantLock lock){
		this.buysell = buysell;
		this.smodel = smodel;
		this.qu = qu;
		this.curdate = curdate;
		this.lock = lock;
		prices = new ArrayList<Double>();
		
	}
	
	/*private double trendprice10 = 0;
	private double trendprice11 = 0;
	private double trendprice12 = 0;
	private double trendprice13 = 0;
	private double trendprice14 = 0;*/

	@Override
	public void run() {
		FutureMC downloader = null;
		if(StockExitUtil.runFuture){
			downloader = new FutureMC();
		}else{
			System.exit(1);
		}
		SymbolEstimator estimator = new SymbolEstimator(buysell, smodel, curdate, lock, qu);
		boolean sold = false;
		//intitialwait();
		while(true){
			String info = downloader.downloadData(qu, getEntryType());
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
					LoggerUtil.getLogger().info("***************Broadcast did not give valid result*************"+"Thread - ");
					continue;
				}
				prices.add(price);
				//if(sold && lasttime.compareTo("15:25") < 0){
				//	estimator.logg(prices,low,high,lasttime);
				//}else 
				if(sold){
					break;
				}else if(lasttime.compareTo("15:28") > 0){
					LoggerUtil.getLogger().info("Counld not exit stock till end of day");
					break;
				}else if(lasttime.compareTo("09:16") >= 0 && lasttime.compareTo("15:28") <= 0){
					sold = estimator.dummyExit(prices,low,high,lasttime);
				}/*else if(lasttime.compareTo("09:55") >= 0 && lasttime.compareTo("10:45") < 0){
					if(trendprice10 == 0){
						trendprice10 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice10);
				}else if(lasttime.compareTo("10:55") >= 0 && lasttime.compareTo("11:45") < 0){
					if(trendprice11 == 0){
						trendprice11 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice11);
				}else if(lasttime.compareTo("11:55") >= 0 && lasttime.compareTo("12:45") < 0){
					if(trendprice12 == 0){
						trendprice12 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice12);
				}else if(lasttime.compareTo("12:55") >= 0 && lasttime.compareTo("13:45") < 0){
					if(trendprice13 == 0){
						trendprice13 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime,trendprice13);
				}else if(lasttime.compareTo("13:55") >= 0 && lasttime.compareTo("14:25") < 0){
					if(trendprice14 == 0){
						trendprice14 = estimator.getAvgTrend(prices);
					}
					sold = estimator.exitAtMidday(prices,low,high,lasttime, trendprice14);
				}else if(lasttime.compareTo("14:25") >= 0 && lasttime.compareTo("15:28") <= 0){
					sold = estimator.exitAtEnd(prices,low,high,lasttime);
				}*/
			}
			intervalwait();
		}
		
	}


	private String getEntryType(){
		if(buysell != null){
			return buysell.getType();
		}else {
			return smodel.getType();
		}
	}
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (1000);
		long curtimestamp;
		while((curtimestamp = System.currentTimeMillis()) < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-curtimestamp+timestamp);
			} catch (Exception e) {
				LoggerUtil.getLogger().info("Thread interrrupted in interval wait - ");
			}
		}
	}

}
