
package com.stockdata.bpwealth;

import com.stockdata.bpwealth.core.CConstants;



public class TradeConfirmation {
    public short RequestType;
    public int Segment;
    public String OrderRequesterCode;
    public String Reserved1;
    public int OrderUpdateTime;
    public String AtMarket;
    public String WithSL;
    public String Reserved2;
    public short ProdType;
    public String Reserved3;
    public String PreOpen;
    public String Reserved4;
    public String RouteID;
    public long NNFField;
    public long BrokerReqID;
    public int BrokerReqTime;
    public String Message;
    public String Reserved5;
    public int TrdPrice;
    public int Token;
    
    public TradeConfirmation(byte[] bytes) throws Exception{
        RequestType = CConstants.getInt16(bytes, 4);
        Segment = CConstants.getInt32(bytes, 6);
        //OrderType = CConstants.getInt16(bytes, 10);
        Token = CConstants.getInt32(bytes, 12);
        //OrderQty = CConstants.getInt32(bytes, 16);
        //TrdQty = CConstants.getInt32(bytes, 20);
        //PendQty = CConstants.getInt32(bytes, 24);
        TrdPrice = CConstants.getInt32(bytes, 28);
        //ClientCode = CConstants.getString(bytes, 32, 10);
        OrderRequesterCode = CConstants.getString(bytes, 42, 10);
        Reserved1 = CConstants.getString(bytes, 52, 2);
        //BrokerOrderID = CConstants.getLong(bytes, 54);
        //ExchangeOrderID = CConstants.getLong(bytes, 62);
        //ExchnageTradeID = CConstants.getInt32(bytes, 70);
        //ExchangeTradeTime = CConstants.getInt32(bytes, 74);
        OrderUpdateTime = CConstants.getInt32(bytes, 78);
        AtMarket = CConstants.getString(bytes, 82, 1);
        WithSL = CConstants.getString(bytes, 83, 1);
        Reserved2 = CConstants.getString(bytes, 84, 4);
        ProdType = CConstants.getInt16(bytes, 88);
        Reserved3 = CConstants.getString(bytes, 90, 60);
        PreOpen = CConstants.getString(bytes, 150, 1);
        Reserved4 = CConstants.getString(bytes, 151, 4);
        RouteID = CConstants.getString(bytes, 155, 10);
        NNFField = CConstants.getLong(bytes, 165);
        BrokerReqID = CConstants.getLong(bytes, 173);
        //Status = CConstants.getString(bytes, 181, 20);
        BrokerReqTime = CConstants.getInt32(bytes, 201);
        Message = CConstants.getString(bytes, 205, 100);
        Reserved5 = CConstants.getString(bytes, 305, 1);
        
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TradeConfirmation [RequestType=" + RequestType + ", Segment="
				+ Segment + ", OrderRequesterCode=" + OrderRequesterCode
				+ ", Reserved1=" + Reserved1 + ", OrderUpdateTime="
				+ OrderUpdateTime + ", AtMarket=" + AtMarket + ", WithSL="
				+ WithSL + ", Reserved2=" + Reserved2 + ", ProdType="
				+ ProdType + ", Reserved3=" + Reserved3 + ", PreOpen="
				+ PreOpen + ", Reserved4=" + Reserved4 + ", RouteID=" + RouteID
				+ ", NNFField=" + NNFField + ", BrokerReqID=" + BrokerReqID
				+ ", BrokerReqTime=" + BrokerReqTime + ", Message=" + Message
				+ ", Reserved5=" + Reserved5 + ", TrdPrice=" + TrdPrice + "]";
	}

	
    
    
}
