package xworker.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityBuilderActions {
    public static HttpEntity create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        EntityBuilder builder = EntityBuilder.create();
        Boolean chunked = self.doAction("isChunked", actionContext);
        byte[] binary = self.doAction("getBinary", actionContext);
        String contentEncoding = self.doAction("getContentEncoding", actionContext);
        ContentType contentType = HttpClientActions.getContentType(self.doAction("getContentType", actionContext));
        File file = self.doAction("getFile", actionContext);
        Serializable serializable = self.doAction("getSerializable", actionContext);
        InputStream stream = self.doAction("getStream", actionContext);
        String text = self.doAction("getText", actionContext);
        Map<String, Object> parameters = self.doAction("getParameters", actionContext);
        Boolean gzipCompress = self.doAction("isGzipCompress", actionContext);

        if(chunked){
            builder.chunked();
        }
        if(binary != null){
            builder.setBinary(binary);
        }
        if(contentEncoding != null){
            builder.setContentEncoding(contentEncoding);
        }
        if(contentType != null){
            builder.setContentType(contentType);
        }
        if(file != null){
            builder.setFile(file);
        }
        if(serializable != null){
            builder.setSerializable(serializable);
        }
        if(stream != null){
            builder.setStream(stream);
        }
        if(text != null){
            builder.setText(text);
        }
        if(gzipCompress){
            builder.gzipCompress();
        }
        if(parameters != null){
            List<NameValuePair> params = new ArrayList<>();
            for(String key : parameters.keySet()){
                Object value = parameters.get(key);
                params.add(new BasicNameValuePair(key, String.valueOf(value)));
            }
            if(params.size() > 9){
                builder.setParameters(params);
            }
        }

        return builder.build();
    }
}
