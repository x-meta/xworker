package xworker.javafx.thing.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.util.XWorkerUtils;

public class ThingStructureViewer {
    Thing thing;
    Stage stage;

    javafx.scene.control.ListView<String> actionListView;
    javafx.scene.control.ListView<String> classListView;
    javafx.scene.control.ListView<String> extendsListView;
    javafx.scene.control.TextField thingPathText;
    javafx.scene.web.WebView webView;

    public ThingStructureViewer(){
        ActionContext actionContext = new ActionContext();
        Thing nodeThing = World.getInstance().getThing("xworker.javafx.thing.editor.ThingStructureViewer");
        stage = nodeThing.doAction("create", actionContext);

        actionListView = actionContext.getObject("actionListView");
        classListView = actionContext.getObject("classListView");
        extendsListView = actionContext.getObject("extendsListView");
        thingPathText = actionContext.getObject("thingPathText");
        webView = actionContext.getObject("webView");

        ChangeListener<String> changeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null){
                    Thing thing = World.getInstance().getThing(newValue);
                    if(thing != null){
                        webView.getEngine().loadContent(XWorkerUtils.getThingDesc(thing));
                    }
                }
            }
        };
        actionListView.getSelectionModel().selectedItemProperty().addListener(changeListener);
        classListView.getSelectionModel().selectedItemProperty().addListener(changeListener);
        extendsListView.getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    public void setThing(Thing thing){
        this.thing = thing;

        thingPathText.setText(thing.getMetadata().getPath());
        //类
        classListView.getItems().clear();
        for(Thing desc : thing.getAllDescriptors()){
            classListView.getItems().add(desc.getMetadata().getPath());
        }

        //继承
        extendsListView.getItems().clear();
        for(Thing ext : thing.getAllExtends()){
            extendsListView.getItems().add(ext.getMetadata().getPath());
        }

        //动作
        actionListView.getItems().clear();
        for(Thing action : thing.getActionsThings()){
            actionListView.getItems().add(action.getMetadata().getName());
        }
    }

    public void show(){
        stage.show();
    }

}
