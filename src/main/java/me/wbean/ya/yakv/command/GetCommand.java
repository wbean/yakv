/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.command
 */
package me.wbean.ya.yakv.command;

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
public class GetCommand implements Command{
    @Override
    public ResponseMessage execute(RequestMessage request) {
        Store store = StoreFactory.getStore("MEMORY");
        try{
            KVEntry ret = store.get(request.getParamOf(0));
            if(ret != null && !ret.checkTtl()){
                store.remove(ret.getKey());
                return new ResponseMessage(null);
            }
            return new ResponseMessage(ret.getValue());
        }catch (Exception e){
            return new ResponseMessage("ERROR:get failed");
        }
    }
}
