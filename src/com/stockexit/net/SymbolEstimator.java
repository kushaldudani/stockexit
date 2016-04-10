package com.stockexit.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import com.stockdata.bpwealth.OrderDispatcher;
import com.stockdata.bpwealth.TradeConfirmation;
import com.stockdata.bpwealth.broadcast.TickData;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.SendMail;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;

public class SymbolEstimator {
	
	private BuySell buysell;
	private SecondModel smodel;
	private String curdate;
	private ReentrantLock lock;
	private SynQueue<TickData> qu;
	private Map<String, Integer> tokensmap;
	private Map<Integer, Integer> marketlotmap;
	
	//private double stopthreshold;
	
	public SymbolEstimator(BuySell buysell, SecondModel smodel, String curdate, ReentrantLock lock,
			SynQueue<TickData> qu) {
		this.buysell = buysell;
		this.smodel = smodel;
		this.curdate = curdate;
		this.lock = lock;
		this.qu = qu;
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
	private void updateStock(double tradedprice, String lasttime, String ismidday, int qyy){
		try{
			if(buysell!=null){
				buysell.setExited(true);
				buysell.setExitprice(tradedprice);
				buysell.setProfit(getPft(buysell.getEnterprice(), tradedprice, buysell.getType()));
				//int daystried = buysell.getDaystried() + 1;
				//buysell.setDaystried(daystried);
				String letter = buysell.getType().substring(0, 1);
				buysell.setType(letter+lasttime);
				DbManager db = new DbManager();
				db.openSession();
				db.insertOrUpdate(buysell);
				db.closeSession();
				LoggerUtil.getLogger().info("Sold - "+ismidday +" - " + buysell.getSymbol() );
				//removeLongShort(buysell.getSymbol().split("-")[0], buysell.getType());
			}else{
				String sss = smodel.getSymbol().split("-")[0];
				if(sss.equals("NIFTY")&&qyy < getEntryBudget()){
					double trueexitprice = ((smodel.getExitprice()*smodel.getDaystried()) + tradedprice*qyy)/(smodel.getDaystried()+qyy);
					smodel.setExitprice(trueexitprice);
					int newdaystried = smodel.getDaystried() + qyy;
					smodel.setDaystried(newdaystried);
					int left = (getEntryBudget() - qyy);
					smodel.setHasbudget(left);
				}else {
					smodel.setExited(true);
					double trueexitprice = ((smodel.getExitprice()*smodel.getDaystried()) + tradedprice*qyy)/(smodel.getDaystried()+qyy);
					smodel.setExitprice(trueexitprice);
					smodel.setProfit(getPft(smodel.getEnterprice(), trueexitprice, smodel.getType()));
					int newdaystried = smodel.getDaystried() + qyy;
					smodel.setHasbudget(newdaystried);
					smodel.setDaystried(0);
					String letter = smodel.getType().substring(0, 1);
					smodel.setType(letter+lasttime);
				}
				
				DbManager db = new DbManager();
				db.openSession();
				db.insertOrUpdate(smodel);
				db.closeSession();
				LoggerUtil.getLogger().info("Sold - "+ismidday +" - " + smodel.getSymbol() );
				removeLongShort(sss, smodel.getType());
				if(sss.equals("NIFTY")&&!smodel.isExited()){
					new Thread(new ExitWorker(null,smodel,qu,curdate,lock)).start();
				}
			}
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator UpdateStock failed", e);
		}
	}
	private void removeLongShort(String sss, String type){
		if(type.substring(0, 1).equals("L") && !sss.equals("NIFTY")){
			Model2Exit.removeLongEntry(sss);
		}else if(type.substring(0, 1).equals("S") && !sss.equals("NIFTY")){
			Model2Exit.removeShortEntry(sss);
		}
	}
	private double getEntryEnterprice(){
		if(buysell != null){
			return buysell.getEnterprice();
		}else {
			return smodel.getEnterprice();
		}
	}
	private double getEntryExitprice(){
		if(buysell != null){
			return buysell.getExitprice();
		}else {
			return smodel.getExitprice();
		}
	}
	private String getEntrySymbol(){
		if(buysell != null){
			return buysell.getSymbol();
		}else {
			return smodel.getSymbol();
		}
	}
	private String getEntryType(){
		if(buysell != null){
			return buysell.getType();
		}else {
			return smodel.getType();
		}
	}
	private String getEntryExpiry(){
		if(buysell != null){
			return buysell.getExpiry();
		}else {
			return smodel.getExpiry();
		}
	}
	private int getEntryMcase(){
		if(buysell != null){
			return buysell.getMcase();
		}else {
			return smodel.getMcase();
		}
	}
	private int getEntryDaystried(){
		if(buysell != null){
			return buysell.getDaystried();
		}else {
			return smodel.getDaystried();
		}
	}
	private int getEntryBudget(){
		if(buysell != null){
			return buysell.getbudget();
		}else {
			return smodel.getHasbudget();
		}
	}
	private double getEntryNextopenprice(){
		if(buysell != null){
			return buysell.getNextopenprice();
		}else {
			throw new RuntimeException("Operation not supported");
		}
	}
	
	private String getEntryStoplossid(){
		if(buysell != null){
			return buysell.getStoplossid();
		}else {
			throw new RuntimeException("Operation not supported");
		}
	}
	
	/*public boolean exitAtMidday(List<Double> prices, double low, double high, 
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
			if(profit1 >= targetpft || profit1 >= 1.4){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}else if(trendpft > 1 && profit1 < 0.8 && profit2 < 0.8 && profit3 < 0.8){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}else if(trendpft > 0.5 && profit1 < 0.4 && profit2 < 0.4 && profit2 < 0.4){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}else if(buysell.getDaystried() >= 2 && profit1 >= 0.2){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}else if(sellIfSlipFromTrendPft(trendpft, targetpft, profit1)){
				return sellStock(price1, profit1, "Middayday",lasttime);
			}
		}
		
		return false;
		//buysell.getDaystried()+1)>=2 && (profit1>-1&&profit1<0.5)
	}*/
	
	/*private int lingeringstslipcntr = 0;
	
	private boolean sellIfSlipFromTrendPft(double trendpft, double targetpft, double curprofit){
		// break it into 2 conditions, negative only for lingering stocks
		if(buysell.getDaystried() >= 2){
			if((trendpft > -0.3) && (curprofit-trendpft) < -0.25){
				lingeringstslipcntr++;
			}
			if(lingeringstslipcntr >= 9){
				return true;
			}
		}
		return false;
	}*/
	// 2 things has to be done here
	// these logic is good, only thing is the numbers need to vary depending on past stock volatility
	// relax the 0.4 number
	/*private double getTargetProfit(double trendpft) {
		if(trendpft < -1.4){
			return -0.35;
		}else if(trendpft < -0.6){
			return -0.18;
		}else if(trendpft < 0){
			if(buysell.getDaystried() >= 2){
				return 0.05;
			}else{
				return 0.2;
			}
		}else if(trendpft > 0.9){
			return (trendpft+0.2);
		}else if(trendpft > 0.3){
			return (trendpft+0.25);
		}else {
			if(buysell.getDaystried() >= 2){
				return (trendpft+0.1);
			}else{
				return (trendpft+0.3);
			}
		}
	}*/

	/*public double getAvgTrend(List<Double> prices){
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
	}*/
	
	/*private long exitAtEndTimer = 0;
	
    // add different profit target depending on the days tried
	public boolean exitAtEnd(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = getPft(enterprice,curprice,buysell.getType());
		
		int size = prices.size();
		
		if((curprofit >= getExitAtEndProfit()) || (exitAtEndTimer > 0)){
			LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
			if(exitAtEndTimer == 0){
				exitAtEndTimer = System.currentTimeMillis();
			}
			if( ((System.currentTimeMillis()-exitAtEndTimer) > 75000) ){
				return sellStock(curprice, curprofit, "Endday",lasttime);
			}
		}else if(size>=3){ 
			double price1 = prices.get(size-1);
			double price2 = prices.get(size-2);
			double price3 = prices.get(size-3);
			double loss1 = getPft(enterprice,price1,buysell.getType());
			double loss2 = getPft(enterprice,price2,buysell.getType());
			double loss3 = getPft(enterprice,price3,buysell.getType());
			int qty = buysell.getbudget(); 
			loss1 = loss1*qty; loss2 = loss2*qty; loss3 = loss3*qty;
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
	
	private double getExitAtEndProfit(){
		int daystring = buysell.getDaystried()+1;
		double exitAtEndProfit;
		if(daystring == 1){
			exitAtEndProfit = 0.05;
		}else if(daystring == 2 ){
			exitAtEndProfit = -0.15;
		}else{
			exitAtEndProfit = -0.3;
		}
		return exitAtEndProfit;
	}*/
	
	private double dummyLocalMax = 0;
	private double dummyLocalMin = 0;
	//private double recordedProfitAt2 = -200;
	/*private long targetTimer = 0;
	private int totalticks = 0;
	
	// return time in milliseconds
	private long getTimerForLocalMax(double dummyLocalMax) {
		if(dummyLocalMax >= 1){
			return 4;
		}else if(dummyLocalMax >= 0.58){
			return 5;
		}else if(dummyLocalMax >= 0.4){
			return 60000;
		}else if(dummyLocalMax >= 0.22){
			return 250000;
		}else if(dummyLocalMax > 0){
			return 3500000;
		}
		
		return 0;
	}*/
	
	public boolean dummyExit(List<Double> prices, double low, double high, 
			String lasttime){
		int size = prices.size();
		String sss = getEntrySymbol().split("-")[0];
		double curprice = prices.get(size-1);
		double enterprice = getEntryEnterprice();
		double curprofit = getPft(enterprice,curprice,getEntryType());
		LoggerUtil.getLogger().info(sss + "  " + lasttime+"  " +curprofit);
		if(curprofit > dummyLocalMax){
			dummyLocalMax = curprofit;
			/*long targetval = getTimerForLocalMax(dummyLocalMax);
			if(targetval < 100){
				targetTimer = targetval + totalticks;
			}else{
				targetTimer = targetval + System.currentTimeMillis();
			}*/
		}
		if(curprofit < dummyLocalMin) {
			dummyLocalMin = curprofit;
		}
		/*if(lasttime.compareTo("12:30") >= 0 && recordedProfitAt2 == -200){
			recordedProfitAt2 = curprofit;
		}
		
		double dipfromprofitat2 = curprofit - recordedProfitAt2;*/
		double thprofit = Math.max(1.55, (0.8*dummyLocalMax));
		if(lasttime.compareTo("12:30") >= 0 && thprofit >= 1.55){
			return sellStock(curprice, curprofit, "Endday1",lasttime, getEntryBudget());
		}
		if(lasttime.compareTo("14:30") >= 0){
			return sellStock(curprice, curprofit, "Endday2",lasttime, getEntryBudget());
		}
		
		/*if((targetTimer>0) && (targetTimer>100) && ((System.currentTimeMillis()-targetTimer)>0)){
			return sellStock(curprice, curprofit, "Endday",lasttime);
		}else if((targetTimer>0) && (targetTimer<100) && ((totalticks-targetTimer)>=0)){
			return sellStock(curprice, curprofit, "Endday",lasttime);
		}*/
		if(lasttime.compareTo("09:45") >= 0 && dummyLocalMax >= 2.5 && curprofit <= 1.3){
			return sellStock(curprice, curprofit, "PullBack",lasttime, getEntryBudget());
		}/*else if(lasttime.compareTo("09:30") >= 0 && !sss.equals("NIFTY") && curprofit >= getNiftyBasedProfitThreshold() 
				&& (getNiftyUpPercent() <= -0.7||getNiftyDownFromHighPercent() >=0.7) && getEntryType().equals("Long")){
			return sellStock(curprice, curprofit, "LongNiftyBased",lasttime, getEntryBudget());
		}else if(lasttime.compareTo("09:30") >= 0 && !sss.equals("NIFTY") && curprofit >= getNiftyBasedProfitThreshold()
				&& (getNiftyUpPercent() >= 0.7||getNiftyUpFromLowPercent() >=0.7) && getEntryType().equals("Short")){
			return sellStock(curprice, curprofit, "ShortNiftyBased",lasttime, getEntryBudget());
		}*/
		//else if(lasttime.compareTo("09:45") >= 0 && sss.equals("NIFTY") && curprofit < -0.75){
		//	return sellStock(curprice, curprofit, "Endday",lasttime, getEntryBudget());
		//}
		/*else if(sss.equals("NIFTY")){
			int niftyhedgeqty = getEntryBudget()/getQuantityToBeFired(sss, curprice);
			int logshortdiff = Model2Exit.getLongShortDiff();
			if(logshortdiff < niftyhedgeqty && curprofit > -0.2){
				int qyy = (niftyhedgeqty - logshortdiff);
				qyy = (qyy * getQuantityToBeFired(sss, curprice));
				return sellStock(curprice, curprofit, "Nifty",lasttime, qyy);
			}
		}else if(size>=3){ // for intraday huge movement
			double price1 = prices.get(size-1);
			//double price2 = prices.get(size-2);
			//double price3 = prices.get(size-3);
			double loss1 = getPft(enterprice,price1,getEntryType());
			//double loss2 = getPft(enterprice,price2,getEntryType());
			//double loss3 = getPft(enterprice,price3,getEntryType());
			if(loss1 < lossthreshold) {
				return sellStock(price1, loss1, "Stoploss",lasttime, getEntryBudget());
			}
		}*/
		
		return false;
	}
	
	/*private double getNiftyBasedProfitThreshold(){
		if(buysell != null){
			return 1.55;
		}else{
			return 0.95;
		}
	}*/
	
	//private static final int NIFTYPRINCIPAL = 400000;
	
	/*private int getQuantityToBeFired(String symbol, double price) {
		int marketlot = marketlotmap.get(tokensmap.get(symbol));
		double value = (price*marketlot);
		double principalToBeUsed = NIFTYPRINCIPAL;
		double factor = principalToBeUsed/value;
		int lowerint = (int) Math.floor(factor);
		double decimaldiff = factor - lowerint;
		if(decimaldiff > 0.6){
			lowerint = lowerint + 1;
		}
		if(lowerint == 0){lowerint = 1;}
		return lowerint;
	}*/
	
	/*private double getNiftyUpFromLowPercent(){
		return TickListener.getNiftyUpFromLow();
	}
	
	private double getNiftyDownFromHighPercent(){
		return TickListener.getNiftyDownFromHigh();
	}
	
	private double getNiftyUpPercent(){
		if(buysell!=null){
			return TickListener.getNiftyUppercent();
		}else{
			return TickListener.getNiftyUpFromClosepercent();
		}
	}*/
	
	/*private double getSlippage(double entryNextopenprice, double entryEnterprice, String type) {
		if(type.equals("Long")){
			double slippage = ((entryEnterprice-entryNextopenprice)/(entryNextopenprice))*100;
			return slippage;
		}else{
			double slippage = ((entryNextopenprice-entryEnterprice)/(entryNextopenprice))*100;
			return slippage;
		}
	}*/
	
	
	/*private double exitAtStartMax = 0;
	private long exitAtStartTimer = 0;
	
	public boolean exitAtStart(List<Double> prices, double low, double high, 
			String lasttime){
		double curprice = prices.get(prices.size()-1);
		double enterprice = buysell.getEnterprice();
		double curprofit = getPft(enterprice,curprice,buysell.getType());
		LoggerUtil.getLogger().info(buysell.getSymbol() + "  " + lasttime+"  " +curprofit);
		if(curprofit > exitAtStartMax){
			exitAtStartMax = curprofit;
		}
		
		if(curprofit >= 1){
			return sellStock(curprice, curprofit, "Startday",lasttime);
		}else if((exitAtStartMax >= getExitAtStartProfit()) && (prices.size() >=2)){
			if(exitAtStartTimer == 0){
				exitAtStartTimer = System.currentTimeMillis();
			}
			double price2 = prices.get(prices.size()-2);
			double profit2 = getPft(enterprice, price2, buysell.getType());
			double exitAtStartPftLossBreaker = getExitAtStartProfit() - 0.35;
			if( ((System.currentTimeMillis()-exitAtStartTimer) > 100000) ||
					(curprofit < exitAtStartPftLossBreaker && profit2 < exitAtStartPftLossBreaker) ){
				return sellStock(curprice, curprofit, "Startday",lasttime);
			}
		}
		return false;
	}
	
	private double getExitAtStartProfit(){
		int daystring = buysell.getDaystried()+1;
		double exitAtStartProfit;
		if(daystring == 1){
			exitAtStartProfit = 0.48;
		}else if(daystring == 2 ){
			exitAtStartProfit = 0.25;
		}else{
			exitAtStartProfit = 0.05;
		}
		return exitAtStartProfit;
	}*/
	
	private double calculateStoplossprice(){
		if(getEntryType().equals("Long")){
			return (getEntryEnterprice() - (getEntryEnterprice()*0.019));
		}else{
			return (getEntryEnterprice() + (getEntryEnterprice()*0.019));
		}
	}

	private boolean sellStock(double curprice, double curprofit, String ismidday, String lasttime, int uqyy) {
		lock.lock();
		try{
			String ssymb = getEntrySymbol().split("-")[0];
			short underlyingtype = 1;
			if(ssymb.equals("NIFTY")){underlyingtype = 0;}
			String stoplossid = getEntryStoplossid();
			String[] stoplossids = stoplossid.split(";");
			OrderDispatcher od = new OrderDispatcher();
			od.connect();
			for(int i=0;i<uqyy;i++){
				if(getEntryType().equals("Long")){
					String slid = stoplossids[i+1];
					if(StockExitUtil.isReal){
						od.cancelOrder((short)2, (short)1, 
							Integer.toString(tokensmap.get(ssymb)), ssymb, 
							marketlotmap.get(tokensmap.get(ssymb)), getEntryExpiry(), 1, 
							underlyingtype,Long.parseLong(slid.split(",")[0]), Long.parseLong(slid.split(",")[1]));
					}
				}else{
					String slid = stoplossids[i+1];
					if(StockExitUtil.isReal){
						od.cancelOrder((short)2, (short)0, 
							Integer.toString(tokensmap.get(ssymb)), ssymb, 
							marketlotmap.get(tokensmap.get(ssymb)), getEntryExpiry(), 1, 
							underlyingtype,Long.parseLong(slid.split(",")[0]), Long.parseLong(slid.split(",")[1]));
					}
				}
			}
			long loopstarttime = System.currentTimeMillis();
	    	while(((System.currentTimeMillis()-loopstarttime)<4000)){
	    		intervalwait();
	    	}
	    	int mqyy;
	    	if(StockExitUtil.isReal){
	    		mqyy = od.getExchangeConfirmationCnt(ssymb, uqyy);
	    		if(mqyy == -1){
	    			LoggerUtil.getLogger().info("NotSold connection problem- "+ismidday +" - " + getEntrySymbol() );
	    			SendMail.generateAndSendEmail("Not able to square off - "+ getEntrySymbol() + " qty - "+uqyy+
						" connection problem. please square off from terminal");
	    			return true;
	    		}
	    	}else{
	    		mqyy = (dummyLocalMin <= -1.9)?0:uqyy;
	    	}
	    	od = new OrderDispatcher();
			od.connect();
			for(int i=0;i<mqyy;i++){
				if(getEntryType().equals("Long")){
					double limitprice = curprice*(0.995);
					int limitprice100 = (int) (limitprice*100);
					limitprice100 = roundup(limitprice100);
					if(StockExitUtil.isReal){
						od.sendOrder((short)0, (short)1, 
							Integer.toString(tokensmap.get(ssymb)), ssymb, limitprice100, 
							marketlotmap.get(tokensmap.get(ssymb)), getEntryExpiry(), 1,
							underlyingtype);
					}
				}else{
					double limitprice = curprice*(1.005);
					int limitprice100 = (int) (limitprice*100);
					limitprice100 = roundup(limitprice100);
					if(StockExitUtil.isReal){
						od.sendOrder((short)0, (short)0, 
							Integer.toString(tokensmap.get(ssymb)), ssymb, limitprice100, 
							marketlotmap.get(tokensmap.get(ssymb)), getEntryExpiry(), 1,
							underlyingtype);
					}
				}
				intervalwait();
			}
			List<TradeConfirmation> trade = pollTrade(od, ssymb, curprice, mqyy);
			if(trade != null && trade.size() == mqyy){
				double sum=0;
				for(TradeConfirmation tc : trade){
					double tradedprice = ((double)(tc.TrdPrice)/(double)100);
					sum = sum + tradedprice;
				}
				int slhitcnt = uqyy-mqyy;
				sum = sum + (slhitcnt*calculateStoplossprice());
				double avgtradedprice = (sum/(double)uqyy);
				updateStock(avgtradedprice, lasttime, ismidday, uqyy);
				SendMail.generateAndSendEmail("Successfully squared off - "+ getEntrySymbol() + " qty - "+uqyy+"  "+getEntryType() + 
						" at price - " + getEntryExitprice()+" please verify, enterprice - "+getEntryEnterprice()+" AND out of which qty - "+slhitcnt+" hit stoploss and should have been squared off by CTCL");
			}else{
				LoggerUtil.getLogger().info("NotSold but order dispatched- "+ismidday +" - " + getEntrySymbol() );
				int slhitcnt = uqyy-mqyy;
				SendMail.generateAndSendEmail("Tried squaring off - "+ getEntrySymbol() + " qty - "+uqyy+
						" but did not get trade confirmation. please verify from terminal that there are no remaining positions ALSO please note that out of which qty - "+slhitcnt+" hit stoploss and should have been squared off by CTCL");
			}
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator SellStock failed "+getEntrySymbol(), e);
		}finally { lock.unlock();}
		LoggerUtil.getLogger().info("NotSold connection problem- "+ismidday +" - " + getEntrySymbol() );
		SendMail.generateAndSendEmail("Not able to square off - "+ getEntrySymbol() + " qty - "+uqyy+
				" connection problem. please square off from terminal");
		return true;
	}
	
	private List<TradeConfirmation> pollTrade(OrderDispatcher od, String symbol, double price, int mqyy) {
		if(StockExitUtil.isReal){
			long loopstarttime = System.currentTimeMillis();
			while(((System.currentTimeMillis()-loopstarttime)<3000)){
				intervalwait();
			}
			return od.getTradeConfirmation(symbol);
		}else{
			List<TradeConfirmation> trades = new ArrayList<>();
			for(int i=0;i<mqyy;i++){
				trades.add(new TradeConfirmation((int)(price*100)));
			}
			return trades;
		}
	}

	private int roundup(int limitprice) {
		int rnd = limitprice - (limitprice%10);
		return rnd;
	}
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (500);
		long curtimestamp;
		while((curtimestamp = System.currentTimeMillis()) < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-curtimestamp+timestamp);
			} catch (InterruptedException e) {}
		}
	}

	public void logg(List<Double> prices, double low, double high,
			String lasttime) {
		double curprice = prices.get(prices.size()-1);
		double enterprice = getEntryEnterprice();
		double curprofit = getPft(enterprice,curprice,getEntryType());
		LoggerUtil.getLogger().info(getEntrySymbol() + "  " + lasttime+"  " +curprofit);
	}

}
