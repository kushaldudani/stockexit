
package com.stockexit.net;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TickData")
public class TickData implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4193595031950155741L;

	@Id
	@Column(name = "Symbol")
    private String symbol;
    
	@Column(name = "Bidprice")
    private double bidprice;
    
    @Column(name = "High")
    private double high;
	
    @Column(name = "Low")
    private double low;
    
    @Column(name = "Bidqty")
    private double bidqty;
    
    @Column(name = "Askprice")
    private double askprice;
	
    @Column(name = "Askqty")
    private double askqty;
    
    @Column(name = "Lastprice")
    private double lastprice;
    
    @Column(name = "Lastqty")
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


	
	
}