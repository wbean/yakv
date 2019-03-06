/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.test
 */
package me.wbean.ya.yakv.test;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;


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

    //@BeforeClass
    public static void startServer() throws IOException, InterruptedException {
        new Thread(new ServerRunner(new Server(8899))).start();
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void connectTest() throws IOException {
        Socket socket = new Socket("127.0.0.1", 8000);
    }

    @Test
    public void writeMsgTest() throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 8000);
        String msg = "hello";
        byte[] msgByte = msg.getBytes();

        int unsignedLen = ((short)msgByte.length) & 0x0FFFF;

        byte[] finalMsg = new byte[2];
        finalMsg[0] = (byte) ((unsignedLen & 0xff00) >> 8);
        finalMsg[1] = (byte) (unsignedLen & 0xff);

        finalMsg = ArrayUtils.addAll(finalMsg, msgByte);

        TimeUnit.SECONDS.sleep(2);

        socket.getOutputStream().write(finalMsg);

        InputStream is = socket.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);


        int aa;
        while ((aa = isr.read()) > 0){
            System.out.println(aa);
        }
    }
}
