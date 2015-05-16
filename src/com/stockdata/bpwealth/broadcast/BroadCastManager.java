
package com.stockdata.bpwealth.broadcast;


import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;





public class BroadCastManager{
	
	public static void mainrun(Map<String,SynQueue<TickData>> queuemap)  {
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
		
		String ip = "203.123.141.236";
		int port = 46026;
		
		
		Map<String,Integer> tokensMap = StockExitUtil.buildTokensMap();
		
		
		List<Integer> syms = new ArrayList<Integer>();
		for(String sss : queuemap.keySet()){
			syms.add(tokensMap.get(sss));
		}
		BroadCastManager bmanager = new BroadCastManager(ip, port);
		bmanager.subscribe(syms,queuemap);
	}
	
    private String ip;
    private int port;
    private ExecutorService executorService;
    public BroadCastManager(String ip,int port) {
        this.ip = ip;
        this.port = port;
        executorService = Executors.newFixedThreadPool(1);
    }
 
    private void subscribe(List<Integer> tokens, Map<String,SynQueue<TickData>> queuemap) {
    	Calendar cal = Calendar.getInstance(); 
		String hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d",cal.get(Calendar.MINUTE));
		String lasttime = hour+":"+minute;
    	while(lasttime.compareTo("09:15") >= 0 && lasttime.compareTo("15:30") < 0){
			MarketDepthRequest req = new MarketDepthRequest(tokens);
			try{
	        Socket echoSocket = new Socket(ip, port);
	        echoSocket.setReceiveBufferSize(6092);
	        echoSocket.setSendBufferSize(1024);
	        echoSocket.setTcpNoDelay(true);
	        echoSocket.setSoTimeout(30*1000);
	        
	        
	        	DataOutputStream outToServer = new DataOutputStream(echoSocket.getOutputStream());
	        	outToServer.write(req.getStruct());
	        	outToServer.flush();
	        	
	        	executorService.execute(new TickListener(echoSocket,queuemap));
	        	executorService.shutdown();
	        }catch(Exception e){
	        	LoggerUtil.getLogger().log(Level.SEVERE, "Broadcast cannot request to listen to data", e);
	        }
	        boolean result = false;
			while(true){
				try {
					result = executorService.awaitTermination(7, TimeUnit.HOURS);
					LoggerUtil.getLogger().info("Broadcast Connection lost - "+result);
					break;
				} catch (Exception e) {
					LoggerUtil.getLogger().log(Level.SEVERE, "Broadcast Connection interrupted", e);
				}
			}
			cal = Calendar.getInstance(); 
			hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
			minute = String.format("%02d",cal.get(Calendar.MINUTE));
			lasttime = hour+":"+minute;
		}
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
