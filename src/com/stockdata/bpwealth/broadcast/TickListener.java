package com.stockdata.bpwealth.broadcast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.Inflater;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.CompressionHeader;
import com.stockdata.bpwealth.core.MessageHeader;
import com.stockexit.util.LoggerUtil;
import com.stockexit.util.StockExitUtil;
import com.stockexit.util.SynQueue;

public class TickListener implements Runnable{
	
	Socket echoSocket;
	Map<String,List<SynQueue<TickData>>> queuemap;
	Map<Integer, String> reversetokensmap = null;
	static double niftyuppercent = 0;
	
	public TickListener(Socket echoSocket, Map<String,List<SynQueue<TickData>>> queuemap) {
		this.echoSocket = echoSocket;
		this.reversetokensmap = StockExitUtil.buildReverseTokensMap();
		this.queuemap = queuemap;
		//dbmanager = new TickDataManager();
		//dbmanager.openSession();
	}

    @Override
    public void run() {
         InputStream   dIn = null;
        try {
            dIn = echoSocket.getInputStream();
            short msg_length=0;
            int index = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            
            Calendar cal = Calendar.getInstance(); checklasttime=cal.getTimeInMillis();
   		 	String hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
   		 	String minute = String.format("%02d",cal.get(Calendar.MINUTE));
   		 	String lasttime = hour+":"+minute;
            while (lasttime.compareTo("09:15") >= 0 && lasttime.compareTo("15:30") < 0) {
               cal = Calendar.getInstance();
       		   hour = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
       		   minute = String.format("%02d",cal.get(Calendar.MINUTE));
       		   lasttime = hour+":"+minute;
       		   if((cal.getTimeInMillis()-checklasttime) > 15000){
       			   LoggerUtil.getLogger().log(Level.SEVERE, "TickListener socket no more listeneing");
       			   break;
       		   }
               byte[] fresh = new byte[10240];
               int size  = dIn.read(fresh);
               if(size <= 0){ LoggerUtil.getLogger().info("Size read - "+size); continue;}
               baos.write(fresh, 0, size);
               while (baos.size()>index+2) {
            	   msg_length = CConstants.getInt16(baos.toByteArray(), index);
                   msg_length = (short) (msg_length+4);
                   if(baos.size()-index<msg_length){
                	   //  System.out.println("More to come:"+(baos.size()-index)+"/"+msg_length);
                       break;
                   }
                   //System.out.println("Message length:"+msg_length+"\tStream:"+baos.size()+"\tIndex:"+index    );
                   byte[] data = new byte[msg_length];
                   System.arraycopy(baos.toByteArray(), index, data, 0, data.length);
                   parseData(data);
                   index +=msg_length;
               }
               //System.out.println("Continuing to listen to socket at index:"+index);
               if(baos.size()>msg_length){//There are bytes left after
                    //System.out.println("Bytes are left to be carried forward:"+(baos.size()-index));
                    byte[] remnant = new byte[baos.size()-index];
                    ///    System.out.println("Remnant will receive bytes from:"+(baos.size()-index));
                    System.arraycopy(baos.toByteArray(), index, remnant, 0, remnant.length);    
                    baos.reset();
                    baos.write(remnant);
               }else if(baos.size()==msg_length){
                    baos.reset();
               }
               index = 0;
            }    
        } catch (Exception ex) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Error listening to Tick Data", ex);
        } finally {
            try {
                dIn.close();
            } catch (IOException ex) {}
           // dbmanager.closeSession();
        }
        
   }
    
    
    private void parseData(byte[] data){
    	CompressionHeader compHeader = new CompressionHeader(data);
        //System.out.println("New Packet Recieved, Compressed Size:"+compHeader.MsgCompLen+"\tDecopressed Size:"+compHeader.MsgLen);
        
        byte[] message = new byte[data.length-4];
        System.arraycopy(data, 4, message, 0, message.length);
         //<editor-fold desc="Decompression Logic">
        if(compHeader.MsgCompLen!=compHeader.MsgLen){//Decompression required
      	  try {
      		  Inflater inflater = new Inflater();
      		  inflater.setInput(message);
      		  ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compHeader.MsgLen);
      		  byte[] buffer = new byte[1024];  
      		  while (!inflater.finished()) {  
      			  int count = 0;  
      			  count = inflater.inflate(buffer);
      			  outputStream.write(buffer, 0, count);  
      		  }  
                outputStream.close();
           
                byte[] output = outputStream.toByteArray();  
                inflater.end();
                message = output;
      	  } catch (Exception ex) {
                LoggerUtil.getLogger().log(Level.SEVERE, "Decompressing in parsetick data failed", ex);
            }
        }
        //</editor-fold>
        
        //Read the uncompressed (if was compressed) payload here
         byte[] headerBytes = new byte[4];
         System.arraycopy(message, 0, headerBytes, 0, 4);
         MessageHeader header = new MessageHeader(headerBytes);
         //System.out.println("Message Length:"+header.MsgLen);
         //System.out.println("Message Code:"+header.MsgCode); 
         //System.out.println("Message Parsed, Message was compressed:"+(compHeader.MsgCompLen!=compHeader.MsgLen));
        if(header.MsgCode==CConstants.TransactionCode.Broadcast_Request_Response){
           MarketWatchResponse watch = new MarketWatchResponse(message);
           writeToDb(watch);
           //System.out.println(sym+" "+watch.getBestBid()+" "+watch.getDayHigh()+" "+watch.getDayLow());
        }
    }
    
    private long checklasttime;
    
    private void writeToDb(MarketWatchResponse watch){
    	Calendar cal = Calendar.getInstance();
    	TickData tdt = new TickData();
    	String sss = reversetokensmap.get(watch.getToken());
    	tdt.setSymbol(sss+"-"+cal.getTimeInMillis());
    	tdt.setBidprice(watch.getBestBid());
    	tdt.setHigh(watch.getDayHigh());
    	tdt.setLow(watch.getDayLow());
    	tdt.setBidqty(watch.Best_Bid_Qty);
    	tdt.setAskprice(watch.getBestAsk());
    	tdt.setAskqty(watch.Best_Ask_Quantity);
    	tdt.setLastprice(watch.getLTP());
    	tdt.setLastqty(watch.Last_Quantity);
    	
    	List<SynQueue<TickData>> allqueues = queuemap.get(sss);
    	for(SynQueue<TickData> quu : allqueues){
    		quu.enqueue(tdt);
    	}
    	if(sss.equals("NIFTY") && watch.getOpen() > 0){
    		double niftyup = (((watch.getLTP()-watch.getOpen())/watch.getOpen())*100);
    		setNiftyUppercent(niftyup);
    	}
    	checklasttime = cal.getTimeInMillis();
    }
    
    private static synchronized void setNiftyUppercent(double niftyup){
    	niftyuppercent = niftyup;
    }
    
    public static synchronized double getNiftyUppercent(){
    	return niftyuppercent;
    }
    
}