package xworker.javafx.thingeditor.editors;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import org.xmeta.ActionContext;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.javafx.thing.model.IndexTableViewModelEvent;
import xworker.javafx.thingeditor.Editor;
import xworker.javafx.thingeditor.SimpleThingEditor;
import xworker.javafx.util.FXThingLoader;
import xworker.javafx.util.JavaFXUtils;
import xworker.lang.executor.Executor;

public class PackageViewEditor extends AbstractEditor<Index>{
    Thing thing;
    @FXML
    TableView packageTabView;
    ActionContext actionContext = new ActionContext();
    @ActionField
    public xworker.javafx.thing.model.IndexTableViewModel packageTabModel;
    Index index;

    public PackageViewEditor(){
        thing = World.getInstance().getThing("xworker.javafx.thingeditor.editors.PackageViewEditor/@148/@packageTabView");
        FXThingLoader.load(this, thing, actionContext);

        packageTabModel.setOnOpenIndex(new EventHandler<IndexTableViewModelEvent>() {
            @Override
            public void handle(IndexTableViewModelEvent event) {
                Index index = event.getIndex();
                Editor<?> editor = EditorFactory.createEditor(index.getIndexObject());
                if(editor != null){
                    simpleThingEditor.openEditor(editor);
                }
            }
        });
    }

    @Override
    public void setContent(Index content) {
        this.index = content;
        packageTabModel.setIndex(this.index);
    }

    @Override
    public Index getContent() {
        return packageTabModel.getSelectionModel().getSelectedItem();
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public String getTitle() {
        return thing.getMetadata().getLabel();
    }

    @Override
    public String getLabel() {
        return thing.getMetadata().getLabel();
    }

    @Override
    public String getTooltip() {
        return thing.getMetadata().getLabel();
    }

    @Override
    public void save() {
    }

    @Override
    public Node getEditorNode() {
        return packageTabView;
    }

    @Override
    public void init() {

    }

    @Override
    public String getId() {
        return SimpleThingEditor.ID_PACKAGEVIEW;
    }

    @Override
    public Image getIcon() {
        return JavaFXUtils.getImage("icons/category.gif");
    }
}
