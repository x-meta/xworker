package xworker.javafx.thingeditor;

import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 编辑器的接口，在编辑器区域打开的。
 */
public interface Editor<T> {
    public void setContent(T content);

    public T getContent();

    public boolean isDirty();

    public String getTitle();

    public String getLabel();

    public String getTooltip();

    public void save();

    public Node getEditorNode();

    public void init();

    public String getId();

    public Image getIcon();

    public void setSimpleThingEditor(SimpleThingEditor simpleThingEditor);
}
