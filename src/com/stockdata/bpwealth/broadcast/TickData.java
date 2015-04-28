
package com.stockdata.bpwealth.broadcast;




public class TickData  {


	

	
    private String symbol;
    
	
    private double bidprice;
    
    
    private double high;
	
    
    private double low;
    
    
    private double bidqty;
    
    
    private double askprice;
	
    
    private double askqty;
    
    
    private double lastprice;
    
    
    private double lastqty;

   	
	

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}




	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}




	/**
	 * @return the bidprice
	 */
	public double getBidprice() {
		return bidprice;
	}




	/**
	 * @param bidprice the bidprice to set
	 */
	public void setBidprice(double bidprice) {
		this.bidprice = bidprice;
	}




	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}




	/**
	 * @param high the high to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}




	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}




	/**
	 * @param low the low to set
	 */
	public void setLow(double low) {
		this.low = low;
	}




	/**
	 * @return the bidqty
	 */
	public double getBidqty() {
		return bidqty;
	}




	/**
	 * @param bidqty the bidqty to set
	 */
	public void setBidqty(double bidqty) {
		this.bidqty = bidqty;
	}




	/**
	 * @return the askprice
	 */
	public double getAskprice() {
		return askprice;
	}




	/**
	 * @param askprice the askprice to set
	 */
	public void setAskprice(double askprice) {
		this.askprice = askprice;
	}




	/**
	 * @return the askqty
	 */
	public double getAskqty() {
		return askqty;
	}




	/**
	 * @param askqty the askqty to set
	 */
	public void setAskqty(double askqty) {
		this.askqty = askqty;
	}




	




	/**
	 * @return the lastprice
	 */
	public double getLastprice() {
		return lastprice;
	}




	/**
	 * @param lastprice the lastprice to set
	 */
	public void setLastprice(double lastprice) {
		this.lastprice = lastprice;
	}




	/**
	 * @return the lastqty
	 */
	public double getLastqty() {
		return lastqty;
	}




	/**
	 * @param lastqty the lastqty to set
	 */
	public void setLastqty(double lastqty) {
		this.lastqty = lastqty;
	}




	public TickData() {
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TickData [symbol=" + symbol + ", bidprice=" + bidprice
				+ ", high=" + high + ", low=" + low + ", bidqty=" + bidqty
				+ ", askprice=" + askprice + ", askqty=" + askqty
				+ ", lastprice=" + lastprice + ", lastqty=" + lastqty + "]";
	}


	
	
}