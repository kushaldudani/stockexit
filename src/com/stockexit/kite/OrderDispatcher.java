
package com.stockexit.kite;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;



public class OrderDispatcher {
	
	
	public static void main(String[] args) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		System.out.println("sdkjfjsdkfsd");
		//OrderDispatcher connector = new OrderDispatcher();
		
		Thread.sleep(2000);
		//connector.cancelOrder((short)0, "NIFTY", "2016-06-30", 0, "160530000095273");
		//System.out.println(connector.getExchangeConfirmationCnt("NIFTY", 1, "2016-06-30"));
		
	}
    
    //Enques reading so there are no concurrent reads on our side
    //private TradeConfirmation trade=null;
    //private String symbol;
    private Map<String, List<String>> ordermap = new HashMap<>();
    private Map<String, List<String>> cancelmap = new HashMap<>();
    private String accesstoken = readAccessToken();
    private String apikey = "yoke3sr4lgvh8aij";
    private Map<String, Integer> tokensmap=StockExitUtil.buildTokensMap();
	private Map<Integer, Integer> marketlotmap=StockExitUtil.buildMarketLotMap();
    
    public OrderDispatcher() throws IOException{
    	
    }
    
    
    public synchronized List<TradeConfirmation> getTradeConfirmation(String symbol, String expiry){
    	String fakesymbol = symbol.replace("_", "-");
    	String[] expiryvals = expiry.split("-");
    	fakesymbol = fakesymbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    	List<TradeConfirmation> trades = new ArrayList<TradeConfirmation>();
    	if(ordermap.containsKey(fakesymbol)){
    		List<String> orderids = ordermap.get(fakesymbol);
    		for(String orderid : orderids){
    			TradeConfirmation toreturn = getTrade(orderid);
    			if(toreturn!=null){
    				trades.add(toreturn);
    			}
    		}
    	}
    	return trades;
    }
    
    public synchronized int getExchangeConfirmationCnt(String symbol, int qyy, String expiry){
    	String fakesymbol = symbol.replace("_", "-");
    	String[] expiryvals = expiry.split("-");
    	fakesymbol = fakesymbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    	if(cancelmap.containsKey(fakesymbol)){
    		List<String> cancellist = cancelmap.get(fakesymbol);
    		if(cancellist.size() != qyy){
    			return -1;
    		}
    		int cancelledcnt = 0;
    		for(String ec : cancellist){
    			if(ec.equals("1")){
    				cancelledcnt++;
    			}
    		}
    		return cancelledcnt;
    	}
    	return -1;
    }
    
    private synchronized void setCancelConfirmation(String symbol, String cancel){
    	if(!cancelmap.containsKey(symbol)){
    		cancelmap.put(symbol, new ArrayList<String>());
    	}
    	cancelmap.get(symbol).add(cancel);
    }
    
    private synchronized void setOrderConfirmation(String symbol, String orderid){
    	if(!ordermap.containsKey(symbol)){
    		ordermap.put(symbol, new ArrayList<String>());
    	}
    	ordermap.get(symbol).add(orderid);
    }
    
    public synchronized void cancelOrder(short side, String symbol,
			String expiry, int qty, String piorderid) {
    	String fakesymbol = symbol.replace("_", "-");
    	symbol = fakesymbol;
    	String[] expiryvals = expiry.split("-");
    	symbol = symbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    	String cancel="1";
    	try {
    		URL obj = new URL("https://api.kite.trade/orders/"+piorderid+"?api_key="+apikey+"&access_token="+accesstoken);
    		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
    		con.setRequestMethod("GET");
    		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
    		String inputLine;
    		while ((inputLine = in.readLine()) != null) {
    			if(inputLine.contains("COMPLETE")){
    				cancel="0";
    				break;
    			}
    		}
    		in.close();
    		if(cancel.equals("1")){
    			cancel = "0";
    			obj = new URL("https://api.kite.trade/orders/regular/"+piorderid+"?api_key="+apikey+"&access_token="+accesstoken);
    			con = (HttpsURLConnection) obj.openConnection();
    			con.setRequestMethod("DELETE");
    			in = new BufferedReader(
    				new InputStreamReader(con.getInputStream()));
    			in.close();
    			cancel = "1";
    		}
    	} catch (Exception ex) {
        	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector cancelorder", 
            		ex);
        }
    	setCancelConfirmation(symbol, cancel);
	}
   
    public synchronized void sendOrder(
   		 short side, String symbol,
   		 double rate, String expiry, int qty)  {
        try {
        	int marketlot = marketlotmap.get(tokensmap.get(symbol));
        	String fakesymbol = symbol.replace("_", "-");
        	symbol = fakesymbol;
        	String[] expiryvals = expiry.split("-");
        	symbol = symbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    		short orderSide = (short) (side+1);//0 Buy 1 Sell;
    		int initialQty = qty * marketlot;
    		String productType = "MIS";
    		String clientOrderID = "";
            
    		double limitPrice = rate;
            double triggerPrice=0;
            String orderType="LIMIT";
            
            OrderRequest ord = new OrderRequest(symbol, orderSide, initialQty, limitPrice, triggerPrice, productType, clientOrderID, orderType,apikey,accesstoken);
            
            LoggerUtil.getLogger().info(ord+"");
    		String orderid = ord.toStream();
            if(orderid!=null){
            	setOrderConfirmation(symbol, orderid);
            }
    		
        } catch (Exception ex) {
        	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector sendorder", 
            		ex);
        }
    }
    
    private TradeConfirmation getTrade(String orderid) {
    	try{
    		URL obj = new URL("https://api.kite.trade/orders/"+orderid+"?api_key="+apikey+"&access_token="+accesstoken);
    		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
    		con.setRequestMethod("GET");
    		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
    		String inputLine;
    		String tradedprice = null;
    		while ((inputLine = in.readLine()) != null) {
    			if(inputLine.contains("average_price")){
    				String[] values = inputLine.split("\"average_price\":");
    				StringBuilder sb = new StringBuilder();
    				for(int i=0;i<values[1].length();i++){
    					char current = values[1].charAt(i);
    					if((current>='0'&&current<='9')||current=='.'){
    						sb.append(current);
    					}else if(current != ' '){
    						break;
    					}
    				}
    				tradedprice = sb.toString();
    				break;
    			}
    		}
    		in.close();
    		if(tradedprice!=null && Double.parseDouble(tradedprice)>1/*0 is risky*/){
    			return new TradeConfirmation(Double.parseDouble(tradedprice));
    		}
    	}catch(Exception e){
    		LoggerUtil.getLogger().log(Level.SEVERE, "get trade failed", e);
    	}
    	
		return null;
	}
    
    public static String readAccessToken(){
		InputStreamReader is = null;
		BufferedReader br = null;
		String accesstoken = null;
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("/Users/kushd/nse/accesstoken")));
			br =  new BufferedReader(is);
			
			
			String line; 
			while ((line = br.readLine()) != null) {
				accesstoken = line.trim();
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "Access token read failed", e);
		}finally{
			 try {
				br.close();
				is.close();
				
			} catch (IOException e) {}
		}
		return accesstoken;
	}
    
}
