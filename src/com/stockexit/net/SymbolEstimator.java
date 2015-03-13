package com.stockexit.net;

import java.util.List;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;

public class SymbolEstimator {
	
	private BuySell buysell;
	private double middayprofitthreshold;
	private double middaylossthreshold;
	private double enddayprofitthreshold;
	private double enddaylossthreshhold;
	private boolean crossedmpt=false;
	
	public SymbolEstimator(BuySell buysell) {
		this.buysell = buysell;
		this.middayprofitthreshold = getmiddayprofitthreshold();
		this.middaylossthreshold = getmiddaylossthreshold();
		this.enddayprofitthreshold = getenddayprofitthreshold();
		this.enddaylossthreshhold = getenddaylossthreshold();
	}
	
	public boolean exitMidday(List<Double> prices, double low, double high, 
			String lasttime){
		
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		LoggerUtil.getLogger().info("Thread - " + buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		if(curprofit > middayprofitthreshold){
			middayprofitthreshold = curprofit;
			crossedmpt = true;
		}else if(crossedmpt && ((middayprofitthreshold-curprofit) >= 0.2)){
			return sellStock(curprice, curprofit);
		}
		
		if(curprofit < middaylossthreshold){
			return sellStock(curprice, curprofit);
		}
		
		return false;
	}
	
	

	public void exitAtEnd(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		LoggerUtil.getLogger().info("Thread - " + buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		if(curprofit >= enddayprofitthreshold || curprofit <= enddaylossthreshhold){
			sellStock(curprice, curprofit);
		}else{
			updateStock();
		}
	}
	//need to add retry in both
	private boolean updateStock(){
		try{
			int daystried = buysell.getDaystried() + 1;
			buysell.setDaystried(daystried);
			DbManager db = new DbManager();
			db.openSession();
			db.insertOrUpdate(buysell);
			db.closeSession();
			LoggerUtil.getLogger().info("Updated - " + buysell.getSymbol() );
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator UpdateStock failed", e);
		}
		return false;
	}
	private boolean sellStock(double curprice, double curprofit) {
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
			LoggerUtil.getLogger().info("Sold - " + buysell.getSymbol() );
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator SellStock failed", e);
		}
		return false;
	}
	
	
	
	private double getmiddayprofitthreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = 1.8;
		if(daystring == 2){
			startvalue = (startvalue * 0.75);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.5);
		}else{
			startvalue = (startvalue * 0.35);
		}
		return startvalue;
	}
	
	private double getmiddaylossthreshold(){
		return -3.0;
	}

	private double getenddayprofitthreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = 0.8;
		if(daystring == 2){
			startvalue = (startvalue * 0.75);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.4);
		}else{
			startvalue = 0;
		}
		return startvalue;
	}
	
	private double getenddaylossthreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = -2.4;
		if(daystring == 2){
			startvalue = (startvalue * 0.9);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.4);
		}else{
			startvalue = (startvalue * 0.25);
		}
		return startvalue;
	}

}
