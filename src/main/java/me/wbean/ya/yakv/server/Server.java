package me.wbean.ya.yakv.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.xml.crypto.KeySelector;

public class Server{
    Logger logger = Logger.getLogger(Server.class.getName());

    private static volatile AtomicBoolean running;



    private Map<SelectionKey, SocketChannel> currentSocket;

    private Selector connectSelector = Selector.open();
    private Selector ioSelector = Selector.open();

    private int port;


    public Server(int port) throws IOException {
        this.port = port;
        running = new AtomicBoolean(false);
        currentSocket = new ConcurrentHashMap<>(16);

    }

    public void start() throws IOException {
        if(!running.compareAndSet(false, true)){
            throw new RuntimeException("server already start");
        }

        new Thread(new ServerRunnable(port, connectSelector, ioSelector, currentSocket)).start();

        new Thread(new IOProcessRunnable(ioSelector)).start();
    }
}
