package xworker.javafx.dataobject;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionField;
import xworker.dataObject.DataObject;
import xworker.dataObject.DataStore;
import xworker.javafx.util.FXThingLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataObjectEditor {
    Thing thing;
    ActionContext actionContext;
    ActionContext parentContext;

    /** 按钮列表，主要是哪些选中数据才有效的按钮 */
    List<Node> buttons = new ArrayList<>();

    @ActionField
    public javafx.scene.control.ButtonBar buttonBar;
    @ActionField
    public javafx.scene.control.Button createButton;
    @ActionField
    public xworker.dataObject.DataStore dataStore;
    @ActionField
    public javafx.scene.control.Button deleteButton;
    @ActionField
    public javafx.scene.control.Button editButton;
    @ActionField
    public javafx.scene.control.Pagination pagination;
    @ActionField
    public javafx.scene.control.Button queryButton;
    @ActionField
    public xworker.javafx.dataobject.DataObjectForm queryForm;
    @ActionField
    public javafx.scene.layout.VBox rootNode;
    @ActionField
    public javafx.scene.control.TableView<DataObject> tableView;

    Map<String, Object> initValues = new HashMap<>();

    public DataObjectEditor(Thing thing, ActionContext parentContext){
        this.thing = thing;
        this.parentContext = parentContext;
        this.actionContext = new ActionContext();
        this.actionContext.put("parentContext", parentContext);

        initNode();

        Thing dataObject = thing.doAction("getDataObject", parentContext);
        Thing condition = thing.doAction("getCondition", parentContext);
        Thing queryFormThing = thing.doAction("getQueryForm", parentContext);

        if(dataObject != null){
            if(condition == null){
                condition = dataObject.getThing("Condition@0");
            }
            if(queryFormThing == null){
                Thing qs = dataObject.getThing("QueryFormDataObject@0");
                if(qs != null && qs.getChilds().size() > 0){
                    queryFormThing = qs.getChilds().get(0);
                }
            }
        }

        if(dataObject != null){
            dataStore.setDataObject(dataObject, condition);
        }
        if(queryFormThing != null){
            queryForm.setDataObject(queryFormThing);
        }else{
            queryForm.setDataObject(dataObject);
        }

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataObject>() {
            @Override
            public void changed(ObservableValue<? extends DataObject> observable, DataObject oldValue, DataObject newValue) {
                thing.doAction("selectionChanged", parentContext, "oldValue", oldValue,"newValue", newValue, "data"
                        , "editor", this);
            }
        });
        query();

    }

    public TableView<DataObject> getTableView(){
        return tableView;
    }

    public DataStore getDataStore(){
        return dataStore;
    }

    public DataObjectForm getQueryForm(){
        return queryForm;
    }

    public void setCreateInitValues(Map<String, Object> initValues){
        this.initValues = initValues;
    }

    public void setQueryForm(Thing queryFormThing){
        queryForm.setDataObject(queryFormThing);
    }

    public void setDataObject(Thing dataObject){
        dataStore.setDataObject(dataObject);
    }

    public void setDataObject(Thing dataObject, Thing condition){
        dataStore.setDataObject(dataObject, condition);
    }


    private void initNode() {
        Thing thing = World.getInstance().getThing("xworker.javafx.dataobject.prototype.DataObjectPrototype/@Nodes/@rootNode");
        actionContext.push().put("check", this.thing.getBoolean("check"));
        try {
            FXThingLoader.load(this, thing, actionContext);
        } finally {
            actionContext.pop();
        }

        if(!this.thing.getBoolean("createButton")){
            buttonBar.getButtons().remove(createButton);
        }
        if(!this.thing.getBoolean("editButton")){
            buttonBar.getButtons().remove(editButton);
        }
        if(!this.thing.getBoolean("deleteButton")){
            buttonBar.getButtons().remove(deleteButton);
        }
    }

    public Node getRootNode(){
        return rootNode;
    }

    public void query(){
        Map<String, Object> values = queryForm.getValues();
        dataStore.load(values);
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        DataObjectEditor dataObjectEditor = new DataObjectEditor(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), dataObjectEditor);

        actionContext.peek().put("parent", dataObjectEditor.getRootNode());
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return dataObjectEditor.getRootNode();
    }
}
