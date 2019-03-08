package me.wbean.ya.yakv.message;

public class ResponseMessage {

    private long sockedId;

    private String data;

    public ResponseMessage(String data){
        this.data = data;
    }

    public ResponseMessage(long sockedId, String data) {
        this.sockedId = sockedId;
        this.data = data;
    }

    public void setSockedId(long sockedId) {
        this.sockedId = sockedId;
    }

    public String getData(){
        return this.data;
    }

    public long getSockedId(){
        return this.sockedId;
    }

    public byte[] getDataBytes(){
        if(data == null){
            return MessageHelper.addHeader("null".getBytes());
        }else {
            return MessageHelper.addHeader(this.data.getBytes());
        }
    }
}
