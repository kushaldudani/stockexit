//package com.stockexit.net;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class SymbolData {
//	
//	private String symbol;
//	
//	private double buyprice;
//	
//	private Date buydate;
//	
//	private double todayHigh;
//	
//	private double todayLow;
//	
//	private double todayClose;
//	
//	private List<Double> prices;
//	
//	
//	public SymbolData(String symbol, double buyprice, Date buydate){
//		this.symbol = symbol;
//		this.buyprice = buyprice;
////		this.buydate = buydate;
//	}
//
//	/**
//	 * @return the symbol
//	 */
//	public String getSymbol() {
//		return symbol;
//	}
//
//	
//	/**
//	 * @return the buyprice
//	 */
//	public double getBuyprice() {
//		return buyprice;
//	}
//
//	
//	/**
//	 * @return the buydate
//	 */
//	public Date getBuydate() {
//		return buydate;
//	}
//
//	/**
//	 * @return the todayHigh
//	 */
//	public double getTodayHigh() {
//		return todayHigh;
//	}
//
//	/**
//	 * @param todayHigh the todayHigh to set
//	 */
//	public void setTodayHigh(double todayHigh) {
//		this.todayHigh = todayHigh;
//	}
//
//	/**
//	 * @return the todayLow
//	 */
//	public double getTodayLow() {
//		return todayLow;
//	}
//
//	/**
//	 * @param todayLow the todayLow to set
//	 */
//	public void setTodayLow(double todayLow) {
//		this.todayLow = todayLow;
//	}
//
//	/**
//	 * @return the todayClose
//	 */
//	public double getTodayClose() {
//		return todayClose;
//	}
//
//	/**
//	 * @param todayClose the todayClose to set
//	 */
//	public void setTodayClose(double todayClose) {
//		this.todayClose = todayClose;
//	}
//
//	/**
//	 * @return the prices
//	 */
//	public synchronized List<Double> getPrices() {
//		return prices;
//	}
//
//	/**
//	 * @param prices the prices to set
//	 */
//	public synchronized void addPrices(Double price) {
//		if(prices == null){
//			prices = new ArrayList<Double>();
//		}
//		prices.add(price);
//	}
//
//}
