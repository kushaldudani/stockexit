package com.stockexit.net;

import java.util.List;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;

public class SymbolEstimator {
	
	private int steak10count = 0;
	
	public boolean exitMidday(BuySell buysell,List<Double> prices, double low, double high, 
			String lasttime){
		
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		
		if(curprofit >= 2){
			return sellStock(curprice, curprofit, buysell);
		}
		int steak = getCurrentSteak(prices);
		if(steak >= 10){
			steak10count++;
		}
		if(steak10count >= 2){
			return sellStock(curprice, curprofit, buysell);
		}
		
		return false;
	}
	
	

	public boolean exitAtEnd(BuySell buysell,List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		
		if(curprofit >= 1 || curprofit <= 2.5){
			return sellStock(curprice, curprofit, buysell);
		}
		
		return false;
	}
	
	
	private boolean sellStock(double curprice, double curprofit, BuySell buysell) {
		try{
			buysell.setExited(true);
			buysell.setExitprice(curprice);
			buysell.setProfit(curprofit);
			DbManager db = new DbManager();
			db.openSession();
			db.insertOrUpdate(buysell);
			db.closeSession();
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator SellStock failed", e);
		}
		return false;
	}
	
	
	private int getCurrentSteak(List<Double> prices){
		int steak=0;
		for(int i=1;i<prices.size();i++){
			double curvalue = prices.get(i);
			double prevvalue = prices.get(i-1);
			if(curvalue > prevvalue){
				steak++;
			}else if(curvalue < prevvalue){
				steak--;
			}
		}
		
		return steak;
	}

}
