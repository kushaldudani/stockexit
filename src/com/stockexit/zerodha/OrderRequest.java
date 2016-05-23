
package com.stockexit.zerodha;


import java.io.IOException;


public class OrderRequest {
	public final static short ORDERSIDE_BUY = 1;
	public final static short ORDERSIDE_SELL = 2;

	String exchange = "NFO";
	String symbol;
	String piOrderID = "";
	String clientOrderID;
	String strategyName = "MOMENTUM";
	short orderSide;
	int initialQty;
	int disclosedQty;
	int remainingQty;
	double limitPrice;
	double triggerPrice;
	double totalTradedValue = 0.0d;
	double lastTradedPrice = 0.0d;
	double averageTradedPrice = 0.0d;
	int tradedQty = 0;
	int lastTradeQty = 0;
	String orderType;
	String productType;
	String clientCode = "DA8326";
	String validity = "DAY";
	int orderStatus = 0;
	int entryTime = 0;
	int execTime = 0;
   
    
	public OrderRequest(String symbol, short orderSide, int initialQty,
			double limitPrice, double triggerPrice, String productType, String clientOrderID,
			String orderType) {
		this.symbol = symbol;
		this.orderSide = orderSide;
		this.initialQty = initialQty;
		this.disclosedQty = initialQty;
		this.remainingQty = initialQty;
		this.limitPrice = limitPrice;
		this.triggerPrice = triggerPrice;
		this.productType = productType;
		this.clientOrderID = clientOrderID;
		this.orderType = orderType;
	}
	
	public void toStream(NativeDataOutputStream dos) throws IOException {
		
		dos.writeBytes(padRight(exchange, 10));
		dos.writeBytes(padRight(symbol, 64));
		dos.writeBytes(padRight(piOrderID, 20));
		dos.writeBytes(padRight(clientOrderID, 10));
		dos.writeBytes(padRight(strategyName, 10)); // strategy name
													// optional
		dos.writeShort(orderSide);
		dos.writeInt(initialQty); // Initial Order size for a new Order
		dos.writeInt(disclosedQty); // Disclosed Qty??
		dos.writeInt(remainingQty); // Remaining Qty ??
		dos.writeDouble(limitPrice);
		dos.writeDouble(triggerPrice);
		dos.writeDouble(totalTradedValue);
		dos.writeDouble(lastTradedPrice);
		dos.writeDouble(averageTradedPrice);
		dos.writeInt(tradedQty);
		dos.writeInt(lastTradeQty);
		dos.writeBytes(padRight(orderType, 12));
		dos.writeBytes(padRight(productType, 12));
		dos.writeBytes(padRight(clientCode, 12));
		dos.writeBytes(padRight(validity, 5));
		dos.writeInt(orderStatus);
		dos.writeInt(entryTime);
		dos.writeInt(execTime);
	}

	
	private static byte[] padRight(String str, int length) {

		byte[] dst = new byte[length];
		byte[] src = str.getBytes();
		System.arraycopy(src, 0, dst, 0, src.length);
		return dst;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrderRequest [exchange=" + exchange + ", symbol=" + symbol
				+ ", piOrderID=" + piOrderID + ", clientOrderID="
				+ clientOrderID + ", strategyName=" + strategyName
				+ ", orderSide=" + orderSide + ", initialQty=" + initialQty
				+ ", disclosedQty=" + disclosedQty + ", remainingQty="
				+ remainingQty + ", limitPrice=" + limitPrice
				+ ", triggerPrice=" + triggerPrice + ", totalTradedValue="
				+ totalTradedValue + ", lastTradedPrice=" + lastTradedPrice
				+ ", averageTradedPrice=" + averageTradedPrice + ", tradedQty="
				+ tradedQty + ", lastTradeQty=" + lastTradeQty + ", orderType="
				+ orderType + ", productType=" + productType + ", clientCode="
				+ clientCode + ", validity=" + validity + ", orderStatus="
				+ orderStatus + ", entryTime=" + entryTime + ", execTime="
				+ execTime + "]";
	}
   
}
