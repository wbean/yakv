package me.wbean.ya.yakv.message;

import java.nio.charset.Charset;

public class DefaultMessageDecoder implements MessageDecoder {
    @Override
    public RequestMessage decode(byte[] bytes) {
        String data = new String(bytes, Charset.forName("UTF-8"));
        String[] dataArr = data.split(" ");
        String[] param = null;
        if(dataArr.length > 1){
            param = new String[dataArr.length - 1];
            for(int i = 1; i<dataArr.length; i++){
                param[i-1] = dataArr[i];
            }
        }

        return new RequestMessage(dataArr[0], param);
    }
}
