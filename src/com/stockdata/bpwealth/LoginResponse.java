
package com.stockdata.bpwealth;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.Request;


public class LoginResponse extends Request {
    public byte Success;
    String ClientName;
    String TrxPassword;
    byte PwdResetByAdmin;
    int ServerDateTime;
    short SegForTrading;
    short SegForViewing;
    short MaxScripsInMW;
    int LastLogin;
    String Reserved1;
    int LastLoginIP;
    int Reserved2;
    int ClientConnIP;
    int BroadcastIP;
    short InteractivePort;
    short BroadcastPort;
    short ClientType;
    String BrokerID;
    String Message;
    int LastPwdModified;
    int LastTrxModified;
    short BranchID;
    String Reserved3;
    UserInfoDet[] det;
    String Reserved4;
    String ClientCode;
    String Reserved5;
    
    public LoginResponse(byte[] bytes) throws Exception{
         Success = bytes[4];
         //System.out.println("Success Byte:"+Success);
         ClientCode = CConstants.getString(bytes, 5, 50);
         //System.out.println("Client Code:"+ClientCode);
         TrxPassword = CConstants.getString(bytes, 55, 40);
         //System.out.println("Transaction Password:"+ClientCode);
         PwdResetByAdmin = bytes[95];
         //System.out.println("Pwd Reset By Admin Byte:"+Success);
         ServerDateTime = CConstants.getInt32(bytes, 96);
         SegForTrading = CConstants.getInt16(bytes, 100);
         SegForViewing = CConstants.getInt16(bytes, 102);
         MaxScripsInMW = CConstants.getInt16(bytes, 104);
         LastLogin = CConstants.getInt32(bytes, 106);
         Reserved1 = CConstants.getString(bytes, 110, 20);
         LastLoginIP = CConstants.getInt32(bytes, 130);
         Reserved2 = CConstants.getInt32(bytes, 134);
         ClientConnIP = CConstants.getInt32(bytes, 138);
         BroadcastIP = CConstants.getInt32(bytes, 142);
         InteractivePort = CConstants.getInt16(bytes, 146);
         BroadcastPort = CConstants.getInt16(bytes, 148);
         ClientType = CConstants.getInt16(bytes, 150);
         BrokerID = CConstants.getString(bytes, 152, 10);
         Message = CConstants.getString(bytes, 162, 60);
         LastPwdModified = CConstants.getInt32(bytes, 222);
         LastTrxModified = CConstants.getInt32(bytes, 226);
         BranchID = CConstants.getInt16(bytes, 230);
         Reserved3 = CConstants.getString(bytes, 232, 68);
         det = new UserInfoDet[17];
         for (int i = 0; i < 17; i++) {
             byte[] subStruct = new byte[32];
             System.arraycopy(bytes, 300+i, subStruct, 0, 32);
             det[i] = new UserInfoDet(subStruct);
            // System.out.println("UserInfo:"+det[i].UserInfo);
        }
         
        
    }
    
    @Override
    public String toString(){
       return Success+""; 
    }
    
    
}
