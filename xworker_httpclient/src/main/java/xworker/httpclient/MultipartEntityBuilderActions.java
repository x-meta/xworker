package xworker.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class MultipartEntityBuilderActions {
    public static HttpEntity create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        String boundary = self.doAction("getBoundary", actionContext);
        String charset = self.doAction("getCharset", actionContext);
        ContentType contentType = HttpClientActions.getContentType(self.doAction("getContentType", actionContext));
        Boolean laxMode = self.doAction("isLaxMode", actionContext);
        String subType = self.doAction("getSubType", actionContext);
        String mode = self.doAction("getMode", actionContext);
        Boolean strictMode = self.doAction("isStrictMode", actionContext);

        if(boundary != null && !boundary.isEmpty()){
            builder.setBoundary(boundary);
        }
        if(charset != null && !charset.isEmpty()){
            builder.setCharset(Charset.forName(charset));
        }
        if(contentType != null){
            builder.setContentType(contentType);
        }
        if(laxMode != null && laxMode){
            builder.setLaxMode();
        }
        if(subType != null && !subType.isEmpty()){
            builder.setMimeSubtype(subType);
        }
        if("BROWSER_COMPATIBLE".equals(mode)){
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        }else if("RFC6532".equals(mode)){
            builder.setMode(HttpMultipartMode.RFC6532);
        }else if("STRICT".equals(mode)){
            builder.setMode(HttpMultipartMode.STRICT);
        }
        if(strictMode != null && strictMode){
            builder.setStrictMode();
        }

        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext, "builder", builder);
        }

        return builder.build();
    }

    public static void createBytesBody(ActionContext actionContext){
        Thing self = actionContext.getObject("self");


        String bodyName = self.doAction("getBodyName", actionContext);
        ContentType contentType = HttpClientActions.getContentType(self.doAction("getContentType", actionContext));
        String filename = self.doAction("getFilename", actionContext);
        byte[] bytes = self.doAction("getBytes", actionContext);

        if(actionContext.getObject("builder") instanceof FormBodyPartBuilder){
            FormBodyPartBuilder builder = actionContext.getObject("builder");

            if (contentType != null) {
                builder.setBody(new ByteArrayBody(bytes, contentType, filename));
            } else {
                builder.setBody(new ByteArrayBody(bytes, filename));
            }
        }else {
            MultipartEntityBuilder builder = actionContext.getObject("builder");

            if (contentType != null && filename != null) {
                builder.addBinaryBody(bodyName, bytes, contentType, filename);
            } else {
                builder.addBinaryBody(bodyName, bytes);
            }
        }
    }

    public static void createFileBody(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String bodyName = self.doAction("getBodyName", actionContext);
        ContentType contentType = HttpClientActions.getContentType(self.doAction("getContentType", actionContext));
        String filename = self.doAction("getFilename", actionContext);
        File file = self.doAction("getFile", actionContext);

        if(actionContext.getObject("builder") instanceof FormBodyPartBuilder){
            FormBodyPartBuilder builder = actionContext.getObject("builder");
            if (contentType != null && filename != null) {
                builder.setBody(new FileBody(file, contentType, filename));
            } else {
                builder.setBody(new FileBody(file));
            }
        }else {
            MultipartEntityBuilder builder = actionContext.getObject("builder");

            if (contentType != null && filename != null) {
                builder.addBinaryBody(bodyName, file, contentType, filename);
            } else {
                builder.addBinaryBody(bodyName, file);
            }
        }
    }

    public static void createInputStreamBody(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String bodyName = self.doAction("getBodyName", actionContext);
        ContentType contentType = HttpClientActions.getContentType(self.doAction("getContentType", actionContext));
        String filename = self.doAction("getFilename", actionContext);
        InputStream inputStream = self.doAction("getInputStream", actionContext);

        if(actionContext.getObject("builder") instanceof FormBodyPartBuilder){
            FormBodyPartBuilder builder = actionContext.getObject("builder");
            if (contentType != null && filename != null) {
                builder.setBody(new InputStreamBody(inputStream, contentType, filename));
            } else {
                builder.setBody(new InputStreamBody(inputStream, filename));
            }
        }else {
            MultipartEntityBuilder builder = actionContext.getObject("builder");

            if (contentType != null && filename != null) {
                builder.addBinaryBody(bodyName, inputStream, contentType, filename);
            } else {
                builder.addBinaryBody(bodyName, inputStream);
            }
        }
    }

    public static void createTextBody(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String bodyName = self.doAction("getBodyName", actionContext);
        ContentType contentType = HttpClientActions.getContentType(self.doAction("getContentType", actionContext));
        String text = self.doAction("getText", actionContext);

        if(actionContext.getObject("builder") instanceof FormBodyPartBuilder){
            FormBodyPartBuilder builder = actionContext.getObject("builder");
            builder.setBody(new StringBody(text, contentType));
        }else {
            MultipartEntityBuilder builder = actionContext.getObject("builder");

            if(contentType != null){
                builder.addTextBody(bodyName, text, contentType);
            }else{
                builder.addTextBody(bodyName, text);
            }
        }
    }

    public static void createFormBodyPartBuilder(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MultipartEntityBuilder builder = actionContext.getObject("builder");

        String bodyName = self.doAction("getBodyName", actionContext);
        Map<String, Object> fields = self.doAction("getFields", actionContext);

        FormBodyPartBuilder formBodyPartBuilder = FormBodyPartBuilder.create();
        formBodyPartBuilder.setName(bodyName);

        if(fields != null){
            for(String key : fields.keySet()){
                formBodyPartBuilder.addField(key, String.valueOf(fields.get(key)));
            }
        }

        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext, "builder", formBodyPartBuilder);
        }
        FormBodyPart formBodyPart =  formBodyPartBuilder.build();
        builder.addPart(formBodyPart);
    }
}
