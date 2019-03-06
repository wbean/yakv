package me.wbean.ya.yakv.command;

import me.wbean.ya.yakv.message.RequestMessage;
import me.wbean.ya.yakv.message.ResponseMessage;

public interface Command {
    ResponseMessage execute(RequestMessage request);
}
