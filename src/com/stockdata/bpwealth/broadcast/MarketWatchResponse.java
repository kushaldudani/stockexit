
package com.stockdata.bpwealth.broadcast;


import java.util.Calendar;
import java.util.GregorianCalendar;

import com.stockdata.bpwealth.core.CConstants;


public class MarketWatchResponse {
    public int Token;
    public int Segment;
    public int Last_Rate;
    public int Last_Quantity;
    public int Total_Quantity;
    public int High;
    public int Low;
    public int Open_Rate;
    public int Previous_Close;
    public int Average_Rate;
    public int Best_Bid_Qty;
    public int Best_Bid_Rate;
    public int Best_Ask_Quantity;
    public int Best_Ask_Rate;
    public int Total_Bid_Qty;
    public int Total_Ask_Qty;
    public int Time;
    public int Open_Interest;
    public int Change_In_OI;
    public int Day_High_OI;
    public int Day_Low_OI;
    public int Upper_Circuit;
    public int Lower_Circuit;
    public int Highest_Ever;
    public int Lowest_Ever;
    public MarketWatchResponse(){
        
    }
    public MarketWatchResponse(byte[] data){
        Token = CConstants.getInt32(data, 4);
        Segment = CConstants.getInt32(data, 8);
        Last_Rate = CConstants.getInt32(data, 12);
        
        Last_Quantity = CConstants.getInt32(data, 16);
        Total_Quantity = CConstants.getInt32(data, 20);
        High = CConstants.getInt32(data, 24);
        Low = CConstants.getInt32(data, 28);
        Open_Rate = CConstants.getInt32(data, 32);
        Previous_Close = CConstants.getInt32(data, 36);
        Average_Rate = CConstants.getInt32(data, 40);
        Best_Bid_Qty = CConstants.getInt32(data, 44);
        Best_Bid_Rate = CConstants.getInt32(data, 48);
        Best_Ask_Quantity = CConstants.getInt32(data, 52);
        Best_Ask_Rate = CConstants.getInt32(data, 56);
        Total_Bid_Qty = CConstants.getInt32(data, 60);
        Total_Ask_Qty = CConstants.getInt32(data, 64);
        Time = CConstants.getInt32(data, 68);
        Open_Interest = CConstants.getInt32(data, 72);
        Change_In_OI = CConstants.getInt32(data, 76);
        Day_High_OI = CConstants.getInt32(data, 80);
        Day_Low_OI = CConstants.getInt32(data, 84);
        Upper_Circuit = CConstants.getInt32(data, 88);
        Lower_Circuit = CConstants.getInt32(data, 92);
        Highest_Ever = CConstants.getInt32(data, 96);
        Lowest_Ever = CConstants.getInt32(data, 100);
        //System.out.println(Token+"\t"+Best_Bid_Rate+"\t"+Best_Bid_Qty+"\t"+Upper_Circuit+"\t"+Time);
        
    }
    
    public float getBestBid(){
        return (float)(this.Best_Bid_Rate/(float)100.00);
    }
    public float getBestAsk(){
        return (float)(this.Best_Ask_Rate/(float)100.00);
    }
    public float getDayHigh(){
        return (float)(this.High/(float)100.00);
    }
    public float getDayLow(){
        return (float)(this.Low/(float)100.00);
    }
    
    public float getLTP(){
        return (float)(this.Last_Rate/(float)100.00);
    }
    public float getOpen(){
        return (float)(this.Open_Rate/(float)100.00);
    }
    
    public long getVolume(){
    return this.Total_Quantity;
    }
      
    public long getLTDate(){
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.YEAR,1980);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.setTimeInMillis(cal.getTimeInMillis()+((long)Time*1000L));
        return cal.getTimeInMillis();
    }
    
    
    public long getMillis(){
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.YEAR,1980);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.roll(Calendar.SECOND, Time);
        return cal.getTimeInMillis();
    }
	public int getToken() {
		return Token;
	}

    

    
}
