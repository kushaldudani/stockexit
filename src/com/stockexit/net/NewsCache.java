package com.stockexit.net;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;



public class NewsCache implements Runnable {
	
	private List<ExitWorker> workers;
	static AtomicBoolean daysmax = new AtomicBoolean(false);
	
	public NewsCache(List<ExitWorker> workers) {
		this.workers = workers;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Calendar cal = Calendar.getInstance();
				String hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
				String minute = String.format("%02d", cal.get(Calendar.MINUTE));
				String lasttime = hour + ":" + minute;
				if (lasttime.compareTo("15:00") >= 0) {
					break;
				}
				intervalwait(2000);
				int size = workers.size();
				double daysprofit = 0;
				for (ExitWorker wr : workers) {
					daysprofit = daysprofit + wr.getEstimator().getCurPft();
				}
				if(size < 8 && daysprofit >= 8.5){
					daysmax.set(true);
				}
			} catch (Exception e) {
				LoggerUtil.getLogger().log(Level.SEVERE, "Exception in NewsCache", e);
			}
		}
	}
	
	
	
	private void intervalwait(int timetowait) {
		long timestamp = System.currentTimeMillis();
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
