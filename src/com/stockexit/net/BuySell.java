package com.stockexit.net;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BuySell")
public class BuySell implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7959616550810686827L;

	@Id
	@Column(name = "Symbol")
    private String symbol;
    
	@Column(name = "Enterprice")
    private double enterprice;
    
    @Column(name = "Exitprice")
    private double exitprice;
	
    @Column(name = "Profit")
    private double profit;
    
    @Column(name = "Exited")
    private boolean exited;
    
    @Column(name = "Hasbudget")
    private boolean hasbudget;

   	
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
	 * @return the enterprice
	 */
	public double getEnterprice() {
		return enterprice;
	}


	/**
	 * @param enterprice the enterprice to set
	 */
	public void setEnterprice(double enterprice) {
		this.enterprice = enterprice;
	}


	/**
	 * @return the exitprice
	 */
	public double getExitprice() {
		return exitprice;
	}


	/**
	 * @param exitprice the exitprice to set
	 */
	public void setExitprice(double exitprice) {
		this.exitprice = exitprice;
	}


	/**
	 * @return the profit
	 */
	public double getProfit() {
		return profit;
	}


	/**
	 * @param profit the profit to set
	 */
	public void setProfit(double profit) {
		this.profit = profit;
	}


	/**
	 * @return the exited
	 */
	public boolean isExited() {
		return exited;
	}


	/**
	 * @param exited the exited to set
	 */
	public void setExited(boolean exited) {
		this.exited = exited;
	}


	/**
	 * @return the hasbudget
	 */
	public boolean isHasbudget() {
		return hasbudget;
	}


	/**
	 * @param hasbudget the hasbudget to set
	 */
	public void setHasbudget(boolean hasbudget) {
		this.hasbudget = hasbudget;
	}


	public BuySell() {
	}


	
	
}
