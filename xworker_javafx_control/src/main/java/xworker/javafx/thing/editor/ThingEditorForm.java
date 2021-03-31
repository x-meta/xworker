package xworker.javafx.thing.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.javafx.thing.form.ThingForm;

import java.util.HashMap;
import java.util.Map;

public class ThingEditorForm implements ThingEditorContentNode{
    ThingEditor thingEditor;
    Node node;

    javafx.scene.control.Button addChildButton;
    javafx.scene.control.Button saveButton;
    xworker.javafx.thing.form.ThingForm thingForm;
    javafx.scene.control.Button toXmlButton;

    Map<String, Map<String, Object>> valuesChache = new HashMap<>();

    Thing thing;

    public ThingEditorForm(ThingEditor thingEditor){
        this.thingEditor = thingEditor;

        Thing nodeThing = World.getInstance().getThing("xworker.javafx.thing.editor.FormNode/@Nodes/@formBox");
        ActionContext actionContext = new ActionContext();
        node = nodeThing.doAction("create", actionContext);
        addChildButton = actionContext.getObject("addChildButton");
        saveButton = actionContext.getObject("saveButton");
        thingForm = actionContext.getObject("thingForm");
        toXmlButton = actionContext.getObject("toXmlButton");

        addChildButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thingEditor.showAddChild();
            }
        });
        toXmlButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thingEditor.showXmlEditor();
            }
        });

    }

    public Node getNode(){
        return node;
    }

    @Override
    public boolean save() {
        if(this.thing != null){
            valuesChache.put(this.thing.getMetadata().getPath(), thingForm.getValues());
        }

        World world = World.getInstance();
        for(String key : valuesChache.keySet()){
            Thing thing = world.getThing(key);
            if(thing != null){
                thing.putAll(valuesChache.get(key));

            }
        }

        valuesChache.clear();
        return true;
    }

    @Override
    public boolean beforeChangeThing(Thing newThing) {
        if(this.thing != null){
            valuesChache.put(this.thing.getMetadata().getPath(), thingForm.getValues());
        }
        return true;
    }

    public void setThing(Thing thing, Thing descriptor){
        if(thing != this.thing && this.thing != null){
            //保存编辑的缓存
            valuesChache.put(this.thing.getMetadata().getPath(), thingForm.getValues());
        }

        this.thing = thing;
        thingForm.setThing(thing, descriptor);

        Map<String, Object> values = valuesChache.get(thing.getMetadata().getPath());
        if(values != null){
            thingForm.setValues(values);
        }
    }

    public ThingForm getThingForm(){
        return thingForm;
    }


}
