/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.server
 */
package me.wbean.ya.yakv.server;

import me.wbean.ya.yakv.CommandExecuteRunnable;
import me.wbean.ya.yakv.message.MessageHelper;
import me.wbean.ya.yakv.message.ResponseMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
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
    private volatile AtomicLong socketId = new AtomicLong(0);

    private Map<Long, SocketChannel> socketChannelMap;
    private ByteBuffer readByteBuffer = ByteBuffer.allocateDirect(MessageHelper.MAX_MSG_LENGTH);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocateDirect(MessageHelper.MAX_MSG_LENGTH);

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
                logger.log(Level.INFO,"IOProcessRunnable.run", e);
            }
        }
    }

    private void initSocket(){
        SocketChannel socketChannel;
        while ((socketChannel = this.currentSockets.poll())!= null){
            try {
                socketChannel.configureBlocking(false);
                SelectionKey selectionKey = socketChannel.register(readSelector, SelectionKey.OP_READ);
                selectionKey.attach(this.socketId.incrementAndGet());
                socketChannelMap.put(this.socketId.get(), socketChannel);
            } catch (IOException e) {
                logger.info("io exception");
            }

        }
    }

    private void writeSocket() throws IOException{
        ResponseMessage responseMessage;
        while ((responseMessage = outputQueue.poll()) != null){
            SocketChannel socketChannel = socketChannelMap.get(responseMessage.getSockedId());
            SelectionKey key = socketChannel.keyFor(writeSelector);
            if(key != null){
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                List<ResponseMessage> attachmentList = (List<ResponseMessage>) key.attachment();
                if(attachmentList == null){
                    attachmentList = new ArrayList<>();
                }
                attachmentList.add(responseMessage);
            }else {
                List<ResponseMessage> attachmentList = new ArrayList<>();
                attachmentList.add(responseMessage);
                socketChannel.register(writeSelector, SelectionKey.OP_WRITE, attachmentList);
            }
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
                List<ResponseMessage> attachments = (List<ResponseMessage>) key.attachment();
                SocketChannel socketChannel = (SocketChannel)key.channel();
                for (ResponseMessage attachment : attachments) {
                    writeByteBuffer.clear();
                    logger.info(String.format("write message to: %d , message:%s", attachment.getSockedId(), attachment.getData()));
                    writeByteBuffer.put(attachment.getDataBytes());
                    writeByteBuffer.flip();
                    while (writeByteBuffer.hasRemaining()){
                        socketChannel.write(writeByteBuffer);
                    }
                }

                writeByteBuffer.clear();
                writeByteBuffer.put(MessageHelper.END_SIGN);
                writeByteBuffer.flip();
                socketChannel.write(writeByteBuffer);

                attachments.clear();
            }
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            iterator.remove();
        }
        selectionKeySet.clear();
    }

    private void readSocket() throws IOException {
        int count = readSelector.selectNow();
        if(count == 0){
            return;
        }

        Set<SelectionKey> selectionKeySet = readSelector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeySet.iterator();
        while (iterator.hasNext()){
            SelectionKey key = iterator.next();
            if(key.isReadable()){
                logger.info("start read:"+(long)key.attachment());
                readByteBuffer.clear();
                SocketChannel socketChannel = (SocketChannel) key.channel();
                int readLen;

                try {
                    while ((readLen = socketChannel.read(readByteBuffer)) >= -1) {
                        if (readLen == 0) {
                            break;
                        }

                        if (readLen == -1) {
                            logger.info("close socket:" + (long) key.attachment());
                            key.channel();
                            socketChannel.close();
                            break;
                        }

                        readByteBuffer.flip();

                        while (readByteBuffer.hasRemaining()) {
                            byte[] lengthByte = new byte[2];
                            readByteBuffer.get(lengthByte);
                            int length = (lengthByte[0] << 8) + lengthByte[1];
                            if (length <= 0 || length > (1 << 16)) {
                                logger.info("protocol error");
                            }
                            byte[] bytes = new byte[length];
                            readByteBuffer.get(bytes);
                            logger.info(String.format("receive msg from: %d, message:%s", key.attachment(), new String(bytes)));
                            threadPoolExecutor.submit(new CommandExecuteRunnable((long) key.attachment(), bytes, this.outputQueue));
                        }

                        readByteBuffer.clear();
                    }
                }catch (IOException e){
                    logger.log(Level.WARNING, "IOException while read from socket", e);
                    key.cancel();
                    socketChannel.close();
                }

            }
            iterator.remove();
        }
        selectionKeySet.clear();
    }
}
