package xworker.javafx.scene.paint;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class ImagePatternActions {
    public static ImagePattern create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Image image = JavaFXUtils.getImage(self, "image", actionContext);
        ImagePattern ip = null;
        if(image != null){
            if(self.valueExists("x") && self.valueExists("y") && self.valueExists("width")
                    && self.valueExists("height") && self.valueExists("proportional")){
                ip = new ImagePattern(image, self.getDouble("x"), self.getDouble("y"),
                        self.getDouble("width"), self.getDouble("height"),
                        self.getBoolean("proportional"));
            }else{
                ip = new ImagePattern(image);
            }
        }

        actionContext.g().put(self.getMetadata().getName(), ip);
        return ip;
    }
}
