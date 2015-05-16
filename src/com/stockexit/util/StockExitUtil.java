package com.stockexit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;







public class StockExitUtil {
	
	public static boolean runFuture = true;
	
	
private static Map<String,Integer> tokensmap = null;
	
	public static synchronized Map<String,Integer> buildTokensMap(){
		if(tokensmap != null){
			return tokensmap;
		}
		InputStreamReader is = null;
		BufferedReader br = null;
		tokensmap = new HashMap<String,Integer>();
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("data/tokens.txt")));
			br =  new BufferedReader(is);
			String line; 
			while ((line = br.readLine()) != null) {
				String[] vals = line.split("\\|");
				vals[3]  = vals[3].replace("-", "_");
				vals[3] = vals[3].replaceAll("\\s", "");
				vals[6] = vals[6].replaceAll("\\s", "");
				tokensmap.put(vals[3], Integer.parseInt(vals[6]));
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "TokensMap load failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
			} catch (IOException e) {}
		}
		return tokensmap;
	}
	
	private static Map<Integer, String> reversetokensmap = null;
	public static synchronized Map<Integer,String> buildReverseTokensMap(){
		if(reversetokensmap != null){
			return reversetokensmap;
		}
		InputStreamReader is = null;
		BufferedReader br = null;
		reversetokensmap = new HashMap<Integer,String>();
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("data/tokens.txt")));
			br =  new BufferedReader(is);
			String line; 
			while ((line = br.readLine()) != null) {
				String[] vals = line.split("\\|");
				vals[3]  = vals[3].replace("-", "_");
				vals[3] = vals[3].replaceAll("\\s", "");
				vals[6] = vals[6].replaceAll("\\s", "");
				reversetokensmap.put(Integer.parseInt(vals[6]), vals[3]);
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "ReverseTokensMap load failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
			} catch (IOException e) {}
		}
		return reversetokensmap;
	}
	
	
	private static Map<Integer, Integer> marketlotmap = null;
	public static synchronized Map<Integer,Integer> buildMarketLotMap(){
		if(marketlotmap != null){
			return marketlotmap;
		}
		InputStreamReader is = null;
		BufferedReader br = null;
		marketlotmap = new HashMap<Integer,Integer>();
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("data/tokens.txt")));
			br =  new BufferedReader(is);
			String line; 
			while ((line = br.readLine()) != null) {
				String[] vals = line.split("\\|");
				vals[6] = vals[6].replaceAll("\\s", "");
				vals[7] = vals[7].replaceAll("\\s", "");
				marketlotmap.put(Integer.parseInt(vals[6]), Integer.parseInt(vals[7]));
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "MarketLotMap load failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
			} catch (IOException e) {}
		}
		return marketlotmap;
	}
	
	
	public static String convertMonth(String num){
    	if(num.equals("01")){
    		return "JAN";
    	}else if(num.equals("02")){
    		return "FEB";
    	}else if(num.equals("03")){
    		return "MAR";
    	}else if(num.equals("04")){
    		return "APR";
    	}else if(num.equals("05")){
    		return "MAY";
    	}else if(num.equals("06")){
    		return "JUN";
    	}else if(num.equals("07")){
    		return "JUL";
    	}else if(num.equals("08")){
    		return "AUG";
    	}else if(num.equals("09")){
    		return "SEP";
    	}else if(num.equals("10")){
    		return "OCT";
    	}else if(num.equals("11")){
    		return "NOV";
    	}else if(num.equals("12")){
    		return "DEC";
    	}else{
    		throw new RuntimeException("Invalid Month in OrderManager");
    	}
    }
	
	public static String readPassword(){
		InputStreamReader is = null;
		BufferedReader br = null;
		String password = null;
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("/Users/kushd/nse/password")));
			br =  new BufferedReader(is);
			
			
			String line; 
			while ((line = br.readLine()) != null) {
				password = line.trim();
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "StockExit Read Password failed", e);
		}finally{
			 try {
				br.close();
				is.close();
				
			} catch (IOException e) {}
		}
		return password;
	}
}
