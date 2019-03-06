package me.wbean.ya.yakv.message;

public interface MessageDecoder {
    RequestMessage decode(byte[] bytes);
}
