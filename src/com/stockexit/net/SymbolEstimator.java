package com.stockexit.net;

import java.util.List;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;

public class SymbolEstimator {
	
	private BuySell buysell;
	private String curdate;
	private double lossthreshold;
	private double stopthreshold;
	
	public SymbolEstimator(BuySell buysell, String curdate) {
		this.buysell = buysell;
		this.curdate = curdate;
		this.lossthreshold = getlossthreshold();
		this.stopthreshold = getstopthreshold();
	}
	
	public boolean exitAtMidday(List<Double> prices, double low, double high, 
			String lasttime){
		
		double enterprice = buysell.getEnterprice();
		int size = prices.size();
		
		if(size>=3){ 
			double price1 = prices.get(size-1);
			double price2 = prices.get(size-2);
			double price3 = prices.get(size-3);
			double profit1 = ((price1-enterprice)/(enterprice))*100;
			double profit2 = ((price2-enterprice)/(enterprice))*100;
			double profit3 = ((price3-enterprice)/(enterprice))*100;
			LoggerUtil.getLogger().info(buysell.getSymbol()+ "  "+ profit1+"  "+profit2+"  "+profit3);
			if((buysell.getDaystried()+1)>=2 && (profit1>-1&&profit1<0.5)){
				return sellStock(price1, profit1, "Middayday");
			}else if(profit1 < stopthreshold && 
					profit2 < stopthreshold && profit3 < stopthreshold){
				return sellStock(price1, profit1, "Middayday");
			}
		}
		
		return false;
	}
	
	

	public boolean exitAtEnd(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		int size = prices.size();
		
		if(curprofit >= 0){
			return sellStock(curprice, curprofit, "Endday");
		}else if(size>=3){ 
			double price1 = prices.get(size-1);
			double price2 = prices.get(size-2);
			double price3 = prices.get(size-3);
			double loss1 = ((price1-enterprice)/(enterprice))*100;
			double loss2 = ((price2-enterprice)/(enterprice))*100;
			double loss3 = ((price3-enterprice)/(enterprice))*100;
			LoggerUtil.getLogger().info(buysell.getSymbol()+ "  "+ loss1+"  "+loss2+"  "+loss3);
			if(loss1 < lossthreshold && 
					loss2 < lossthreshold && loss3 < lossthreshold){
				return sellStock(price1, loss1, "Endday");
			}
		}else if(curdate.equals(buysell.getExpiry())){
			return sellStock(curprice, curprofit, "Endday");
		}
		
		return false;
	}
	
	
	//need to add retry in both
	public void updateStock(){
		try{
			int daystried = buysell.getDaystried() + 1;
			buysell.setDaystried(daystried);
			DbManager db = new DbManager();
			db.openSession();
			db.insertOrUpdate(buysell);
			db.closeSession();
			LoggerUtil.getLogger().info("Updated - " + buysell.getSymbol() );
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator UpdateStock failed", e);
		}
	}
	private boolean sellStock(double curprice, double curprofit, String type) {
		try{
			buysell.setExited(true);
			buysell.setExitprice(curprice);
			buysell.setProfit(curprofit);
			int daystried = buysell.getDaystried() + 1;
			buysell.setDaystried(daystried);
			DbManager db = new DbManager();
			db.openSession();
			db.insertOrUpdate(buysell);
			db.closeSession();
			LoggerUtil.getLogger().info("Sold - "+type +" - " + buysell.getSymbol() );
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator SellStock failed", e);
		}
		return false;
	}
	
	
	private double getlossthreshold(){
		int daystring = buysell.getDaystried()+1;
		double lossthreshold;
		if(daystring == 1){
			lossthreshold = -4.5;
		}else if(daystring == 2 || daystring == 3){
			lossthreshold = -2.5;
		}else{
			lossthreshold = -1;
		}
		return lossthreshold;
	}
	
	private double getstopthreshold(){
		int daystring = buysell.getDaystried()+1;
		double stothreshold;
		if(daystring == 1){
			stothreshold = -11;
		}else if(daystring == 2 || daystring == 3){
			stothreshold = -7;
		}else{
			stothreshold = -7;
		}
		return stothreshold;
	}

}
