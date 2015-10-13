package com.stockexit.net;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import com.stockdata.bpwealth.OrderDispatcher;
import com.stockdata.bpwealth.TradeConfirmation;
import com.stockdata.bpwealth.broadcast.TickData;
import com.stockdata.bpwealth.broadcast.TickListener;
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
	private double lossthreshold;
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
			StockExit.removeLongEntry(sss);
		}else if(type.substring(0, 1).equals("S") && !sss.equals("NIFTY")){
			StockExit.removeShortEntry(sss);
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
		
		if(lasttime.compareTo("15:01") >= 0){
			return sellStock(curprice, curprofit, "Endday",lasttime, getEntryBudget());
		}
		if(curdate.equals(getEntryExpiry()) && lasttime.compareTo("14:01") >= 0){
			return sellStock(curprice, curprofit, "Endday",lasttime, getEntryBudget());
		}
		
		/*if((targetTimer>0) && (targetTimer>100) && ((System.currentTimeMillis()-targetTimer)>0)){
			return sellStock(curprice, curprofit, "Endday",lasttime);
		}else if((targetTimer>0) && (targetTimer<100) && ((totalticks-targetTimer)>=0)){
			return sellStock(curprice, curprofit, "Endday",lasttime);
		}*/
		if(lasttime.compareTo("09:45") >= 0 && getEntryMcase() == 2 && getEntryType().equals("Long") && curprofit >= 0.7 && getNiftyUpPercent() <= -0.2){
			return sellStock(curprice, curprofit, "Endday1",lasttime, getEntryBudget());
		}else if(curprofit >= 2.85 && getEntryMcase() != 1){
			return sellStock(curprice, curprofit, "Endday2",lasttime, getEntryBudget());
		}else if(buysell!=null&& lasttime.compareTo("09:45") >= 0 && !sss.equals("NIFTY") && curprofit >= -1.5 && curprofit < -1 
				&& getNiftyUpPercent() <= -0.75 && getEntryType().equals("Long")){
			return sellStock(curprice, curprofit, "Endday4",lasttime, getEntryBudget());
		}else if(buysell!=null&& lasttime.compareTo("09:45") >= 0 && !sss.equals("NIFTY") && curprofit >= -1.5 && curprofit < -1 
				&& getNiftyUpPercent() >= 0.75 && getEntryType().equals("Short")){
			return sellStock(curprice, curprofit, "Endday5",lasttime, getEntryBudget());
		}else if(buysell!=null && !sss.equals("NIFTY") && 
				getSlippage(getEntryNextopenprice(), getEntryEnterprice(), getEntryType()) > 0.2 
				&& curprofit >= getNiftyBasedProfitThreshold()){
			return sellStock(curprice, curprofit, "Endday6",lasttime, getEntryBudget());
		}else if(lasttime.compareTo("09:30") >= 0 && !sss.equals("NIFTY") && curprofit >= getNiftyBasedProfitThreshold() 
				&& getNiftyUpPercent() <= -0.2 && getEntryType().equals("Long")){
			return sellStock(curprice, curprofit, "Endday7",lasttime, getEntryBudget());
		}else if(lasttime.compareTo("09:30") >= 0 && !sss.equals("NIFTY") && curprofit >= getNiftyBasedProfitThreshold()
				&& getNiftyUpPercent() >= 0.2 && getEntryType().equals("Short")){
			return sellStock(curprice, curprofit, "Endday8",lasttime, getEntryBudget());
		}
		//else if(lasttime.compareTo("09:45") >= 0 && sss.equals("NIFTY") && curprofit < -0.75){
		//	return sellStock(curprice, curprofit, "Endday",lasttime, getEntryBudget());
		//}
		else if(sss.equals("NIFTY")){
			int niftyhedgeqty = getEntryBudget()/getQuantityToBeFired(sss, curprice);
			int logshortdiff = StockExit.getLongShortDiff();
			if(logshortdiff < niftyhedgeqty && curprofit > -0.2){
				int qyy = (niftyhedgeqty - logshortdiff);
				qyy = (qyy * getQuantityToBeFired(sss, curprice));
				return sellStock(curprice, curprofit, "Endday",lasttime, qyy);
			}
		}else if(size>=3){ // for intraday huge movement
			double price1 = prices.get(size-1);
			double price2 = prices.get(size-2);
			double price3 = prices.get(size-3);
			double loss1 = getPft(enterprice,price1,getEntryType());
			double loss2 = getPft(enterprice,price2,getEntryType());
			double loss3 = getPft(enterprice,price3,getEntryType());
			if(loss1 < lossthreshold && 
					loss2 < lossthreshold && loss3 < lossthreshold && lasttime.compareTo("09:45") >= 0) {
				return sellStock(price1, loss1, "Endday",lasttime, getEntryBudget());
			}
		}
		
		return false;
	}
	
	private double getNiftyBasedProfitThreshold(){
		if(buysell != null){
			return 1.25;
		}else{
			return 0.95;
		}
	}
	
	private static final int NIFTYPRINCIPAL = 400000;
	
	private int getQuantityToBeFired(String symbol, double price) {
		int marketlot = marketlotmap.get(tokensmap.get(symbol));
		double value = (price*marketlot);
		double principalToBeUsed = NIFTYPRINCIPAL;
		double factor = principalToBeUsed/value;
		int lowerint = (int) Math.floor(factor);
		double decimaldiff = factor - lowerint;
		if(decimaldiff > 0.6){
			lowerint = lowerint + 1;
		}
		return lowerint;
	}
	
	private double getNiftyUpPercent(){
		if(buysell!=null){
			return TickListener.getNiftyUppercent();
		}else{
			return TickListener.getNiftyUpFromClosepercent();
		}
	}
	
	private double getSlippage(double entryNextopenprice, double entryEnterprice, String type) {
		if(type.equals("Long")){
			double slippage = ((entryEnterprice-entryNextopenprice)/(entryNextopenprice))*100;
			return slippage;
		}else{
			double slippage = ((entryNextopenprice-entryEnterprice)/(entryNextopenprice))*100;
			return slippage;
		}
	}
	
	
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
	
	

	private boolean sellStock(double curprice, double curprofit, String ismidday, String lasttime, int qyy) {
		lock.lock();
		try{
			String ssymb = getEntrySymbol().split("-")[0];
			short underlyingtype = 1;
			if(ssymb.equals("NIFTY")){underlyingtype = 0;}
			OrderDispatcher od = new OrderDispatcher();
			od.connect();
			for(int i=0;i<qyy;i++){
				if(getEntryType().equals("Long")){
					double limitprice = curprice*(0.995);
					int limitprice100 = (int) (limitprice*100);
					limitprice100 = roundup(limitprice100);
					od.sendOrder((short)0, (short)1, 
							Integer.toString(tokensmap.get(ssymb)), ssymb, limitprice100, 
							marketlotmap.get(tokensmap.get(ssymb)), getEntryExpiry(), 1,
							underlyingtype);
				}else{
					double limitprice = curprice*(1.005);
					int limitprice100 = (int) (limitprice*100);
					limitprice100 = roundup(limitprice100);
					od.sendOrder((short)0, (short)0, 
							Integer.toString(tokensmap.get(ssymb)), ssymb, limitprice100, 
							marketlotmap.get(tokensmap.get(ssymb)), getEntryExpiry(), 1,
							underlyingtype);
				}
				intervalwait();
			}
			List<TradeConfirmation> trade = null;
			trade = pollTrade(od, ssymb);
			if(trade != null && trade.size() == qyy){
				double sum=0;
				int sz = trade.size();
				for(TradeConfirmation tc : trade){
					double tradedprice = ((double)(tc.TrdPrice)/(double)100);
					sum = sum + tradedprice;
				}
				double avgtradedprice = (sum/(double)sz);
				updateStock(avgtradedprice, lasttime, ismidday, qyy);
				SendMail.generateAndSendEmail("Successfully squared off - "+ getEntrySymbol() + " qty - "+qyy+"  "+getEntryType() + 
						" at price - " + getEntryExitprice()+" please verify, enterprice - "+getEntryEnterprice());
			}else{
				LoggerUtil.getLogger().info("NotSold but order dispatched- "+ismidday +" - " + getEntrySymbol() );
				SendMail.generateAndSendEmail("Tried squaring off - "+ getEntrySymbol() + " qty - "+qyy+
						" but did not get trade confirmation. please square off from terminal");
			}
			return true;
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "In SymbolEstimator SellStock failed "+getEntrySymbol(), e);
		}finally { lock.unlock();}
		LoggerUtil.getLogger().info("NotSold connection problem- "+ismidday +" - " + getEntrySymbol() );
		SendMail.generateAndSendEmail("Not able to square off - "+ getEntrySymbol() + " qty - "+qyy+
				" connection problem. please square off from terminal");
		return true;
	}
	
	private List<TradeConfirmation> pollTrade(OrderDispatcher od, String symbol) {
		long loopstarttime = System.currentTimeMillis();
    	while(((System.currentTimeMillis()-loopstarttime)<1100)){
    		intervalwait();
    	}
    	return od.getTradeConfirmation(symbol);
	}

	private int roundup(int limitprice) {
		int rnd = limitprice - (limitprice%10);
		return rnd;
	}
	
	private double getlossthreshold(){
		if(buysell == null){
			return -4.5;
		}else{
			return -3.5;
		}
	}
	
	private void intervalwait() {
		long timestamp = System.currentTimeMillis();
		int timetowait = (500);
		while(System.currentTimeMillis() < (timestamp+timetowait)){
			try {
				Thread.sleep(timetowait-System.currentTimeMillis()+timestamp);
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
