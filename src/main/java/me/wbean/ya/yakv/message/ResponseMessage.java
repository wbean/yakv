package me.wbean.ya.yakv.message;

public class ResponseMessage {

    private int sockedId;

    private String data;

    public ResponseMessage(String data){
        this.data = data;
    }

    public ResponseMessage(int sockedId, String data) {
        this.sockedId = sockedId;
        this.data = data;
    }

    public void setSockedId(int sockedId) {
        this.sockedId = sockedId;
    }

    public String getData(){
        return this.data;
    }

    public int getSockedId(){
        return this.sockedId;
    }
}
