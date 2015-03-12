package com.stockexit.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;

public class MCDownloader {
	
	private static final String baseUrl = "http://www.moneycontrol.com/mccode/common/get_pricechart_div.php?nse_id=Y&sc_id=";
	private HttpClient client;
	private Map<String,String> symbolMap;
	
	public MCDownloader() {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30*1000).setConnectTimeout(30*1000).build();
		client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		symbolMap = StockExitUtil.buildSymbolMap();
	}

	
	public String downloadData(BuySell buysell){
		String price = null;
		String low = null;
		String high = null; String lasttime = null;
		String u = symbolMap.get(buysell.getSymbol().split("-")[0]);
		if(u == null){
			LoggerUtil.getLogger().info("StockExit Mapping not present for - " + buysell.getSymbol());
		}
		String url = baseUrl + (u.split(" "))[1];
		LoggerUtil.getLogger().info(url);
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try{
			HttpResponse response = retry(url);
			if(response.getStatusLine().getStatusCode() == 200){
				inputStreamReader = new InputStreamReader(response.getEntity().getContent());
				bufferedReader = new BufferedReader(inputStreamReader);
				String line; boolean islowline=false; boolean isfirstTime=true;
				while ((line = bufferedReader.readLine()) != null) {
					//System.out.println(line);
					if(isfirstTime && line.contains("CL")){
						String[] values = line.split(">");
						String[] values2 = values[1].split("<");
						lasttime = values2[0].split(" ")[2];
						isfirstTime=false;
					}
					if(line.contains("Nse_Prc_tick")){
						String[] values = line.split(">");
						String[] values2 = values[3].split("<");
						price = values2[0];
					}
					if(islowline && line.contains("PR5")){
						String[] values = line.split(">");
						for(String et : values){
							if(Character.isDigit(et.charAt(0))){
								low = et.split("<")[0];
							}
						}
					}
					if(islowline && line.contains("PL5")){
						String[] values = line.split(">");
						for(String et : values){
							if(Character.isDigit(et.charAt(0))){
								high = et.split("<")[0];
							}
						}
						break;
					}
					if(line.contains("Today's Low/High")){
						islowline=true;
					}
				}
				
			}else{
				LoggerUtil.getLogger().info("Data fetch failed for " + u);
				return null;
			}
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE,"Data fetch exception for " + u,e);
			return null;
		}finally{
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {}
			}
			if(inputStreamReader != null){
				try {
					inputStreamReader.close();
				} catch (IOException e) {}
			}
		}
		return price+"/"+low+"/"+high+"/"+lasttime;
	}
	
	
	private HttpResponse retry(String url){
		HttpGet request = new HttpGet(url);
		int responsecode=0;
		int nooftries = 1;
		HttpResponse response=null;
		while(responsecode != 200 && nooftries <= 5){
			try{
				response = client.execute(request);
				responsecode = response.getStatusLine().getStatusCode();
			}catch(Exception e){}
			try {
				Thread.sleep(nooftries * 1000);
			} catch (InterruptedException e) {}
			nooftries++;
		}
		
		return response;
	}
	

}
