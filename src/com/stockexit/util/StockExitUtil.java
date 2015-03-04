package com.stockexit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class StockExitUtil {
	
	
private static Map<String,String> symbolmap = null;
	
	public static synchronized Map<String,String> buildSymbolMap(){
		if(symbolmap != null){
			return symbolmap;
		}
		InputStreamReader is = null;
		BufferedReader br = null;
		symbolmap = new HashMap<String,String>();
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("data/finalurls.txt")));
			br =  new BufferedReader(is);
			String line; 
			while ((line = br.readLine()) != null) {
				String[] vals = line.split("qqq");
				symbolmap.put(vals[1], vals[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			 try {
				br.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return symbolmap;
	}

}
