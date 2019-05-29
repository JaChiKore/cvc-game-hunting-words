package edu.uab.cvc.huntingwords.tasks;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class PostSendBuilder {
    private static PostSendBuilder instance = null;

    private PostSendBuilder() {}

    public static PostSendBuilder getInstance() {
        if (instance == null) {
            instance = new PostSendBuilder();
        }
        return (instance);
    }

    public String getPostData(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
