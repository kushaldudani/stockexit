
package com.stockdata.bpwealth;


import java.security.MessageDigest;
import java.util.Formatter;
import java.util.logging.Level;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.MessageHeader;
import com.stockdata.bpwealth.core.Request;
import com.stockdata.bpwealth.core.CompressionHeader;
import com.stockexit.util.LoggerUtil;


public class LoginRequest extends Request{
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LoginRequest [ClientCode=" + ClientCode + ", Reserved0="
				+ Reserved0 + ", ConnType=" + ConnType + ", LocalIP=" + LocalIP
				+ ", PublicIP=" + PublicIP + ", Reserved1=" + Reserved1
				+ ", VersionNo=" + VersionNo + ", Reserved2=" + Reserved2
				+ ", PAN=" + PAN + ", TwoFactor=" + TwoFactor + ", Reserved3="
				+ Reserved3 + "]";
	}

	public String ClientCode;
    public String Reserved0="";
    public String Password;
    public short ConnType;
    public String LocalIP="";
    public String PublicIP="";
    public String Reserved1="";
    public String VersionNo;
    public int Reserved2;
    public String PAN="";
    public byte TwoFactor;
    public byte Reserved3;
    //Size 187 Bytes
    public LoginRequest()
    {
        this.compHeader = new CompressionHeader(CConstants.Sizes.Login_Request_Size,CConstants.Sizes.Login_Request_Size);
        this.msgHeader =  new  MessageHeader(CConstants.Sizes.Login_Request_Size,CConstants.TransactionCode.Login_Request_Response);
        this.struct = new byte[CConstants.Sizes.Login_Request_Size+4];
        System.arraycopy(this.compHeader.getByteArray(), 0,this.struct, 0, 4);
        System.arraycopy(this.msgHeader.getByteArray(), 0, this.struct, 4, 4);
    }
    
    @Override
    public byte[] getStruct(){
        try 
        {
            CConstants.setString(ClientCode, 10, struct, 8);
            CConstants.setString(Reserved0, 2, struct, 18);
            byte[] bytesOfMessage = Password.getBytes();

            MessageDigest sh1 = MessageDigest.getInstance("SHA1");
            byte[] thedigest = sh1.digest(bytesOfMessage);
            Password = "";
            for (int i = 0; i < thedigest.length; i++) {
                Formatter formatter = new Formatter();
                 formatter.format("%02x", thedigest[i]);
                 Password += formatter.toString();
                 formatter.close();
            }
            
            CConstants.setString(Password, 40, struct, 20);
            CConstants.setShort(ConnType, struct, 60);
            
            byte[] localIPArr = new byte[4];
            String[] split = LocalIP.split("\\.");
            for (int i = 0; i < localIPArr.length; i++) {
            localIPArr[i] = (byte)Integer.parseInt(split[i]);    
            }
            CConstants.setInt(CConstants.getInt32(localIPArr, 0), struct, 62);
            
            byte[] PublicIPArr = new byte[4];
            String[] split2 = PublicIP.split("\\.");
            for (int i = 0; i < PublicIPArr.length; i++) {
            PublicIPArr[i] = (byte)Integer.parseInt(split2[i]);    
            }
            
            CConstants.setInt(CConstants.getInt32(PublicIPArr, 0), struct, 66);
            
            
            CConstants.setString(Reserved1, 105, struct, 70);
            byte[] VersionArr = new byte[4];
            String[] split3 = VersionNo.split("\\.");
             for (int i = 0; i < VersionArr.length; i++) {
            VersionArr[i] = (byte)Integer.parseInt(split3[i]);    
            }
            CConstants.setInt(CConstants.getInt32(VersionArr, 0), struct, 175);
            
            
            CConstants.setInt(Reserved2, struct, 179);
            CConstants.setString(PAN, 10, struct, 183);
            struct[193] = TwoFactor;
            struct[194] = 0;
            return struct;
        } catch (Exception ex) {
            LoggerUtil.getLogger().log(Level.SEVERE, "Exception in making Login Request", ex);
            System.exit(1);
            return null;
        }
    }
    
    
    
}
