/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv
 */
package me.wbean.ya.yakv;

import me.wbean.ya.yakv.server.Server;

import java.io.IOException;

/**
 *
 * @author wbean
 * @date 2019/3/5 下午5:51
 */
public class Application {

    public static void main(String[] args) throws IOException {
        new Server(8000).start();
    }
}
