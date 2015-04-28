
package com.stockdata.bpwealth.core;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;


public class CConstants {
    
        
        public static  class TransactionCode
        {  
        //Broadcast Related
        public static short Broadcast_Request_Response = 5001;
        public static short Request_Stop_Broadcast = 92;
        public static short Tick_Data_Request = 28;
        public static short Tick_Data_Response = 29;
        
        //Interactive Related
        public static short Login_Request_Response = 1;
        public static short General_Erro_Response = 97;
        public static short Logoff_Request_Response = 61;
        public static short Change_Pwd = 38;
        public static short Change_Trn_Pwd = 438;
        public static short Order_Error = 99;
        public static short Order_Place_Request = 100;
        public static short Order_Modify_Request = 110;
        public static short Order_Cancel_Request = 120;
        public static short Order_Broker_Received = 101;
        public static short Order_Modify_Broker_Received = 111;
        public static short Order_Cancel_Broker_Received = 121;
        public static short Order_RMS_Processed = 102;
        public static short Order_Modify_RMS_Processed = 112;
        public static short Order_Cancel_RMS_Processed = 122;
        public static short Order_Sent_To_Exchange = 1103;
        public static short Order_Received_By_Exchange = 1104;
        public static short Exchange_Confirmation = 1105;
        public static short Exchange_Reject = 1106;
        public static short Exchange_Freeze = 1107;
        public static short Exchange_Killed = 1108;
        public static short Trade_Confirmation = 2222;
        public static short Stoploss_Triggerred = 2212;
        public static short Master_Download_Request = 500;
        public static short Cash_Segment_Master_Response = 501;
        public static short FO_Master_Response_Summary = 502;
        public static short FO_Master_Response_Details = 503;
        public static short FO_Master_Download_Complete = 504;
        public static short Order_request_response = 11;
        }
        
        public static class Sizes{
            public static short Login_Request_Size = 187+4;//Message plus message header
            public static short Login_Response_Size = 827+4;//Message plus header
        }
        
        public static String getString(byte[] bytes,int index, int size) throws Exception{
            byte[] array = new byte[size];
            System.arraycopy(bytes, index, array, 0, array.length);
            return getString(array);
        }
        
        public static String getString(byte[] data) throws Exception{
             return new String(data,Charset.forName("UTF-8")).replaceAll("\0", "");
        }
        
        public static int getInt32(byte[] bytes, int beginIndex){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(bytes, beginIndex, buffer.array(), 0, buffer.array().length);
        return buffer.getInt();
        }
        public static double getDouble(byte[] bytes, int beginIndex){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(bytes, beginIndex, buffer.array(), 0, buffer.array().length);
        return buffer.getDouble();
        }
        public static long getLong(byte[] bytes, int beginIndex){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(bytes, beginIndex, buffer.array(), 0, buffer.array().length);
        return buffer.getLong();
        }
        
        public static short getInt16(byte[] bytes, int beginIndex){
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        System.arraycopy(bytes, beginIndex, buffer.array(), 0, buffer.array().length);
        return buffer.getShort();
        }
        
        
        
        public static void setInt(int int32,byte[] bytes,int index){
        ByteBuffer buff = ByteBuffer.allocate(4);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.putInt((int)int32);
        System.arraycopy(buff.array() , 0, bytes, index, buff.array().length);
        }
       
        public static void setDouble(double double64,byte[] bytes,int index){
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.putDouble(double64);
        System.arraycopy(buff.array() , 0, bytes, index, buff.array().length);
        }
        public static void setLong(long double64,byte[] bytes,int index){
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.putLong(double64);
        System.arraycopy(buff.array() , 0, bytes, index, buff.array().length);
        }
        public static void setShort(short int16, byte[] bytes, int index){
        ByteBuffer buff = ByteBuffer.allocate(2);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.putShort(int16);
        System.arraycopy(buff.array() , 0, bytes, index, buff.array().length);
        }
        
        public static void setString(String str, int size, byte[] array,int index) throws UnsupportedEncodingException{
            byte[] byteArray = new byte[size];
            byte[] stringBytes=str.getBytes("ISO-8859-1");
            System.arraycopy(stringBytes, 0, byteArray, 0, stringBytes.length);
            System.arraycopy(byteArray, 0, array, index, byteArray.length);
        }
        
}
