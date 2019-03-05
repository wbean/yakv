package me.wbean.ya.yakv.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Server {
    Logger logger = Logger.getLogger(Server.class.getName());

    private volatile AtomicBoolean running;

    ServerSocketChannel serverSocketChannel;
    public Server(int port) throws IOException {
        running = new AtomicBoolean(false);

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
    }

    public void start() throws IOException {
        if(!running.compareAndSet(false, true)){
            throw new RuntimeException("server already start");
        }

        Selector connectSelector = Selector.open();
        serverSocketChannel.register(connectSelector,  SelectionKey.OP_ACCEPT);

        while (true){
            int readyCount = connectSelector.select(1000);
            if(readyCount == 0) {
                logger.info("wait for connect");
                continue;
            }
            Iterator<SelectionKey> selectionKeys = connectSelector.keys().iterator();
            while (selectionKeys.hasNext()){
                SelectionKey key = selectionKeys.next();
                if(key.isConnectable()){
                    logger.info("connected");

                }else if(key.isAcceptable()){
                    logger.info("accept");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(48);
                    socketChannel.read(byteBuffer);
                    String msg = new String(byteBuffer.array());
                    logger.info(msg);
                }else if(key.isReadable()){
                    logger.info("read");
                }else if(key.isWritable()){
                    logger.info("write");

                }
            }
        }



    }
}
