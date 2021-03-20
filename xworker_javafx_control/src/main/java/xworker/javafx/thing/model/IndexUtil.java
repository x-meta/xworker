package xworker.javafx.thing.model;

import javafx.scene.image.Image;
import org.xmeta.Index;
import xworker.javafx.util.JavaFXUtils;

import java.io.File;

public class IndexUtil {
    public static Image getImage(Index index){
        Image image = null;
        String type = index.getType();
        if(Index.TYPE_CATEGORY.equals(type)){
            image = JavaFXUtils.getImage("icons/category.gif");
        }else if(Index.TYPE_THINGMANAGER.equals(type)){
            image = JavaFXUtils.getImage("icons/thingmanager.gif");
        }else if(Index.TYPE_THING.equals(type)){
            image = JavaFXUtils.getImage("icons/thing.gif");
        }else if(Index.TYPE_WORKING_SET.equals(type)){
            image = JavaFXUtils.getImage("icons/workingset.gif");
        }else if(Index.TYPE_PROJECT.equals(type)){
            image = JavaFXUtils.getImage("icons/project.png");
        }else if("parent".equals(type)){
            image = JavaFXUtils.getImage("icons/parent.gif");
        }else if(Index.TYPE_FILE.equals(type)){
            image = JavaFXUtils.getImage((File) index.getIndexObject());
        }

        return image;
    }
}
