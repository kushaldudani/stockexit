package com.stockexit.net;

import java.util.List;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;

public class SymbolEstimator {
	
	private BuySell buysell;
	//private double middayprofitthreshold;
	//private double middaylossthreshold;
	//private double enddayprofitthreshold;
	//private double enddaylossthreshhold;
	//private int crossedmpt=-1;
	//private double finalthreshold;
	private double lossthreshold;
	
	public SymbolEstimator(BuySell buysell) {
		this.buysell = buysell;
		this.lossthreshold = getlossthreshold();
		//this.middaylossthreshold = getmiddaylossthreshold();
		//this.enddayprofitthreshold = getenddayprofitthreshold();
		//this.enddaylossthreshhold = getenddaylossthreshold();
		//this.finalthreshold = getfinalhreshold();
	}
	
	/*public boolean exitMidday(List<Double> prices, double low, double high, 
			String lasttime){
		
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit+"  "+
		crossedmpt + "  " + middayprofitthreshold);
		if(curprofit >= finalthreshold){
			return sellStock(curprice, curprofit);
		}
		
		if(curprofit >= middayprofitthreshold){
			middayprofitthreshold = curprofit;
			crossedmpt = 0;
		}else if((crossedmpt>=0)) {
			if((middayprofitthreshold-curprofit) >= (0.2*(crossedmpt+1)) ){
				crossedmpt++;
				if(crossedmpt>=2){
					return sellStock(curprice, curprofit);
				}
			}else{
				crossedmpt = 0;
			}
		}
		
		if(curprofit < middaylossthreshold){
			return sellStock(curprice, curprofit);
		}
		
		return false;
	}*/
	
	

	public boolean exitAtEnd(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = ((curprice-enterprice)/(enterprice))*100;
		LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		int size = prices.size();
		
		if(curprofit >= 0){
			return sellStock(curprice, curprofit);
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
				return sellStock(price1, loss1);
			}
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
	
	
	private double getlossthreshold(){
		int daystring = buysell.getDaystried()+1;
		double lossthreshold;
		if(daystring == 1){
			lossthreshold = -4;
		}else if(daystring == 2 || daystring == 3){
			lossthreshold = -2.5;
		}else{
			lossthreshold = -1;
		}
		return lossthreshold;
	}
	
	/*private double getfinalhreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = 2.2;
		if(daystring == 2){
			startvalue = (startvalue * 0.75);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.5);
		}else if(daystring > 3){
			startvalue = (startvalue * 0.35);
		}
		return startvalue;
	}
	
	private double getmiddayprofitthreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = 1.7;
		if(daystring == 2){
			startvalue = (startvalue * 0.75);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.5);
		}else if(daystring > 3){
			startvalue = (startvalue * 0.35);
		}
		return startvalue;
	}
	
	private double getmiddaylossthreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = -3.0;
		if(daystring == 2){
			startvalue = (startvalue * 0.9);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.7);
		}else if(daystring > 3){
			startvalue = (startvalue * 0.5);
		}
		return startvalue;
	}

	private double getenddayprofitthreshold(){
		int daystring = buysell.getDaystried()+1;
		double startvalue = 0.8;
		if(daystring == 2){
			startvalue = (startvalue * 0.75);
		}else if(daystring == 3){
			startvalue = (startvalue * 0.4);
		}else if(daystring > 3){
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
		}else if(daystring > 3){
			startvalue = (startvalue * 0.25);
		}
		return startvalue;
	}*/

}
