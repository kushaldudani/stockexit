
package com.stockexit.zerodha;

import com.stockdata.bpwealth.core.CConstants;



public class TradeConfirmation {
	public String Exchange;
    public String Symbol;
    public String piorderid;
    public String clientorderid;
    public String strategyname;
    public short orderside;
    public int initialqty;
    public int disclosedqty;
    public int remainingqty;
    public double limitprice;
    public double triggerprice;
    public double tradedvalue;
    public double lasttradedprice;
    public double averagetradedprice;
    public int tradedqty;
    public int lasttradedqty;
    public String ordertype;
    public String producttype;
    public String clientcode;
    public String validity;
    public int orderstatus;
    public int entrytime;
    public int exectime;
	
	
    
    ZerodhaHeader msgHeader;
    
    public String stoplossrouteid;
    public String stoplossorderid;
    
    public TradeConfirmation(double TrdPrice, String stoplossrouteid, String stoplossorderid){
    	this.lasttradedprice = TrdPrice;
    	this.stoplossrouteid = stoplossrouteid;
    	this.stoplossorderid = stoplossorderid;
    }
    
    public TradeConfirmation(byte[] bytes) throws Exception{
    	byte[] headerBytes = new byte[14];
		System.arraycopy(bytes, 0, headerBytes, 0, 14);
		msgHeader = new ZerodhaHeader(headerBytes);
		Exchange = CConstants.getString(bytes, 14, 10);
    	Symbol = CConstants.getString(bytes, 24, 64);
    	piorderid = CConstants.getString(bytes, 88, 20);
    	clientorderid = CConstants.getString(bytes, 108, 10);
    	strategyname = CConstants.getString(bytes, 118, 10);
    	orderside = CConstants.getInt16(bytes, 128);
    	initialqty = CConstants.getInt32(bytes, 130);
    	disclosedqty = CConstants.getInt32(bytes, 134);
    	remainingqty = CConstants.getInt32(bytes, 138);
    	limitprice = CConstants.getDouble(bytes, 142);
    	triggerprice = CConstants.getDouble(bytes, 150);
    	tradedvalue = CConstants.getDouble(bytes, 158);
    	lasttradedprice = CConstants.getDouble(bytes, 166);
    	averagetradedprice = CConstants.getDouble(bytes, 174);
    	tradedqty = CConstants.getInt32(bytes, 182);
    	lasttradedqty = CConstants.getInt32(bytes, 186);
    	ordertype = CConstants.getString(bytes, 190, 12);
    	producttype = CConstants.getString(bytes, 202, 12);
    	clientcode = CConstants.getString(bytes, 214, 12);
    	validity = CConstants.getString(bytes, 226, 5);
    	orderstatus = CConstants.getInt32(bytes, 231);
    	entrytime = CConstants.getInt32(bytes, 235);
    	exectime = CConstants.getInt32(bytes, 239);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TradeConfirmation [Exchange=" + Exchange + ", Symbol=" + Symbol
				+ ", piorderid=" + piorderid + ", clientorderid="
				+ clientorderid + ", strategyname=" + strategyname
				+ ", orderside=" + orderside + ", initialqty=" + initialqty
				+ ", disclosedqty=" + disclosedqty + ", remainingqty="
				+ remainingqty + ", limitprice=" + limitprice
				+ ", triggerprice=" + triggerprice + ", tradedvalue="
				+ tradedvalue + ", lasttradedprice=" + lasttradedprice
				+ ", averagetradedprice=" + averagetradedprice + ", tradedqty="
				+ tradedqty + ", lasttradedqty=" + lasttradedqty
				+ ", ordertype=" + ordertype + ", producttype=" + producttype
				+ ", clientcode=" + clientcode + ", validity=" + validity
				+ ", orderstatus=" + orderstatus + ", entrytime=" + entrytime
				+ ", exectime=" + exectime + ", msgHeader=" + msgHeader
				+ ", stoplossrouteid=" + stoplossrouteid + ", stoplossorderid="
				+ stoplossorderid + "]";
	}

	

	
    
    
}
