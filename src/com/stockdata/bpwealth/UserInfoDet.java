
package com.stockdata.bpwealth;

import com.stockdata.bpwealth.core.CConstants;
import com.stockdata.bpwealth.core.Request;




class UserInfoDet extends Request{
    String UserInfo;
    String TerminalID;
    
    public UserInfoDet(byte[] struct) throws  Exception{
        UserInfo  = CConstants.getString(struct, 0, 16);
        TerminalID  = CConstants.getString(struct, 16, 16);
        
    }
    
    
}
