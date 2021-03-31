package xworker.javafx.thingeditor.editors;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.xmeta.Thing;
import xworker.javafx.thing.editor.ThingEditor;
import xworker.javafx.thing.editor.ThingEditorEvent;
import xworker.javafx.util.JavaFXUtils;
import xworker.util.XWorkerUtils;

public class SThingEditor extends AbstractEditor<Thing>{
    ThingEditor editor = new ThingEditor();
    Thing thing;
    VBox strucutreNode = new VBox();
    WebView webView = new WebView();

    public SThingEditor(){
        strucutreNode.getChildren().add(webView);
        VBox.setVgrow(webView, Priority.ALWAYS);
    }

    @Override
    public Node getStructureNode() {
        return strucutreNode;
    }

    @Override
    public void init(){
        editor.setOnSelectThing(new EventHandler<ThingEditorEvent>() {
            @Override
            public void handle(ThingEditorEvent event) {
                org.xmeta.Thing thing = event.getThingEditor().getCurrentThing();
                if(thing != null){
                    JavaFXUtils.showThingDesc(thing.getDescriptor(), webView);
                }
            }
        });
        editor.setOnRemove(new Callback<org.xmeta.Thing, Boolean>() {
            @Override
            public Boolean call(org.xmeta.Thing param) {
                simpleThingEditor.refreshPackageView();
                simpleThingEditor.removeEditor(SThingEditor.this);
                return true;
            }
        });
    }

    @Override
    public void setContent(Thing content) {
        this.thing = content.getRoot();
        editor.setThing(content);
    }

    @Override
    public Thing getContent() {
        return thing;
    }

    @Override
    public boolean isDirty() {
        return editor.isModified();
    }

    @Override
    public String getTitle() {
        return thing.getMetadata().getThingManager().getName() + ":" + thing.getMetadata().getPath();
    }

    @Override
    public String getLabel() {
        return thing.getMetadata().getLabel();
    }

    @Override
    public String getTooltip() {
        return thing.getMetadata().getThingManager().getName() + ":" + thing.getMetadata().getPath();
    }

    @Override
    public void save() {
        editor.save();
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public Node getEditorNode() {
        return editor.getThingEditorNode();
    }

    @Override
    public String getId() {
        return "thing:" + thing.getMetadata().getPath();
    }
}
