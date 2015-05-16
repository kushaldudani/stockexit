
package com.stockdata.bpwealth;

import com.stockdata.bpwealth.core.CConstants;


public class ExchangeConfirmation {
    public short RequestType;
    public int Segment;
    public short OrderType;
    public int Token;
    public int Reserved1;
    public int Qty;
    public int DisclosedQty;
    public int MarketLot;
    public int Price;
    public String ClientCode;
    public String OrderRequesterCode;
    public String Reserved2;
    public long BrokerOrderId;
    public long ExchOrderID;
    public int ExchOrderTime;
    public short ExchReplyCode;
    public int ExchErrorCode;
    public int Status;
    public String Message;
    public String Reserved3;
    public String RouteID;
    public String Reserved4;
    public int ValidityDate;
    public int TriggerPrice;
    public long NNFField;
    public String Reserved5;
   public int TradedQuantity;
    public int FilledQuantity;//This is a manual field which gets updated on trade confirmations
    public ExchangeConfirmation(byte[] bytes) throws Exception{
        RequestType = CConstants.getInt16(bytes, 4);
        Segment = CConstants.getInt32(bytes, 6);
        OrderType = CConstants.getInt16(bytes, 10);
        Token = CConstants.getInt32(bytes, 12);
        Reserved1 = CConstants.getInt32(bytes, 16);
        Qty = CConstants.getInt32(bytes, 20);
        DisclosedQty = CConstants.getInt32(bytes, 24);
        MarketLot = CConstants.getInt32(bytes, 28);
        Price = CConstants.getInt32(bytes, 32);
        ClientCode = CConstants.getString(bytes, 36, 10);
        OrderRequesterCode = CConstants.getString(bytes, 46, 10);
        Reserved2 = CConstants.getString(bytes, 56, 2);
        BrokerOrderId = CConstants.getLong(bytes, 58);
        ExchOrderID = CConstants.getLong(bytes, 66);
        ExchOrderTime = CConstants.getInt32(bytes, 74);
        ExchReplyCode = CConstants.getInt16(bytes, 78);
        ExchErrorCode = CConstants.getInt32(bytes, 80);
        Status = CConstants.getInt16(bytes, 84);
        Message = CConstants.getString(bytes, 86, 100);
        Reserved3 = CConstants.getString(bytes, 186, 44);
        RouteID = CConstants.getString(bytes, 230, 20);
        Reserved4 = CConstants.getString(bytes, 250, 2);
        ValidityDate = CConstants.getInt32(bytes, 252);
        TriggerPrice = CConstants.getInt32(bytes, 256);
        NNFField = CConstants.getLong(bytes,260);
        //Reserved5 = CConstants.getString(bytes, 268,1);
        //TradedQuantity = CConstants.getInt32(bytes, 269);
        
    }
    
    @Override
    public String toString(){
        String str = "";
        str+=("\tExchange Confirmation");
        str+=("\tMessage:"+Message);
        str+=("\tStatus:"+Status);
        str+=("\tRouteID:"+RouteID);
        str+=("\tNNFField:"+NNFField);
        str+=("\tQty:"+Qty);
        str+=("\tPrice:"+Price);
        str+=("\tTriggerPrice:"+TriggerPrice);
        str+=("\tToken:"+Token);
        str+=("\tClientcode:"+ClientCode);
        str+=("\tReply Code:"+ExchReplyCode);
        str+=("\tErrorCode:"+ExchErrorCode);
        str+=("\tBroker Order id:"+BrokerOrderId);
        str+=("\tExchange Order id:"+ExchOrderID);
        str+=("\tExchange Time:"+ExchOrderTime);
        return str;
    }
}
