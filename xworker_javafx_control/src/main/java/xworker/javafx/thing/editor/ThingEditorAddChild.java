package xworker.javafx.thing.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.dataObject.DataObject;
import xworker.util.ThingGroup;
import xworker.util.ThingUtils;
import xworker.util.XWorkerUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingEditorAddChild implements ThingEditorContentNode{
    Node node;
    ThingEditor thingEditor;
    javafx.scene.control.Button addButton;
    javafx.scene.control.ChoiceBox<Thing> childDescBox;
    javafx.scene.control.TreeView<ThingGroup> childsTree;
    javafx.scene.control.Button returnEditorButton;
    javafx.scene.control.Button searchButton;
    javafx.scene.control.TextField searchText;
    xworker.javafx.thing.form.ThingForm thingForm;
    javafx.scene.web.WebView webView;

    public ThingEditorAddChild(ThingEditor thingEditor){
        this.thingEditor = thingEditor;
        Thing nodeThing = World.getInstance().getThing("xworker.javafx.thing.editor.AddChildNode/@Nodes/@addChildBox");
        ActionContext actionContext = new ActionContext();
        node = nodeThing.doAction("create", actionContext);

        addButton = actionContext.getObject("addButton");
        childDescBox = actionContext.getObject("childDescBox");
        childsTree = actionContext.getObject("childsTree");
        returnEditorButton = actionContext.getObject("returnEditorButton");
        searchButton = actionContext.getObject("searchButton");
        searchText = actionContext.getObject("searchText");
        thingForm = actionContext.getObject("thingForm");
        webView = actionContext.getObject("webView");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
        returnEditorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                thingEditor.showForm();
            }
        });
        childDescBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Thing>() {
            @Override
            public void changed(ObservableValue<? extends Thing> observable, Thing oldValue, Thing newValue) {
                if(newValue == null){
                    return;
                }

                //在这里也过滤了excludeDescriptorsForChilds的事物
                List<Thing> desChilds = newValue.getAllChilds("thing");
                //for(child in desChilds){
                //    log.info("child=" + child);
                //}
                //过滤不需要的，即excludeDescriptorsForChilds执行的描述者下的子节点
                Map<String, String> excludes = new HashMap<String, String>();
                String excludeDescriptorsForChilds = newValue.getStringBlankAsNull("excludeDescriptorsForChilds");
                if(excludeDescriptorsForChilds != null) {
                    for(String ext : excludeDescriptorsForChilds.split("[,]")) {
                        excludes.put(ext, ext);
                    }

                    for(int i=0; i<desChilds.size(); i++) {
                        Thing child = desChilds.get(i);
                        if(excludes.get(child.getParent().getMetadata().getPath()) != null) {
                            //父节点，即描述者在excludeDescriptorsForChilds中排除了
                            desChilds.remove(i);
                            i--;
                        }

                    }
                }

                //查询子节点，包含注册的
                List<Thing> childs = XWorkerUtils.searchRegistThings(newValue, "child", Collections.emptyList(), false, true, new ActionContext());
                Map<String, Thing> context = new HashMap<String, Thing>();
                for(Thing child : childs) {
                    //log.info("child=" + rc);
                    if (excludes.get(child.getMetadata().getPath()) != null) {
                        continue;
                    }

                    if (child != null) {
                        //作用域是private的也不添加
                        if ("private".equals(child.getString("modifier"))) {
                            continue;
                        }

                        if (child.getBoolean("th_registMyChilds")) {
                            for (Thing cchild : child.getChilds()) {
                                //屏蔽注册的不添加
                                if (cchild.getBoolean("th_registDisabled")) {
                                    continue;
                                }

                                //作用域是private的也不添加
                                if ("private".equals(cchild.getString("modifier"))) {
                                    continue;
                                }

                                addThing(desChilds, cchild, context);
                            }
                        } else {
                            addThing(desChilds, child, context);
                        }
                    }
                }

                ThingUtils.initFromRegistCache(desChilds, context, newValue, "child");
                //移除非thing节点
                Map<String, String> ctx = new HashMap<>();
                for(int i=0; i<desChilds.size(); i++){
                    Thing thing = desChilds.get(i);
                    if(!"thing".equals(thing.getThingName())){
                        desChilds.remove(i);
                        i--;
                        continue;
                    }
                    if ("private".equals(thing.getString("modifier"))) {
                        desChilds.remove(i);
                        i--;
                        continue;
                    }
                    //去重，为啥会重，暂时未知，以上代码从swt拷贝，按理不会重
                    String path = thing.getMetadata().getPath();
                    if(ctx.get(path) != null){
                        desChilds.remove(i);
                        i--;
                    }else{
                        ctx.put(path, path);
                    }
                }
                ThingGroup thingGroup = new ThingGroup();
                thingGroup.addThings(desChilds);
                thingGroup.sort();
                childsTree.setShowRoot(false);

                TreeItem<ThingGroup> rootItem = new TreeItem<>(thingGroup);
                for(ThingGroup child : thingGroup.getChilds()){
                    initChildTreeItem(rootItem, child);
                }
                childsTree.setRoot(rootItem);
                childsTree.getSelectionModel().select(0);
            }
        });
        childsTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ThingGroup>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ThingGroup>> observable, TreeItem<ThingGroup> oldValue, TreeItem<ThingGroup> newValue) {
                if(newValue == null){
                    addButton.setDisable(true);
                    return;
                }

                ThingGroup group = newValue.getValue();
                Thing thing = group.getThing();
                if(thing != null){
                    addButton.setDisable(false);
                    webView.getEngine().loadContent(XWorkerUtils.getThingDesc(thing));

                    Thing newThing = new Thing(thing);
                    newThing.put("name", null);
                    thingForm.setThing(newThing);
                }else{
                    addButton.setDisable(true);
                    webView.getEngine().loadContent("");
                    thingForm.setThing(null);
                }
            }
        });

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Thing thing = thingForm.getThing(true);
                thingEditor.add(thing);

                Map<String, Object> values = thingForm.getValues();
                values.put("name", "");
                thingForm.setValues(values);
            }
        });
    }

    private void addThing(List<Thing> list, Thing thing, Map<String, Thing >context){
        String path = thing.getMetadata().getPath();
        if(context.get(path) != null){
            return;
        }

        context.put(path, thing);
        list.add(thing);
    }

    private static void initChildTreeItem(TreeItem<ThingGroup> parentItem, ThingGroup thingGroup){
        TreeItem<ThingGroup> item = new TreeItem<>(thingGroup);
        parentItem.getChildren().add(item);

        for(ThingGroup group : thingGroup.getChilds()){
            initChildTreeItem(item, group);
        }
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public boolean beforeChangeThing(Thing newThing) {
        return true;
    }

    @Override
    public void setThing(Thing thing, Thing descriptor) {
        childDescBox.getItems().clear();
        List<Thing> descs = thing.getDescriptors();
        childDescBox.getItems().addAll(descs);

        childDescBox.getSelectionModel().select(0);
    }

    @Override
    public Node getNode() {
        return node;
    }
}
