package com.stockexit.net;

import java.util.ArrayList;
import java.util.List;





public class ExitWorker implements Runnable {
	
	private BuySell buysell;
	private int id;
	private List<Double> prices;
	
	public ExitWorker(BuySell buysell, int id){
		this.buysell = buysell;
		this.id = id;
		prices = new ArrayList<Double>();
	}
	

	@Override
	public void run() {
		MCDownloader downloader = new MCDownloader();
		SymbolEstimator estimator = new SymbolEstimator();
		boolean sold = false;
		intitialwait();
		while(true){
			String info = downloader.downloadData(buysell);
			String lasttime; double high; double low; double price;
			try{
				lasttime = info.split("/")[3];
				high = Double.parseDouble(info.split("/")[2]);
				low = Double.parseDouble(info.split("/")[1]);
				price = Double.parseDouble(info.split("/")[0]);
			}catch(Exception e){
				System.out.println("***************MoneyControl did not give valid result*************"+"Thread - " + buysell.getSymbol());
				continue;
			}
			prices.add(price);
			System.out.println("Thread - " + buysell.getSymbol() + " trying at " + lasttime);
			if(lasttime.compareTo("09:15") >= 0 && lasttime.compareTo("15:15") <= 0){
				sold = estimator.exitMidday(buysell,prices,low,high,lasttime);
			}
			
			if(sold){
				break;
			}else if(lasttime.compareTo("15:15") > 0){
				estimator.exitAtEnd(buysell,prices,low,high,lasttime);
				break;
			}else{
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
				System.out.println("Thread interrrupted in initial wait - "+buysell.getSymbol());
			}
		}
	}
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (300*1000);
		while(System.currentTimeMillis() < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-System.currentTimeMillis()+timestamp);
			} catch (InterruptedException e) {
				System.out.println("Thread interrrupted in interval wait - "+buysell.getSymbol());
			}
		}
	}

}
