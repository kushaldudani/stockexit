package com.stockexit.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.stockexit.util.LoggerUtil;


public class FutureMC implements MCInterface {
	
	
	private HttpClient client;
	private Map<String,String> futuremoney = null;
	
	public FutureMC() {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30*1000).setConnectTimeout(30*1000).build();
		client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		this.futuremoney = buildFutureMoneyMap();
	}

	@Override
	public String downloadData(BuySell buysell){
		String price = null;
		String low = null;
		String high = null;
		String url = futuremoney.get(buysell.getSymbol().split("-")[0]) + buysell.getExpiry();
		LoggerUtil.getLogger().info(url);
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try{
			HttpResponse response = retry(url);
			if(response.getStatusLine().getStatusCode() == 200){
				inputStreamReader = new InputStreamReader(response.getEntity().getContent());
				bufferedReader = new BufferedReader(inputStreamReader);
				String line; 
				while ((line = bufferedReader.readLine()) != null) {
					//System.out.println(line);
					if(line.contains("gr_28 FL")){
						String[] values = line.split(">");
						String[] values2 = values[2].split("<");
						price = values2[0];
						price = price.replace(",", "");
					}else if(line.contains("r_28 FL")){
						String[] values = line.split(">");
						String[] values2 = values[2].split("<");
						price = values2[0];
						price = price.replace(",", "");
					}else if(line.contains("b_28 FL")){
						String[] values = line.split(">");
						String[] values2 = values[2].split("<");
						price = values2[0];
						price = price.replace(",", "");
					}else if(line.contains("High Price")){
						line = bufferedReader.readLine();
						String[] values = line.split(">");
						String[] values2 = values[1].split("<");
						high = values2[0];
						high = high.replace(",", "");
					}else if(line.contains("Low Price")){
						line = bufferedReader.readLine();
						String[] values = line.split(">");
						String[] values2 = values[1].split("<");
						low = values2[0];
						low = low.replace(",", "");
						break;
					}
				}
				
			}else{
				LoggerUtil.getLogger().info("Data fetch failed for " + buysell.getSymbol().split("-")[0]);
				return null;
			}
		}catch(Exception e){
			LoggerUtil.getLogger().log(Level.SEVERE, "Data fetch exception for " + 
														buysell.getSymbol().split("-")[0],e);
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
		return price+"/"+low+"/"+high;
	}
	
	
	private HttpResponse retry(String url){
		HttpGet request = new HttpGet(url);
		request.setHeader("Connection", "keep-alive");
		int responsecode=0;
		int nooftries = 1;
		HttpResponse response=null;
		while(responsecode != 200 && nooftries <= 5){
			try{
				response = client.execute(request);
				responsecode = response.getStatusLine().getStatusCode();
			}catch(Exception e){LoggerUtil.getLogger().log(Level.SEVERE, "Exception in retry " + url,e);}
			try {
				Thread.sleep(nooftries * 1000);
			} catch (InterruptedException e) {}
			nooftries++;
		}
		
		return response;
	}
	
	
	private static Map<String,String> futuremoneymap = null;

	private static synchronized Map<String,String> buildFutureMoneyMap(){
		if(futuremoneymap != null){
			return futuremoneymap;
		}
		InputStreamReader is = null;
		BufferedReader br = null;
		futuremoneymap = new HashMap<String,String>();
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("data/futuremoney.txt")));
			br =  new BufferedReader(is);
			String line; 
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(" ");
				futuremoneymap.put(vals[0], vals[1]);
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "FutureMoneymap load failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
			} catch (IOException e) {}
		}
		return futuremoneymap;
	}

	

}
