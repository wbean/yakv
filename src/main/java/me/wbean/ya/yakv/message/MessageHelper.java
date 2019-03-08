/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv
 */
package me.wbean.ya.yakv.message;

import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午2:52
 */
public class MessageHelper {
    public static final ResponseMessage OK = new ResponseMessage("OK");

    public static final int MAX_MSG_LENGTH = 1 << 16;
    public static final int MAX_DATA_LENGTH = (1 << 16) - 2;
    public static final int HEADER_LENGTH = 2;

    /**
     * 两位0表示本次返回结束
     */
    public static final byte[] END_SIGN = new byte[2];


    public static byte[] addHeader(byte[] bytes){
        if(bytes.length > MAX_DATA_LENGTH){

        }
        int unsignedLen = ((short)bytes.length) & 0x0FFFF;

        byte[] finalMsg = new byte[HEADER_LENGTH];
        finalMsg[0] = (byte) ((unsignedLen & 0xff00) >> 8);
        finalMsg[1] = (byte) (unsignedLen & 0xff);

        finalMsg = ArrayUtils.addAll(finalMsg, bytes);
        return finalMsg;
    }

    public static byte[] getEndSign(){
        return new byte[2];
    }
}
