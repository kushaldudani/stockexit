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
}
