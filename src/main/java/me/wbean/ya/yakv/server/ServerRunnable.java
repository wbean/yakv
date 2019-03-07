/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.server
 */
package me.wbean.ya.yakv.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wbean
 * @date 2019/3/6 下午1:50
 */
public class ServerRunnable implements Runnable{
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private ServerSocketChannel serverSocketChannel;
    private Selector connectSelector;
    private Queue<SocketChannel> currentSocket;

    public ServerRunnable(int port, Selector connectSelector, Queue<SocketChannel> currentSocket) throws IOException {
        this.connectSelector = connectSelector;
        this.currentSocket = currentSocket;

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(this.connectSelector,  SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true){
            int readyCount;
            try {
                readyCount = connectSelector.select(1000);

                if(readyCount == 0) {
                    continue;
                }
                Iterator<SelectionKey> selectionKeys = connectSelector.selectedKeys().iterator();
                while (selectionKeys.hasNext()){
                    SelectionKey key = selectionKeys.next();
                    if(key.isAcceptable()){
                        logger.info("accept");
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        currentSocket.offer(socketChannel);
                    }
                    logger.info("select key remove");
                    selectionKeys.remove();
                }
            } catch (IOException e) {
                logger.log(Level.INFO,"Server run IOException", e);
            }
        }
    }
}
