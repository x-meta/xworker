package xworker.javafx.scene.media;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.NodeActions;
import xworker.javafx.util.JavaFXUtils;

public class MediaViewActions {
    public static void init(MediaView node, Thing thing, ActionContext actionContext){
        NodeActions.init(node, thing, actionContext);

        if(thing.valueExists("fitHeight")){
            node.setFitHeight(thing.getDouble("fitHeight"));
        }
        if(thing.valueExists("fitWidth")){
            node.setFitWidth(thing.getDouble("fitWidth"));
        }
        if(thing.valueExists("mediaPlayer")){
            MediaPlayer mediaPlayer = JavaFXUtils.getObject(thing, "mediaPlayer", actionContext);
            if(mediaPlayer != null) {
                node.setMediaPlayer(mediaPlayer);
            }
        }
        if(thing.valueExists("preserveRatio")){
            node.setPreserveRatio(thing.getBoolean("preserveRatio"));
        }
        if(thing.valueExists("smooth")){
            node.setSmooth(thing.getBoolean("smooth"));
        }
        if(thing.valueExists("viewport")){
            Rectangle2D viewport = JavaFXUtils.getRectangle2D(thing, "viewport", actionContext);
            if(viewport != null) {
                node.setViewport(viewport);
            }
        }
        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static MediaView create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Region parent = actionContext.getObject("parent");

        Media media = new Media("file:/e:/git/xworker/xworker_explorer/xworker/webroot/temp/S03E01.mp4");
        MediaPlayer mediaPlayer1 = new MediaPlayer(media);
        MediaView node = new MediaView(mediaPlayer1);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof MediaPlayer){
                MediaPlayer mediaPlayer = (MediaPlayer) obj;
                node.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();
            }
        }

        Platform.runLater(() -> {
            mediaPlayer1.play();
            System.out.println("Played");
        });
        return node;
    }
}
