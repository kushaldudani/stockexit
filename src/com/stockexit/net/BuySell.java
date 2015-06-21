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
    private int hasbudget;
    
    @Column(name = "Daystried")
    private int daystried;
    
    @Column(name = "Expiry")
    private String expiry;
    
    @Column(name = "Type")
    private String type;
    
    @Column(name = "Closeprice")
    private double closeprice;
   	
    @Column(name = "Nextopenprice")
    private double nextopenprice;
    
    @Column(name = "Mcase")
    private int mcase;
    
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
	public int getbudget() {
		return hasbudget;
	}


	/**
	 * @param hasbudget the hasbudget to set
	 */
	public void setHasbudget(int hasbudget) {
		this.hasbudget = hasbudget;
	}


	/**
	 * @return the daystried
	 */
	public int getDaystried() {
		return daystried;
	}


	/**
	 * @param daystried the daystried to set
	 */
	public void setDaystried(int daystried) {
		this.daystried = daystried;
	}


	/**
	 * @return the expiry
	 */
	public String getExpiry() {
		return expiry;
	}


	/**
	 * @param expiry the expiry to set
	 */
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * @return the closeprice
	 */
	public double getCloseprice() {
		return closeprice;
	}


	/**
	 * @param closeprice the closeprice to set
	 */
	public void setCloseprice(double closeprice) {
		this.closeprice = closeprice;
	}


	/**
	 * @return the nextopenprice
	 */
	public double getNextopenprice() {
		return nextopenprice;
	}


	/**
	 * @param nextopenprice the nextopenprice to set
	 */
	public void setNextopenprice(double nextopenprice) {
		this.nextopenprice = nextopenprice;
	}


	/**
	 * @return the mcase
	 */
	public int getMcase() {
		return mcase;
	}


	/**
	 * @param mcase the mcase to set
	 */
	public void setMcase(int mcase) {
		this.mcase = mcase;
	}


	public BuySell() {
	}


	
	
}
