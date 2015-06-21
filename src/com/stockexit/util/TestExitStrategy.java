package com.stockexit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class TestExitStrategy {
	
	public static void main(String[] args) {
		String stock = "OFSS";
		String filename = "/Users/kushd/nse/files/15-9-17-5";
		
		InputStreamReader is = null;
		BufferedReader br = null;
		List<String> ticklines = new ArrayList<String>();
		
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File(filename)));
			br =  new BufferedReader(is);
			
			
			String line; 
			while ((line = br.readLine()) != null) {
				String tick = line.trim();
				if(tick.contains(("INFO: "+stock+"-"))){
					ticklines.add(tick);
				}
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "TestExitStrategy reading file failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
				
			} catch (IOException e) {}
		}
	
		List<Double> profits = new ArrayList<Double>();
		for(String entry : ticklines){
			String[] parts = entry.split("  ");
			profits.add(Double.parseDouble(parts[2]));
			boolean sold = optimalexit(profits, parts[1]);
			if(sold){break;}
		}
		System.out.println(profits.size());
		System.out.println("Global Max : "+localMax);
	}
	
	private static double localMax=-99;
	private static double positiveticks=0;
	private static double negativeticks=0;
	private static double zeroticks=0;
	
	
	private static boolean optimalexit(List<Double> profits, String lasttime){
		int sz = profits.size();
		double curprofit = profits.get(sz-1);
		if(curprofit > localMax){
			localMax = curprofit;
			positiveticks=0;
			negativeticks=0;
			zeroticks=0;
		}else if(sz>=2 && curprofit > profits.get(sz-2)){
			positiveticks++;
		}else if(sz>=2 && curprofit < profits.get(sz-2)){
			negativeticks++;
		}else{
			zeroticks++;
		}
		
		System.out.println(curprofit + "  " + localMax+"  "+positiveticks+"  "+negativeticks+"  "+zeroticks 
				+"  "+lasttime);
		
		/*if(curprofit >= 1){
			return true;
		}else if((exitAtStartMax >= getExitAtStartProfit()) && (prices.size() >=2)){
			if(exitAtStartTimer == 0){
				exitAtStartTimer = System.currentTimeMillis();
			}
			double profit2 = profits.get(sz-2);
			double exitAtStartPftLossBreaker = getExitAtStartProfit() - 0.35;
			if( ((System.currentTimeMillis()-exitAtStartTimer) > 100000) ||
					(curprofit < exitAtStartPftLossBreaker && profit2 < exitAtStartPftLossBreaker) ){
				return true;
			}
		}*/
		return false;
	}
}
