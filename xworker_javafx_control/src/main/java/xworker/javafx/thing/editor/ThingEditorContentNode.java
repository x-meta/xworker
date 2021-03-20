package xworker.javafx.thing.editor;

import javafx.scene.Node;
import org.xmeta.Thing;

public interface ThingEditorContentNode {
    /**
     * 保存。
     *
     * @return
     */
    public boolean save();

    /**
     * 要改变编辑的模型了。
     *
     * @param newThing
     * @return
     */
    public boolean beforeChangeThing(Thing newThing);

    public void setThing(Thing thing, Thing descriptor);

    public Node getNode();
}
