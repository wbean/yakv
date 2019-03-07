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

    public String[] getParam(){
        return this.param;
    }

    public String getParamOf(int index){
        return this.param[index];
    }
}
