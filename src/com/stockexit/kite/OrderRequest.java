
package com.stockexit.kite;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.stockexit.util.LoggerUtil;


public class OrderRequest {
	public final static String BUY = "BUY";
	public final static String SELL = "SELL";

	String exchange = "NFO";
	String symbol;
	String piOrderID = "";
	String clientOrderID;
	String strategyName = "MOMENTUM";
	String orderSide;
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
	
	String apikey;
	String accesstoken;
   
    
	public OrderRequest(String symbol, short orderSide, int initialQty,
			double limitPrice, double triggerPrice, String productType, String clientOrderID,
			String orderType, String apikey, String accesstoken) {
		this.symbol = symbol;
		this.orderSide = orderSide==1?BUY:SELL;
		this.initialQty = initialQty;
		this.disclosedQty = initialQty;
		this.remainingQty = initialQty;
		this.limitPrice = limitPrice;
		this.triggerPrice = triggerPrice;
		this.productType = productType;
		this.clientOrderID = clientOrderID;
		this.orderType = orderType;
		
		this.apikey = apikey;
		this.accesstoken = accesstoken;
	}
	
	public String toStream() throws Exception {
		URL obj = new URL("https://api.kite.trade/orders/regular");
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("X-Kite-version", "1");
		if(symbol.contains("&")){
			symbol = symbol.replace("&", "%26");
		}
		String urlParameters = "api_key="+apikey+"&access_token="+accesstoken+"&tradingsymbol="+symbol+"&exchange="+exchange+"&transaction_type="+orderSide+"&order_type="+orderType+"&quantity="+initialQty+"&product="+productType+"&validity="+validity+"&price="+limitPrice;
		if(triggerPrice>0){
			urlParameters = urlParameters + "&trigger_price="+triggerPrice;
		}
		LoggerUtil.getLogger().info(urlParameters);
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		String orderid = null;
		while ((inputLine = in.readLine()) != null) {
			if(inputLine.contains("order_id")){
				String[] values = inputLine.split("\"");
				for(String val : values){
					if(val.length()>0&&val.charAt(0)>='0'&&val.charAt(0)<='9'){
						orderid = val;
						break;
					}
				}
			}
			if(orderid!=null){
				break;
			}
		}
		in.close();
		
		return orderid;
	}

	
	static byte[] padRight(String str, int length) {

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
