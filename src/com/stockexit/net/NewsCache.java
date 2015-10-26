package com.stockexit.net;

import java.util.Calendar;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;



public class NewsCache implements Runnable {
	
	private Set<String> stocks;
	private String date;
	private String daybefore;
	private static Map<String, Integer> newsMap = new HashMap<String, Integer>();
	
	public NewsCache(Set<String> stocks, String date, String daybefore) {
		this.stocks = stocks;
		this.date = date;
		this.daybefore = daybefore;
	}
	
	private static synchronized void setNewsImportance(String stock, int importance) {
		newsMap.put(stock, importance);
	}
	
	public static synchronized int getNewsImportance(String stock) {
		if(newsMap.containsKey(stock)){
			return newsMap.get(stock);
		}
		return 0;
	}

	@Override
	public void run() {
		while (true) {
			NewsLinesReader db=null;
			try {
				Calendar cal = Calendar.getInstance();
				String hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
				String minute = String.format("%02d", cal.get(Calendar.MINUTE));
				String lasttime = hour + ":" + minute;
				if (lasttime.compareTo("14:25") >= 0) {
					break;
				}
				intervalwait();
				db = new NewsLinesReader();
				db.openSession();
				for (String stock : stocks) {
					List<Newsline> newslist = db.getNewsLines(stock, date);
					findNewsImportance(newslist, stock);
					List<Newsline> newslist2 = db.getNewsLines(stock, daybefore);
					findNewsImportance(newslist2, stock);
				}
			} catch (Exception e) {
				LoggerUtil.getLogger().log(Level.SEVERE, "Exception in NewsCache", e);
			}finally {
				if(db!=null){db.closeSession();}
			}
		}
	}
	
	private void findNewsImportance(List<Newsline> newslist, String stock) {
		for (Newsline entry : newslist) {
			if (entry.getSubject().equals("Financial Result Updates")) {
				setNewsImportance(stock, 1);
			}
			if (entry.getSubject().equals("Press Release")) {
				setNewsImportance(stock, 1);
			}
			if (entry.getSubject().contains("result")) {
				setNewsImportance(stock, 1);
			}
		}
	}
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (120000);
		long curtimestamp;
		while((curtimestamp = System.currentTimeMillis()) < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-curtimestamp+timestamp);
			} catch (InterruptedException e) {
				LoggerUtil.getLogger().info("Thread interrrupted in interval wait - ");
			}
		}
	}

}
