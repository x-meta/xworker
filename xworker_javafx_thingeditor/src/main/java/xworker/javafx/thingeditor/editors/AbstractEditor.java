package xworker.javafx.thingeditor.editors;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import xworker.javafx.thingeditor.Editor;
import xworker.javafx.thingeditor.SimpleThingEditor;

public abstract  class AbstractEditor<T> implements Editor<T> {
    SimpleThingEditor simpleThingEditor;

    public void setSimpleThingEditor(SimpleThingEditor simpleThingEditor){
        this.simpleThingEditor = simpleThingEditor;
    }

    public Node getStructureNode(){
        return new VBox();
    }
}
