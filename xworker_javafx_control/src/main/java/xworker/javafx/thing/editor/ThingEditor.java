package xworker.javafx.thing.editor;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.cache.ThingCache;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.UtilString;
import xworker.javafx.util.FXThingLoader;
import xworker.javafx.util.JavaFXUtils;
import xworker.lang.executor.Executor;
import xworker.thingeditor.ThingMenu;

import java.text.DateFormat;
import java.util.function.Consumer;

public class ThingEditor {
    Thing thing;
    Thing currentThing;
    ActionContext actionContext;
    ThingEditorForm editorForm = new ThingEditorForm(this);
    ThingEditorAddChild addChild = new ThingEditorAddChild(this);
    ThingEditorXml xmlEditor = new ThingEditorXml(this);

    ThingEditorContentNode contentNode;

    @FXML
    javafx.scene.control.MenuBar menuBar;
    @FXML
    javafx.scene.control.Label titleLabel;
    @FXML
    javafx.scene.control.Button toolbarDeleteButton;
    @FXML
    javafx.scene.control.Button toolbarEditorModeButton;
    @FXML
    javafx.scene.control.Button toolbarFindButton;
    @FXML
    javafx.scene.control.Button toolbarMoveDownButton;
    @FXML
    javafx.scene.control.Button toolbarMoveUpButton;
    @FXML
    javafx.scene.control.Button toolbarSaveButton;
    @FXML
    javafx.scene.control.TreeView<Thing> outlineTree;
    @FXML
    javafx.scene.layout.VBox contentBox;
    @FXML
    javafx.scene.layout.VBox mainBox;
    @FXML
    javafx.scene.control.MenuButton actionMenuButton;
    @FXML
    javafx.scene.control.Button classStructureButton;
    @FXML
    javafx.scene.control.ChoiceBox<Thing> descriptorChoiceBox;
    @FXML
    SimpleObjectProperty<Callback<Thing, Boolean>> onRemoveProperty;
    SimpleBooleanProperty modifiedProperty = new SimpleBooleanProperty();

    public ThingEditor(){
        this(new ActionContext());
    }

