package xworker.javafx.thing.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;
import xworker.lang.executor.Executor;

import java.util.function.Consumer;

public class ThingEditor {
    Thing thing;
    Thing currentThing;
    ActionContext actionContext;
    ThingEditorForm editorForm = new ThingEditorForm(this);
    ThingEditorAddChild addChild = new ThingEditorAddChild(this);
    ThingEditorXml xmlEditor = new ThingEditorXml(this);

    ThingEditorContentNode contentNode;

    javafx.scene.control.MenuBar menuBar;
    javafx.scene.control.Label titleLabel;
    javafx.scene.control.Button toolbarDeleteButton;
    javafx.scene.control.Button toolbarEditorModeButton;
    javafx.scene.control.Button toolbarFindButton;
    javafx.scene.control.Button toolbarMoveDownButton;
    javafx.scene.control.Button toolbarMoveUpButton;
    javafx.scene.control.Button toolbarSaveButton;
    javafx.scene.control.TreeView<Thing> outlineTree;
    javafx.scene.layout.VBox contentBox;
    javafx.scene.layout.VBox mainBox;
    javafx.scene.control.MenuButton actionMenuButton;
    javafx.scene.control.Button classStructureButton;
    javafx.scene.control.ChoiceBox<Thing> descriptorChoiceBox;
    SimpleObjectProperty<Callback<Thing, Boolean>> onRemoveProperty;
    SimpleBooleanProperty modifiedProperty = new SimpleBooleanProperty();

    public ThingEditor(){
        actionContext = new ActionContext();
    }

    public ThingEditor(ActionContext actionContext){
        this.actionContext = new ActionContext();
        this.actionContext.put("parentContext", actionContext);

        init();

        modifiedProperty.bindBidirectional(editorForm.thingForm.modifiedProperty());
    }

