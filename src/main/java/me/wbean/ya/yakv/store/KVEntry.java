/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.store
 */
package me.wbean.ya.yakv.store;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午5:51
 */
public class KVEntry {
    private String key;

    private String value;

    private int ttl;

    private long createdAt;

    public KVEntry(String key, String value, int ttl, long createdAt) {
        this.key = key;
        this.value = value;
        this.ttl = ttl;
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean checkTtl(){
        return System.currentTimeMillis() - createdAt <= ttl;
    }
}
