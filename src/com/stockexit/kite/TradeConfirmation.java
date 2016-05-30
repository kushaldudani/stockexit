
package com.stockexit.kite;




public class TradeConfirmation {
	public double lasttradedprice;
	   
    public String stoplossrouteid;
    public String stoplossorderid;
    
    public TradeConfirmation(double TrdPrice, String stoplossrouteid, String stoplossorderid){
    	this.lasttradedprice = TrdPrice;
    	this.stoplossrouteid = stoplossrouteid;
    	this.stoplossorderid = stoplossorderid;
    }
    
    public TradeConfirmation(double TrdPrice){
    	this.lasttradedprice = TrdPrice;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TradeConfirmation [lasttradedprice=" + lasttradedprice
				+ ", stoplossrouteid=" + stoplossrouteid + ", stoplossorderid="
				+ stoplossorderid + "]";
	}
    
}
