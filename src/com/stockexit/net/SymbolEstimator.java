package com.stockexit.net;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.stockdata.bpwealth.OrderDispatcher;
import com.stockdata.bpwealth.TradeConfirmation;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.SendMail;
import com.stockexit.util.StockExitUtil;

public class SymbolEstimator {
	
	private BuySell buysell;
	private String curdate;
	private double lossthreshold;
	private Map<String, Integer> tokensmap;
	private Map<Integer, Integer> marketlotmap;
	//private double stopthreshold;
	
	public SymbolEstimator(BuySell buysell, String curdate) {
		this.buysell = buysell;
		this.curdate = curdate;
		this.lossthreshold = getlossthreshold();
		this.tokensmap = StockExitUtil.buildTokensMap();
		this.marketlotmap = StockExitUtil.buildMarketLotMap();
		//this.stopthreshold = getstopthreshold();
	}
	
	private double getPft(double enterprice, double curprice, String type){
		double profit;
		if(type.equals("Long")){
			profit = ((curprice-enterprice)/(enterprice))*100;
			return profit;
		}else{
			profit = ((enterprice-curprice)/(enterprice))*100;
			return profit;
		}
	}
	
	public boolean exitAtMidday(List<Double> prices, double low, double high, 
			String lasttime, double trend){
		
		double enterprice = buysell.getEnterprice();
		int size = prices.size();
		if(trend>0){
			double trendpft = getPft(enterprice, trend, buysell.getType());
			double targetpft = getTargetProfit(trendpft);
			double price1 = prices.get(size-1);
			double price2 = prices.get(size-2);
			double price3 = prices.get(size-3);
			double profit1 = getPft(enterprice,price1,buysell.getType());
			double profit2 = getPft(enterprice,price2,buysell.getType());
			double profit3 = getPft(enterprice,price3,buysell.getType());
			LoggerUtil.getLogger().info(buysell.getSymbol()+ "  "+ profit1+"  trendprofit-"+trendpft);
			if(profit1 >= targetpft || profit1 >= 1.8){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}else if(trendpft > 1 && profit1 < 0.8
					&& profit2 < 0.8 && profit3 < 0.8){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}else if(trendpft > 0.25 && profit1 < -0.8
					&& profit2 < -0.8 && profit3 < -0.8){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}
		}
		
		return false;
		//buysell.getDaystried()+1)>=2 && (profit1>-1&&profit1<0.5)
	}
	
	private double getTargetProfit(double trendpft) {
		if(trendpft < -2){
			return -0.8;
		}else if(trendpft < -1){
			return -0.2;
		}else if(trendpft < 0){
			return 0.2;
		}else if(trendpft > 1){
			return (trendpft+0.2);
		}else if(trendpft > 0.5){
			return (trendpft+0.25);
		}else {
			return (trendpft+0.4);
		}
	}

	public double getAvgTrend(List<Double> prices){
		int sz= prices.size();
		if(sz<5){
			return 0;
		}
		int i = sz-1;
		double avg = 0; int cnt = 0;
		while(i>sz-50){
			if(i>=0){
				avg = avg + prices.get(i);
				cnt++;
			}
			i--;
		}
		
		double trend = (avg)/((double)cnt);
		return trend;
	}
	

	public boolean exitAtEnd(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = getPft(enterprice,curprice,buysell.getType());
		LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		int size = prices.size();
		
		if(curprofit >= 0){
			return sellStock(curprice, curprofit, "Endday",lasttime);
		}else if(size>=3){ 
			double price1 = prices.get(size-1);
			double price2 = prices.get(size-2);
			double price3 = prices.get(size-3);
			double loss1 = getPft(enterprice,price1,buysell.getType());
			double loss2 = getPft(enterprice,price2,buysell.getType());
			double loss3 = getPft(enterprice,price3,buysell.getType());
			LoggerUtil.getLogger().info(buysell.getSymbol()+ "  "+ loss1+"  "+loss2+"  "+loss3);
			if(loss1 < lossthreshold && 
					loss2 < lossthreshold && loss3 < lossthreshold){
				return sellStock(price1, loss1, "Endday",lasttime);
			}
		}
		
		if(curdate.equals(buysell.getExpiry())){
			return sellStock(curprice, curprofit, "Endday",lasttime);
		}
		
		return false;
	}
	
