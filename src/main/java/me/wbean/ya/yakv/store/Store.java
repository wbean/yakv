/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.store
 */
package me.wbean.ya.yakv.store;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午7:38
 */
public interface Store {
    KVEntry get(String key);

    void set(KVEntry kvEntry);

    void remove(String key);
}
