
package com.stockdata.bpwealth.broadcast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;





public class BroadCastManager{
	
	public static void mainrun(Map<String,List<SynQueue<TickData>>> queuemap)  {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		
		/*List<String> dates = readDates();
		if(dates.size()<1){
			LoggerUtil.getLogger().info("Not sufficient entries in dates file");
		    System.exit(1);
		}
		String lastentry = dates.get(dates.size()-1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		if(!(sdf.format(today.getTime()).equals(lastentry)) ){
		    LoggerUtil.getLogger().info("Market should be closed today");
		    System.exit(1);
		}*/
		
		//String ip = "203.123.141.236";
		//int port = 46026;
		
		
		Map<String,Integer> tokensMap = StockExitUtil.buildTokensMap();
		
		
		List<Integer> syms = new ArrayList<>();
		for(String sss : queuemap.keySet()){
			syms.add(tokensMap.get(sss));
		}
		BroadCastManager bmanager = new BroadCastManager();
		bmanager.subscribe(syms,queuemap);
	}
	
    //private String ip;
    //private int port;
    //private ExecutorService executorService;
    public BroadCastManager() {
        //this.ip = ip;
        //this.port = port;
    }
 
    private void subscribe(List<Integer> tokens, Map<String,List<SynQueue<TickData>>> queuemap) {
    	Calendar cal = Calendar.getInstance(); 
		String hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d",cal.get(Calendar.MINUTE));
		String lasttime = hour+":"+minute;
    	while(lasttime.compareTo("09:15") >= 0 && lasttime.compareTo("15:30") < 0){
    		boolean result = false;
    		try{
    			KiteBroadcast.mainrun(tokens, queuemap);
    			result = true;
	        }catch(Exception e){
	        	LoggerUtil.getLogger().log(Level.SEVERE, "Broadcast cannot request to listen to data");
	        }
    		if(result){
    			long timestart = System.currentTimeMillis();
    			long timeend = System.currentTimeMillis();
    			while((timeend-timestart)<20500000){
    				try {
    					long diff = 20500000 - System.currentTimeMillis() + timestart;
    					if(diff > 0){
    						Thread.sleep(diff);
    					}
    				} catch (Exception e) {}
    				timeend = System.currentTimeMillis();
    			}
    		}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			cal = Calendar.getInstance(); 
			hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
			minute = String.format("%02d",cal.get(Calendar.MINUTE));
			lasttime = hour+":"+minute;
		}
    	System.exit(0);
    }
    
    
    
    
    /*private static List<String> readDates(){
		InputStreamReader is = null;
		BufferedReader br = null;
		List<String> dates = new ArrayList<String>();
		
		try {
			is = new InputStreamReader(new FileInputStream(new 
					File("/Users/kushd/nse/dates")));
			br =  new BufferedReader(is);
			
			
			String line; 
			while ((line = br.readLine()) != null) {
				String date = line.trim();
				dates.add(date);
			}
		} catch (Exception e) {
			LoggerUtil.getLogger().log(Level.SEVERE, "BroadcastManager Read Dates failed", e);
			System.exit(1);
		}finally{
			 try {
				br.close();
				is.close();
				
			} catch (IOException e) {}
		}
		return dates;
	}*/
    
}

/*class BroadcastSubject extends Observable
{
    public void updateRates(int token,MarketWatchResponse watch){
        setChanged();
        notifyObservers(new Object[]{token,watch});
    }
}*/
