/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.command
 */
package me.wbean.ya.yakv.command;

import me.wbean.ya.yakv.message.MessageUtil;
import me.wbean.ya.yakv.message.RequestMessage;
import me.wbean.ya.yakv.message.ResponseMessage;
import me.wbean.ya.yakv.store.KVEntry;
import me.wbean.ya.yakv.store.Store;
import me.wbean.ya.yakv.store.StoreFactory;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午5:42
 */
public class SetCommand implements Command{
    @Override
    public ResponseMessage execute(RequestMessage request) {
        Store store = StoreFactory.getStore("MEMORY");

        try {
            String[] params = request.getParam();

            String key = params[0];
            int ttl = Integer.valueOf(params[1]);

            StringBuilder valueBuilder = new StringBuilder();
            for (int j = 2; j < params.length; j++) {
                valueBuilder.append(params[j]);
                valueBuilder.append(' ');
            }

            store.set(new KVEntry(key, valueBuilder.toString(), ttl, System.currentTimeMillis()));

            return MessageUtil.OK;
        }catch (Exception e){
            return new ResponseMessage("ERROR:set failed");
        }
    }
}