    public ThingEditor(ActionContext actionContext){
        this.actionContext = new ActionContext();
        this.actionContext.put("parentContext", actionContext);

        init();

        modifiedProperty.bindBidirectional(editorForm.thingForm.modifiedProperty());
        modifiedProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    titleLabel.setTextFill(Color.RED);
                }else{
                    titleLabel.setTextFill(Color.BLACK);
                }
            }
        });
    }

    private void init(){
        World world = World.getInstance();
        Thing editorThing = world.getThing("xworker.javafx.thing.editor.ThingEditorNode/@Nodes/@mainBox");
        mainBox = FXThingLoader.load(this, editorThing, actionContext);

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
                if(contentNode == addChild){
                    showForm();
                }

                if(onSelectThingProperty().get() != null){
                    onSelectThingProperty().get().handle(new ThingEditorEvent(ThingEditor.this));
                }
            }
        });

        //双击一个节点重新加载该节点
        outlineTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() >= 2){
                    TreeItem<Thing> treeItem = outlineTree.getSelectionModel().getSelectedItem();
                    if(treeItem != null){
                        setCurrentThing(treeItem.getValue());
                        if(contentNode == addChild){
                            showForm();
                        }
                    }
                }
            }
        });
        descriptorChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Thing>() {
            @Override
            public void changed(ObservableValue<? extends Thing> observable, Thing oldValue, Thing newValue) {
                if(newValue != null){
                    contentNode.setThing(currentThing, newValue);

                    //菜单
                    menuBar.getMenus().clear();
                    for(ThingMenu thingMenu : ThingMenu.getThingMenus(currentThing, newValue, "javafx", actionContext)){
                        Menu menu = new Menu();
                        menu.setText(thingMenu.getLabel());
                        menuBar.getMenus().add(menu);

                        for(ThingMenu childMenu : thingMenu.getChilds()){
                            initMenu(childMenu, menu);
                        }
                    }
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

    private void initMenu(ThingMenu thingMenu, Menu menu){
        MenuItem menuItem = null;

        if(thingMenu.getChilds().size() > 0){
            menuItem = new Menu();
            for(ThingMenu childThingMenu : thingMenu.getChilds()){
                initMenu(childThingMenu, (Menu) menuItem);
            }
        }else{
            menuItem = new MenuItem();
            menuItem.setOnAction(new ThingEditorMenuEventHandler(thingMenu));
        }

        menuItem.setText(thingMenu.getLabel());
        Image image = JavaFXUtils.getImage(thingMenu.getThing(), "icon", actionContext);
        if(image != null){
            menuItem.setGraphic(new ImageView(image));
        }
        menu.getItems().add(menuItem);
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
            child = child.detach();
            currentThing.addChild(child);
            TreeItem<Thing> parentTtem = outlineTree.getSelectionModel().getSelectedItem();
            TreeItem<Thing> childItem = new TreeItem<>(child);
            parentTtem.getChildren().add(childItem);

            parentTtem.setExpanded(true);
            modifiedProperty.set(true);
        }
    }

    /**
     * 复制当前节点的模型路径到剪切板。
     */
    public void copyPath(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentThing.getMetadata().getPath());
        clipboard.setContent(content);
    }

    /**
     * 以XML格式复制当前节点的模型到剪切板，并尝试删除当前节点。
     */
    public void cut(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentThing.toXML());
        clipboard.setContent(content);

        remove();
    }

    /**
     * 以XML格式复制当前节点的模型到剪切板。
     */
    public void copy(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(currentThing.toXML());
        clipboard.setContent(content);
    }

    /**
     * 检查系统剪切板中的内容，如果是XML那么尝试解析成模型，如果成功那么粘贴到当前节点，并刷新节点。
     */
    public void paste(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
        if(content != null && !content.isEmpty()){
            try {
                Thing thing = new Thing();
                XmlCoder.parse(thing, content);

                currentThing.paste(thing);
                refresh();

                modifiedProperty.set(true);
            }catch(Exception e){
            }
        }
    }

    /**
     * 检查系统剪切板中的内容，如果是XML那么尝试解析成模型，如果成功那么粘贴为当前节点的子节点，并刷新节点。
     */
    public void pasteAsChild(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
        if(content != null && !content.isEmpty()){
            try {
                Thing thing = new Thing();
                XmlCoder.parse(thing, content);

                currentThing.addChild(thing);
                refresh();

                modifiedProperty.set(true);
            }catch(Exception e){
            }
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(message);
        alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
            @Override
            public void accept(ButtonType buttonType) {
                if(buttonType == ButtonType.OK){
                    if(currentThing == currentThing.getRoot()){
                        String message = UtilString.getString("lang:d=当前节点是根节点，确定要删除整个模型？&Current nod is root, are you sure to remove whole thing?", actionContext);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setHeaderText(message);
                        alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                            @Override
                            public void accept(ButtonType buttonType) {
                                if(buttonType == ButtonType.OK){
                                    ThingCache.remove(currentThing.getMetadata().getPath());
                                    currentThing.remove();
                                    if(onRemoveProperty.get() != null) {
                                        if (onRemoveProperty.get().call(currentThing)) {
                                            modifiedProperty.set(true);
                                        }
                                    }
                                }
                            }
                        });
                    }else{
                        Thing parent = currentThing.getParent();
                        parent.removeChild(currentThing);

                        modifiedProperty.set(true);

                        TreeItem<Thing> currentItem = outlineTree.getSelectionModel().getSelectedItem();
                        currentItem.getParent().getChildren().remove(currentItem);
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
                ObservableList<TreeItem<Thing>> childs = parentItem.getChildren();

                int index = childs.indexOf(currentItem);
                childs.set(index, parentItem.getChildren().get(index + 1));
                childs.set(index + 1, currentItem);

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

        TreeItem<Thing> rootItem = new TreeItem<>(this.thing);
        outlineTree.setRoot(rootItem);

        for(Thing child : this.thing.getChilds()){
            initOutlineTreeItem(rootItem, child);
        }

        rootItem.setExpanded(true);
        selectThing(thing);
    }

    /**
     * 在节点树上选中指定的模型，该模型应该是正在编辑的模型的某个子节点。
     *
     * @param thing
     */
    public void selectThing(Thing thing){
        selectThing(thing, outlineTree.getRoot());
    }

    private boolean selectThing(Thing thing, TreeItem<Thing> treeItem){
        if(treeItem.getValue() == thing){
            outlineTree.getSelectionModel().select(treeItem);
            return true;
        }

        for(TreeItem<Thing> childItem : treeItem.getChildren()){
            if(selectThing(thing,childItem)){
                return true;
            }
        }

        return false;
    }

    private void initOutlineTreeItem(TreeItem<Thing> parentItem, Thing thing){
        TreeItem<Thing> item = new TreeItem<>(thing);
        parentItem.getChildren().add(item);

        for(Thing child : thing.getChilds()){
            initOutlineTreeItem(item, child);
        }
    }

    /**
     * 返回当前正在编辑的节点。
     *
     * @return
     */
    public Thing getCurrentThing(){
        return currentThing;
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

    public final ObjectProperty<EventHandler<ThingEditorEvent>> onSelectThingProperty() { return onSelectThing; }
    public final void setOnSelectThing(EventHandler<ThingEditorEvent> value) { onSelectThingProperty().set(value); }
    public final EventHandler<ThingEditorEvent> getOnSelectThing() { return onSelectThingProperty().get(); }
    private ObjectProperty<EventHandler<ThingEditorEvent>> onSelectThing = new ObjectPropertyBase<EventHandler<ThingEditorEvent>>() {
        @Override
        public Object getBean() {
            return ThingEditor.this;
        }

        @Override
        public String getName() {
            return "onOpenIndex";
        }
    };
}
