/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.test
 */
package me.wbean.ya.yakv.test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import me.wbean.ya.yakv.Client;
import me.wbean.ya.yakv.server.Server;

/**
 *
 * @author wbean
 * @date 2019/3/6 下午5:50
 */
public class ServerTest {


    static class ServerRunner implements Runnable{

        private Server server;

        public ServerRunner(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            try {
                server.start();
            } catch (IOException e) {
            }
        }
    }

    Client client;
    public ServerTest(){
        try {
            client = new Client("127.0.0.1", 8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //@BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        new Thread(new ServerRunner(new Server(8899))).start();
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void connectTest() throws IOException {
        Socket socket = new Socket("127.0.0.1", 8000);
    }

    @Test(threadPoolSize = 20, invocationCount = 800,  timeOut = 20000)
    public void multiSocketTest() throws IOException, InterruptedException {
        Client client2 = new Client("127.0.0.1", 8000);
        Assert.assertEquals(client2.execute("hello"), "hello");
    }

    @Test(threadPoolSize = 1, invocationCount = 300, timeOut = 20000)
    public void singleSocketTest() throws IOException {
        Assert.assertEquals(client.execute("hello"), "hello");
    }
}
