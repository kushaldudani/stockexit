
package com.stockexit.zerodha;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.stockdata.bpwealth.core.CConstants;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;



public class OrderDispatcher {
	
	
	public static void main(String[] args) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		System.out.println("sdkjfjsdkfsd");
		//OrderDispatcher connector = new OrderDispatcher();
		
		Thread.sleep(2000);
		
	}
    
   private Socket echoSocket;
   //private int counter=0;
   private String ip = "198.38.94.24";
   private int port = 18579;
    //Enques reading so there are no concurrent reads on our side
    private Map<String, List<TradeConfirmation>> exhMap=new HashMap<>();
    //private TradeConfirmation trade=null;
    //private String symbol;
    private Map<String, List<TradeConfirmation>> trademap = new HashMap<>();
    private AtomicInteger isUsed = new AtomicInteger(0);
    
    public OrderDispatcher() throws IOException{
    	//this.symbol = symbol;
        echoSocket = new Socket(ip, port);
        echoSocket.setReceiveBufferSize(6092);
        echoSocket.setSendBufferSize(2048);
        echoSocket.setTcpNoDelay(true);
        echoSocket.setKeepAlive(true);
        echoSocket.setSoTimeout(10*1000);
        new Thread(new Listen()).start();
    }
    
    public void closeSocket() {
    	try{
    	echoSocket.close();
    	}catch(Exception e){
    		
    	}
    }
    
    
    public synchronized List<TradeConfirmation> getTradeConfirmation(String symbol, String expiry){
    	String fakesymbol = symbol.replace("_", "-");
    	String[] expiryvals = expiry.split("-");
    	fakesymbol = fakesymbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    	isUsed.set(1);
    	if(trademap.containsKey(fakesymbol)){
    		return trademap.get(fakesymbol);
    	}
    	return new ArrayList<TradeConfirmation>();
    }
    
    public synchronized int getExchangeConfirmationCnt(String symbol, int qyy, String expiry){
    	String fakesymbol = symbol.replace("_", "-");
    	String[] expiryvals = expiry.split("-");
    	fakesymbol = fakesymbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    	isUsed.set(1);
    	if(exhMap.containsKey(fakesymbol)){
    		List<TradeConfirmation> conflist = exhMap.get(fakesymbol);
    		if(conflist.size() != qyy){
    			return -1;
    		}
    		int cancelledcnt = 0;
    		for(TradeConfirmation ec : conflist){
    			if(ec.msgHeader.messageCode == 303){
    				cancelledcnt++;
    			}
    		}
    		return cancelledcnt;
    	}
    	return -1;
    }
    
    private synchronized void setExchangeConfirmation(String symbol, TradeConfirmation exhconf){
    	if(!exhMap.containsKey(symbol)){
    		exhMap.put(symbol, new ArrayList<TradeConfirmation>());
    	}
    	exhMap.get(symbol).add(exhconf);
    }
    
    private synchronized void setTradeConfirmation(String symbol, TradeConfirmation trade){
    	if(!trademap.containsKey(symbol)){
    		trademap.put(symbol, new ArrayList<TradeConfirmation>());
    	}
    	trademap.get(symbol).add(trade);
    }
    
    public synchronized void cancelOrder(short side, String symbol,
			String expiry, int qty, String piorderid) {
    	try {
    		ZerodhaHeader header = new ZerodhaHeader((short)301);
        	
        	String fakesymbol = symbol.replace("_", "-");
        	symbol = fakesymbol;
        	String[] expiryvals = expiry.split("-");
        	symbol = symbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    		short orderSide = (short) (side+1);//0 Buy 1 Sell;
    		int initialQty = qty;
    		String productType = "MIS";
    		String clientOrderID = "";
            
    		double limitPrice=0;
            double triggerPrice=0;
            String orderType="SL";
            
            OrderRequest ord = new OrderRequest(symbol, orderSide, initialQty, limitPrice, triggerPrice, productType, clientOrderID, orderType);
            ord.piOrderID = piorderid;
            
            LoggerUtil.getLogger().info(ord+"");
            NativeDataOutputStream ndos = new NativeDataOutputStream(new BufferedOutputStream(echoSocket.getOutputStream()));
            header.toStream(ndos);
    		ord.toStream(ndos);
    	} catch (Exception ex) {
        	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector cancelorder", 
            		ex);
        }
		
	}
   
    public synchronized void sendOrder(
   		 short side, String symbol,
   		 double rate, String expiry, int qty)  {
        try {
            
        	ZerodhaHeader header = new ZerodhaHeader((short)101);
        	
        	String fakesymbol = symbol.replace("_", "-");
        	symbol = fakesymbol;
        	String[] expiryvals = expiry.split("-");
        	symbol = symbol+expiryvals[0].substring(2, 4)+StockExitUtil.convertMonth(expiryvals[1])+"FUT";
    		short orderSide = (short) (side+1);//0 Buy 1 Sell;
    		int initialQty = qty;
    		String productType = "MIS";
    		String clientOrderID = "";
            
    		double limitPrice = rate;
            double triggerPrice=0;
            String orderType="LIMIT";
            
            OrderRequest ord = new OrderRequest(symbol, orderSide, initialQty, limitPrice, triggerPrice, productType, clientOrderID, orderType);
            
            LoggerUtil.getLogger().info(ord+"");
            NativeDataOutputStream ndos = new NativeDataOutputStream(new BufferedOutputStream(echoSocket.getOutputStream()));
            header.toStream(ndos);
    		ord.toStream(ndos);
            
        } catch (Exception ex) {
        	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector sendorder", 
            		ex);
        	//return null;
        }
    }

    
    
    

    
	public class parseData {
		byte[] data;

		public parseData(byte[] data) {
			this.data = data;
		}

		
		public void run() {

				try {
					TradeConfirmation conf = new TradeConfirmation(data);
					LoggerUtil.getLogger().info("conf:" + conf);
					if(conf.msgHeader.messageCode == 302 || conf.msgHeader.messageCode == 303) {
						setExchangeConfirmation(conf.Symbol, conf);
					}
					if(conf.msgHeader.messageCode == 603) {
						setTradeConfirmation(conf.Symbol, conf);
					}
					

				} catch (Exception ex) {
					LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector confirmation", 
		            		ex);
				}
		}
	}
	
	
    private class Listen implements Runnable{
    	
    	

        @Override
        public void run() {
             InputStream   dIn = null;
            try {
                dIn = echoSocket.getInputStream();
                  
                    short msg_length=0;
                    int index = 0;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    while(isUsed.get() == 0){
                        
                        byte[] fresh = new byte[10240];
                        int size  = dIn.read( fresh );
                        if(size<1)continue;
                        baos.write(fresh, 0, size);
                       // System.out.println(baos.size()+"\t"+index);
                        while (baos.size()>index) 
                        {
                        if(baos.size()<=2)
                        {
                         
                            break;
                        }    
                        
                        msg_length = CConstants.getInt16(baos.toByteArray(), (index+2));
                       
                        if(baos.size()-index<msg_length){
                          //System.out.println("More to come:"+(baos.size()-index)+"/"+msg_length);
                            break;
                        }
                       // System.out.println("Message length:"+msg_length+"\tStream:"+baos.size()+"\tIndex:"+index    );
                        byte[] data = new byte[msg_length];
                        System.arraycopy(baos.toByteArray(), index, data, 0, data.length);
                        new parseData(data).run();
                        index +=msg_length;
                        }
                        
                      //  System.out.println("Continuing to listen to socket at index:"+index);
                        if(baos.size()>msg_length){//There are bytes left after
                      //  System.out.println("Bytes are left to be carried forward:"+(baos.size()-index));
                        byte[] remnant = new byte[baos.size()-index];
                      // System.out.println("Remnant will receive bytes from:"+(baos.size()-index));
                        System.arraycopy(baos.toByteArray(), index, remnant, 0, remnant.length);    
                        baos.reset();
                        baos.write(remnant);
                        }
                        else if(baos.size()==msg_length){
                            baos.reset();
                            
                        }
                        index = 0;
                        LoggerUtil.getLogger().info("Reset index to zero");
                        
                  }    
                
            } catch (Exception ex) {
            	
            } 
                        
            LoggerUtil.getLogger().info("Listen thread exiting");    
                    
                    
            
                
                }
            }
 
    
}
