
package com.stockdata.bpwealth;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.CompressionHeader;
import com.stockdata.bpwealth.core.MessageHeader;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;



public class OrderDispatcher {
	
	
	public static void main(String[] args) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		OrderDispatcher connector = new OrderDispatcher();
		connector.connect();
		
		Thread.sleep(2000);
		//connector.sendOrder((short)0, (short)1, "45239", 
			//	"GODREJIND", 33600, 1001, "2015-06-25", 1);
	}
    
   private Socket echoSocket;
   //private int counter=0;
   private String ip = "203.123.141.236";
   private int port = 51525;
    //Enques reading so there are no concurrent reads on our side
    private Map<String, OrderConfirmation> ordMap=new HashMap<String, OrderConfirmation>();
    private ExchangeConfirmation item=null;
    //private TradeConfirmation trade=null;
    //private String symbol;
    private Map<String, TradeConfirmation> trademap = new HashMap<String, TradeConfirmation>();
    private Map<Integer, String> reversetokensmap = null;
    private String password = null;
    private AtomicInteger isLoggedin = new AtomicInteger(0);
    
    public OrderDispatcher() throws IOException{
    	//this.symbol = symbol;
        echoSocket = new Socket(ip, port);
        echoSocket.setReceiveBufferSize(6092);
        echoSocket.setSendBufferSize(2048);
        echoSocket.setTcpNoDelay(true);
        echoSocket.setKeepAlive(true);
        echoSocket.setSoTimeout(30*1000);
        reversetokensmap = StockExitUtil.buildReverseTokensMap();
        new Thread(new ListenDirector()).start();
        password = StockExitUtil.readPassword();
    }
    
    
    public synchronized TradeConfirmation getTradeConfirmation(String symbol){
    	if(trademap.containsKey(symbol)){
    		return trademap.get(symbol);
    	}
    	return null;
    }
    
    public synchronized OrderConfirmation getOrderConfirmation(String symbol){
    	if(ordMap.containsKey(symbol)){
    		return ordMap.get(symbol);
    	}
    	return null;
    }
    
    private synchronized void setOrderConfirmation(String symbol, OrderConfirmation ordconf){
    	ordMap.put(symbol, ordconf);
    }
    
    private synchronized void setTradeConfirmation(String symbol, TradeConfirmation trade){
    	trademap.put(symbol, trade);
    }
   
    public synchronized void sendOrder(short requestType,
   		 short side, String token, String symbol,
   		 int rate, int marketLot, String expiry, int qty)  {
        try {
            
            //AlgoDB db = new AlgoDB();
            //db.setPooler(pooler);
            
            //order = db.newOrder(order);
        	String[] expiryvals = expiry.split("-");
       	 	OrderRequest ord = new OrderRequest();
            ord.RequestType = requestType;//0 Place 1 modify 2 cancel
            ord.Segment = 2;
            ord.side = side;//0 Buy 1 Sell
            ord.Token = token;
            String fakesymbol = symbol.replace("_", "-");
            ord.Symbol = fakesymbol;
            ord.ScripName = symbol+" "+expiryvals[2]+StockExitUtil.convertMonth(expiryvals[1])+
           		 expiryvals[0].substring(2, 4);
            ord.InstType = (short)0;
            ord.UnderlyingType = (short)1;
            Calendar cal = new GregorianCalendar(Integer.parseInt(expiryvals[0]), 
           		 Integer.parseInt(expiryvals[1])-1, Integer.parseInt(expiryvals[2]),
           		 14,30,0);
            ord.ExpiryDate = getNSESeconds(cal.getTimeInMillis());
            
            ord.quantity = qty;
            ord.DiscQty = qty;
            ord.rate = rate;
            
            ord.ProdType = 0;
            ord.ClientCode = "SLS011";
            ord.OrderRequesterCode = "SLS011";
            ord.ProCli = (short)1;
            
            ord.id = (short)1;
            ord.LocalOrderTime = getNSESeconds(Calendar.getInstance().getTimeInMillis());
            //ord.PendQty = 1;
            //ord.ExchOrderID = 2015020201731230L;
            ord.NNFField = 111111111111100L;
            ord.MarketLot = marketLot;
            
            LoggerUtil.getLogger().info(ord+"");
            this.sendBytes(ord.getStruct(),true);
            
            //long loopstarttime = System.currentTimeMillis();
            //while((getTradeConfirmation(symbol)==null)&& (System.currentTimeMillis()-loopstarttime)<30000){
            	
            //}
            //return getTradeConfirmation(symbol);
            
            //setChanged();
            //notifyObservers(order);
            //db.saveOrUpdateOrder(order);
            //System.out.println("New Order:"+ord);
            
            //db.close();
            //PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(StrtoAPI, true)));
            //writer.println(ord.toString());
            //writer.close();
            
        } catch (Exception ex) {
        	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector sendorder", 
            		ex);
        	//return null;
        }
    }

    private static int getNSESeconds(long millis){
        Calendar NSEBegin = new GregorianCalendar(1980, 0, 1, 0, 0, 0);
        long diff = millis-NSEBegin.getTimeInMillis();
        long seconds = diff/1000L;
        return (int)seconds;
        
    }
    
    public synchronized void connect() throws Exception {
    	LoginRequest req = new LoginRequest();
		req.ClientCode = "SLS011";
		req.ConnType = 2;//1 LAN 2 INTERNET 3 VSAT
		req.LocalIP = "192.168.100.143";
		req.Password = password;
		req.PublicIP = "171.77.170.207" ;
		req.TwoFactor = 1;
		req.VersionNo = "1.0.0.85";
		LoggerUtil.getLogger().info(req.toString());
        sendRequest(req.getStruct());
        
        
        if(isLoggedin.get()==0){
        	sendRequest(req.getStruct());
        	long loopstarttime = System.currentTimeMillis();
        	while((System.currentTimeMillis()-loopstarttime)<1300){
        	
        	}
        }
    }
    
    private void sendRequest(byte[] request) {
    	LoggerUtil.getLogger().info("Sending Login Request");
        try {
            this.sendBytes(request, true);
        } catch (IOException ex) {
        	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector sendrequest", 
            		ex);
        }
    }

    
	public class parseData implements Runnable {
		byte[] data;

		public parseData(byte[] data) {
			this.data = data;
		}

		@Override
		public void run() {

			CompressionHeader compHeader = new CompressionHeader(data);
			// System.out.println("New Packet Recieved, Compressed Size:"+compHeader.MsgCompLen+"\tDecopressed Size:"+compHeader.MsgLen);

			byte[] message = new byte[data.length - 4];
			System.arraycopy(data, 4, message, 0, message.length);
			// <editor-fold desc="Decompression Logic">
			if (compHeader.MsgCompLen != compHeader.MsgLen) {// Decompression
																// required
				// System.out.println("Continue with decompression");

				Inflater inflater = new Inflater();
				inflater.setInput(message);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
						compHeader.MsgLen);
				byte[] buffer = new byte[1024];
				while (!inflater.finished()) {
					int count = 0;
					try {
						count = inflater.inflate(buffer);
					} catch (DataFormatException ex) {
						LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector DataFormatException", 
		                		ex);
					}
					outputStream.write(buffer, 0, count);
				}
				try {
					outputStream.close();
				} catch (IOException ex) {
					LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector exception in parsing data", 
	                		ex);
				}
				byte[] output = outputStream.toByteArray();
				inflater.end();
				message = output;
			}
			// </editor-fold>

			// Read the uncompressed (if was compressed) payload here
			byte[] headerBytes = new byte[4];
			System.arraycopy(message, 0, headerBytes, 0, 4);
			MessageHeader header = new MessageHeader(headerBytes);
			// System.out.println("Message Length:"+header.MsgLen);

			if (header.MsgCode != 251 && header.MsgCode != 252
					&& header.MsgCode != 44 && header.MsgCode != 4
					&& header.MsgCode != 6) {// To be ignored
				//System.out.println("-------------------------------");
				LoggerUtil.getLogger().info("Message Code:" + header.MsgCode);
			}
			// /System.out.println("Message Parsed, Message was compressed:"+(compHeader.MsgCompLen!=compHeader.MsgLen));

			if (header.MsgCode == CConstants.TransactionCode.Login_Request_Response) {

				LoggerUtil.getLogger().info("Message Type: Login Response");
				try {
					LoginResponse response = new LoginResponse(message);
					LoggerUtil.getLogger().info(response.toString());
					// try (PrintWriter writer = new PrintWriter(new
					// BufferedWriter(new FileWriter(APItoStr, true)))) {
					// writer.println(response.toString());
					// }
					if (response.Success == 1) {
						isLoggedin.set(1);;
						LoggerUtil.getLogger().info("Login succesful");
					} else {
						//isLoggedin = false;
						LoggerUtil.getLogger().info("Login Failed");
					}
				} catch (Exception ex) {
					LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector loginresponse", 
		            		ex);
				}
			} else 
				if (header.MsgCode == CConstants.TransactionCode.General_Erro_Response) {
				//try {
					LoggerUtil.getLogger().info("Message Type: General Error");
					// GeneralError error = new GeneralError(message);
				//} catch (Exception ex) {
				//	ex.printStackTrace();
				//}

			} else if (header.MsgCode == CConstants.TransactionCode.Order_request_response
					|| header.MsgCode == CConstants.TransactionCode.Order_Sent_To_Exchange
					|| header.MsgCode == CConstants.TransactionCode.Order_Broker_Received
					|| header.MsgCode == CConstants.TransactionCode.Order_Error
					|| header.MsgCode == CConstants.TransactionCode.Order_Modify_RMS_Processed
					|| header.MsgCode == CConstants.TransactionCode.Order_RMS_Processed
					|| header.MsgCode == CConstants.TransactionCode.Order_Cancel_RMS_Processed) {

				try {

					OrderConfirmation ordconf = new OrderConfirmation(message);
					setOrderConfirmation(reversetokensmap.get(ordconf.Token),ordconf);
					LoggerUtil.getLogger().info(ordconf+"");
					/*
					 * try (PrintWriter writer = new PrintWriter(new
					 * BufferedWriter(new FileWriter(APItoStr, true)))) {
					 * writer.println(ord_conf.toString()); } AlgoDB db = new
					 * AlgoDB(); db.setPooler(pooler); Order ord =
					 * db.getOrderByID(ord_conf.LocalOrderID); if(ord==null) {
					 * //TODO log failure to fetch response
					 * logger.info("API Received unknown Order Confirmation");
					 * db.close(); return; }
					 * 
					 * 
					 * ord.ctcl_uid = ord_conf.BrokerOrderID;
					 * 
					 * if(!ord_conf.Message.trim().isEmpty()){ ord.order_status
					 * = ord_conf.Message; }
					 * 
					 * 
					 * setChanged(); notifyObservers(ord);
					 * db.saveOrUpdateOrder(ord); orders.put(ord.id, ord);
					 * //frm.ord.updateUI();
					 * 
					 * db.close(); setChanged(); notifyObservers(ord_conf);
					 */
				} catch (Exception ex) {
					LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector orderconfirmation", 
		            		ex);
				}
			}

			else if (header.MsgCode == CConstants.TransactionCode.Exchange_Confirmation
					|| header.MsgCode == CConstants.TransactionCode.Exchange_Freeze
					|| header.MsgCode == CConstants.TransactionCode.Exchange_Killed
					|| header.MsgCode == CConstants.TransactionCode.Exchange_Reject) {
				try {

					item = new ExchangeConfirmation(
							message);
					LoggerUtil.getLogger().info(item+"");
					/*
					 * try (PrintWriter writer = new PrintWriter(new
					 * BufferedWriter(new FileWriter(APItoStr, true)))) {
					 * writer.println(item.toString()); } AlgoDB db = new
					 * AlgoDB(); db.setPooler(pooler); Order ord =
					 * db.getOrderByCTCLID(""+item.BrokerOrderId); if(ord==null)
					 * { //TODO log failure to fetch response
					 * logger.info("API Received unknown Exchange Confirmation"
					 * +item);
					 * 
					 * db.close();
					 * 
					 * } else { ord.ExchOrderID = item.ExchOrderID;
					 * if(!item.Message.trim().isEmpty()){ ord.order_status =
					 * item.Message; } db.saveOrUpdateOrder(ord); db.close();
					 * setChanged(); notifyObservers(ord);
					 * 
					 * }
					 */

				} catch (Exception ex) {
					LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector exchangeconfirmation", 
		            		ex);
				}

			}

			else if (header.MsgCode == CConstants.TransactionCode.Trade_Confirmation) {

				try {
					// Exchange Trade Confirmation
					TradeConfirmation trade = new TradeConfirmation(message);
					LoggerUtil.getLogger().info(trade+"");
					String symbol = reversetokensmap.get(trade.Token);
					setTradeConfirmation(symbol, trade);
					//counter=1;
					/*
					 * try (PrintWriter writer = new PrintWriter(new
					 * BufferedWriter(new FileWriter(APItoStr, true)))) {
					 * writer.println(trade.toString()); } AlgoDB db = new
					 * AlgoDB(); db.setPooler(pooler); Order ord =
					 * db.getOrderByCTCLID(trade.BrokerOrderID+"");
					 * if(ord==null){
					 * logger.info("No Order In System to match with the Trade"
					 * +trade.ExchnageTradeID);
					 * 
					 * db.close();
					 * 
					 * } else { Trade trd = new Trade(); trd.OrderType =
					 * ord.side; trd.ClientCode = trade.ClientCode;
					 * trd.ExchnageTradeID = trade.ExchnageTradeID;
					 * trd.ExchangeOrderID = ord.ExchOrderID; trd.LocalOrderID =
					 * ord.id; trd.TrdPrice = trade.TrdPrice; NFScripMaster
					 * token =
					 * db.getNFOToken(" WHERE scrip_id='"+ord.scrip_id+"'");
					 * trd.TrdQty= trade.TrdQty*token.lotSize; trd.scrip_id =
					 * ord.scrip_id; trd.strategy_id = ord.strategy_id;
					 * trd.timeframe = ord.timeFrame; trd.timestamp =
					 * trade.ExchangeTradeTime;
					 * 
					 * ord.filledqty += trd.TrdQty; setChanged();
					 * notifyObservers(trd); setChanged(); notifyObservers(ord);
					 * 
					 * db.updatePosition(trd.ClientCode,trd.scrip_id,trd.strategy_id
					 * ,trd.timeframe,trd.TrdQty,trd.OrderType,trd.TrdPrice);
					 * db.saveTrade(trd); db.saveOrUpdateOrder(ord); db.close();
					 * 
					 * //frm.ord.updateUI(); // frm.trd.updateUI();
					 * //frm.pos.updateUI(); }
					 */
				} catch (Exception ex) {
					LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector tradeconfirmation", 
		            		ex);
				}

			}
		}
	}
	
	private class ListenDirector implements Runnable {
		
		private ExecutorService executorService;
		@Override
		public void run() {
			long loopstarttime = System.currentTimeMillis();
	    	while((System.currentTimeMillis()-loopstarttime)<60000){
	    		executorService = Executors.newFixedThreadPool(1);
	    		executorService.execute(new Listen(loopstarttime));
	    		executorService.shutdown();
	    		boolean result = false;
	    		while(true){
	    			try {
	    				result = executorService.awaitTermination(7, TimeUnit.HOURS);
	    				LoggerUtil.getLogger().info("Listen Connection lost - "+result);
	    				break;
	    			} catch (Exception e) {
	    				LoggerUtil.getLogger().log(Level.SEVERE, "Listen Connection interrupted", e);
	    			}
	    		}
	    		
	    	}
		}
		
	}
    
    private class Listen implements Runnable{
    	
    	private long loopstarttime;
    	public Listen(long loopstarttime){
    		this.loopstarttime = loopstarttime;
    	}

        @Override
        public void run() {
             InputStream   dIn = null;
            try {
                dIn = echoSocket.getInputStream();
                  
                    short msg_length=0;
                    int index = 0;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    while((System.currentTimeMillis()-loopstarttime)<60000){
                        
                        byte[] fresh = new byte[10240];
                        int size  = dIn.read( fresh );
                        if(size<1)continue;
                        baos.write(fresh, 0, size);
                       // System.out.println(baos.size()+"\t"+index);
                        while (baos.size()>index) 
                        {
                        if(baos.size()<2)
                        {
                         
                            break;
                        }    
                        
                        msg_length = CConstants.getInt16(baos.toByteArray(), index);
                        if(msg_length==18687){
                        //    System.out.println("Irritating Heartbeat");
                            sendBytes(baos.toByteArray(), false);
                            baos.reset();
                            index = 0;
                            continue;
                        }
                        msg_length =(short) (msg_length+4);
                        if(baos.size()-index<msg_length){
                          //System.out.println("More to come:"+(baos.size()-index)+"/"+msg_length);
                            break;
                        }
                       // System.out.println("Message length:"+msg_length+"\tStream:"+baos.size()+"\tIndex:"+index    );
                        byte[] data = new byte[msg_length];
                        System.arraycopy(baos.toByteArray(), index, data, 0, data.length);
                        new Thread((new parseData(data))).start();
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
            	LoggerUtil.getLogger().log(Level.SEVERE, "BPConnector Listen", 
	            		ex);
            } 
                        
            LoggerUtil.getLogger().info("Listen thread exiting");    
                    
                    
            
                
                }
            }
 
    private synchronized  void sendBytes(byte[] request, boolean compress) throws IOException{
        if(!compress){
        DataOutputStream outToServer = new DataOutputStream(echoSocket.getOutputStream());
        outToServer.write(request);
        outToServer.flush();
        return;
        }
        byte[] message = new byte[request.length-4];
        System.arraycopy(request, 4, message, 0, message.length);
        Deflater deflater = new Deflater(); 
        deflater.setInput(message);  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(message.length);   
       
        deflater.finish();  
        byte[] buffer = new byte[1024];   
        while (!deflater.finished()) {  
         int count = deflater.deflate(buffer); // returns the generated code... index  
         outputStream.write(buffer, 0, count);   
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();

        byte[] newStruct = new byte[output.length+4];
        System.arraycopy(request, 0, newStruct, 0, 4);
        System.arraycopy(output, 0, newStruct, 4, output.length);
        CConstants.setShort((short)output.length, newStruct, 0);
        
        DataOutputStream outToServer = new DataOutputStream(echoSocket.getOutputStream());
        outToServer.write(newStruct);
        outToServer.flush();
    }
}
