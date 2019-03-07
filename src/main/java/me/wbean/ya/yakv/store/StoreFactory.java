/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.store
 */
package me.wbean.ya.yakv.store;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午7:41
 */
public class StoreFactory {

    enum StoreInstanceEnum{
        /**
         * 内存存储
         */
        MEMORY(new MemoryStore());

        private Store instance;

        private StoreInstanceEnum(Store store){
            this.instance = store;
        }

        public Store getInstance(){
            return this.instance;
        }
    }

    public static Store getStore(String key){
        try{
            return StoreInstanceEnum.valueOf(key.toUpperCase()).getInstance();
        }catch (Exception e){
            return null;
        }
    }
}
