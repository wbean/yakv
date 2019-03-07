package me.wbean.ya.yakv.command;

import me.wbean.ya.yakv.message.RequestMessage;
import me.wbean.ya.yakv.message.ResponseMessage;

public class HeartBeatCommand implements Command{

    @Override
    public ResponseMessage execute(RequestMessage request){
        return new ResponseMessage("hello");
    }
}