	public boolean exitAtStart(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = getPft(enterprice,curprice,buysell.getType());
		LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		
		if(curprofit >= 1.1){
			return sellStock(curprice, curprofit, "Startday",lasttime);
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
	private boolean sellStock(double curprice, double curprofit, String ismidday, String lasttime) {
		try{
			String ssymb = buysell.getSymbol().split("-")[0];
			OrderDispatcher od = new OrderDispatcher(ssymb);
			od.connect();
			TradeConfirmation trade = null;
			if(buysell.getType().equals("Long")){
				double limitprice = curprice*(0.995);
				int limitprice100 = (int) (limitprice*100);
				limitprice100 = roundup(limitprice100);
				trade = od.sendOrder((short)0, (short)1, 
					Integer.toString(tokensmap.get(ssymb)), ssymb, limitprice100, 
					marketlotmap.get(tokensmap.get(ssymb)), buysell.getExpiry());
			}else{
				double limitprice = curprice*(1.005);
				int limitprice100 = (int) (limitprice*100);
				limitprice100 = roundup(limitprice100);
				trade = od.sendOrder((short)0, (short)0, 
					Integer.toString(tokensmap.get(ssymb)), ssymb, limitprice100, 
					marketlotmap.get(tokensmap.get(ssymb)), buysell.getExpiry());
			}
			if(trade != null){
				buysell.setExited(true);
				double tradedprice = ((double)(trade.TrdPrice)/(double)100);
				buysell.setExitprice(tradedprice);
				buysell.setProfit(curprofit);
				int daystried = buysell.getDaystried() + 1;
				buysell.setDaystried(daystried);
				String letter = buysell.getType().substring(0, 1);
				buysell.setType(letter+lasttime);
				DbManager db = new DbManager();
				db.openSession();
				db.insertOrUpdate(buysell);
				db.closeSession();
				LoggerUtil.getLogger().info("Sold - "+ismidday +" - " + buysell.getSymbol() );
				SendMail.generateAndSendEmail("Successfully squared off - "+ buysell.getSymbol() + 
						" at price - " + tradedprice+" please verify");
			}else if(od.getOrderConfirmation() != null){
				LoggerUtil.getLogger().info("NotSold but order dispatched- "+ismidday +" - " + buysell.getSymbol() );
				SendMail.generateAndSendEmail("Tried squaring off - "+ buysell.getSymbol() + 
						"but did not get trade confirmation. please square off from terminal");
			}else{
				LoggerUtil.getLogger().info("NotSold connection problem- "+ismidday +" - " + buysell.getSymbol() );
				SendMail.generateAndSendEmail("Not able to square off - "+ buysell.getSymbol() + 
						"connection problem. please square off from terminal");
			}
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator SellStock failed "+buysell.getSymbol(), e);
		}
		LoggerUtil.getLogger().info("NotSold connection problem- "+ismidday +" - " + buysell.getSymbol() );
		SendMail.generateAndSendEmail("Not able to square off - "+ buysell.getSymbol() + 
				"connection problem. please square off from terminal");
		return true;
	}
	
	private int roundup(int limitprice) {
		int rnd = limitprice - (limitprice%10);
		return rnd;
	}
	
	private double getlossthreshold(){
		int daystring = buysell.getDaystried()+1;
		double lossthreshold;
		if(daystring == 1){
			lossthreshold = -7.5;
		}else if(daystring == 2 || daystring == 3){
			lossthreshold = -7.5;
		}else{
			lossthreshold = -7.5;
		}
		return lossthreshold;
	}
	
	/*private double getstopthreshold(){
		int daystring = buysell.getDaystried()+1;
		double stothreshold;
		if(daystring == 1){
			stothreshold = -11;
		}else if(daystring == 2 || daystring == 3){
			stothreshold = -11;
		}else{
			stothreshold = -11;
		}
		return stothreshold;
	}*/

}
