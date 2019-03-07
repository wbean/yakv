package me.wbean.ya.yakv;

import me.wbean.ya.yakv.command.Command;
import me.wbean.ya.yakv.command.CommandFactory;
import me.wbean.ya.yakv.message.DefaultMessageDecoder;
import me.wbean.ya.yakv.message.RequestMessage;
import me.wbean.ya.yakv.message.MessageDecoder;
import me.wbean.ya.yakv.message.ResponseMessage;

import java.util.Queue;

public class CommandExecuteRunnable implements Runnable {
    byte[] requestBody;
    MessageDecoder messageDecoder;
    int socketId;
    Queue<ResponseMessage> outputQueue;

    public CommandExecuteRunnable(int socketId, byte[] requestBody, Queue<ResponseMessage> outputQueue){
        this.socketId = socketId;
        this.requestBody = requestBody;
        this.messageDecoder = new DefaultMessageDecoder();
        this.outputQueue = outputQueue;
    }

    @Override
    public void run() {
        RequestMessage request = this.messageDecoder.decode(requestBody);
        Command command = CommandFactory.getCommand(request.getCommand());
        if(command == null){
            this.outputQueue.offer(new ResponseMessage(socketId, "error command"));
        }
        ResponseMessage result = command.execute(request);
        result.setSockedId(socketId);
        this.outputQueue.offer(result);
    }
}
