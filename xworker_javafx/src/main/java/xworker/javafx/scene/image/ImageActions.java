package xworker.javafx.scene.image;

import javafx.scene.image.Image;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

import java.io.InputStream;

public class ImageActions {
    public static Image create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object url = JavaFXUtils.getObject(self, "url", actionContext);
        if(url == null){
            return null;
        }

        Image image = null;
        boolean requestedWidth = self.valueExists("requestedWidth");
        boolean requestedHeight = self.valueExists("requestedHeight");
        boolean preserveRatio = self.valueExists("preserveRatio");
        boolean smooth = self.valueExists("smooth");
        boolean backgroundLoading = self.valueExists("backgroundLoading");

        if(url instanceof String){
            if(requestedWidth && requestedHeight && preserveRatio && smooth && backgroundLoading){
                image = new Image((String)url , self.getDouble("requestedWidth"),
                        self.getDouble("requestedHeight"),
                        self.getBoolean("preserveRatio"),
                        self.getBoolean("smooth"),
                        self.getBoolean("backgroundLoading"));
            }else if(requestedWidth && requestedHeight && preserveRatio && smooth){
                image = new Image((String)url , self.getDouble("requestedWidth"),
                        self.getDouble("requestedHeight"),
                        self.getBoolean("preserveRatio"),
                        self.getBoolean("smooth"));
            }else if(backgroundLoading){
                image = new Image((String) url, self.getBoolean("backgroundLoading"));
            }else{
                image = new Image((String) url);
            }
        }else if(url instanceof InputStream){
            if(requestedWidth && requestedHeight && preserveRatio && smooth){
                image = new Image((InputStream) url , self.getDouble("requestedWidth"),
                        self.getDouble("requestedHeight"),
                        self.getBoolean("preserveRatio"),
                        self.getBoolean("smooth"));
            }else{
                image = new Image((InputStream) url);
            }
        }
        actionContext.g().put(self.getMetadata().getName(), image);

        actionContext.peek().put("parent", image);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return image;
    }
}
