package xworker.javafx.thingeditor.editors;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.web.HTMLEditor;
import xworker.javafx.util.JavaFXUtils;
import xworker.lang.executor.Executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SHTMLEditor extends AbstractEditor<File>{
    public static final String TAG = TextFileEditor.class.getName();

    HTMLEditor editor = new HTMLEditor();
    File file;
    SimpleBooleanProperty dirty = new SimpleBooleanProperty(false);

    @Override
    public void setContent(File content) {
        try{
            byte[] bytes = new byte[(int) content.length()];
            FileInputStream fin = new FileInputStream(content);
            fin.read(bytes);
            fin.close();

            editor.setHtmlText(new String(bytes));
            this.file = content;
            dirty.set(false);
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
        return dirty.get();
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
        if(this.file != null){
            try{
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(editor.getHtmlText().getBytes());
                fout.close();
                dirty.set(false);
            }catch(Exception e){
                Executor.warn(TAG, "Save file error, file=" + file);
            }
        }
    }

    @Override
    public Node getEditorNode() {
        return editor;
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
