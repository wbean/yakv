/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv.server
 */
package me.wbean.ya.yakv.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import sun.jvm.hotspot.runtime.Bytes;

/**
 *
 * @author wbean
 * @date 2019/3/6 下午1:49
 */
public class IOProcessRunnable implements Runnable{
    private static final Logger logger = Logger.getLogger(IOProcessRunnable.class.getName());
    private Selector ioSelector;

    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2<<16);

    public IOProcessRunnable(Selector ioSelector){
        this.ioSelector = ioSelector;
    }

    @Override
    public void run() {
        while (true){
            try {
                int count = ioSelector.select();
                if(count == 0){
                    continue;
                }

                Iterator<SelectionKey> iterator = ioSelector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        logger.info("start read");
                        byteBuffer.clear();
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        int readLen;


                        while ((readLen = socketChannel.read(byteBuffer)) >= -1){
                            if(readLen == 0){
                                break;
                            }

                            if(readLen == -1){
                                socketChannel.close();
                                break;
                            }

                            byteBuffer.flip();

                            byte[] lengthByte = new byte[2];
                            byteBuffer.get(lengthByte);

                            int length = lengthByte[0] << 8 + lengthByte[1];
                            byte[] bytes = new byte[length];
                            if (byteBuffer.hasRemaining()){
                                byteBuffer.get(bytes);
                            }

                            byteBuffer.compact();
                            logger.info("receive msg:" + new String(bytes));
                        }

                    }

                    iterator.remove();
                }

            } catch (IOException e) {
            }
        }
    }
}
