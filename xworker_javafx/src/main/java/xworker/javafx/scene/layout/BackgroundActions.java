package xworker.javafx.scene.layout;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class BackgroundActions {
    public static Background createImageBackground(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");
        Image image = JavaFXUtils.getImage(self, "image", actionContext);
        if (image != null) {
            BackgroundPosition pos = null;
            if("CENTER".equals(self.getString("position"))){
                pos = BackgroundPosition.CENTER;
            }else{
                pos = BackgroundPosition.DEFAULT;
            }
            BackgroundImage bimage = new BackgroundImage(image, BackgroundRepeat.valueOf(self.getString("repeatX")),
                    BackgroundRepeat.valueOf(self.getString("repeatY")), pos, BackgroundSize.DEFAULT);
            Background background = new Background(bimage);
            actionContext.g().put(self.getMetadata().getName(), background);

            return background;
        }

        return null;
    }
}
