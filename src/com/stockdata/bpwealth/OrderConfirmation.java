
package com.stockdata.bpwealth;

import com.stockdata.bpwealth.core.CConstants;


public class OrderConfirmation {
    public short RequestType;
    public int Segment;
    public short OrderType;
    public  int Token;
    public String ClientCode;
    public String OrderRequesterCode;
    public short LocalOrderID;
    public long BrokerOrderID;//This is CTCL ID
    public int BrokerOrderTime;
    public short Reserved1;
    public short Reserved2;
    public short Status=-1;
    public int Reserved3;
    public int Qty;
    public int MarketLot;
    public int Price;
    public String Message;
    public String Reserved4;
    public String RouteID;
    public String Reserved5;
    public long NNFField;
    public int Reserved6;
    
    public OrderConfirmation(byte[] bytes) throws Exception//Including Messsage Header but not comp header
    {
        
        RequestType = CConstants.getInt16(bytes, 4);
        Segment = CConstants.getInt32(bytes, 6);
        OrderType = CConstants.getInt16(bytes, 10);
        Token = CConstants.getInt32(bytes, 12);
        ClientCode = CConstants.getString(bytes, 16, 10);
        OrderRequesterCode = CConstants.getString(bytes, 26, 10);
        LocalOrderID = CConstants.getInt16(bytes, 36);
        BrokerOrderID = CConstants.getLong(bytes, 38);
        BrokerOrderTime = CConstants.getInt32(bytes, 46);
        Reserved1 = CConstants.getInt16(bytes, 50);
        Reserved2 = CConstants.getInt16(bytes, 52);
        Status = CConstants.getInt16(bytes, 54);
        Reserved3 = CConstants.getInt32(bytes, 56);
        Qty = CConstants.getInt32(bytes, 60);
        MarketLot = CConstants.getInt32(bytes, 64);
        Price = CConstants.getInt32(bytes, 68);
        Message = CConstants.getString(bytes, 72, 100);
        Reserved4 = CConstants.getString(bytes, 172, 22);
        RouteID = CConstants.getString(bytes, 194, 10);
        Reserved5 = CConstants.getString(bytes, 204, 1);
        NNFField = CConstants.getLong(bytes, 205);
        Reserved6 = CConstants.getInt32(bytes, 213);
        
    }
    
    @Override
    public String toString(){
        String str= "";
        str+=("Order Confirmation Details:");
        str+=("\tStatus:"+Status);
        str+=("\tToken:"+Token);
        str+=("\tPrice:"+Price);
        str+=("\tQty:"+Qty);
        str+=("\tRouteID:"+RouteID);
        str+=("\tBrokerOrderID:"+BrokerOrderID);
        str+=("\tLocalOrderID:"+LocalOrderID);
        str+=("\tnnf fIELD:"+NNFField);
        str+=("\tMessage:"+Message);
        return str;
    }
}
