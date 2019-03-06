package me.wbean.ya.yakv.server;

import me.wbean.ya.yakv.message.ResponseMessage;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Server{
    Logger logger = Logger.getLogger(Server.class.getName());

    private static volatile AtomicBoolean running;

    private Queue currentSocket;

    private Selector connectSelector = Selector.open();
    private Selector readSelector = Selector.open();
    private Selector writeSelector = Selector.open();

    private int port;

    private ThreadPoolExecutor threadPoolExecutor;

    private Queue<ResponseMessage> outputQueue;


    public Server(int port) throws IOException {
        this.port = port;
        running = new AtomicBoolean(false);
        currentSocket = new ArrayBlockingQueue(1024);
        threadPoolExecutor = new ThreadPoolExecutor(200, 200, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1024), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("command-execute");
                return t;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                throw new RuntimeException("request too much");
            }
        });
        this.outputQueue = new ConcurrentLinkedQueue<>();
    }

    public void start() throws IOException {
        if(!running.compareAndSet(false, true)){
            throw new RuntimeException("server already start");
        }

        new Thread(new ServerRunnable(port, connectSelector, currentSocket)).start();

        new Thread(new IOProcessRunnable(readSelector, writeSelector, currentSocket, threadPoolExecutor, this.outputQueue)).start();

        logger.info("server start");
    }
}
