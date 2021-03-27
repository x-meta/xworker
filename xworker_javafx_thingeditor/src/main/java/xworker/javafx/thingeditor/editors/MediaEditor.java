package xworker.javafx.thingeditor.editors;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.HTMLEditor;
import xworker.javafx.util.JavaFXUtils;
import xworker.lang.executor.Executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MediaEditor extends AbstractEditor<File>{
    public static final String TAG = TextFileEditor.class.getName();

    VBox node = new VBox();
    File file;

    @Override
    public void setContent(File content) {
        try{
            Media media = new Media(content.toURI().toURL().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            MediaView mediaView = new MediaView(mediaPlayer);
            node.getChildren().clear();
            node.getChildren().add(mediaView);
            VBox.setVgrow(mediaView, Priority.ALWAYS);

            this.file = content;
        }catch(Exception e){
            Executor.warn(TAG, "Open file error, file=" + content, e);
        }
    }

    @Override
    public File getContent() {
        return file;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public String getTitle() {
        return file.getAbsolutePath();
    }

    @Override
    public String getLabel() {
        return file.getName();
    }

    @Override
    public String getTooltip() {
        return file.getAbsolutePath();
    }

    @Override
    public void save() {
    }

    @Override
    public Node getEditorNode() {
        return node;
    }

    @Override
    public void init() {

    }

    @Override
    public String getId() {
        try {
            return file.getCanonicalPath();
        }catch (Exception e){
            return file.getAbsolutePath();
        }
    }

    @Override
    public Image getIcon() {
        return JavaFXUtils.getImage(file);
    }
}
