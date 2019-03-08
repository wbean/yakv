/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.ya.yakv
 */
package me.wbean.ya.yakv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author wbean
 * @date 2019/3/7 下午3:21
 */
public class Client {
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private String host;
    private int port;
    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket("127.0.0.1", 8000);
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    public String execute(String command) throws IOException {
        byte[] msgByte = command.getBytes();

        int unsignedLen = ((short)msgByte.length) & 0x0FFFF;

        byte[] finalMsg = new byte[2];
        finalMsg[0] = (byte) ((unsignedLen & 0xff00) >> 8);
        finalMsg[1] = (byte) (unsignedLen & 0xff);

        finalMsg = ArrayUtils.addAll(finalMsg, msgByte);

        out.write(finalMsg);

        byte[] responseLen = new byte[2];
        StringBuilder sb = new StringBuilder();
        while (in.read(responseLen) > 0){
            int length = (responseLen[0] << 8) + responseLen[1];
            if(length == 0){
                //长度为0，表示结束标志位
                break;
            }
            byte[] responseBody = new byte[length];
            in.read(responseBody);
            sb.append(new String(responseBody));
        }

        return sb.toString();
    }

    public void close() throws IOException {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        String host;
        int port;
        if(args.length == 0){
            host = "127.0.0.1";
            port = 8000;
        }else {
            host = args[0];
            port = Integer.valueOf(args[1]);
        }

        Client client = new Client(host, port);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ( !"bye".equals(line = bufferedReader.readLine())){
            if(StringUtils.isBlank(line)){
                continue;
            }
            String ret = client.execute(line);
            System.out.println(ret);
        }

        client.close();
    }
}
