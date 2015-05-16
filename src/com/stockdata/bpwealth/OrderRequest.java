
package com.stockdata.bpwealth;


import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.CompressionHeader;
import com.stockdata.bpwealth.core.MessageHeader;


public class OrderRequest {
    public short RequestType;
    public int Segment;
    public short side;
    public String Token="";
    
    public String Symbol="";
    public String ScripName="";
    public String Series="";
    public short InstType;
    public short UnderlyingType;
    public int ExpiryDate;
    public int StrikePrice=-1;
    public int Resereved0;
   
    public int quantity;
    public int DiscQty;
    public int rate;
    public int TriggerPrice;
    
    public String AtMarket="N";
    public String Stoploss="N";
    public String IOC="N";
    public short ProdType;
    public String AfterHours="N";
    
    public String ClientCode="";
    public String OrderRequesterCode;
    public short ProCli;
    
    public short id; 
    public int LocalOrderTime;
    public long BrokerOrderID;
    public int BrokerOrderTime;
    public short Reserved1;
    public short Reserved2;
    
    public String order_status="";
    public String Msg="";
    public String SLTrigerred="";
    
    public long ExchOrderID;
    public int ExchOrderTime;
    public int last_updated;
    
    public int LastTradeTime;
    public int TradeQty;
    public int PendQty;
    public int OldQty;
    public long OldBrokerID;
    public long NNFField;
    public String Reserved3="";
    public short Reserverd4;
    public int MarketLot=1;
    public String Reserved5="";
    public String PreOpen="";
    public int Reserved6;
    public String RouteID="";
    public String Reserved7="";
    public int MktProt;
    public String Reserved8="";
    
    CompressionHeader compHeader;
    MessageHeader msgHeader;
    byte[] struct;
    public OrderRequest(){
        this.compHeader = new CompressionHeader((short)406,(short) 406);
        this.msgHeader =  new  MessageHeader((short)406,CConstants.TransactionCode.Order_Place_Request);
        this.struct = new byte[410];
        System.arraycopy(this.compHeader.getByteArray(), 0, this.struct, 0, 4);
        System.arraycopy(this.msgHeader.getByteArray(), 0, this.struct, 4, 4);
    }
    
    public byte[] getStruct(){
        try 
        {
            CConstants.setShort(RequestType, struct, 8);
            CConstants.setInt(Segment , struct, 10);
            CConstants.setShort(side , struct, 14);
            CConstants.setInt(Integer.parseInt(Token) , struct, 16);
            CConstants.setString(Symbol, 10, struct, 20);
            CConstants.setString(ScripName, 50, struct, 30);
            CConstants.setString(Series, 2, struct, 80);
            CConstants.setShort(InstType, struct, 82);
            CConstants.setShort(UnderlyingType, struct, 84);
            CConstants.setInt(ExpiryDate, struct, 86);
            CConstants.setInt(StrikePrice, struct, 90);
            CConstants.setInt(Resereved0, struct, 94);
            CConstants.setInt(quantity, struct, 98);
            CConstants.setInt(DiscQty, struct, 102);
            CConstants.setInt(rate, struct, 106);
            CConstants.setInt(TriggerPrice, struct, 110);
            CConstants.setString(AtMarket, 1, struct, 114);
            CConstants.setString(Stoploss, 1, struct, 115);
            CConstants.setString(IOC, 1, struct, 116);
            CConstants.setShort(ProdType, struct, 117);
            CConstants.setString(AfterHours, 1, struct, 119);
            CConstants.setString(ClientCode, 10, struct, 120);
            CConstants.setString(OrderRequesterCode, 10, struct, 130);
            CConstants.setShort(ProCli, struct, 140);
            CConstants.setShort(id, struct, 142);
            CConstants.setInt(LocalOrderTime, struct, 144);
            CConstants.setLong(BrokerOrderID, struct, 148);
            CConstants.setInt(BrokerOrderTime, struct, 156);
            CConstants.setShort(Reserved1, struct, 160);
            CConstants.setShort(Reserved2, struct, 162);
            CConstants.setString(order_status,20, struct, 164);
            CConstants.setString(Msg,100, struct, 184);//Should be 184
            CConstants.setString(SLTrigerred,1, struct, 284);
            CConstants.setLong(ExchOrderID,struct, 285);
            CConstants.setInt(ExchOrderTime,struct, 293);
            CConstants.setInt(last_updated,struct, 297);
            CConstants.setInt(LastTradeTime,struct, 301);
            CConstants.setInt(TradeQty,struct, 305);
            CConstants.setInt(PendQty,struct, 309);
            CConstants.setInt(OldQty, struct, 313);
            CConstants.setLong(OldBrokerID, struct, 317);
            CConstants.setLong(NNFField, struct, 325);
            CConstants.setString(Reserved3,22, struct, 333);
            CConstants.setShort(Reserverd4,struct, 355);
            CConstants.setInt(MarketLot,struct, 357);
            CConstants.setString(Reserved5,20, struct, 361);
            CConstants.setString(PreOpen,1, struct, 381);
            CConstants.setInt(Reserved6,struct, 382);
            CConstants.setString(RouteID, 10, struct, 386);
            CConstants.setString(Reserved7, 9, struct, 396);
            CConstants.setInt(MktProt,struct, 405);
            CConstants.setString(Reserved8,1,struct, 409);
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OrderRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    return struct;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrderRequest [RequestType=" + RequestType + ", Segment="
				+ Segment + ", side=" + side + ", Token=" + Token + ", Symbol="
				+ Symbol + ", ScripName=" + ScripName + ", Series=" + Series
				+ ", InstType=" + InstType + ", UnderlyingType="
				+ UnderlyingType + ", ExpiryDate=" + ExpiryDate
				+ ", StrikePrice=" + StrikePrice + ", Resereved0=" + Resereved0
				+ ", quantity=" + quantity + ", DiscQty=" + DiscQty + ", rate="
				+ rate + ", TriggerPrice=" + TriggerPrice + ", AtMarket="
				+ AtMarket + ", Stoploss=" + Stoploss + ", IOC=" + IOC
				+ ", ProdType=" + ProdType + ", AfterHours=" + AfterHours
				+ ", ClientCode=" + ClientCode + ", OrderRequesterCode="
				+ OrderRequesterCode + ", ProCli=" + ProCli + ", id=" + id
				+ ", LocalOrderTime=" + LocalOrderTime + ", BrokerOrderID="
				+ BrokerOrderID + ", BrokerOrderTime=" + BrokerOrderTime
				+ ", Reserved1=" + Reserved1 + ", Reserved2=" + Reserved2
				+ ", order_status=" + order_status + ", Msg=" + Msg
				+ ", SLTrigerred=" + SLTrigerred + ", ExchOrderID="
				+ ExchOrderID + ", ExchOrderTime=" + ExchOrderTime
				+ ", last_updated=" + last_updated + ", LastTradeTime="
				+ LastTradeTime + ", TradeQty=" + TradeQty + ", PendQty="
				+ PendQty + ", OldQty=" + OldQty + ", OldBrokerID="
				+ OldBrokerID + ", NNFField=" + NNFField + ", Reserved3="
				+ Reserved3 + ", Reserverd4=" + Reserverd4 + ", MarketLot="
				+ MarketLot + ", Reserved5=" + Reserved5 + ", PreOpen="
				+ PreOpen + ", Reserved6=" + Reserved6 + ", RouteID=" + RouteID
				+ ", Reserved7=" + Reserved7 + ", MktProt=" + MktProt
				+ ", Reserved8=" + Reserved8 +  "]";
	}

   
}
