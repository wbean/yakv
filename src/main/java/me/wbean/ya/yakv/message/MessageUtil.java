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
public class MessageUtil {
    public static final ResponseMessage OK = new ResponseMessage("OK");


    public static byte[] addHeader(byte[] bytes){
        int unsignedLen = ((short)bytes.length) & 0x0FFFF;

        byte[] finalMsg = new byte[2];
        finalMsg[0] = (byte) ((unsignedLen & 0xff00) >> 8);
        finalMsg[1] = (byte) (unsignedLen & 0xff);

        finalMsg = ArrayUtils.addAll(finalMsg, bytes);
        return finalMsg;
    }

    public static byte[] getEndSign(){
        return new byte[2];
    }
}
