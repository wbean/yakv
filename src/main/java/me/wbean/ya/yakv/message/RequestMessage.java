package me.wbean.ya.yakv.message;

public class RequestMessage {
    private String command;

    private String[] param;

    public RequestMessage(String command, String[] param){
        this.command = command;
        this.param = param;
    }

    public String getCommand(){
        return this.command;
    }
}
