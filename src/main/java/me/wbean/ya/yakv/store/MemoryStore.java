/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.store
 */
package me.wbean.ya.yakv.store;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午5:46
 */
public class MemoryStore implements Store {

    Map<String, KVEntry> storeMap;

    public MemoryStore(){
        storeMap = new ConcurrentHashMap<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    Iterator<KVEntry> iterator = storeMap.values().iterator();
                    while (iterator.hasNext()){
                        KVEntry kvEntry = iterator.next();
                        if(! kvEntry.checkTtl()){
                            iterator.remove();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public KVEntry get(String key) {
        return storeMap.get(key);
    }

    @Override
    public void set(KVEntry kvEntry) {
        storeMap.put(kvEntry.getKey(), kvEntry);
    }

    @Override
    public void remove(String key){
        storeMap.remove(key);
    }

}