    private void init(){
        World world = World.getInstance();
        Thing editorThing = world.getThing("xworker.javafx.thing.editor.ThingEditorNode/@Nodes/@mainBox");
        mainBox = editorThing.doAction("create", actionContext);

        menuBar = actionContext.getObject("menuBar");
        titleLabel = actionContext.getObject("titleLabel");
        outlineTree = actionContext.getObject("outlineTree");
        toolbarDeleteButton = actionContext.getObject("toolbarDeleteButton");
        toolbarEditorModeButton = actionContext.getObject("toolbarEditorModeButton");
        toolbarFindButton = actionContext.getObject("toolbarFindButton");
        toolbarMoveDownButton = actionContext.getObject("toolbarMoveDownButton");
        toolbarMoveUpButton = actionContext.getObject("toolbarMoveUpButton");
        toolbarSaveButton = actionContext.getObject("toolbarSaveButton");
        contentBox = actionContext.getObject("contentBox");
        actionMenuButton = actionContext.getObject("actionMenuButton");
        classStructureButton = actionContext.getObject("classStructureButton");
        descriptorChoiceBox = actionContext.getObject("descriptorChoiceBox");

        //默认表单编辑
        contentNode = editorForm;
        contentBox.getChildren().add(contentNode.getNode());

        outlineTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Thing>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Thing>> observable, TreeItem<Thing> oldValue, TreeItem<Thing> newValue) {
                if(newValue == null){
                    return;
                }

                Thing newThing = newValue.getValue();
                if(contentNode.beforeChangeThing(newThing)) {
                }else{
                    //outlineTree.getSelectionModel().selectPrevious();
                }

                setCurrentThing(newValue.getValue());
            }
        });
        descriptorChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Thing>() {
            @Override
            public void changed(ObservableValue<? extends Thing> observable, Thing oldValue, Thing newValue) {
                if(newValue != null){
                    contentNode.setThing(currentThing, newValue);
                }
            }
        });

        classStructureButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ThingStructureViewer viewer = new ThingStructureViewer();
                viewer.setThing(currentThing);
                viewer.show();;
            }
        });

        toolbarSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ThingEditor.this.save();
            }
        });
        toolbarDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ThingEditor.this.remove();
            }
        });
        toolbarMoveDownButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ThingEditor.this.moveDown();
            }
        });
        toolbarMoveUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ThingEditor.this.moveUp();
            }
        });
    }

    /**
     * 设置删除根节点时的回调，如果返回false，那么不删除。
     *
     * @param callback
     */
    public void setOnRemove(Callback<Thing, Boolean> callback){
        if(onRemoveProperty == null){
            onRemoveProperty = new SimpleObjectProperty<>();
        }
        onRemoveProperty.set(callback);
    }

    /**
     * 为当前模型添加一个子节点。
     *
     * @param child
     */
    public void add(Thing child){
        if(currentThing != null && child != null){
            currentThing.addChild(child);
            TreeItem<Thing> parentTtem = outlineTree.getSelectionModel().getSelectedItem();
            TreeItem<Thing> childItem = new TreeItem<>(child);
            parentTtem.getChildren().add(childItem);

            parentTtem.setExpanded(true);
            modifiedProperty.set(true);
        }
    }

    /**
     * 刷新当前节点。
     */
    public void refresh(){
        TreeItem<Thing> parentItem = outlineTree.getSelectionModel().getSelectedItem();
        parentItem.getChildren().clear();
        parentItem.setValue(currentThing);

        for(Thing child : currentThing.getChilds()) {
            initOutlineTreeItem(parentItem, child);
        }
    }

    /**
     * 移除当前节点。
     */
    public void remove(){
        String message = UtilString.getString("lang:d=确实要删除当前节点么？&Are you sure to remove current node?", actionContext);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
            @Override
            public void accept(ButtonType buttonType) {
                if(buttonType == ButtonType.YES){
                    if(currentThing == currentThing.getRoot()){
                        String message = UtilString.getString("lang:d=当前节点是根节点，确定要删除整个模型？&Current nod is root, are you sure to remove whole thing?", actionContext);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
                        alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                            @Override
                            public void accept(ButtonType buttonType) {
                                if(onRemoveProperty != null){
                                    if(onRemoveProperty.get().call(currentThing)){
                                        currentThing.remove();

                                        modifiedProperty.set(true);
                                    }
                                }
                            }
                        });
                    }else{
                        TreeItem<Thing> currentItem = outlineTree.getSelectionModel().getSelectedItem();
                        currentItem.getParent().getChildren().remove(currentItem);

                        Thing parent = currentThing.getParent();
                        parent.removeChild(currentThing);

                        modifiedProperty.set(true);
                    }
                }
            }
        });
    }

    public boolean isModified(){
        return modifiedProperty.get();
    }

    public BooleanProperty modifiedProperty(){
        return modifiedProperty;
    }

    /**
     * 移动当前节点到上一级。
     *
     */
    public void moveUp(){
       Thing parent = currentThing.getParent();
       if(parent != null){
           if(parent.getChilds().indexOf(currentThing) > 0) {
               parent.changeChildIndex(currentThing, -1, -1);
               TreeItem<Thing> currentItem = outlineTree.getSelectionModel().getSelectedItem();
               TreeItem<Thing> parentItem = currentItem.getParent();
               int index = parentItem.getChildren().indexOf(currentItem);
               parentItem.getChildren().set(index, parentItem.getChildren().get(index - 1));
               parentItem.getChildren().set(index - 1, currentItem);

               outlineTree.getSelectionModel().select(currentItem);
               modifiedProperty.set(true);
           }
       }
    }

    /**
     * 移动当前节点到下一级。
     */
    public void moveDown(){
        Thing parent = currentThing.getParent();
        if(parent != null){
            if(parent.getChilds().indexOf(currentThing) < parent.getChilds().size() - 1) {
                parent.changeChildIndex(currentThing, -1, 1);
                TreeItem<Thing> currentItem = outlineTree.getSelectionModel().getSelectedItem();
                TreeItem<Thing> parentItem = currentItem.getParent();
                int index = parentItem.getChildren().indexOf(currentItem);
                parentItem.getChildren().set(index, parentItem.getChildren().get(index + 1));
                parentItem.getChildren().set(index + 1, currentItem);

                outlineTree.getSelectionModel().select(currentItem);
                modifiedProperty.set(true);
            }
        }
    }

    /**
     * 保存正在编辑的模型，。
     */
    public void save(){
        editorForm.save();
        if(contentNode == xmlEditor){
            xmlEditor.save();
        }

        currentThing.save();
        modifiedProperty.set(false);
    }

    private void setCurrentThing(Thing thing){
        //内容编辑区设置模型
        currentThing = thing;

        //标题
        titleLabel.setText(currentThing.getMetadata().getName() + " - " + currentThing.getMetadata().getLabel()  + "(" + currentThing.getThingName() + ")");

        //描述者下拉列表
        descriptorChoiceBox.getItems().clear();
        for(Thing desc : currentThing.getDescriptors()){
            descriptorChoiceBox.getItems().add(desc);
        }
        descriptorChoiceBox.getSelectionModel().select(0);

        //行为列表
        actionMenuButton.getItems().clear();
        for(Thing actionThing : currentThing.getActionsThings()){
            MenuItem menuItem = new MenuItem();
            menuItem.setText(actionThing.getMetadata().getName());
            menuItem.setUserData(actionThing);
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    MenuItem item = (MenuItem) event.getSource();
                    Thing thing = (Thing) item.getUserData();

                    ActionContext actionContext = new ActionContext();
                    actionContext.put("thingEditor", ThingEditor.this);
                    Object result = currentThing.doAction(thing.getMetadata().getName(), actionContext);
                    Executor.info("ThingEditor", "Run action " + thing.getMetadata().getName() + " result=\n" + result);
                }
            });
            actionMenuButton.getItems().add(menuItem);
        }
    }

    public void showForm(){
        if(contentNode != editorForm && contentNode.beforeChangeThing(currentThing)) {
            contentNode = editorForm;
            if (currentThing != null) {
                contentNode.setThing(currentThing, null);
            }
            contentBox.getChildren().clear();
            contentBox.getChildren().add(contentNode.getNode());
        }
    }

    public void showAddChild(){
        if(contentNode != addChild && contentNode.beforeChangeThing(currentThing)) {
            contentNode = addChild;
            if (currentThing != null) {
                contentNode.setThing(currentThing, null);
            }
            contentBox.getChildren().clear();
            contentBox.getChildren().add(contentNode.getNode());
        }
    }

    public void showXmlEditor(){
        if(contentNode != xmlEditor && contentNode.beforeChangeThing(currentThing)) {
            contentNode = xmlEditor;
            if (currentThing != null) {
                contentNode.setThing(currentThing, null);
            }
            contentBox.getChildren().clear();
            contentBox.getChildren().add(contentNode.getNode());
        }
    }

    public void setThing(Thing thing){
        if(thing == null){
            throw new NullPointerException();
        }

        this.thing = thing.getRoot();

        TreeItem<Thing> rootItem = new TreeItem<>(thing);
        outlineTree.setRoot(rootItem);

        for(Thing child : thing.getChilds()){
            initOutlineTreeItem(rootItem, child);
        }

        rootItem.setExpanded(true);
        outlineTree.getSelectionModel().select(0);
    }

    private void initOutlineTreeItem(TreeItem<Thing> parentItem, Thing thing){
        TreeItem<Thing> item = new TreeItem<>(thing);
        parentItem.getChildren().add(item);

        for(Thing child : thing.getChilds()){
            initOutlineTreeItem(item, child);
        }
    }

    public Node getThingEditorNode(){
        return mainBox;
    }

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing thing = self.doAction("getThing", actionContext);
        ThingEditor thingEditor = new ThingEditor(actionContext);
        actionContext.g().put(self.getMetadata().getName(), thingEditor);
        if(thing != null){
            thingEditor.setThing(thing);
        }

        actionContext.peek().put("parent", thingEditor.getThingEditorNode());
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return thingEditor.getThingEditorNode();
    }

}
