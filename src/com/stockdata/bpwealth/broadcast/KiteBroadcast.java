package com.stockdata.bpwealth.broadcast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;


@ClientEndpoint
public class KiteBroadcast {

	static Map<Integer, String> reversetokensmap = null;
	
	@SuppressWarnings("unchecked")
	public static void mainrun(List<Integer> tokens, final Map<String,List<SynQueue<TickData>>> queuemap) throws Exception {
		reversetokensmap = StockExitUtil.buildReverseTokensMap();
			String accesstoken = readAccessToken();
			final KiteBroadcast clientEndPoint = new KiteBroadcast(new URI("wss://websocket.kite.trade/?api_key=yoke3sr4lgvh8aij&user_id=DA8326&public_token="+accesstoken),tokens,queuemap);
            clientEndPoint.addMessageHandler(new KiteBroadcast.MessageHandler() {
                public void handleMessage(byte[] message) {
                    List<DepthResponse> depths = DepthResponse.parse(message);
                    for(DepthResponse depth : depths){
                    	Calendar cal = Calendar.getInstance();
                    	TickData tdt = new TickData();
                    	String sss = reversetokensmap.get(depth.instrument_token);
                    	tdt.setSymbol(sss+"-"+cal.getTimeInMillis());
                    	tdt.setBidprice(depth.getLTP());
                    	tdt.setHigh(depth.getDayHigh());
                    	tdt.setLow(depth.getDayLow());
                    	tdt.setBidqty(depth.ltq);
                    	tdt.setAskprice(depth.getLTP());
                    	tdt.setAskqty(depth.ltq);
                    	tdt.setLastprice(depth.getLTP());
                    	tdt.setLastqty(depth.ltq);
                    	
                    	List<SynQueue<TickData>> allqueues = queuemap.get(sss);
                    	for(SynQueue<TickData> quu : allqueues){
                    		quu.enqueue(tdt);
                    	}
                    }
                }
            });
            JSONObject dataObject = new JSONObject();
            dataObject.put("a", "mode");
            JSONArray columns = new JSONArray();
            columns.add("quote");
            JSONArray rows = new JSONArray();
            for(Integer token : tokens){
            	rows.add(token);
            }
            columns.add(rows);
            dataObject.put("v", columns);
            
            String reqmessage = dataObject.toJSONString();
            LoggerUtil.getLogger().info(reqmessage);
            clientEndPoint.sendMessage(reqmessage);
	}

	    Session userSession = null;
	    private MessageHandler messageHandler;
	    private final List<Integer> tokens;
	    private final Map<String,List<SynQueue<TickData>>> queuemap;

	    public KiteBroadcast(URI endpointURI, List<Integer> tokens, Map<String,List<SynQueue<TickData>>> queuemap) throws Exception {
	    		this.tokens = tokens;
	    		this.queuemap = queuemap;
	            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	            container.setAsyncSendTimeout(10*100);
	            container.connectToServer(this, endpointURI);
	    }

	    
	    
	    
	    /**
	     * Callback hook for Connection open events.
	     *
	     * @param userSession the userSession which is opened.
	     */
	    @OnOpen
	    public void onOpen(Session userSession) {
	        LoggerUtil.getLogger().info("opening websocket");
	        this.userSession = userSession;
	    }

	    /**
	     * Callback hook for Connection close events.
	     *
	     * @param userSession the userSession which is getting closed.
	     * @param reason the reason for connection close
	     */
	    @OnClose
	    public void onClose(Session userSession, CloseReason reason) {
	    	LoggerUtil.getLogger().info("closing websocket");
	        this.userSession = null;
	        boolean result = false;
	        while(result == false){
	        	try{
	        		mainrun(tokens, queuemap);
	        		result = true;
	        	}catch(Exception e){
	        		LoggerUtil.getLogger().log(Level.SEVERE, "From onclose method Broadcast cannot request to listen to data");
	        	}
	        	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
	        }
	    }

	    /**
	     * Callback hook for Message Events. This method will be invoked when a client send a message.
	     *
	     * @param message The text message
	     */
	    @OnMessage
	    public void onMessage(byte[] message) {
	        if (this.messageHandler != null) {
	            this.messageHandler.handleMessage(message);
	        }
	    }

	    /**
	     * register message handler
	     *
	     * @param msgHandler
	     */
	    public void addMessageHandler(MessageHandler msgHandler) {
	        this.messageHandler = msgHandler;
	    }

	    /**
	     * Send a message.
	     *
	     * @param message
	     */
	    public void sendMessage(String message) {
	        this.userSession.getAsyncRemote().sendText(message);
	    }

	    /**
	     * Message handler.
	     *
	     * @author Jiji_Sasidharan
	     */
	    public static interface MessageHandler {

	        public void handleMessage(byte[] message);
	    }
	
	    
	    
	    public static String readAccessToken(){
			InputStreamReader is = null;
			BufferedReader br = null;
			String accesstoken = null;
			try {
				is = new InputStreamReader(new FileInputStream(new 
						File("/Users/kushd/nse/access")));
				br =  new BufferedReader(is);
				
				
				String line; 
				while ((line = br.readLine()) != null) {
					accesstoken = line.trim();
				}
			} catch (Exception e) {
				LoggerUtil.getLogger().log(Level.SEVERE, "Access token read failed", e);
			}finally{
				 try {
					br.close();
					is.close();
					
				} catch (IOException e) {}
			}
			return accesstoken;
		}
}
