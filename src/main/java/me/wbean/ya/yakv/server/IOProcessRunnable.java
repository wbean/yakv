/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.server
 */
package me.wbean.ya.yakv.server;

import me.wbean.ya.yakv.CommandExecuteRunnable;
import me.wbean.ya.yakv.message.ResponseMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


/**
 *
 * @author wbean
 * @date 2019/3/6 下午1:49
 */
public class IOProcessRunnable implements Runnable{
    private static final Logger logger = Logger.getLogger(IOProcessRunnable.class.getName());
    private Selector readSelector;
    private Selector writeSelector;
    private Queue<SocketChannel> currentSockets;
    private Queue<ResponseMessage> outputQueue;
    private ThreadPoolExecutor threadPoolExecutor;
    private volatile AtomicInteger atomicInteger = new AtomicInteger(0);

    private Map<Integer, SocketChannel> socketChannelMap;
    private ByteBuffer readByteBuffer = ByteBuffer.allocateDirect(1<<16);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocateDirect(1<<16);

    public IOProcessRunnable(Selector readSelector, Selector writeSelector, Queue currentSockets, ThreadPoolExecutor threadPoolExecutor, Queue outputQueue){
        this.readSelector = readSelector;
        this.writeSelector = writeSelector;
        this.currentSockets = currentSockets;
        this.outputQueue = outputQueue;
        this.threadPoolExecutor = threadPoolExecutor;
        this.socketChannelMap = new ConcurrentHashMap<>(128);
    }



    @Override
    public void run() {
        while (true){
            try {

                initSocket();

                readSocket();

                writeSocket();

            } catch (IOException e) {
                logger.info("io exception");
            }
        }
    }

    private void initSocket(){
        SocketChannel socketChannel;
        while ((socketChannel = this.currentSockets.poll())!= null){
            try {
                socketChannel.configureBlocking(false);
                SelectionKey selectionKey = socketChannel.register(readSelector, SelectionKey.OP_READ);
                selectionKey.attach(this.atomicInteger.incrementAndGet());
                socketChannelMap.put(this.atomicInteger.get(), socketChannel);
            } catch (IOException e) {
                logger.info("io exception");
            }

        }
    }

    private void writeSocket() throws IOException{
        ResponseMessage responseMessage;
        while ((responseMessage = outputQueue.poll()) != null){
            SocketChannel socketChannel = socketChannelMap.get(responseMessage.getSockedId());
            SelectionKey selectionKey = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
            selectionKey.attach(responseMessage);
        }

        int count = writeSelector.selectNow();
        if(count == 0){
            return;
        }
        Set<SelectionKey> selectionKeySet = writeSelector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeySet.iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            if(key.isWritable()){
                writeByteBuffer.clear();
                ResponseMessage attachment = (ResponseMessage) key.attachment();
                logger.info("start write:" + attachment.getSockedId());
                SocketChannel socketChannel = (SocketChannel)key.channel();
                if(attachment.getData() == null){
                    writeByteBuffer.put("null".getBytes());
                }else{
                    writeByteBuffer.put(attachment.getData().getBytes());
                }
                writeByteBuffer.flip();
                while (writeByteBuffer.hasRemaining()){
                    socketChannel.write(writeByteBuffer);
                }
            }
        }


    }

    private void readSocket() throws IOException {
        int count = readSelector.selectNow();
        if(count == 0){
            return;
        }

        Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            if(key.isReadable()){
                logger.info("start read");
                readByteBuffer.clear();
                SocketChannel socketChannel = (SocketChannel) key.channel();
                int readLen;


                while ((readLen = socketChannel.read(readByteBuffer)) >= -1){
                    if(readLen == 0){
                        break;
                    }

                    if(readLen == -1){
                        socketChannel.close();
                        break;
                    }

                    readByteBuffer.flip();

                    byte[] lengthByte = new byte[2];
                    readByteBuffer.get(lengthByte);

                    int length = (lengthByte[0] << 8) + lengthByte[1];
                    byte[] bytes = new byte[length];
                    if (readByteBuffer.hasRemaining()){
                        readByteBuffer.get(bytes);
                    }

                    readByteBuffer.compact();
                    logger.info("receive msg:" + new String(bytes));
                    threadPoolExecutor.submit(new CommandExecuteRunnable((int)key.attachment(), bytes, this.outputQueue));
                }

            }

            iterator.remove();
        }
    }
}
